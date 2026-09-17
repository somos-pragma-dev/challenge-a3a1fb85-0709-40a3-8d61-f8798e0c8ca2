package com.pragma.conciliation.domain.service;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import reactor.core.publisher.Flux;
import java.time.Instant;

/**
 * Contrato para estrategias de reprocesamiento de movimientos conciliados.
 * Permite elegir entre diferentes mecanismos de recuperación ante fallos.
 */
public interface ReprocessingStrategy {

    /**
     * Tipo de estrategia de reprocesamiento.
     */
    enum StrategyType {
        KAFKA_REPLAY,
        POSTGRES_SNAPSHOT
    }

    /**
     * Obtiene el tipo de estrategia implementada.
     */
    StrategyType getType();

    /**
     * Obtiene los movimientos que necesitan reprocesamiento desde la última marca de tiempo.
     * @param since Instante desde el cual buscar movimientos pendientes
     * @return Flux de eventos de movimiento que requieren reprocesamiento
     */
    Flux<MovementReceivedEvent> findMovementsForReprocessing(Instant since);

    /**
     * Obtiene una reconciliación específica por su ID para reprocesamiento.
     * @param reconciliationId ID de la reconciliación a recuperar
     * @return Mono con la reconciliación o vacío si no existe
     */
    reactor.core.publisher.Mono<Reconciliation> findReconciliationById(String reconciliationId);

    /**
     * Obtiene todas las reconciliations pendientes dentro de una ventana de tiempo.
     * @param from Instante inicial de la ventana
     * @param to Instante final de la ventana
     * @return Flux de reconciliaciones pendientes
     */
    Flux<Reconciliation> findPendingReconciliations(Instant from, Instant to);

    /**
     * Obtiene la descripción de la estrategia para logging y métricas.
     */
    String getDescription();

    /**
     * Verifica si la estrategia está disponible y operativa.
     * @return Mono que emite true si la estrategia puede ejecutarse
     */
    reactor.core.publisher.Mono<Boolean> isAvailable();
}