# AGENTS.md

Instrucciones para el agente de IA que abra este repositorio (Claude Code, Cursor, Codex, Copilot, Gemini). Se cargan solas: no hay que pegar nada en ningun chat.

## Que es este repositorio

Es el codigo base de un reto de aprendizaje de Pragma: **Diseño de sistema de conciliación bancaria en tiempo real**.

| | |
|---|---|
| Tema | TEST-CT |
| Nivel | senior-l2 |
| Chapter | Generico |
| Especialidad | Inferido del contexto |
| Stack | Java 21 / Spring Boot 3.4.0 |
| Patron arquitectonico | hexagonal/clean con CQRS y event sourcing |
| Tiempo estimado | 1 semana |

## Tu tarea

Dejar este proyecto en estado **verificable**: que el comando de verificacion corra sin errores. Escribi los archivos en disco, en este repositorio. No generes ZIPs ni archivos adjuntos.

En orden:

1. Corre `el comando de build o arranque canonico del stack elegido` y mira que falla.
2. Completa lo que falte de la lista de abajo: manifiesto de dependencias, punto de entrada, capa de interfaz y las capas del patron declarado.
3. Arregla SOLO los errores que impiden compilar o arrancar.
4. Volve a correr `el comando de build o arranque canonico del stack elegido` hasta que pase.
5. Pará ahí.

## Regla dura: las fases son trabajo del humano

**PROHIBIDO implementar los entregables de las fases.** El valor del reto esta en que la persona los resuelva. Tu trabajo es que tenga un proyecto que arranca; el hueco pedagogico se queda como esta.

No resuelvas nada de esto:

- **Fase 1 — Diseño de la máquina de estados**: Diagrama de estados de la conciliación
- **Fase 2 — Definición de la ventana de matching**: Documento que describe la ventana de matching y su justificación
- **Fase 3 — Manejo de idempotencia**: Documento que describe el manejo de idempotencia en el proceso de conciliación
- **Fase 4 — Estrategia de reprocesamiento**: Documento que describe la estrategia de reprocesamiento elegida
- **Fase 5 — Alerta de lag de conciliación**: Documento que describe el proceso de alerta de lag de conciliación

Distincion operativa:

- **Arreglar** (si): import faltante, tipo que no existe, dependencia sin declarar, error de sintaxis, archivo referenciado que no existe.
- **No tocar** (no): logica de negocio incompleta, validaciones ausentes, secretos hardcodeados, APIs deprecadas que funcionan, concurrencia insegura, patrones mejorables. Eso es lo que la persona tiene que encontrar.

## Lo que falta y tenes que completar

### 1. Boilerplate del stack (1)

Sin esto el proyecto no compila ni arranca. **Es tu trabajo crearlo**, y no toca nada de lo pedagogico: es andamiaje del stack.

- [ ] **Punto de entrada del stack elegido** — Sin un punto de entrada reconocible, el runtime no tiene por donde arrancar la aplicacion.

### 2. Referencias colgando (76)

Salieron de un analisis estatico del codigo que SI esta en el repo. Cada una rompe la compilacion:

- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `ReconciliationConfig`
      ReconciliationConfig se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.conciliation.infrastructure.config.ReconciliationConfig.
- [ ] `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java` — `MovementType`
      MovementType se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.conciliation.domain.events.MovementType.
- [ ] `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `PostgresReconciliationRepository`
      El import com.pragma.conciliation.infrastructure.persistence.PostgresReconciliationRepository no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/java/com/pragma/conciliation/domain/repository/ReconciliationRepository.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReprocessingStrategy.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommandHandler.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Mono pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/monitoring/ConciliationLagMonitor.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaReplayReprocessor.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Mono pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/test/java/com/pragma/conciliation/domain/ReconciliationServiceTest.java` — `reactor.core.publisher`
      El import reactor.core.publisher.Mono pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- [ ] `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationConfig.getMatchingWindowMinutes`
      Se invoca `getMatchingWindowMinutes` sobre `ReconciliationConfig`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationConfig.getSlaAlertMinutes`
      Se invoca `getSlaAlertMinutes` sobre `ReconciliationConfig`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationConfig.getKafkaBootstrapServers`
      Se invoca `getKafkaBootstrapServers` sobre `ReconciliationConfig`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationRepository.getClass`
      Se invoca `getClass` sobre `ReconciliationRepository`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `IdempotencyStore.getClass`
      Se invoca `getClass` sobre `IdempotencyStore`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.hasAllReferences`
      Se invoca `hasAllReferences` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.coreBankReference`
      Se invoca `coreBankReference` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.paymentGatewayReference`
      Se invoca `paymentGatewayReference` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.settlementReference`
      Se invoca `settlementReference` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getId`
      Se invoca `getId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setId`
      Se invoca `setId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMovementId`
      Se invoca `setMovementId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setEventId`
      Se invoca `setEventId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setVersion`
      Se invoca `setVersion` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setState`
      Se invoca `setState` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setReceivedAt`
      Se invoca `setReceivedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMatchedAt`
      Se invoca `setMatchedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setCoreBankReference`
      Se invoca `setCoreBankReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setPaymentGatewayReference`
      Se invoca `setPaymentGatewayReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setSettlementReference`
      Se invoca `setSettlementReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMovementAmount`
      Se invoca `setMovementAmount` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMovementCurrency`
      Se invoca `setMovementCurrency` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setManualResolution`
      Se invoca `setManualResolution` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setProcessedBy`
      Se invoca `setProcessedBy` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMovementId`
      Se invoca `getMovementId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getEventId`
      Se invoca `getEventId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getVersion`
      Se invoca `getVersion` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getState`
      Se invoca `getState` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getReceivedAt`
      Se invoca `getReceivedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMatchedAt`
      Se invoca `getMatchedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getCoreBankReference`
      Se invoca `getCoreBankReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getPaymentGatewayReference`
      Se invoca `getPaymentGatewayReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getSettlementReference`
      Se invoca `getSettlementReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMovementAmount`
      Se invoca `getMovementAmount` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMovementCurrency`
      Se invoca `getMovementCurrency` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getManualResolution`
      Se invoca `getManualResolution` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getProcessedBy`
      Se invoca `getProcessedBy` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.resolution`
      Se invoca `resolution` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.processedBy`
      Se invoca `processedBy` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.force`
      Se invoca `force` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.from`
      Se invoca `from` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.to`
      Se invoca `to` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.strategy`
      Se invoca `strategy` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresSnapshotReprocessor.java` — `ReconciliationService.processReconciliation`
      Se invoca `processReconciliation` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setCoreBankReference`
      Se invoca `setCoreBankReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setPaymentGatewayReference`
      Se invoca `setPaymentGatewayReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setSettlementReference`
      Se invoca `setSettlementReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconciliationService.matchMovement`
      Se invoca `matchMovement` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setMovementAmount`
      Se invoca `setMovementAmount` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `Reconciliation.setMovementAmount`
      Se invoca `setMovementAmount` sobre `Reconciliation`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconciliationService.mismatchMovement`
      Se invoca `mismatchMovement` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setReprocess`
      Se invoca `setReprocess` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconciliationService.reprocessMovement`
      Se invoca `reprocessMovement` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setEventId`
      Se invoca `setEventId` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setVersion`
      Se invoca `setVersion` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setSource`
      Se invoca `setSource` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setMovementId`
      Se invoca `setMovementId` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setReference`
      Se invoca `setReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setAmount`
      Se invoca `setAmount` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setCurrency`
      Se invoca `setCurrency` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setOccurredAt`
      Se invoca `setOccurredAt` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setCorrelationId`
      Se invoca `setCorrelationId` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java` — `IdempotencyStore.isProcessed`
      Se invoca `isProcessed` sobre `IdempotencyStore`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java` — `IdempotencyStore.getLatestVersion`
      Se invoca `getLatestVersion` sobre `IdempotencyStore`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

### Presentes (22)

- `pom.xml`
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java`
- `src/main/resources/application.yml`
- `src/main/java/com/pragma/conciliation/domain/model/ReconciliationState.java`
- `src/main/java/com/pragma/conciliation/domain/model/Reconciliation.java`
- `src/main/java/com/pragma/conciliation/domain/events/MovementReceivedEvent.java`
- `src/main/java/com/pragma/conciliation/domain/repository/ReconciliationRepository.java`
- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java`
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java`
- `src/main/java/com/pragma/conciliation/domain/service/ReprocessingStrategy.java`
- `src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommand.java`
- `src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommandHandler.java`
- `src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumer.java`
- `src/main/java/com/pragma/conciliation/infrastructure/config/ReconciliationConfig.java`
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java`
- `src/main/java/com/pragma/conciliation/infrastructure/monitoring/ConciliationLagMonitor.java`
- `src/main/java/com/pragma/conciliation/infrastructure/messaging/IdempotencyStore.java`
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresSnapshotReprocessor.java`
- `src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaReplayReprocessor.java`
- `src/test/java/com/pragma/conciliation/domain/ReconciliationServiceTest.java`
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java`
- `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java`

### Capas del patron declarado

Cada una tiene que existir como directorio real con al menos un archivo. Codigo plano en la raiz no satisface el patron.

- `src/main/java/com/pragma/conciliation`
- `src/main/java/com/pragma/conciliation/application`
- `src/main/java/com/pragma/conciliation/application/commands`
- `src/main/java/com/pragma/conciliation/application/queries`
- `src/main/java/com/pragma/conciliation/domain`
- `src/main/java/com/pragma/conciliation/domain/model`
- `src/main/java/com/pragma/conciliation/domain/events`
- `src/main/java/com/pragma/conciliation/domain/repository`
- `src/main/java/com/pragma/conciliation/domain/service`
- `src/main/java/com/pragma/conciliation/infrastructure`
- `src/main/java/com/pragma/conciliation/infrastructure/config`
- `src/main/java/com/pragma/conciliation/infrastructure/messaging`
- `src/main/java/com/pragma/conciliation/infrastructure/persistence`
- `src/main/java/com/pragma/conciliation/infrastructure/rest`
- `src/main/resources`
- `src/test/java/com/pragma/conciliation`
- `src/test/java/com/pragma/conciliation/application`
- `src/test/java/com/pragma/conciliation/domain`
- `src/test/java/com/pragma/conciliation/infrastructure`

## Verificacion

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando pasando es la definicion de "terminado" para vos.

## Convenciones que tenes que respetar

- Un solo ecosistema: no declares librerias de otro lenguaje ni mezcles gestores de paquetes.
- Toda libreria que uses tiene que estar declarada en el manifiesto de dependencias.
- Todo import declarado tiene que usarse; todo tipo usado tiene que existir o venir de una dependencia declarada.
- El patron es **hexagonal/clean con CQRS y event sourcing**: los contratos (interfaces, puertos) los define la capa interna y los implementa la externa, nunca al revés.
- Los archivos que crees llevan implementacion real, no stubs: sin `TODO`, sin cuerpos vacios, sin `// getters y setters`.

## Contexto del candidato

Sirve para calibrar el nivel del codigo, no para resolver las fases.

- Brecha que el reto ataca: Diseño de motor de conciliación que consume streams de movimientos desde 3 fuentes (core bancario, gateway de pagos, sistema de liquidación) y detecta discrepancias en ventanas móviles. Cada movimiento debe reconciliarse contra las 3 fuentes con tolerancia a mensajes fuera de orden y llegadas duplicadas. El desarrollador senior debe diseñar la máquina de estados de cada Reconciliation (Pending, Matched, Mismatched, Manual), justificar la ventana de matching (5 min vs 1 hora), definir cómo maneja idempotencia con eventId + version, y elegir entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL. Adicionalmente debe explicar cómo alertar al equipo de operaciones cuando el lag de conciliación supera un SLA.

---

*Generado por Challenge Generator — Pragma. `README.md` tiene el enunciado completo del reto para la persona. `PROMPT_MEJORA.md` es la variante para pegar en un chat, si se prefiere ese flujo.*
