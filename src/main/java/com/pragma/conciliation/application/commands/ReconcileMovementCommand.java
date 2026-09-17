package com.pragma.conciliation.application.commands;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.events.MovementReceivedEvent.MovementType;
import java.math.BigDecimal;
import java.time.Instant;

public class ReconcileMovementCommand {

    private final String eventId;
    private final int version;
    private final String movementId;
    private final String source;
    private final String reference;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;
    private final String correlationId;
    private final MovementType movementType;
    private final String customerId;
    private final String accountId;
    private final Instant receivedAt;
    private final boolean forceReprocessing;

    public ReconcileMovementCommand(
            String eventId,
            int version,
            String movementId,
            String source,
            String reference,
            BigDecimal amount,
            String currency,
            Instant occurredAt,
            String correlationId,
            MovementType movementType,
            String customerId,
            String accountId,
            Instant receivedAt,
            boolean forceReprocessing) {
        this.eventId = eventId;
        this.version = version;
        this.movementId = movementId;
        this.source = source;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
        this.correlationId = correlationId;
        this.movementType = movementType;
        this.customerId = customerId;
        this.accountId = accountId;
        this.receivedAt = receivedAt;
        this.forceReprocessing = forceReprocessing;
    }

    public static ReconcileMovementCommand fromEvent(MovementReceivedEvent event) {
        return new ReconcileMovementCommand(
            event.getEventId(),
            event.getVersion(),
            event.getMovementId(),
            event.getSource(),
            event.getReference(),
            event.getAmount(),
            event.getCurrency(),
            event.getOccurredAt(),
            event.getCorrelationId(),
            event.getMovementType(),
            event.getCustomerId(),
            event.getAccountId(),
            event.getReceivedAt(),
            false
        );
    }

    public String getEventId() { return eventId; }
    public int getVersion() { return version; }
    public String getMovementId() { return movementId; }
    public String getSource() { return source; }
    public String getReference() { return reference; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getCorrelationId() { return correlationId; }
    public MovementType getMovementType() { return movementType; }
    public String getCustomerId() { return customerId; }
    public String getAccountId() { return accountId; }
    public Instant getReceivedAt() { return receivedAt; }
    public boolean isForceReprocessing() { return forceReprocessing; }

    public String getIdempotencyKey() {
        return eventId + "_v" + version;
    }
}