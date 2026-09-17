package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.application.commands.ReconcileMovementCommand;
import com.pragma.conciliation.application.commands.ReconcileMovementCommandHandler;
import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class KafkaMovementConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaMovementConsumer.class);
    
    private static final String CORE_BANK_SOURCE = "CORE_BANK";
    private static final String PAYMENT_GATEWAY_SOURCE = "PAYMENT_GATEWAY";
    private static final String SETTLEMENT_SOURCE = "SETTLEMENT";
    
    private final ReconcileMovementCommandHandler commandHandler;
    private final IdempotencyStore idempotencyStore;
    private final AtomicBoolean consumerEnabled;
    private int processedCount;
    private int errorCount;
    private Instant lastProcessedAt;
    
    public KafkaMovementConsumer(ReconcileMovementCommandHandler commandHandler,
                                  IdempotencyStore idempotencyStore) {
        this.commandHandler = commandHandler;
        this.idempotencyStore = idempotencyStore;
        this.consumerEnabled = new AtomicBoolean(true);
        this.processedCount = 0;
        this.errorCount = 0;
    }
    
    @KafkaListener(
        topics = "${conciliation.kafka.topics.movements:movements}",
        groupId = "${conciliation.kafka.consumer.group-id:conciliation-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMovement(
            @Payload MovementReceivedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.KEY) String key,
            Acknowledgment acknowledgment) {
        
        if (!consumerEnabled.get()) {
            log.warn("Consumer deshabilitado, ignorando mensaje: key={}, offset={}", key, offset);
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            return;
        }
        
        String consumerTraceId = String.format("%s-%d-%d", event.getMovementId(), partition, offset);
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Recibiendo movimiento de {}: movementId={}, eventId={}, version={}, " +
                    "partition={}, offset={}",
                    event.getSource(), event.getMovementId(), event.getEventId(),
                    event.getVersion(), partition, offset);
            
            validateEventSource(event);
            
            boolean alreadyProcessed = idempotencyStore.findByEventIdAndVersion(
                    event.getEventId(), event.getVersion()).isPresent();
            
            if (!alreadyProcessed) {
                log.debug("Procesando evento nuevo: eventId={}, version={}", 
                         event.getEventId(), event.getVersion());
                
                ReconcileMovementCommand command = ReconcileMovementCommand.builder()
                        .movementId(event.getMovementId())
                        .eventId(event.getEventId())
                        .version(event.getVersion())
                        .source(event.getSource())
                        .reference(event.getReference())
                        .amount(event.getAmount())
                        .currency(event.getCurrency())
                        .occurredAt(event.getOccurredAt())
                        .correlationId(event.getCorrelationId())
                        .movementType(event.getMovementType())
                        .customerId(event.getCustomerId())
                        .accountId(event.getAccountId())
                        .build();
                
                commandHandler.handle(command);
                
                idempotencyStore.markAsProcessed(event.getEventId(), event.getVersion());
                
                processedCount++;
                lastProcessedAt = Instant.now();
                
                log.info("Movimiento procesado exitosamente en {}ms: movementId={}, eventId={}",
                        System.currentTimeMillis() - startTime, event.getMovementId(), event.getEventId());
            } else {
                log.info("Evento duplicado ignorado por idempotencia: eventId={}, version={}",
                        event.getEventId(), event.getVersion());
            }
            
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            
        } catch (IllegalArgumentException e) {
            errorCount++;
            log.error("Error de validación al procesar movimiento: {} - {}", 
                     consumerTraceId, e.getMessage());
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            
        } catch (Exception e) {
            errorCount++;
            log.error("Error al procesar movimiento: {} - {}", consumerTraceId, e.getMessage(), e);
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
    
    @KafkaListener(
        topics = "${conciliation.kafka.topics.settlements:settlements}",
        groupId = "${conciliation.kafka.consumer.group-id:conciliation-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSettlement(
            @Payload MovementReceivedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.debug("Recibiendo liquidación: movementId={}, eventId={}, partition={}, offset={}",
                event.getMovementId(), event.getEventId(), partition, offset);
        
        consumeMovement(event, partition, offset, null, acknowledgment);
    }
    
    private void validateEventSource(MovementReceivedEvent event) {
        String source = event.getSource();
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("El campo source no puede ser nulo o vacío");
        }
        
        List<String> validSources = List.of(CORE_BANK_SOURCE, PAYMENT_GATEWAY_SOURCE, SETTLEMENT_SOURCE);
        if (!validSources.contains(source)) {
            throw new IllegalArgumentException(
                    String.format("Fuente de movimiento inválida: %s. Fuentes válidas: %s", 
                                  source, validSources));
        }
    }
    
    public void enable() {
        consumerEnabled.set(true);
        log.info("KafkaMovementConsumer habilitado");
    }
    
    public void disable() {
        consumerEnabled.set(false);
        log.info("KafkaMovementConsumer deshabilitado");
    }
    
    public boolean isEnabled() {
        return consumerEnabled.get();
    }
    
    public int getProcessedCount() {
        return processedCount;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public Instant getLastProcessedAt() {
        return lastProcessedAt;
    }
    
    public void resetMetrics() {
        processedCount = 0;
        errorCount = 0;
        lastProcessedAt = null;
        log.info("Métricas del consumidor reiniciadas");
    }
}