package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.application.commands.ReconcileMovementCommand;
import com.pragma.conciliation.application.commands.ReconcileMovementCommandHandler;
import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaMovementConsumer - Pruebas de integración del consumidor")
class KafkaMovementConsumerTest {

    @Mock
    private ReconcileMovementCommandHandler commandHandler;

    @Mock
    private IdempotencyStore idempotencyStore;

    @Mock
    private Acknowledgment acknowledgment;

    private KafkaMovementConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new KafkaMovementConsumer(commandHandler, idempotencyStore);
    }

    @Nested
    @DisplayName("Manejo de mensajes fuera de orden")
    class OutOfOrderMessages {

        @Test
        @DisplayName("Debería procesar mensaje con versión 1 primero")
        void shouldProcessVersion1First() {
            MovementReceivedEvent event = createEvent("MOV-OOO", "EVT-OOO", 1);
            
            when(idempotencyStore.isProcessed("EVT-OOO", 1)).thenReturn(false);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenReturn(createReconciliation("MOV-OOO", ReconciliationState.PENDING));

            consumer.consumeMovement(event, acknowledgment);

            verify(commandHandler).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment).acknowledge();
        }

        @Test
        @DisplayName("Debería rechazar mensaje con versión menor al último procesado")
        void shouldRejectMessageWithLowerVersion() {
            MovementReceivedEvent oldEvent = createEvent("MOV-OLD", "EVT-OLD", 1);
            
            when(idempotencyStore.isProcessed("EVT-OLD", 1)).thenReturn(true);
            when(idempotencyStore.getLatestVersion("MOV-OLD")).thenReturn(3);

            consumer.consumeMovement(oldEvent, acknowledgment);

            verify(commandHandler, never()).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment).acknowledge();
        }

        @Test
        @DisplayName("Debería aceptar mensaje con versión mayor al último procesado")
        void shouldAcceptMessageWithHigherVersion() {
            MovementReceivedEvent newEvent = createEvent("MOV-NEW", "EVT-NEW", 5);
            
            when(idempotencyStore.isProcessed("EVT-NEW", 5)).thenReturn(false);
            when(idempotencyStore.getLatestVersion("MOV-NEW")).thenReturn(2);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenReturn(createReconciliation("MOV-NEW", ReconciliationState.PENDING));

            consumer.consumeMovement(newEvent, acknowledgment);

            verify(commandHandler).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment).acknowledge();
        }

        @Test
        @DisplayName("Debería manejar secuencia fuera de orden: 2, 1, 3")
        void shouldHandleOutOfOrderSequence_2_1_3() {
            MovementReceivedEvent eventV2 = createEvent("MOV-SEQ", "EVT-SEQ", 2);
            MovementReceivedEvent eventV1 = createEvent("MOV-SEQ", "EVT-SEQ", 1);
            MovementReceivedEvent eventV3 = createEvent("MOV-SEQ", "EVT-SEQ", 3);
            
            when(idempotencyStore.isProcessed("EVT-SEQ", 2)).thenReturn(false);
            when(idempotencyStore.isProcessed("EVT-SEQ", 1)).thenReturn(true);
            when(idempotencyStore.isProcessed("EVT-SEQ", 3)).thenReturn(false);
            when(idempotencyStore.getLatestVersion("MOV-SEQ")).thenReturn(0);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenReturn(createReconciliation("MOV-SEQ", ReconciliationState.PENDING));

            consumer.consumeMovement(eventV2, acknowledgment);
            consumer.consumeMovement(eventV1, acknowledgment);
            consumer.consumeMovement(eventV3, acknowledgment);

            verify(commandHandler, times(2)).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment, times(3)).acknowledge();
        }
    }

    @Nested
    @DisplayName("Escenarios de mensajes duplicados")
    class DuplicateMessages {

        @Test
        @DisplayName("Debería ignorar mensaje duplicado con misma versión")
        void shouldIgnoreDuplicateMessageWithSameVersion() {
            MovementReceivedEvent event = createEvent("MOV-DUP", "EVT-DUP", 1);
            
            when(idempotencyStore.isProcessed("EVT-DUP", 1)).thenReturn(true);

            consumer.consumeMovement(event, acknowledgment);

            verify(commandHandler, never()).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment).acknowledge();
        }

        @Test
        @DisplayName("Debería procesar reintento con versión incrementada")
        void shouldProcessRetryWithIncrementedVersion() {
            MovementReceivedEvent retryEvent = createEvent("MOV-RETRY", "EVT-RETRY", 2);
            
            when(idempotencyStore.isProcessed("EVT-RETRY", 2)).thenReturn(false);
            when(idempotencyStore.getLatestVersion("MOV-RETRY")).thenReturn(1);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenReturn(createReconciliation("MOV-RETRY", ReconciliationState.PENDING));

            consumer.consumeMovement(retryEvent, acknowledgment);

            verify(commandHandler).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment).acknowledge();
        }

        @Test
        @DisplayName("Debería registrar idempotencia después de procesamiento exitoso")
        void shouldRecordIdempotencyAfterSuccessfulProcessing() {
            MovementReceivedEvent event = createEvent("MOV-IDEM", "EVT-IDEM", 1);
            Reconciliation reconciliation = createReconciliation("MOV-IDEM", ReconciliationState.MATCHED);
            
            when(idempotencyStore.isProcessed("EVT-IDEM", 1)).thenReturn(false);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenReturn(reconciliation);

            consumer.consumeMovement(event, acknowledgment);

            verify(idempotencyStore).recordProcessing("EVT-IDEM", 1, "MOV-IDEM");
        }
    }

    @Nested
    @DisplayName("Manejo de errores")
    class ErrorHandling {

        @Test
        @DisplayName("Debería hacer acknowledge 即使 cuando el handler falla")
        void shouldAcknowledgeEvenWhenHandlerFails() {
            MovementReceivedEvent event = createEvent("MOV-ERR", "EVT-ERR", 1);
            
            when(idempotencyStore.isProcessed("EVT-ERR", 1)).thenReturn(false);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenThrow(new RuntimeException("Handler error"));

            consumer.consumeMovement(event, acknowledgment);

            verify(acknowledgment).acknowledge();
        }

        @Test
        @DisplayName("Debería registrar error en store de idempotencia para análisis")
        void shouldRecordErrorInIdempotencyStoreForAnalysis() {
            MovementReceivedEvent event = createEvent("MOV-ERR2", "EVT-ERR2", 1);
            
            when(idempotencyStore.isProcessed("EVT-ERR2", 1)).thenReturn(false);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenThrow(new RuntimeException("Processing failed"));

            consumer.consumeMovement(event, acknowledgment);

            verify(idempotencyStore).recordFailure("EVT-ERR2", 1, anyString());
        }

        @Test
        @DisplayName("Debería manejar evento con datos inválidos")
        void shouldHandleEventWithInvalidData() {
            MovementReceivedEvent invalidEvent = createEvent(null, "EVT-INVALID", 1);

            consumer.consumeMovement(invalidEvent, acknowledgment);

            verify(commandHandler, never()).handle(any(ReconcileMovementCommand.class));
            verify(acknowledgment).acknowledge();
        }
    }

    @Nested
    @DisplayName("Validaciones de correlación")
    class CorrelationValidation {

        @Test
        @DisplayName("Debería usar correlationId para trazabilidad")
        void shouldUseCorrelationIdForTraceability() {
            String correlationId = UUID.randomUUID().toString();
            MovementReceivedEvent event = createEvent("MOV-CORR", "EVT-CORR", 1);
            event = new MovementReceivedEvent(
                    "EVT-CORR", 1, "MOV-CORR", "CORE_BANK", "REF-CORR",
                    new BigDecimal("1000.00"), "USD", Instant.now(), Instant.now(),
                    correlationId, MovementReceivedEvent.MovementType.CREDIT,
                    "CUST-001", "ACC-001"
            );
            
            when(idempotencyStore.isProcessed("EVT-CORR", 1)).thenReturn(false);
            when(commandHandler.handle(any(ReconcileMovementCommand.class)))
                    .thenReturn(createReconciliation("MOV-CORR", ReconciliationState.PENDING));

            consumer.consumeMovement(event, acknowledgment);

            ArgumentCaptor<ReconcileMovementCommand> captor = 
                    ArgumentCaptor.forClass(ReconcileMovementCommand.class);
            verify(commandHandler).handle(captor.capture());
            assertThat(captor.getValue().getCorrelationId()).isEqualTo(correlationId);
        }
    }

    private MovementReceivedEvent createEvent(String movementId, String eventId, int version) {
        return new MovementReceivedEvent(
                eventId,
                version,
                movementId,
                "CORE_BANK",
                "REF-" + movementId,
                new BigDecimal("1000.00"),
                "USD",
                Instant.now(),
                Instant.now(),
                UUID.randomUUID().toString(),
                MovementReceivedEvent.MovementType.CREDIT,
                "CUST-001",
                "ACC-001"
        );
    }

    private Reconciliation createReconciliation(String movementId, ReconciliationState state) {
        return new Reconciliation(
                UUID.randomUUID().toString(),
                movementId,
                "EVT-" + movementId,
                1,
                state,
                Instant.now(),
                null,
                null,
                null,
                null,
                new BigDecimal("1000.00"),
                "USD",
                List.of()
        );
    }
}