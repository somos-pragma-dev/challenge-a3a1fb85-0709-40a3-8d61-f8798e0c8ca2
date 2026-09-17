package com.pragma.conciliation.infrastructure.persistence;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.domain.service.ReprocessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class PostgresSnapshotReprocessor implements ReprocessingStrategy {

    private static final Logger log = LoggerFactory.getLogger(PostgresSnapshotReprocessor.class);

    private final ReconciliationRepository reconciliationRepository;
    private final ReconciliationService reconciliationService;

    @Value("${conciliation.reprocessing.snapshot.batch-size:50}")
    private int batchSize;

    @Value("${conciliation.reprocessing.snapshot.max-retries:3}")
    private int maxRetries;

    @Value("${conciliation.reprocessing.snapshot.states:PENDING,MISMATCHED}")
    private List<String> targetStates;

    public PostgresSnapshotReprocessor(
            ReconciliationRepository reconciliationRepository,
            ReconciliationService reconciliationService) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationService = reconciliationService;
    }

    @Override
    public String getStrategyName() {
        return "POSTGRES_SNAPSHOT";
    }

    @Override
    public ReprocessingResult reprocess(String reconciliationId, int attemptNumber) {
        log.info("Iniciando reprocesamiento por snapshot para reconciliationId={}, intento={}",
                reconciliationId, attemptNumber);

        if (attemptNumber > maxRetries) {
            log.error("Máximo de intentos alcanzado para reconciliationId={}", reconciliationId);
            return ReprocessingResult.failure(
                    reconciliationId,
                    "Máximo de intentos alcanzado: " + maxRetries
            );
        }

        Optional<Reconciliation> reconciliationOpt =
                reconciliationRepository.findById(reconciliationId);

        if (reconciliationOpt.isEmpty()) {
            log.warn("No se encontró reconciliation para reprocesar: {}", reconciliationId);
            return ReprocessingResult.failure(reconciliationId, "Reconciliation no encontrado");
        }

        Reconciliation reconciliation = reconciliationOpt.get();

        if (!canReprocess(reconciliation)) {
            log.warn("Reconciliation no apta para reprocesamiento: {} en estado {}",
                    reconciliationId, reconciliation.getState());
            return ReprocessingResult.failure(
                    reconciliationId,
                    "Estado no apta para reprocesamiento: " + reconciliation.getState()
            );
        }

        try {
            reconciliationService.processReconciliation(reconciliation);
            log.info("Reprocesamiento exitoso para reconciliationId={}", reconciliationId);
            return ReprocessingResult.success(reconciliationId);

        } catch (Exception e) {
            log.error("Error en reprocesamiento para reconciliationId={}", reconciliationId, e);
            reconciliation.markAsMismatched("Error en reprocesamiento: " + e.getMessage());
            reconciliationRepository.save(reconciliation);

            return ReprocessingResult.failure(reconciliationId, e.getMessage());
        }
    }

    @Override
    public ReprocessingResult reprocessBatch(List<String> reconciliationIds) {
        log.info("Iniciando reprocesamiento por lote de {} elementos", reconciliationIds.size());

        int successCount = 0;
        int failureCount = 0;

        for (String reconciliationId : reconciliationIds) {
            ReprocessingResult result = reprocess(reconciliationId, 1);
            if (result.success()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        log.info("Reprocesamiento por lote completado: {} exitosos, {} fallidos",
                successCount, failureCount);

        return new ReprocessingResult(
                true,
                reconciliationIds,
                String.format("Lote: %d exitosos, %d fallidos", successCount, failureCount)
        );
    }

    @Override
    public List<Reconciliation> findReconciliationsToReprocess() {
        List<ReconciliationState> states = targetStates.stream()
                .map(ReconciliationState::valueOf)
                .toList();

        log.debug("Buscando reconciliations en estados: {} para reprocesamiento", states);

        List<Reconciliation> reconciliations = reconciliationRepository.findByStateIn(states);

        Instant cutoffTime = Instant.now().minusSeconds(30 * 60);

        return reconciliations.stream()
                .filter(r -> r.getReceivedAt().isBefore(cutoffTime))
                .filter(this::canReprocess)
                .limit(batchSize)
                .toList();
    }

    private boolean canReprocess(Reconciliation reconciliation) {
        ReconciliationState currentState = reconciliation.getState();

        if (currentState == ReconciliationState.PENDING) {
            return reconciliation.getPendingDuration().getSeconds() > 300;
        }

        if (currentState == ReconciliationState.MISMATCHED) {
            return true;
        }

        return false;
    }

    public void reprocessStaleReconciliations() {
        log.info("Iniciando reprocesamiento de conciliaciones obsoletas");

        List<Reconciliation> staleReconciliations = findReconciliationsToReprocess();

        if (staleReconciliations.isEmpty()) {
            log.info("No hay conciliaciones obsoletas para reprocesar");
            return;
        }

        log.info("Se reprocesarán {} conciliaciones obsoletas", staleReconciliations.size());

        for (Reconciliation reconciliation : staleReconciliations) {
            reprocess(reconciliation.getId(), 1);
        }
    }
}