package com.pragma.conciliation.infrastructure.persistence;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostgresReconciliationRepository implements ReconciliationRepository {

    private static final Logger log = LoggerFactory.getLogger(PostgresReconciliationRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Mono<Reconciliation> save(Reconciliation reconciliation) {
        return Mono.fromCallable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = toEntity(reconciliation);
                entityManager.persist(entity);
                entityManager.getTransaction().commit();
                log.debug("Entidad persistida: id={}", entity.getId());
                return toDomain(entity);
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                log.error("Error al persistir conciliación: {}", e.getMessage(), e);
                throw new RuntimeException("Error al guardar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Reconciliation> findById(String id) {
        return Mono.fromCallable(() -> {
            ReconciliationEntity entity = entityManager.find(ReconciliationEntity.class, id);
            return entity != null ? toDomain(entity) : null;
        });
    }

    @Override
    public Mono<Reconciliation> findByMovementId(String movementId) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.movementId = :movementId",
                    ReconciliationEntity.class);
            query.setParameter("movementId", movementId);
            query.setMaxResults(1);
            List<ReconciliationEntity> results = query.getResultList();
            return results.isEmpty() ? null : toDomain(results.get(0));
        });
    }

    @Override
    public Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    ReconciliationEntity.class);
            query.setParameter("eventId", eventId);
            query.setParameter("version", version);
            query.setMaxResults(1);
            List<ReconciliationEntity> results = query.getResultList();
            return results.isEmpty() ? null : toDomain(results.get(0));
        });
    }

    @Override
    public Mono<Boolean> existsByEventIdAndVersion(String eventId, int version) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    Long.class);
            query.setParameter("eventId", eventId);
            query.setParameter("version", version);
            return query.getSingleResult() > 0;
        });
    }

    @Override
    public Flux<Reconciliation> findByState(ReconciliationState state) {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state",
                    ReconciliationEntity.class);
            query.setParameter("state", state.name());
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime) {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state AND r.receivedAt < :cutoffTime",
                    ReconciliationEntity.class);
            query.setParameter("state", state.name());
            query.setParameter("cutoffTime", cutoffTime);
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Flux<Reconciliation> findAllByOrderByReceivedAtAsc() {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r ORDER BY r.receivedAt ASC",
                    ReconciliationEntity.class);
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration) {
        return Mono.fromCallable(() -> {
            Instant cutoffTime = Instant.now().minus(maxPendingDuration);
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state AND r.receivedAt < :cutoffTime",
                    ReconciliationEntity.class);
            query.setParameter("state", ReconciliationState.PENDING.name());
            query.setParameter("cutoffTime", cutoffTime);
            return query.getResultList().stream()
                    .map(this::toDomain)
                    .toList();
        });
    }

    @Override
    public Mono<Long> countByState(ReconciliationState state) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.state = :state",
                    Long.class);
            query.setParameter("state", state.name());
            return query.getSingleResult();
        });
    }

    @Override
    public Mono<Reconciliation> update(Reconciliation reconciliation) {
        return Mono.fromCallable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = entityManager.find(
                        ReconciliationEntity.class, reconciliation.getId());
                if (entity == null) {
                    throw new RuntimeException("Conciliación no encontrada: " + reconciliation.getId());
                }
                updateEntityFromDomain(entity, reconciliation);
                entityManager.getTransaction().commit();
                log.debug("Entidad actualizada: id={}", entity.getId());
                return toDomain(entity);
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                log.error("Error al actualizar conciliación: {}", e.getMessage(), e);
                throw new RuntimeException("Error al actualizar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = entityManager.find(ReconciliationEntity.class, id);
                if (entity != null) {
                    entityManager.remove(entity);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw new RuntimeException("Error al eliminar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Boolean> existsByIdempotencyKey(String idempotencyKey) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    Long.class);
            String[] parts = idempotencyKey.split("-");
            if (parts.length >= 2) {
                query.setParameter("eventId", parts[0]);
                query.setParameter("version", Integer.parseInt(parts[parts.length - 1]));
                return query.getSingleResult() > 0;
            }
            return false;
        });
    }

    private ReconciliationEntity toEntity(Reconciliation reconciliation) {
        ReconciliationEntity entity = new ReconciliationEntity();
        entity.setId(reconciliation.getId());
        entity.setMovementId(reconciliation.getMovementId());
        entity.setEventId(reconciliation.getEventId());
        entity.setVersion(reconciliation.getVersion());
        entity.setState(reconciliation.getState().name());
        entity.setReceivedAt(reconciliation.getReceivedAt());
        entity.setMatchedAt(reconciliation.getMatchedAt());
        entity.setCoreBankReference(reconciliation.getCoreBankReference());
        entity.setPaymentGatewayReference(reconciliation.getPaymentGatewayReference());
        entity.setSettlementReference(reconciliation.getSettlementReference());
        entity.setMovementAmount(reconciliation.getMovementAmount());
        entity.setMovementCurrency(reconciliation.getMovementCurrency());
        entity.setManualResolution(reconciliation.getManualResolution());
        entity.setProcessedBy(reconciliation.getProcessedBy());
        return entity;
    }

    private void updateEntityFromDomain(ReconciliationEntity entity, Reconciliation reconciliation) {
        entity.setState(reconciliation.getState().name());
        entity.setMatchedAt(reconciliation.getMatchedAt());
        entity.setCoreBankReference(reconciliation.getCoreBankReference());
        entity.setPaymentGatewayReference(reconciliation.getPaymentGatewayReference());
        entity.setSettlementReference(reconciliation.getSettlementReference());
        entity.setManualResolution(reconciliation.getManualResolution());
        entity.setProcessedBy(reconciliation.getProcessedBy());
    }

    private Reconciliation toDomain(ReconciliationEntity entity) {
        return new Reconciliation(
                entity.getId(),
                entity.getMovementId(),
                entity.getEventId(),
                entity.getVersion(),
                ReconciliationState.valueOf(entity.getState()),
                entity.getReceivedAt(),
                entity.getMatchedAt(),
                entity.getCoreBankReference(),
                entity.getPaymentGatewayReference(),
                entity.getSettlementReference(),
                entity.getMovementAmount(),
                entity.getMovementCurrency(),
                entity.getManualResolution(),
                entity.getProcessedBy()
        );
    }

    @Entity
    @Table(name = "reconciliations")
    private static class ReconciliationEntity {
        private String id;
        private String movementId;
        private String eventId;
        private int version;
        private String state;
        private Instant receivedAt;
        private Instant matchedAt;
        private String coreBankReference;
        private String paymentGatewayReference;
        private String settlementReference;
        private BigDecimal movementAmount;
        private String movementCurrency;
        private String manualResolution;
        private String processedBy;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getMovementId() { return movementId; }
        public void setMovementId(String movementId) { this.movementId = movementId; }
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public Instant getReceivedAt() { return receivedAt; }
        public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
        public Instant getMatchedAt() { return matchedAt; }
        public void setMatchedAt(Instant matchedAt) { this.matchedAt = matchedAt; }
        public String getCoreBankReference() { return coreBankReference; }
        public void setCoreBankReference(String coreBankReference) { this.coreBankReference = coreBankReference; }
        public String getPaymentGatewayReference() { return paymentGatewayReference; }
        public void setPaymentGatewayReference(String paymentGatewayReference) { this.paymentGatewayReference = paymentGatewayReference; }
        public String getSettlementReference() { return settlementReference; }
        public void setSettlementReference(String settlementReference) { this.settlementReference = settlementReference; }
        public BigDecimal getMovementAmount() { return movementAmount; }
        public void setMovementAmount(BigDecimal movementAmount) { this.movementAmount = movementAmount; }
        public String getMovementCurrency() { return movementCurrency; }
        public void setMovementCurrency(String movementCurrency) { this.movementCurrency = movementCurrency; }
        public String getManualResolution() { return manualResolution; }
        public void setManualResolution(String manualResolution) { this.manualResolution = manualResolution; }
        public String getProcessedBy() { return processedBy; }
        public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    }
}