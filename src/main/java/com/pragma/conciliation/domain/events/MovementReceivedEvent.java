package com.pragma.conciliation.domain.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Evento de dominio que representa la recepción de un movimiento bancario
 * desde una de las tres fuentes del sistema de conciliación.
 * 
 * Este evento es la base del procesamiento de conciliación y contiene toda
 * la información necesaria para correlacionar el movimiento con las otras
 * fuentes (core bancario, gateway de pagos, sistema de liquidación).
 * 
 * El evento implementa idempotencia mediante la combinación de eventId y version,
 * permitiendo el reprocesamiento sin duplicar conciliaciones.
 */
public class MovementReceivedEvent {
    
    private final String eventId;
    private final int version;
    private final String movementId;
    private final String source;
    private final String reference;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;
    private final Instant receivedAt;
    private final String correlationId;
    private final MovementType movementType;
    private final String customerId;
    private final String accountId;
    
    /**
     * Constructor principal del evento.
     * @param movementId identificador único del movimiento en el sistema origen
     * @param source fuente del movimiento (CORE_BANK, PAYMENT_GATEWAY, SETTLEMENT)
     * @param reference identificador de correlación entre fuentes
     * @param amount monto de la transacción
     * @param currency código ISO de la moneda
     * @param movementType tipo de movimiento (CREDIT, DEBIT, TRANSFER, PAYMENT)
     */
    public MovementReceivedEvent(String movementId, String source, String reference,
                                  BigDecimal amount, String currency, MovementType movementType,
                                  String customerId, String accountId) {
        this.eventId = UUID.randomUUID().toString();
        this.version = 1;
        this.movementId = Objects.requireNonNull(movementId, "movementId no puede ser null");
        this.source = validateSource(source);
        this.reference = Objects.requireNonNull(reference, "reference no puede ser null");
        this.amount = Objects.requireNonNull(amount, "amount no puede ser null");
        this.currency = Objects.requireNonNull(currency, "currency no puede ser null");
        this.occurredAt = Instant.now();
        this.receivedAt = Instant.now();
        this.correlationId = reference;
        this.movementType = Objects.requireNonNull(movementType, "movementType no puede ser null");
        this.customerId = customerId;
        this.accountId = accountId;
    }
    
    /**
     * Constructor para reconstruir un evento desde persistencia.
     */
    public MovementReceivedEvent(String eventId, int version, String movementId, String source,
                                  String reference, BigDecimal amount, String currency,
                                  Instant occurredAt, Instant receivedAt, String correlationId,
                                  MovementType movementType, String customerId, String accountId) {
        this.eventId = Objects.requireNonNull(eventId, "eventId no puede ser null");
        this.version = version;
        this.movementId = movementId;
        this.source = validateSource(source);
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
        this.correlationId = correlationId;
        this.movementType = movementType;
        this.customerId = customerId;
        this.accountId = accountId;
    }
    
    // Getters
    public String getEventId() { return eventId; }
    public int getVersion() { return version; }
    public String getMovementId() { return movementId; }
    public String getSource() { return source; }
    public String getReference() { return reference; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public String getCorrelationId() { return correlationId; }
    public MovementType getMovementType() { return movementType; }
    public String getCustomerId() { return customerId; }
    public String getAccountId() { return accountId; }
    
    /**
     * Genera la clave de idempotencia para este evento.
     * Formato: movementId + source + version
     */
    public String getIdempotencyKey() {
        return movementId + "_" + source + "_v" + version;
    }
    
    /**
     * Verifica si este evento corresponde a la misma transacción que otro.
     * Dos eventos se consideran iguales si tienen el mismo correlationId y source.
     */
    public boolean isSameTransactionAs(MovementReceivedEvent other) {
        if (other == null) return false;
        return this.correlationId.equals(other.correlationId) 
            && this.source.equals(other.source);
    }
    
    /**
     * Crea una nueva versión de este evento para reprocesamiento.
     * Incrementa la versión en 1.
     */
    public MovementReceivedEvent withIncrementedVersion() {
        return new MovementReceivedEvent(
            this.eventId,
            this.version + 1,
            this.movementId,
            this.source,
            this.reference,
            this.amount,
            this.currency,
            this.occurredAt,
            Instant.now(),
            this.correlationId,
            this.movementType,
            this.customerId,
            this.accountId
        );
    }
    
    private static String validateSource(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("source no puede ser null o vacío");
        }
        if (!isValidSource(source)) {
            throw new IllegalArgumentException("Fuente inválida: " + source + ". Valores válidos: CORE_BANK, PAYMENT_GATEWAY, SETTLEMENT");
        }
        return source;
    }
    
    private static boolean isValidSource(String source) {
        return source.equals("CORE_BANK") || source.equals("PAYMENT_GATEWAY") || source.equals("SETTLEMENT");
    }
    
    /**
     * Tipos de movimiento bancario soportados por el sistema de conciliación.
     */
    public enum MovementType {
        /**
         * Movimiento de crédito (depósito, abono).
         */
        CREDIT,
        
        /**
         * Movimiento de débito (retiro, cargo).
         */
        DEBIT,
        
        /**
         * Transferencia entre cuentas.
         */
        TRANSFER,
        
        /**
         * Pago a terceros o comercios.
         */
        PAYMENT,
        
        /**
         * Reversión de un movimiento anterior.
         */
        REVERSAL
    }
}