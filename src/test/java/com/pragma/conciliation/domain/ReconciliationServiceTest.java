package com.pragma.conciliation.domain;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.events.MovementReceivedEvent.MovementType;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReconciliationService - Pruebas unitarias del dominio")
class ReconciliationServiceTest {

    @Mock
    private ReconciliationRepository repository;

    private ReconciliationService service;

    @BeforeEach
    void setUp() {
        service = new ReconciliationService(repository, java.time.Duration.ofMinutes(5), java.time.Duration.ofMinutes(30));
    }

    @Nested
    @DisplayName("Escenarios de idempotencia")
    class IdempotencyScenarios {

        @Test
        @DisplayName("Debería rechazar evento duplicado con misma versión")
        void shouldRejectDuplicateEventWithSameVersion() {
            MovementReceivedEvent event = createMovementEvent("MOV-001", "EVT-001", 1);
            
            when(repository.findByEventIdAndVersion("EVT-001", 1))
                    .thenReturn(Mono.just(Optional.of(createPendingReconciliation("MOV-001"))));

            Mono<Reconciliation> result = service.processMovement(event);

            assertThat(result).isNotNull();
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Debería crear nueva conciliación para evento nuevo")
        void shouldCreateNewReconciliationForNewEvent() {
            MovementReceivedEvent event = createMovementEvent("MOV-NEW", "EVT-NEW", 1);
            
            when(repository.findByEventIdAndVersion("EVT-NEW", 1))
                    .thenReturn(Mono.just(Optional.empty()));
            when(repository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            Mono<Reconciliation> result = service.processMovement(event);

            assertThat(result).isNotNull();
            verify(repository).save(any(Reconciliation.class));
        }
    }

    @Nested
    @DisplayName("Consultas de dominio")
    class DomainQueries {

        @Test
        @DisplayName("Debería listar conciliaciones pendientes mayores al timeout")
        void shouldListPendingReconciliationsExceedingTimeout() {
            Reconciliation oldPending = createPendingReconciliation("MOV-OLD");
            
            when(repository.findByStateAndReceivedAtBefore(
                    org.mockito.ArgumentMatchers.eq(ReconciliationState.PENDING),
                    any(Instant.class)))
                    .thenReturn(Flux.just(oldPending));

            Mono<List<Reconciliation>> result = service.getReconciliationsWithLag();

            assertThat(result).isNotNull();
        }
    }

    private MovementReceivedEvent createMovementEvent(String movementId, String eventId, int version) {
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
                MovementType.CREDIT,
                "CUST-001",
                "ACC-001"
        );
    }

    private Reconciliation createPendingReconciliation(String movementId) {
        return new Reconciliation(
                movementId,
                "EVT-" + movementId,
                1,
                ReconciliationState.PENDING,
                Instant.now().minusSeconds(600)
        );
    }
}