package com.pragma.conciliation.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio que representa el proceso de conciliación de un movimiento bancario.
 * 
 * Esta entidad implementa la máquina de estados de conciliación siguiendo el patrón de
 * event sourcing: cada transición de estado genera un evento que queda registrado para
 * auditoría y reprocesamiento.
 * 
 * La conciliación opera contra tres fuentes:
 * - Core bancario (sistema principal de cuentas)
 * - Gateway de pagos (procesador de transacciones)
 * - Sistema de liquidación (compensación entre instituciones)
 */
public class Reconciliation {
    
    private final String id;
    private final String movementId;
    private final String eventId;
    private final int version;
    private ReconciliationState state;
    private final Instant receivedAt;
    private Instant matchedAt;
    private String coreBankReference;
    private String paymentGatewayReference;
    private String settlementReference;
    private BigDecimal movementAmount;
    private String movementCurrency;
    private final List<ReconciliationStateTransition> stateHistory;
    private String manualResolution;
    private String processedBy;
    
    /**
     * Constructor principal para crear una nueva conciliación.
     * El estado inicial siempre es PENDING.
     */
    public Reconciliation(String movementId, String eventId, int version, 
                          BigDecimal amount, String currency) {
        this.id = UUID.randomUUID().toString();
        this.movementId = Objects.requireNonNull(movementId, "movementId no puede ser null");
        this.eventId = Objects.requireNonNull(eventId, "eventId no puede ser null");
        this.version = version;
        this.state = ReconciliationState.PENDING;
        this.receivedAt = Instant.now();
        this.movementAmount = Objects.requireNonNull(amount, "amount no puede ser null");
        this.movementCurrency = Objects.requireNonNull(currency, "currency no puede ser null");
        this.stateHistory = new ArrayList<>();
        recordStateTransition(ReconciliationState.PENDING, "Movimiento recibido para conciliación");
    }
    
    /**
     * Constructor para reconstruir una conciliación desde la base de datos.
     * Usado por el repositorio al cargar entidades persistidas.
     */
    public Reconciliation(String id, String movementId, String eventId, int version,
                          ReconciliationState state, Instant receivedAt, Instant matchedAt,
                          String coreBankRef, String paymentGatewayRef, String settlementRef,
                          BigDecimal amount, String currency, List<ReconciliationStateTransition> history,
                          String manualResolution, String processedBy) {
        this.id = id;
        this.movementId = movementId;
        this.eventId = eventId;
        this.version = version;
        this.state = state;
        this.receivedAt = receivedAt;
        this.matchedAt = matchedAt;
        this.coreBankReference = coreBankRef;
        this.paymentGatewayReference = paymentGatewayRef;
        this.settlementReference = settlementRef;
        this.movementAmount = amount;
        this.movementCurrency = currency;
        this.stateHistory = history != null ? new ArrayList<>(history) : new ArrayList<>();
        this.manualResolution = manualResolution;
        this.processedBy = processedBy;
    }
    
    // Getters
    public String getId() { return id; }
    public String getMovementId() { return movementId; }
    public String getEventId() { return eventId; }
    public int getVersion() { return version; }
    public ReconciliationState getState() { return state; }
    public Instant getReceivedAt() { return receivedAt; }
    public Instant getMatchedAt() { return matchedAt; }
    public String getCoreBankReference() { return coreBankReference; }
    public String getPaymentGatewayReference() { return paymentGatewayReference; }
    public String getSettlementReference() { return settlementReference; }
    public BigDecimal getMovementAmount() { return movementAmount; }
    public String getMovementCurrency() { return movementCurrency; }
    public List<ReconciliationStateTransition> getStateHistory() { return List.copyOf(stateHistory); }
    public String getManualResolution() { return manualResolution; }
    public String getProcessedBy() { return processedBy; }
    
    /**
     * Transiciona al estado MATCHED cuando las tres fuentes coinciden.
     * Solo es válida desde PENDING.
     */
    public void markAsMatched(String coreRef, String gatewayRef, String settlementRef) {
        validateTransitionTo(ReconciliationState.MATCHED);
        
        this.coreBankReference = coreRef;
        this.paymentGatewayReference = gatewayRef;
        this.settlementReference = settlementRef;
        this.matchedAt = Instant.now();
        this.state = ReconciliationState.MATCHED;
        
        recordStateTransition(ReconciliationState.MATCHED, 
            String.format("Conciliado: core=%s, gateway=%s, settlement=%s", coreRef, gatewayRef, settlementRef));
    }
    
    /**
     * Transiciona al estado MISMATCHED cuando las fuentes no coinciden.
     * Solo es válida desde PENDING.
     */
    public void markAsMismatched(String reason) {
        validateTransitionTo(ReconciliationState.MISMATCHED);
        this.state = ReconciliationState.MISMATCHED;
        recordStateTransition(ReconciliationState.MISMATCHED, reason);
    }
    
    /**
     * Transiciona al estado MANUAL por decisión de un operador.
     * Válida desde PENDING o MISMATCHED.
     */
    public void markAsManual(String resolution, String processedBy) {
        validateTransitionTo(ReconciliationState.MANUAL);
        this.manualResolution = resolution;
        this.processedBy = processedBy;
        this.state = ReconciliationState.MANUAL;
        this.matchedAt = Instant.now();
        
        recordStateTransition(ReconciliationState.MANUAL, 
            String.format("Resolución manual: %s por %s", resolution, processedBy));
    }
    
    /**
     * Verifica si las referencias de las tres fuentes están completas.
     */
    public boolean hasAllReferences() {
        return coreBankReference != null && !coreBankReference.isBlank()
            && paymentGatewayReference != null && !paymentGatewayReference.isBlank()
            && settlementReference != null && !settlementReference.isBlank();
    }
    
    /**
     * Calcula el tiempo transcurrido desde que se recibió el movimiento.
     */
    public Duration getPendingDuration() {
        return Duration.between(receivedAt, Instant.now());
    }
    
    /**
     * Verifica si la conciliación excede el tiempo máximo de espera.
     * @param maxPendingDuration duración máxima permitida en estado PENDING
     */
    public boolean exceedsMaxPendingDuration(Duration maxPendingDuration) {
        return getPendingDuration().compareTo(maxPendingDuration) > 0;
    }
    
    /**
     * Genera la clave de idempotencia para esta conciliación.
     * Formato: eventId + version para garantizar unicidad.
     */
    public String getIdempotencyKey() {
        return eventId + "_v" + version;
    }
    
    private void validateTransitionTo(ReconciliationState target) {
        if (!state.canTransitionTo(target)) {
            throw new IllegalStateException(
                String.format("Transición inválida de %s a %s para movimiento %s", 
                    state, target, movementId));
        }
    }
    
    private void recordStateTransition(ReconciliationState newState, String reason) {
        stateHistory.add(new ReconciliationStateTransition(
            Instant.now(),
            this.state,
            newState,
            reason
        ));
    }
    
    /**
     * Registro de transición de estado para auditoría (event sourcing).
     */
    public record ReconciliationStateTransition(
        Instant timestamp,
        ReconciliationState fromState,
        ReconciliationState toState,
        String reason
    ) {}
}