package com.pragma.conciliation.infrastructure.monitoring;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConciliationLagMonitor {

    private static final Logger log = LoggerFactory.getLogger(ConciliationLagMonitor.class);

    private final ReconciliationRepository reconciliationRepository;
    private final MeterRegistry meterRegistry;

    @Value("${conciliation.monitoring.sla-minutes:10}")
    private int slaMinutes;

    @Value("${conciliation.monitoring.alert-threshold-minutes:15}")
    private int alertThresholdMinutes;

    private final AtomicLong currentLagSeconds = new AtomicLong(0);
    private final AtomicReference<Instant> lastAlertTime = new AtomicReference<>(Instant.EPOCH);
    private final AtomicLong pendingCount = new AtomicLong(0);
    private final AtomicLong matchedCount = new AtomicLong(0);
    private final AtomicLong mismatchedCount = new AtomicLong(0);
    private final AtomicLong manualReviewCount = new AtomicLong(0);

    private final Counter lagExceededCounter;
    private final Timer lagCalculationTimer;

    public ConciliationLagMonitor(
            ReconciliationRepository reconciliationRepository,
            MeterRegistry meterRegistry) {
        this.reconciliationRepository = reconciliationRepository;
        this.meterRegistry = meterRegistry;

        this.lagExceededCounter = Counter.builder("conciliation.lag.exceeded")
                .description("Número de veces que el lag de conciliación ha superado el SLA")
                .register(meterRegistry);

        this.lagCalculationTimer = Timer.builder("conciliation.lag.calculation.time")
                .description("Tiempo que tarda el cálculo del lag de conciliación")
                .register(meterRegistry);

        Gauge.builder("conciliation.lag.current.seconds", currentLagSeconds, AtomicLong::get)
                .description("Lag actual de conciliación en segundos")
                .register(meterRegistry);

        Gauge.builder("conciliation.pending.count", pendingCount, AtomicLong::get)
                .description("Número de conciliaciones pendientes")
                .register(meterRegistry);

        Gauge.builder("conciliation.matched.count", matchedCount, AtomicLong::get)
                .description("Número de conciliaciones matched")
                .register(meterRegistry);

        Gauge.builder("conciliation.mismatched.count", mismatchedCount, AtomicLong::get)
                .description("Número de conciliaciones mismatched")
                .register(meterRegistry);

        Gauge.builder("conciliation.manual.review.count", manualReviewCount, AtomicLong::get)
                .description("Número de conciliaciones en revisión manual")
                .register(meterRegistry);
    }

    @Scheduled(fixedRateString = "${conciliation.monitoring.check-interval-ms:60000}")
    public void calculateLagAndAlert() {
        lagCalculationTimer.record(() -> {
            try {
                Instant now = Instant.now();
                Duration slaDuration = Duration.ofMinutes(slaMinutes);

                List<ReconciliationState> statesToCheck = List.of(
                        ReconciliationState.PENDING,
                        ReconciliationState.MATCHED
                );
                
                Flux<Reconciliation> pendingReconciliationsFlux = 
                        reconciliationRepository.findByStateIn(statesToCheck);
                
                List<Reconciliation> pendingReconciliations = pendingReconciliationsFlux.collectList().block();

                long currentPending = pendingReconciliations.stream()
                        .filter(r -> r.getState() == ReconciliationState.PENDING)
                        .count();
                pendingCount.set(currentPending);

                long currentMatched = pendingReconciliations.stream()
                        .filter(r -> r.getState() == ReconciliationState.MATCHED)
                        .count();
                matchedCount.set(currentMatched);

                List<Reconciliation> mismatchedReconciliations =
                        reconciliationRepository.findByState(ReconciliationState.MISMATCHED).collectList().block();
                mismatchedCount.set(mismatchedReconciliations.size());

                List<Reconciliation> manualReconciliations =
                        reconciliationRepository.findByState(ReconciliationState.MANUAL_REVIEW).collectList().block();
                manualReviewCount.set(manualReconciliations.size());

                if (!pendingReconciliations.isEmpty()) {
                    Instant oldestPending = pendingReconciliations.stream()
                            .map(Reconciliation::getReceivedAt)
                            .min(Instant::compareTo)
                            .orElse(now);

                    Duration currentLag = Duration.between(oldestPending, now);
                    long lagSeconds = currentLag.getSeconds();
                    currentLagSeconds.set(lagSeconds);

                    log.info("Lag de conciliación actual: {} segundos (SLA: {} minutos)",
                            lagSeconds, slaMinutes);

                    if (currentLag.compareTo(slaDuration) > 0) {
                        lagExceededCounter.increment();
                        triggerAlert(currentLag, pendingReconciliations.size());
                    }
                } else {
                    currentLagSeconds.set(0);
                    log.debug("No hay conciliaciones pendientes para calcular lag");
                }

            } catch (Exception e) {
                log.error("Error al calcular el lag de conciliación", e);
            }
        });
    }

    private void triggerAlert(Duration currentLag, int pendingCount) {
        Instant now = Instant.now();
        Duration alertCooldown = Duration.ofMinutes(alertThresholdMinutes);

        Instant lastAlert = lastAlertTime.get();
        if (Duration.between(lastAlert, now).compareTo(alertCooldown) < 0) {
            log.debug("Alerta suprimida por cooldown. Última alerta hace: {} segundos",
                    Duration.between(lastAlert, now).getSeconds());
            return;
        }

        if (lastAlertTime.compareAndSet(lastAlert, now)) {
            log.warn("ALERTA: El lag de conciliación ({}) supera el SLA de {} minutos. " +
                            "Conciliaciones pendientes: {}",
                    currentLag.toMinutes(), slaMinutes, pendingCount);

            meterRegistry.counter("conciliation.alerts.triggered", "reason", "sla_exceeded")
                    .increment();
        }
    }

    public long getCurrentLagSeconds() {
        return currentLagSeconds.get();
    }

    public long getPendingCount() {
        return pendingCount.get();
    }

    public boolean isLagWithinSla() {
        return currentLagSeconds.get() <= (slaMinutes * 60L);
    }
}