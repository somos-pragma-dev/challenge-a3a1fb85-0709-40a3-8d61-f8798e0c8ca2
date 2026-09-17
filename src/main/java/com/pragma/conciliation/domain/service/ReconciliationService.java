package com.pragma.conciliation.domain.service;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReconciliationService {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);
    
    private final ReconciliationRepository reconciliationRepository;
    private final Duration defaultMatchingWindow;
    private final Duration maxPendingDuration;

    public ReconciliationService(ReconciliationRepository reconciliationRepository,
                                  com.pragma.conciliation.infrastructure.config.ReconciliationConfig config) {
        this.reconciliationRepository = reconciliationRepository;
        this.defaultMatchingWindow = config.getMatching().getWindow();
        this.maxPendingDuration = config.getMatching().getMaxPendingDuration();
    }

    public Mono<Reconciliation> processMovement(MovementReceivedEvent event) {
        log.debug("Procesando movimiento: eventId={}, movementId={}", event.getEventId(), event.getMovementId());
        return reconciliationRepository.findByMovementId(event.getMovementId())
                .flatMap(existing -> createNewReconciliation(event))
                .switchIfEmpty(Mono.defer(() -> createNewReconciliation(event)))
                .flatMap(this::attemptMatching);
    }

    private Mono<Reconciliation> createNewReconciliation(MovementReceivedEvent event) {
        Reconciliation reconciliation = new Reconciliation(
                event.getMovementId(),
                event.getEventId(),
                event.getVersion(),
                event.getAmount(),
                event.getCurrency(),
                Instant.now()
        );
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> attemptMatching(Reconciliation reconciliation) {
        Instant from = reconciliation.getReceivedAt().minus(defaultMatchingWindow);
        Instant to = reconciliation.getReceivedAt().plus(defaultMatchingWindow);

        return findMatchingReferences(reconciliation)
                .flatMap(result -> {
                    if (result.hasAllReferences()) {
                        return transitionToMatched(reconciliation, result);
                    } else {
                        return transitionToMismatched(reconciliation, "No se encontraron todas las referencias");
                    }
                });
    }

    private Mono<MatchingResult> findMatchingReferences(Reconciliation reconciliation) {
        Instant from = reconciliation.getReceivedAt().minus(defaultMatchingWindow);
        Instant to = reconciliation.getReceivedAt().plus(defaultMatchingWindow);

        return Mono.zip(
                findCoreBankReference(reconciliation.getMovementId(), from, to),
                findPaymentGatewayReference(reconciliation.getMovementId(), from, to),
                findSettlementReference(reconciliation.getMovementId(), from, to)
        ).map(tuple -> new MatchingResult(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3(),
                tuple.getT1() != null && tuple.getT2() != null && tuple.getT3() != null
        ));
    }

    private Mono<String> findCoreBankReference(String movementId, Instant from, Instant to) {
        return Mono.just("CORE-" + movementId);
    }

    private Mono<String> findPaymentGatewayReference(String movementId, Instant from, Instant to) {
        return Mono.just("PGW-" + movementId);
    }

    private Mono<String> findSettlementReference(String movementId, Instant from, Instant to) {
        return Mono.just("STL-" + movementId);
    }

    private Mono<Reconciliation> transitionToMatched(Reconciliation reconciliation,
                                                       MatchingResult result) {
        reconciliation.markAsMatched(result.coreBankReference(),
                result.paymentGatewayReference(),
                result.settlementReference());
        return reconciliationRepository.update(reconciliation);
    }

    private Mono<Reconciliation> transitionToMismatched(Reconciliation reconciliation, String reason) {
        reconciliation.markAsMismatched(reason);
        return reconciliationRepository.update(reconciliation);
    }

    public Mono<Reconciliation> resolveManually(String reconciliationId, String resolution, String processedBy) {
        return reconciliationRepository.findById(reconciliationId)
                .flatMap(reconciliation -> {
                    reconciliation.markAsManual(resolution, processedBy);
                    return reconciliationRepository.update(reconciliation);
                });
    }

    public Mono<List<Reconciliation>> getReconciliationsWithLag() {
        return reconciliationRepository.findPendingWithLagExceeding(maxPendingDuration)
                .collectList();
    }

    public Flux<Reconciliation> getAllReconciliations() {
        return reconciliationRepository.findAllByOrderByReceivedAtAsc();
    }

    public Mono<Reconciliation> getReconciliationById(String id) {
        return reconciliationRepository.findById(id);
    }

    public Mono<Long> countByState(ReconciliationState state) {
        return reconciliationRepository.countByState(state);
    }

    public Mono<Reconciliation> findById(String id) {
        return reconciliationRepository.findById(id);
    }

    public Flux<Reconciliation> findByState(ReconciliationState state) {
        return reconciliationRepository.findByState(state);
    }

    public Mono<List<Reconciliation>> findPending(int page, int size) {
        return reconciliationRepository.findByState(ReconciliationState.PENDING)
                .take(size)
                .collectList();
    }

    public Mono<List<Reconciliation>> findStuck(Duration maxPending, int page, int size) {
        Instant cutoff = Instant.now().minus(maxPending);
        return reconciliationRepository.findByStateAndReceivedAtBefore(ReconciliationState.PENDING, cutoff)
                .take(size)
                .collectList();
    }

    public Mono<Reconciliation> retryMatching(String id, boolean force) {
        return reconciliationRepository.findById(id)
                .flatMap(reconciliation -> {
                    reconciliation.markAsMismatched("Reintento de matching");
                    return attemptMatching(reconciliation);
                });
    }

    public Mono<Integer> reprocess(Instant from, Instant to, String strategy) {
        return reconciliationRepository.findByStateAndReceivedAtBefore(ReconciliationState.PENDING, to)
                .filter(r -> r.getReceivedAt().isAfter(from))
                .flatMap(reconciliation -> attemptMatching(reconciliation).then(Mono.just(1)))
                .reduce(0, Integer::sum);
    }

    public Mono<Map<String, Object>> getStatistics() {
        return Mono.zip(
                countByState(ReconciliationState.PENDING),
                countByState(ReconciliationState.MATCHED),
                countByState(ReconciliationState.MISMATCHED),
                countByState(ReconciliationState.MANUAL_REVIEW)
        ).map(tuple -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("pending", tuple.getT1());
            stats.put("matched", tuple.getT2());
            stats.put("mismatched", tuple.getT3());
            stats.put("manualReview", tuple.getT4());
            stats.put("timestamp", Instant.now().toString());
            return stats;
        });
    }

    public record MatchingResult(
            String coreBankReference,
            String paymentGatewayReference,
            String settlementReference,
            boolean hasAllReferences
    ) {}
}