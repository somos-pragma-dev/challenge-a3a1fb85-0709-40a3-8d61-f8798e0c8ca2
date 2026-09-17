package com.pragma.conciliation.domain.model;

/**
 * Estados posibles de un proceso de conciliación bancaria.
 * Cada estado representa una etapa en el ciclo de vida de la conciliación.
 */
public enum ReconciliationState {
    /**
     * Estado inicial: el movimiento fue recibido y está en espera de ser conciliado
     * con las tres fuentes de datos (core bancario, gateway de pagos, liquidación).
     */
    PENDING,
    
    /**
     * Estado final exitoso: el movimiento fue conciliado correctamente contra las tres fuentes.
     * Todos los identificadores de correlación coinciden y el monto está verificado.
     */
    MATCHED,
    
    /**
     * Estado de inconsistencia: el movimiento no pudo ser conciliado porque alguna
     * de las fuentes no tiene información correspondiente o los montos no coinciden.
     * Requiere revisión manual.
     */
    MISMATCHED,
    
    /**
     * Estado de intervención manual: el movimiento fue revisado por un operador
     * y se tomó una decisión de aceptación o rechazo. Este estado es terminal.
     */
    MANUAL;
    
    /**
     * Verifica si el estado actual permite transición al estado objetivo.
     * Las transiciones válidas son:
     * - PENDING -> MATCHED (conciliación exitosa)
     * - PENDING -> MISMATCHED (conciliación fallida)
     * - PENDING -> MANUAL (decisión manual antes de completar)
     * - MISMATCHED -> MANUAL (operador acepta el desbalance)
     * - MANUAL es terminal, no transita a otro estado.
     */
    public boolean canTransitionTo(ReconciliationState target) {
        return switch (this) {
            case PENDING -> target == MATCHED || target == MISMATCHED || target == MANUAL;
            case MISMATCHED -> target == MANUAL;
            case MATCHED, MANUAL -> false;
        };
    }
    
    /**
     * Indica si el estado es terminal (no requiere más procesamiento).
     */
    public boolean isTerminal() {
        return this == MATCHED || this == MANUAL;
    }
    
    /**
     * Indica si el estado requiere revisión manual.
     */
    public boolean requiresManualReview() {
        return this == MISMATCHED || this == MANUAL;
    }
}