package com.pragma.conciliation.application.commands;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.events.MovementReceivedEvent.MovementType;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.domain.service.ReprocessingStrategy;
import com.pragma.conciliation.domain.service.ReprocessingStrategy.StrategyType;
import com.pragma.conciliation.infrastructure.messaging.IdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReconcileMovementCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ReconcileMovementCommandHandler.class);

    private final ReconciliationRepository reconciliationRepository;
    private final ReconciliationService reconciliationService;
    private final IdempotencyStore idempotencyStore;
    private final Map<StrategyType, ReprocessingStrategy> reprocessingStrategies;

    public ReconcileMovementCommandHandler(
            ReconciliationRepository reconciliationRepository,
            ReconciliationService reconciliationService,
            IdempotencyStore idempotencyStore,
            List<ReprocessingStrategy> strategies) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationService = reconciliationService;
        this.idempotencyStore = idempotencyStore;
        this.reprocessingStrategies = strategies.stream()
            .collect(Collectors.toMap(ReprocessingStrategy::getType, Function.identity()));
    }

    public Mono<Reconciliation> handle(ReconcileMovementCommand command) {
        log.info("Iniciando conciliación para movimiento: eventId={}, version={}, movementId={}",
                command.getEventId(), command.getVersion(), command.getMovementId());

        String idempotencyKey = command.getIdempotencyKey();

        return idempotencyStore.findByEventIdAndVersion(command.getEventId(), command.getVersion())
            .flatMap(record -> {
                if (record != null && !command.isForceReprocessing()) {
                    log.warn("Movimiento ya procesado: idempotencyKey={}", idempotencyKey);
                    return reconciliationRepository.findByEventIdAndVersion(
                        command.getEventId(), command.getVersion()
                    );
                }
                return executeReconciliation(command);
            })
            .switchIfEmpty(Mono.defer(() -> executeReconciliation(command)))
            .doOnSuccess(reconciliation -> {
                if (reconciliation != null) {
                    log.info("Conciliación completada: id={}, state={}, movementId={}",
                        reconciliation.getId(), reconciliation.getState(), reconciliation.getMovementId());
                }
            })
            .doOnError(error ->
                log.error("Error en conciliación: eventId={}, movementId={}, error={}",
                    command.getEventId(), command.getMovementId(), error.getMessage()));
    }

    private Mono<Reconciliation> executeReconciliation(ReconcileMovementCommand command) {
        return createOrUpdateReconciliation(command)
            .flatMap(this::performMatching)
            .flatMap(this::updateReconciliationState)
            .flatMap(this::persistReconciliation)
            .flatMap(this::markAsProcessed);
    }

    private Mono<Reconciliation> createOrUpdateReconciliation(ReconcileMovementCommand command) {
        return reconciliationRepository.findByMovementId(command.getMovementId())
            .flatMap(existing -> {
                if (existing.getVersion() < command.getVersion()) {
                    log.info("Actualizando reconciliación existente: movementId={}, oldVersion={}, newVersion={}",
                        command.getMovementId(), existing.getVersion(), command.getVersion());
                    return updateReconciliation(existing, command);
                }
                log.info("Versión obsoleta ignorada: movementId={}, existingVersion={}, newVersion={}",
                    command.getMovementId(), existing.getVersion(), command.getVersion());
                return Mono.just(existing);
            })
            .switchIfEmpty(Mono.defer(() -> createNewReconciliation(command)));
    }

    private Mono<Reconciliation> createNewReconciliation(ReconcileMovementCommand command) {
        Reconciliation reconciliation = new Reconciliation(
            command.getMovementId(),
            command.getEventId(),
            command.getVersion(),
            ReconciliationState.PENDING,
            command.getReceivedAt()
        );
        log.debug("Creando nueva reconciliación: movementId={}, eventId={}",
            command.getMovementId(), command.getEventId());
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> updateReconciliation(Reconciliation existing, ReconcileMovementCommand command) {
        existing.markAsMismatched("Versión actualizada: " + command.getVersion());
        return reconciliationRepository.save(existing)
            .flatMap(updated -> createNewReconciliation(command));
    }

    private Mono<Reconciliation> performMatching(Reconciliation reconciliation) {
        return reconciliationService.processMovement(
            new MovementReceivedEvent(
                reconciliation.getEventId(),
                reconciliation.getVersion(),
                reconciliation.getMovementId(),
                "CORE_BANK",
                reconciliation.getCoreBankReference() != null ? 
                    reconciliation.getCoreBankReference() : reconciliation.getMovementId(),
                reconciliation.getMovementAmount(),
                reconciliation.getMovementCurrency(),
                Instant.now(),
                Instant.now(),
                reconciliation.getId(),
                MovementType.CREDIT,
                null,
                null
            )
        );
    }

    private Mono<Reconciliation> updateReconciliationState(Reconciliation reconciliation) {
        if (reconciliation.getState() == ReconciliationState.PENDING) {
            Duration pendingDuration = reconciliation.getPendingDuration();
            if (pendingDuration.toMinutes() > 30) {
                log.warn("Movimiento en estado PENDING por más de 30 minutos: movementId={}, duration={}",
                    reconciliation.getMovementId(), pendingDuration.toMinutes());
            }
        }
        return Mono.just(reconciliation);
    }

    private Mono<Reconciliation> persistReconciliation(Reconciliation reconciliation) {
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> markAsProcessed(Reconciliation reconciliation) {
        String idempotencyKey = reconciliation.getIdempotencyKey();
        idempotencyStore.markAsProcessed(reconciliation.getEventId(), reconciliation.getVersion());
        return Mono.just(reconciliation);
    }

    public Flux<Reconciliation> reprocess(StrategyType strategyType, Instant since) {
        ReprocessingStrategy strategy = reprocessingStrategies.get(strategyType);
        if (strategy == null) {
            return Flux.error(
                new IllegalArgumentException("Estrategia de reprocesamiento no encontrada: " + strategyType)
            );
        }

        log.info("Iniciando reprocesamiento con estrategia: {}, desde={}", strategyType, since);

        return strategy.findPendingReconciliations(since, Instant.now())
            .flatMap(reconciliation -> {
                ReconcileMovementCommand command = ReconcileMovementCommand.fromEvent(
                    new MovementReceivedEvent(
                        reconciliation.getEventId(),
                        reconciliation.getVersion(),
                        reconciliation.getMovementId(),
                        "REPROCESS",
                        reconciliation.getCoreBankReference() != null ?
                            reconciliation.getCoreBankReference() : reconciliation.getMovementId(),
                        reconciliation.getMovementAmount(),
                        reconciliation.getMovementCurrency(),
                        Instant.now(),
                        Instant.now(),
                        reconciliation.getId(),
                        MovementType.CREDIT,
                        null,
                        null
                    )
                );
                return handle(command);
            }, 10);
    }
}