package com.pragma.conciliation.infrastructure.rest;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.infrastructure.config.ReconciliationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reconciliations")
public class ReconciliationController {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationController.class);
    
    private final ReconciliationService reconciliationService;
    private final ReconciliationConfig config;
    
    public ReconciliationController(ReconciliationService reconciliationService,
                                     ReconciliationConfig config) {
        this.reconciliationService = reconciliationService;
        this.config = config;
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ReconciliationResponse>> getById(@PathVariable String id) {
        log.debug("Consultando conciliación por ID: {}", id);
        
        return reconciliationService.findById(id)
                .map(r -> ResponseEntity.ok(mapToResponse(r)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public Mono<ResponseEntity<List<ReconciliationResponse>>> getAll(
            @RequestParam(required = false) ReconciliationState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.debug("Consultando conciliaciones: state={}, page={}, size={}", state, page, size);
        
        if (state != null) {
            return reconciliationService.findByState(state)
                    .take(size)
                    .collectList()
                    .map(list -> list.stream().map(this::mapToResponse).toList())
                    .map(ResponseEntity::ok);
        }
        
        return reconciliationService.getAllReconciliations()
                .take(size)
                .collectList()
                .map(list -> list.stream().map(this::mapToResponse).toList())
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/pending")
    public Mono<ResponseEntity<List<ReconciliationResponse>>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.debug("Consultando conciliaciones pendientes: page={}, size={}", page, size);
        
        return reconciliationService.findPending(page, size)
                .map(list -> list.stream().map(this::mapToResponse).toList())
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/stuck")
    public Mono<ResponseEntity<List<ReconciliationResponse>>> getStuck(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        var maxPending = config.getMatching().getMaxPendingDuration();
        log.debug("Consultando conciliaciones bloqueadas (mayor a {}): page={}, size={}", 
                maxPending, page, size);
        
        return reconciliationService.findStuck(maxPending, page, size)
                .map(list -> list.stream().map(this::mapToResponse).toList())
                .map(ResponseEntity::ok);
    }
    
    @PostMapping("/{id}/resolve")
    public Mono<ResponseEntity<ReconciliationResponse>> resolveManually(
            @PathVariable String id,
            @RequestBody ManualResolutionRequest request) {
        
        log.info("Resolución manual solicitada para conciliación {}: resolution={}, processedBy={}",
                id, request.resolution(), request.processedBy());
        
        if (!config.getManualIntervention().isEnabled()) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Intervención manual deshabilitada en la configuración"));
        }
        
        return reconciliationService.findById(id)
                .flatMap(r -> {
                    if (!r.getState().requiresManualReview()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                                "La conciliación no requiere revisión manual. Estado actual: " + r.getState()));
                    }
                    return reconciliationService.resolveManually(id, request.resolution(), request.processedBy());
                })
                .map(r -> ResponseEntity.ok(mapToResponse(r)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/retry")
    public Mono<ResponseEntity<ReconciliationResponse>> retryMatching(
            @PathVariable String id,
            @RequestBody(required = false) RetryRequest request) {
        
        log.info("Reintento de matching solicitado para conciliación: {}", id);
        
        boolean force = request != null && request.force();
        return reconciliationService.retryMatching(id, force)
                .map(r -> ResponseEntity.ok(mapToResponse(r)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/reprocess")
    public Mono<ResponseEntity<Map<String, Object>>> reprocessAll(
            @RequestBody ReprocessRequest request) {
        
        log.info("Reprocesamiento masivo solicitado: from={}, to={}, strategy={}",
                request.from(), request.to(), request.strategy());
        
        return reconciliationService.reprocess(request.from(), request.to(), request.strategy())
                .map(count -> ResponseEntity.ok(Map.of(
                        "reprocessed", count,
                        "timestamp", Instant.now().toString()
                )));
    }
    
    @GetMapping("/stats")
    public Mono<ResponseEntity<Map<String, Object>>> getStats() {
        log.debug("Consultando estadísticas de conciliación");
        
        return reconciliationService.getStatistics()
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Consultando salud del sistema de conciliación");
        
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "matchingWindow", config.getMatching().getWindow().toMinutes() + " minutes",
                "maxPendingDuration", config.getMatching().getMaxPendingDuration().toHours() + " hours",
                "manualInterventionEnabled", config.getManualIntervention().isEnabled()
        );
        
        return ResponseEntity.ok(health);
    }
    
    private ReconciliationResponse mapToResponse(Reconciliation r) {
        return new ReconciliationResponse(
                r.getId(),
                r.getMovementId(),
                r.getEventId(),
                r.getVersion(),
                r.getState().name(),
                r.getReceivedAt().toString(),
                r.getMatchedAt() != null ? r.getMatchedAt().toString() : null,
                r.getCoreBankReference(),
                r.getPaymentGatewayReference(),
                r.getSettlementReference(),
                r.getMovementAmount(),
                r.getMovementCurrency(),
                r.getManualResolution(),
                r.getProcessedBy(),
                r.getStateHistory().stream()
                        .map(t -> Map.of(
                                "from", t.from() != null ? t.from().name() : null,
                                "to", t.to().name(),
                                "reason", t.reason(),
                                "timestamp", t.timestamp().toString()
                        ))
                        .toList()
        );
    }
    
    public record ReconciliationResponse(
            String id,
            String movementId,
            String eventId,
            int version,
            String state,
            String receivedAt,
            String matchedAt,
            String coreBankReference,
            String paymentGatewayReference,
            String settlementReference,
            BigDecimal amount,
            String currency,
            String manualResolution,
            String processedBy,
            List<Map<String, Object>> stateHistory
    ) {}
    
    public record ManualResolutionRequest(
            String resolution,
            String processedBy
    ) {
        public ManualResolutionRequest {
            if (resolution == null || resolution.isBlank()) {
                throw new IllegalArgumentException("La resolución no puede estar vacía");
            }
            if (processedBy == null || processedBy.isBlank()) {
                throw new IllegalArgumentException("El procesado por no puede estar vacío");
            }
        }
    }
    
    public record RetryRequest(
            boolean force
    ) {}
    
    public record ReprocessRequest(
            Instant from,
            Instant to,
            String strategy
    ) {
        public ReprocessRequest {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Los parámetros from y to son obligatorios");
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("from debe ser anterior a to");
            }
            if (strategy == null || strategy.isBlank()) {
                strategy = "KAFKA_REPLAY";
            }
        }
        
        public String strategy() {
            return strategy;
        }
    }
}