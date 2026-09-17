package com.pragma.conciliation.domain.repository;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface ReconciliationRepository {
    Mono<Reconciliation> save(Reconciliation reconciliation);
    Mono<Reconciliation> findById(String id);
    Mono<Reconciliation> findByMovementId(String movementId);
    Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version);
    Mono<Boolean> existsByEventIdAndVersion(String eventId, int version);
    Flux<Reconciliation> findByState(ReconciliationState state);
    Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime);
    Flux<Reconciliation> findAllByOrderByReceivedAtAsc();
    Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration);
    Mono<Long> countByState(ReconciliationState state);
    Mono<Reconciliation> update(Reconciliation reconciliation);
    Mono<Void> deleteById(String id);
    Mono<Boolean> existsByIdempotencyKey(String idempotencyKey);
    
    default Flux<Reconciliation> findByStateIn(List<ReconciliationState> states) {
        return Flux.fromIterable(states)
                .flatMap(this::findByState);
    }
}