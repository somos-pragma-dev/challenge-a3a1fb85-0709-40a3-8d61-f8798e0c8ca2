package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class IdempotencyStore {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyStore.class);

    private final JdbcTemplate jdbcTemplate;
    private final ReconciliationRepository reconciliationRepository;

    public IdempotencyStore(JdbcTemplate jdbcTemplate,
                            ReconciliationRepository reconciliationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reconciliationRepository = reconciliationRepository;
        initializeTable();
    }

    private void initializeTable() {
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS idempotency_keys (
                    event_id VARCHAR(255) NOT NULL,
                    version INTEGER NOT NULL,
                    movement_id VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    processed_at TIMESTAMP,
                    PRIMARY KEY (event_id, version)
                )
                """);
            log.info("Tabla de idempotencia inicializada correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar la tabla de idempotencia", e);
        }
    }

    public boolean tryAcquire(String eventId, int version, String movementId) {
        try {
            jdbcTemplate.update("""
                INSERT INTO idempotency_keys (event_id, version, movement_id, created_at)
                VALUES (?, ?, ?, ?)
                """,
                    eventId,
                    version,
                    movementId,
                    Instant.now()
            );

            log.debug("Adquirido lock de idempotencia para eventId={} version={}",
                    eventId, version);
            return true;

        } catch (DataIntegrityViolationException e) {
            log.debug("Duplicado detectado para eventId={} version={}, verificando estado",
                    eventId, version);

            Optional<IdempotencyRecord> existingRecord = findByEventIdAndVersion(eventId, version);
            if (existingRecord.isPresent() && existingRecord.get().processedAt() != null) {
                log.info("Evento ya procesado: eventId={} version={}", eventId, version);
                return false;
            }

            log.warn("Evento en proceso: eventId={} version={}, rehusando procesamiento",
                    eventId, version);
            return false;
        }
    }

    public Optional<IdempotencyRecord> findByEventIdAndVersion(String eventId, int version) {
        try {
            return jdbcTemplate.query("""
                SELECT event_id, version, movement_id, created_at, processed_at
                FROM idempotency_keys
                WHERE event_id = ? AND version = ?
                """,
                (rs, rowNum) -> new IdempotencyRecord(
                        rs.getString("event_id"),
                        rs.getInt("version"),
                        rs.getString("movement_id"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("processed_at") != null
                                ? rs.getTimestamp("processed_at").toInstant()
                                : null
                ),
                eventId,
                version
            ).stream().findFirst();
        } catch (Exception e) {
            log.error("Error al buscar registro de idempotencia: eventId={} version={}",
                    eventId, version, e);
            return Optional.empty();
        }
    }

    public void markAsProcessed(String eventId, int version) {
        int updated = jdbcTemplate.update("""
            UPDATE idempotency_keys
            SET processed_at = ?
            WHERE event_id = ? AND version = ?
            """,
            Instant.now(),
            eventId,
            version
        );

        if (updated > 0) {
            log.debug("Marcado como procesado: eventId={} version={}", eventId, version);
        } else {
            log.warn("No se encontró registro para marcar como procesado: eventId={} version={}",
                    eventId, version);
        }
    }

    public void cleanupOldRecords(int retentionDays) {
        Instant cutoff = Instant.now().minusSeconds(retentionDays * 24L * 60L * 60L);

        int deleted = jdbcTemplate.update("""
            DELETE FROM idempotency_keys
            WHERE created_at < ? AND processed_at IS NOT NULL
            """,
            cutoff
        );

        log.info("Limpiados {} registros de idempotencia antiguos (mayores a {} días)",
                deleted, retentionDays);
    }

    public record IdempotencyRecord(
            String eventId,
            int version,
            String movementId,
            Instant createdAt,
            Instant processedAt
    ) {
        public boolean isProcessed() {
            return processedAt != null;
        }
    }
}