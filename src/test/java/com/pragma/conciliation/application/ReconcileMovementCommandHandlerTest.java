package com.pragma.conciliation.application;

import com.pragma.conciliation.application.commands.ReconcileMovementCommand;
import com.pragma.conciliation.application.commands.ReconcileMovementCommandHandler;
import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.service.ReconciliationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReconcileMovementCommandHandler - Pruebas de integración del handler")
class ReconcileMovementCommandHandlerTest {

    @Mock
    private ReconciliationService reconciliationService;

    private ReconcileMovementCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ReconcileMovementCommandHandler(reconciliationService);
    }

    @Nested
    @DisplayName("Casos de matching exitoso")
    class SuccessfulMatchingCases {

        @Test
        @DisplayName("Debería crear conciliación cuando no existe movimiento previo")
        void shouldCreateReconciliationWhenNoPreviousMovement() {
            ReconcileMovementCommand command = createCommand("MOV-NEW", "EVT-NEW", 1);
            Reconciliation created = createReconciliation("MOV-NEW", ReconciliationState.PENDING);
            
            when(reconciliationService.processMovement(any(MovementReceivedEvent.class)))
                    .thenReturn(Optional.of(created));

            Reconciliation result = handler.handle(command);

            assertThat(result).isNotNull();
            assertThat(result.getMovementId()).isEqualTo("MOV-NEW");
            assertThat(result.getState()).isEqualTo(ReconciliationState.PENDING);
        }

        @Test
        @DisplayName("Debería completar matching cuando tutte las referencias están disponibles")
        void shouldCompleteMatchingWhenAllReferencesAvailable() {
            ReconcileMovementCommand command = createCommand("MOV-FULL", "EVT-FULL", 1);
            command.setCoreBankReference("CORE-REF");
            command.setPaymentGatewayReference("GW-REF");
            command.setSettlementReference("SETT-REF");
            
            Reconciliation pending = createReconciliation("MOV-FULL", ReconciliationState.PENDING);
            Reconciliation matched = createReconciliation("MOV-FULL", ReconciliationState.MATCHED);
            matched.markAsMatched("CORE-REF", "GW-REF", "SETT-REF");
            
            when(reconciliationService.processMovement(any(MovementReceivedEvent.class)))
                    .thenReturn(Optional.of(pending));
            when(reconciliationService.matchMovement(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(matched);

            Reconciliation result = handler.handle(command);

            assertThat(result.getState()).isEqualTo(ReconciliationState.MATCHED);
            assertThat(result.getCoreBankReference()).isEqualTo("CORE-REF");
            assertThat(result.getPaymentGatewayReference()).isEqualTo("GW-REF");
            assertThat(result.getSettlementReference()).isEqualTo("SETT-REF");
        }

        @Test
        @DisplayName("Debería mantener estado PENDING cuando falta alguna referencia")
        void shouldKeepPendingWhenSomeReferenceMissing() {
            ReconcileMovementCommand command = createCommand("MOV-PARTIAL", "EVT-PARTIAL", 1);
            command.setCoreBankReference("CORE-REF");
            
            Reconciliation pending = createReconciliation("MOV-PARTIAL", ReconciliationState.PENDING);
            
            when(reconciliationService.processMovement(any(MovementReceivedEvent.class)))
                    .thenReturn(Optional.of(pending));

            Reconciliation result = handler.handle(command);

            assertThat(result.getState()).isEqualTo(ReconciliationState.PENDING);
            assertThat(result.getCoreBankReference()).isEqualTo("CORE-REF");
            assertThat(result.getPaymentGatewayReference()).isNull();
        }
    }

    @Nested
    @DisplayName("Casos de mismatch")
    class MismatchCases {

        @Test
        @DisplayName("Debería marcar como MISMATCHED cuando montos no coinciden")
        void shouldMarkAsMismatchedWhenAmountsDiffer() {
            ReconcileMovementCommand command = createCommand("MOV-AMOUNT-DIFF", "EVT-AMOUNT", 1);
            command.setMovementAmount(new BigDecimal("500.00"));
            command.setCoreBankReference("CORE-REF");
            
            Reconciliation pending = createReconciliation("MOV-AMOUNT-DIFF", ReconciliationState.PENDING);
            pending.setMovementAmount(new BigDecimal("1000.00"));
            
            Reconciliation mismatched = createReconciliation("MOV-AMOUNT-DIFF", ReconciliationState.MISMATCHED);
            
            when(reconciliationService.processMovement(any(MovementReceivedEvent.class)))
                    .thenReturn(Optional.of(pending));
            when(reconciliationService.mismatchMovement(anyString(), anyString()))
                    .thenReturn(mismatched);

            Reconciliation result = handler.handle(command);

            assertThat(result.getState()).isEqualTo(ReconciliationState.MISMATCHED);
        }

        @Test
        @DisplayName("Debería rechazar duplicado con misma versión sin crear nuevo registro")
        void shouldRejectDuplicateWithSameVersion() {
            ReconcileMovementCommand command = createCommand("MOV-DUP", "EVT-DUP", 1);
            
            when(reconciliationService.processMovement(any(MovementReceivedEvent.class)))
                    .thenReturn(Optional.empty());

            Reconciliation result = handler.handle(command);

            assertThat(result).isNull();
            verify(reconciliationService, never()).matchMovement(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Debería aceptar versión mayor como reprocesamiento válido")
        void shouldAcceptHigherVersionAsValidReprocessing() {
            ReconcileMovementCommand command = createCommand("MOV-REPRO", "EVT-REPRO", 2);
            Reconciliation reprocessed = createReconciliation("MOV-REPRO", ReconciliationState.PENDING);
            
            when(reconciliationService.processMovement(any(MovementReceivedEvent.class)))
                    .thenReturn(Optional.of(reprocessed));

            Reconciliation result = handler.handle(command);

            assertThat(result).isNotNull();
            assertThat(result.getVersion()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Escenarios de reprocesamiento")
    class ReprocessingScenarios {

        @Test
        @DisplayName("Debería ejecutar reprocesamiento cuando se indica flag")
        void shouldExecuteReprocessingWhenFlagSet() {
            ReconcileMovementCommand command = createCommand("MOV-REPROC", "EVT-REPROC", 1);
            command.setReprocess(true);
            
            Reconciliation reprocessed = createReconciliation("MOV-REPROC", ReconciliationState.PENDING);
            
            when(reconciliationService.reprocessMovement("MOV-REPROC"))
                    .thenReturn(Optional.of(reprocessed));

            Reconciliation result = handler.handle(command);

            assertThat(result.getState()).isEqualTo(ReconciliationState.PENDING);
            verify(reconciliationService).reprocessMovement("MOV-REPROC");
        }

        @Test
        @DisplayName("Debería manejar error cuando movimiento a reprocesar no existe")
        void shouldHandleErrorWhenMovementToReprocessNotFound() {
            ReconcileMovementCommand command = createCommand("MOV-MISSING", "EVT-MISSING", 1);
            command.setReprocess(true);
            
            when(reconciliationService.reprocessMovement("MOV-MISSING"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Movement not found");
        }
    }

    @Nested
    @DisplayName("Validaciones de comando")
    class CommandValidations {

        @Test
        @DisplayName("Debería rechazar comando con movementId nulo")
        void shouldRejectCommandWithNullMovementId() {
            ReconcileMovementCommand command = new ReconcileMovementCommand();
            command.setEventId("EVT-NULL");
            command.setVersion(1);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("movementId cannot be null");
        }

        @Test
        @DisplayName("Debería rechazar comando con version menor a 1")
        void shouldRejectCommandWithVersionLessThanOne() {
            ReconcileMovementCommand command = createCommand("MOV-VERSION", "EVT-VERSION", 0);

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("version must be >= 1");
        }

        @Test
        @DisplayName("Debería rechazar comando con source inválido")
        void shouldRejectCommandWithInvalidSource() {
            ReconcileMovementCommand command = createCommand("MOV-SOURCE", "EVT-SOURCE", 1);
            command.setSource("INVALID_SOURCE");

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid source");
        }
    }

    private ReconcileMovementCommand createCommand(String movementId, String eventId, int version) {
        ReconcileMovementCommand command = new ReconcileMovementCommand();
        command.setMovementId(movementId);
        command.setEventId(eventId);
        command.setVersion(version);
        command.setSource("CORE_BANK");
        command.setReference("REF-" + movementId);
        command.setAmount(new BigDecimal("1000.00"));
        command.setCurrency("USD");
        command.setOccurredAt(Instant.now());
        command.setCorrelationId(UUID.randomUUID().toString());
        return command;
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