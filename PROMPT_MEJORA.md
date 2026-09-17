# Prompt para Mejorar el Codigo Base

Copia y pega el contenido del bloque de abajo en un asistente de IA (Claude, ChatGPT)
para obtener un ZIP con el proyecto completo y arrancable.

Si preferis trabajar en tu editor con un agente local (Claude Code, Cursor, Copilot), usa `AGENTS.md` en vez de este archivo: dice lo mismo pero para que escriba los archivos en disco.

## Las dos reglas que no se negocian

1. **Completa el boilerplate.** Todo lo que el proyecto necesita para compilar y arrancar: manifiesto de dependencias, punto de entrada, configuracion, capa de interfaz, y las capas del patron arquitectonico declarado. Eso es andamiaje y es tu trabajo.
2. **NO resuelvas el reto.** Los entregables de las fases son el trabajo de la persona. El hueco pedagogico se deja como esta: el proyecto arranca, pero lo que el reto pide implementar NO esta implementado.

Dicho de otra forma: si algo impide compilar, arreglalo. Si algo es logica de negocio incompleta, validaciones ausentes, un secreto hardcodeado o un patron mejorable, dejalo exactamente como esta — es lo que la persona tiene que encontrar.

## Lo que le falta a este proyecto

Esto NO lo tenes que adivinar: salio de comparar el proyecto contra la arquitectura declarada del reto y de un analisis estatico del codigo. Completalo TODO.

### Boilerplate del stack que falta

Sin esto no compila ni arranca. Es andamiaje, no toca nada de lo pedagogico:

- **Punto de entrada del stack elegido** — Sin un punto de entrada reconocible, el runtime no tiene por donde arrancar la aplicacion.

### Referencias colgando en el codigo que si esta

Cada una rompe la compilacion:

- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `ReconciliationConfig`: ReconciliationConfig se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.conciliation.infrastructure.config.ReconciliationConfig.
- `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java` — `MovementType`: MovementType se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.conciliation.domain.events.MovementType.
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `PostgresReconciliationRepository`: El import com.pragma.conciliation.infrastructure.persistence.PostgresReconciliationRepository no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/java/com/pragma/conciliation/domain/repository/ReconciliationRepository.java` — `reactor.core.publisher`: El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `reactor.core.publisher`: El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `reactor.core.publisher`: El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/domain/service/ReprocessingStrategy.java` — `reactor.core.publisher`: El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommandHandler.java` — `reactor.core.publisher`: El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `reactor.core.publisher`: El import reactor.core.publisher.Mono pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/infrastructure/monitoring/ConciliationLagMonitor.java` — `reactor.core.publisher`: El import reactor.core.publisher.Flux pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaReplayReprocessor.java` — `reactor.core.publisher`: El import reactor.core.publisher.Mono pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/test/java/com/pragma/conciliation/domain/ReconciliationServiceTest.java` — `reactor.core.publisher`: El import reactor.core.publisher.Mono pertenece a reactor.core.publisher, pero ninguna dependencia declarada en el pom.xml cubre ese paquete. Falta agregar la dependencia o el import esta mal (libreria equivocada).
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationConfig.getMatchingWindowMinutes`: Se invoca `getMatchingWindowMinutes` sobre `ReconciliationConfig`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationConfig.getSlaAlertMinutes`: Se invoca `getSlaAlertMinutes` sobre `ReconciliationConfig`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationConfig.getKafkaBootstrapServers`: Se invoca `getKafkaBootstrapServers` sobre `ReconciliationConfig`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `ReconciliationRepository.getClass`: Se invoca `getClass` sobre `ReconciliationRepository`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/ConciliationApplication.java` — `IdempotencyStore.getClass`: Se invoca `getClass` sobre `IdempotencyStore`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.hasAllReferences`: Se invoca `hasAllReferences` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.coreBankReference`: Se invoca `coreBankReference` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.paymentGatewayReference`: Se invoca `paymentGatewayReference` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java` — `MatchingResult.settlementReference`: Se invoca `settlementReference` sobre `MatchingResult`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getId`: Se invoca `getId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setId`: Se invoca `setId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMovementId`: Se invoca `setMovementId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setEventId`: Se invoca `setEventId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setVersion`: Se invoca `setVersion` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setState`: Se invoca `setState` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setReceivedAt`: Se invoca `setReceivedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMatchedAt`: Se invoca `setMatchedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setCoreBankReference`: Se invoca `setCoreBankReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setPaymentGatewayReference`: Se invoca `setPaymentGatewayReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setSettlementReference`: Se invoca `setSettlementReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMovementAmount`: Se invoca `setMovementAmount` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setMovementCurrency`: Se invoca `setMovementCurrency` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setManualResolution`: Se invoca `setManualResolution` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.setProcessedBy`: Se invoca `setProcessedBy` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMovementId`: Se invoca `getMovementId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getEventId`: Se invoca `getEventId` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getVersion`: Se invoca `getVersion` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getState`: Se invoca `getState` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getReceivedAt`: Se invoca `getReceivedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMatchedAt`: Se invoca `getMatchedAt` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getCoreBankReference`: Se invoca `getCoreBankReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getPaymentGatewayReference`: Se invoca `getPaymentGatewayReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getSettlementReference`: Se invoca `getSettlementReference` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMovementAmount`: Se invoca `getMovementAmount` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getMovementCurrency`: Se invoca `getMovementCurrency` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getManualResolution`: Se invoca `getManualResolution` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java` — `ReconciliationEntity.getProcessedBy`: Se invoca `getProcessedBy` sobre `ReconciliationEntity`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.resolution`: Se invoca `resolution` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.processedBy`: Se invoca `processedBy` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.force`: Se invoca `force` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.from`: Se invoca `from` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.to`: Se invoca `to` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java` — `ReprocessRequest.strategy`: Se invoca `strategy` sobre `ReprocessRequest`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresSnapshotReprocessor.java` — `ReconciliationService.processReconciliation`: Se invoca `processReconciliation` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setCoreBankReference`: Se invoca `setCoreBankReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setPaymentGatewayReference`: Se invoca `setPaymentGatewayReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setSettlementReference`: Se invoca `setSettlementReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconciliationService.matchMovement`: Se invoca `matchMovement` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setMovementAmount`: Se invoca `setMovementAmount` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `Reconciliation.setMovementAmount`: Se invoca `setMovementAmount` sobre `Reconciliation`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconciliationService.mismatchMovement`: Se invoca `mismatchMovement` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setReprocess`: Se invoca `setReprocess` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconciliationService.reprocessMovement`: Se invoca `reprocessMovement` sobre `ReconciliationService`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setEventId`: Se invoca `setEventId` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setVersion`: Se invoca `setVersion` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setSource`: Se invoca `setSource` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setMovementId`: Se invoca `setMovementId` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setReference`: Se invoca `setReference` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setAmount`: Se invoca `setAmount` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setCurrency`: Se invoca `setCurrency` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setOccurredAt`: Se invoca `setOccurredAt` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java` — `ReconcileMovementCommand.setCorrelationId`: Se invoca `setCorrelationId` sobre `ReconcileMovementCommand`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java` — `IdempotencyStore.isProcessed`: Se invoca `isProcessed` sobre `IdempotencyStore`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java` — `IdempotencyStore.getLatestVersion`: Se invoca `getLatestVersion` sobre `IdempotencyStore`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

## Como saber que terminaste

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando corriendo sin errores es la definicion de "listo".

---

```
## Briefing del reto (autoridad)
Este bloque manda sobre los archivos adjuntos. El stack y el rol salen de AQUÍ, no de un topic genérico ni de markdown placeholder.

### Contexto técnico original
Diseño de motor de conciliación que consume streams de movimientos desde 3 fuentes (core bancario, gateway de pagos, sistema de liquidación) y detecta discrepancias en ventanas móviles. Cada movimiento debe reconciliarse contra las 3 fuentes con tolerancia a mensajes fuera de orden y llegadas duplicadas. El desarrollador senior debe diseñar la máquina de estados de cada Reconciliation (Pending, Matched, Mismatched, Manual), justificar la ventana de matching (5 min vs 1 hora), definir cómo maneja idempotencia con eventId + version, y elegir entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL. Adicionalmente debe explicar cómo alertar al equipo de operaciones cuando el lag de conciliación supera un SLA.

### Reto
- Tema: TEST-CT
- Seniority: senior-l2
- Tipo: practical
- Título: Diseño de sistema de conciliación bancaria en tiempo real
- Tiempo estimado: 1 semana

### Fases (trabajo del HUMANO — PROHIBIDO completarlas)
No implementes estos entregables. Dejalos como hueco pedagógico. El asistente solo materializa el proyecto arrancable para que el participante pueda trabajar.
- Fase 1: Diseño de la máquina de estados — objetivo: Definir los estados y transiciones de la conciliación — entregable (NO resolver): Diagrama de estados de la conciliación
- Fase 2: Definición de la ventana de matching — objetivo: Establecer la ventana de tiempo para la conciliación de movimientos — entregable (NO resolver): Documento que describe la ventana de matching y su justificación
- Fase 3: Manejo de idempotencia — objetivo: Implementar idempotencia en el proceso de conciliación — entregable (NO resolver): Documento que describe el manejo de idempotencia en el proceso de conciliación
- Fase 4: Estrategia de reprocesamiento — objetivo: Elegir entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL — entregable (NO resolver): Documento que describe la estrategia de reprocesamiento elegida
- Fase 5: Alerta de lag de conciliación — objetivo: Definir cómo alertar al equipo de operaciones cuando el lag de conciliación supera un SLA — entregable (NO resolver): Documento que describe el proceso de alerta de lag de conciliación

Eres un asistente experto en análisis, corrección y generación de archivos de cualquier tipo:
código fuente, documentación, hojas de cálculo, documentos Word, configuraciones, entre otros.
Voy a enviarte una cadena de texto que contiene uno o más archivos. Cada archivo está delimitado por un marcador con el siguiente formato:
// === ARCHIVO: ruta/del/archivo.extension ===
o también puede aparecer como:
## === ARCHIVO: ruta/del/archivo.extension ===
Lo que sigue al marcador puede ser:

El contenido real del archivo (código, texto, YAML, etc.)
Una descripción en lenguaje natural de lo que debe contener el archivo


TU TAREA
PASO 0 — ¿Esto es un proyecto o una carcasa?
Antes de extraer archivos, leé el Briefing (si está) y diagnosticá el adjunto.

Es CARCASA si ocurre CUALQUIERA de estas:
- No hay manifiesto de dependencias del stack del briefing (manifest.json de VTEX IO / package.json / pom.xml / build.gradle / requirements.txt / go.mod / *.tf / *.csproj, según corresponda)
- Hay un "binario" que en realidad es un comentario ("no puede ser mostrado como texto plano", placeholder .fig/.docx vacío)
- Los markdowns ya completan entregables de fases posteriores ("se implementó fade-in", lista de áreas ya resuelta)

Si es CARCASA:
- MATERIALIZÁ un proyecto que arranca en el stack del briefing (VTEX IO Store Framework, Angular, Terraform, pytest, Nest, etc.). Incluí manifiesto, punto de entrada y capa de interfaz reales.
- NO copies los markdowns de "solución" como si fueran el producto. Son ruido de generación.
- NO resuelvas las fases del briefing (están marcadas PROHIBIDO). Dejá el hueco pedagógico: el flujo existe, las microinteracciones/calidad/infra que el reto pide NO están hechas.
- Después seguí al PASO 5 (ZIP).

Si es un proyecto REAL (manifiesto + código que compila o arranca):
- Seguí PASO 1 en adelante. 🔴 compilación sí. 🟡 pedagógico no.

PASO 1 — Detección y extracción
Identifica todos los archivos presentes en la cadena. Para cada archivo extrae:

Su ruta completa (ej: src/main/java/com/pragma/Service.java)
Su contenido o descripción

PASO 2 — Clasificación por tipo
Clasifica cada archivo en una de estas categorías:
A) Código fuente (Java, Python, TypeScript, JavaScript, Kotlin, etc.)
B) Configuración / documentación (YAML, properties, Markdown, JSON, txt, etc.)
C) Excel (.xlsx, .xls, .csv)
D) Word (.docx, .doc)
E) Otro tipo de archivo binario o especial
PASO 3 — Clasificación de errores en código fuente

Objetivo prioritario: que el proyecto compile. No corrijas flujo de negocio ni lógica funcional.

Antes de modificar cualquier archivo de código fuente, clasifica cada problema encontrado en una de estas dos categorías:
🔴 ERROR DE COMPILACIÓN — corregir siempre
Son errores que impiden que el proyecto arranque, sin valor pedagógico:

Import faltante o incorrecto
Clase, método o variable referenciada que no existe en ningún archivo del proyecto
Error de sintaxis
Anotación con atributos inválidos
Dependencia ausente en pom.xml, package.json, etc.
Archivo referenciado que no existe y debe ser creado con implementación mínima

→ CORREGIR estos errores.
🟡 PROBLEMA FUNCIONAL O DE CALIDAD — preservar siempre
Son problemas que no impiden compilar. Pueden ser intencionales para el aprendizaje:

Clave secreta hardcodeada ("secret", "password123")
API deprecada que funciona pero tiene reemplazo moderno
Lógica de negocio incorrecta o incompleta
Código redundante o de baja legibilidad
Falta de validaciones en flujo de negocio
Patrones de diseño incorrectos pero funcionales
Concurrencia no segura
Configuración funcional pero no óptima

→ PRESERVAR tal cual. No corregir, no mejorar, no comentar.
PASO 4 — Procesamiento según tipo de archivo
Tipo A — Código fuente
Aplica únicamente las correcciones clasificadas como 🔴 ERROR DE COMPILACIÓN.
No alteres ningún elemento clasificado como 🟡 PROBLEMA FUNCIONAL O DE CALIDAD.
Si falta un archivo referenciado, créalo con la implementación mínima necesaria para compilar.
Tipo B — Configuración / documentación
Extrae el contenido tal cual, sin modificaciones salvo errores evidentes de sintaxis
(ej: YAML mal indentado).
Tipo C — Excel (.xlsx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un archivo Excel funcional con:

Fila de encabezados en negrita con color de fondo distintivo
Columnas con ancho ajustado al contenido
Tipos de dato correctos por columna
Validaciones si la descripción lo indica
Hojas nombradas descriptivamente si hay más de una
Filas de ejemplo si no hay datos reales

Tipo D — Word (.docx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un documento Word funcional con:

Estilos de título (Título 1, Título 2) para jerarquía de secciones
Fuente legible (Calibri o equivalente), tamaño 11-12pt para cuerpo
Márgenes estándar
Tabla de contenido si tiene múltiples secciones
Tablas con encabezados en negrita si aplica

Tipo E — Otro
Genera el archivo con el contenido o estructura más apropiada según la descripción.
PASO 5 — Exportación en ZIP
Empaqueta todos los archivos en un único archivo ZIP descargable respetando exactamente
la estructura de rutas indicada por los marcadores.
El ZIP debe incluir:

Archivos de código con únicamente los errores de compilación corregidos
Archivos de configuración y documentación sin cambios
Archivos nuevos creados para resolver dependencias de compilación faltantes
Archivos Excel y Word generados desde descripción

IMPORTANTE: El ZIP debe estar listo para descargar al finalizar. No preguntes si el usuario
quiere generarlo. Simplemente genera el archivo y proporciona el enlace de descarga; No debes desplegar en el chat el resumen de lo que arreglaste al Zip, solo entregalo.

REGLAS IMPORTANTES

No omitas ningún archivo aunque no tenga errores ni modificaciones
Respeta los nombres y rutas exactas indicadas por los marcadores
Si un archivo no tiene marcador claro, infiere el nombre desde su contenido
Si la cadena contiene solo documentación, placeholders o binarios fake, NO la reproduzcas:
aplicá PASO 0 (materializar el proyecto del briefing). Reproducir la carcasa es un fallo.
No agregues texto después del enlace de descarga del ZIP
No preguntes si el usuario quiere el ZIP: simplemente generalo siempre
Si detectas que falta un archivo de configuración necesario para compilar
(pom.xml, package.json, requirements.txt, build.gradle, etc.), créalo e inclúyelo
inferiendo su contenido desde los imports y frameworks detectados en el código
Nunca corrijas problemas 🟡 aunque parezcan obvios o fáciles de mejorar.
El participante que recibirá este proyecto los debe encontrar y resolver él mismo.


INPUT
Aquí está la cadena con los archivos:

// === ARCHIVO: pom.xml ===
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.pragma</groupId>
    <artifactId>conciliation</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>conciliation</name>
    <description>Sistema de conciliacion bancaria en tiempo real</description>
    
    <properties>
        <java.version>21</java.version>
        <resilience4j.version>2.1.0</resilience4j.version>
        <micrometer.version>1.13.0</micrometer.version>
        <testcontainers.version>1.19.8</testcontainers.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.github.resilience4j</groupId>
                <artifactId>resilience4j-bom</artifactId>
                <version>${resilience4j.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>io.micrometer</groupId>
                <artifactId>micrometer-bom</artifactId>
                <version>${micrometer.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <!-- Spring Boot WebFlux -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        
        <!-- Spring Kafka -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        
        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Spring Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- Spring Data Reactive PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Micrometer Prometheus -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        
        <!-- Spring Boot Actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Resilience4j -->
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot2</artifactId>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        
        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/conciliation/ConciliationApplication.java ===
package com.pragma.conciliation;

import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.infrastructure.config.ReconciliationConfig;
import com.pragma.conciliation.infrastructure.messaging.IdempotencyStore;
import com.pragma.conciliation.infrastructure.messaging.KafkaMovementConsumer;
import com.pragma.conciliation.infrastructure.monitoring.ConciliationLagMonitor;
import com.pragma.conciliation.infrastructure.persistence.PostgresReconciliationRepository;
import com.pragma.conciliation.infrastructure.rest.ReconciliationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.pragma.conciliation.application.commands.ReconcileMovementCommandHandler;
import com.pragma.conciliation.domain.service.ReprocessingStrategy;
import com.pragma.conciliation.infrastructure.messaging.KafkaReplayReprocessor;
import com.pragma.conciliation.infrastructure.persistence.PostgresSnapshotReprocessor;

import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

/**
 * Punto de entrada de la aplicacion de conciliacion bancaria.
 * Configura el contexto de Spring Boot, los beans de infraestructura
 * y los componentes del dominio para el procesamiento de movimientos.
 */
@SpringBootApplication
@EnableKafka
@EnableConfigurationProperties(ReconciliationConfig.class)
public class ConciliationApplication {

    private static final Logger log = LoggerFactory.getLogger(ConciliationApplication.class);
    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(30);

    private final ReconciliationConfig config;
    private final ApplicationContext applicationContext;

    public ConciliationApplication(ReconciliationConfig config, ApplicationContext applicationContext) {
        this.config = validateConfig(config);
        this.applicationContext = applicationContext;
        log.info("Iniciando aplicacion de conciliacion con configuracion: matchingWindow={}, slaAlertMinutes={}",
                config.getMatchingWindowMinutes(), config.getSlaAlertMinutes());
    }

    private ReconciliationConfig validateConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalStateException("ReconciliationConfig no puede ser nulo");
        }
        int windowMinutes = config.getMatchingWindowMinutes();
        if (windowMinutes < 5 || windowMinutes > 60) {
            throw new IllegalArgumentException(
                    "La ventana de matching debe estar entre 5 y 60 minutos, valor actual: " + windowMinutes);
        }
        int slaMinutes = config.getSlaAlertMinutes();
        if (slaMinutes < 1 || slaMinutes > 60) {
            throw new IllegalArgumentException(
                    "El SLA de alerta debe estar entre 1 y 60 minutos, valor actual: " + slaMinutes);
        }
        return config;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            ApplicationContext context = SpringApplication.run(ConciliationApplication.class, args);
            long initTime = System.currentTimeMillis() - startTime;
            log.info("Aplicacion iniciada en {} ms", initTime);
            
            ReconciliationConfig rc = context.getBean(ReconciliationConfig.class);
            log.info("Configuracion activa - Ventana: {} min, SLA: {} min, Kafka: {}",
                    rc.getMatchingWindowMinutes(),
                    rc.getSlaAlertMinutes(),
                    rc.getKafkaBootstrapServers());
            
            context.getBean(ConciliationLagMonitor.class).startMonitoring();
            log.info("Monitor de lag iniciado");
            
        } catch (Exception e) {
            log.error("Error fatal al iniciar la aplicacion: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    @Bean
    public ReconciliationService reconciliationService(ReconciliationRepository repository,
                                                        IdempotencyStore idempotencyStore,
                                                        ReprocessingStrategy reprocessingStrategy) {
        log.debug("Creando bean ReconciliationService con repository={}, idempotencyStore={}",
                repository.getClass().getSimpleName(),
                idempotencyStore.getClass().getSimpleName());
        return new ReconciliationService(repository, idempotencyStore, reprocessingStrategy, config);
    }

    @Bean
    public ReconcileMovementCommandHandler reconcileCommandHandler(ReconciliationService service) {
        return new ReconcileMovementCommandHandler(service);
    }

    @Bean
    public ReconciliationController reconciliationController(ReconciliationService service) {
        return new ReconciliationController(service);
    }

    @Bean
    public KafkaMovementConsumer kafkaConsumer(ReconcileMovementCommandHandler handler,
                                                IdempotencyStore idempotencyStore) {
        return new KafkaMovementConsumer(handler, idempotencyStore, config);
    }

    @Bean
    public IdempotencyStore idempotencyStore(ReconciliationRepository repository) {
        return new IdempotencyStore(repository);
    }

    @Bean
    public ReprocessingStrategy kafkaReplayStrategy(KafkaReplayReprocessor reprocessor) {
        return reprocessor;
    }

    @Bean
    public ReprocessingStrategy postgresSnapshotStrategy(PostgresSnapshotReprocessor reprocessor) {
        return reprocessor;
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler(ProducerFactory<String, Object> producerFactory) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                new DefaultKafkaProducerFactory<>(Map.of()));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        log.info("Configurado error handler de Kafka con retry: 3 intentos, 1s de backoff");
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);
        log.info("Factory de Kafka listener configurada con 3 consommateurs");
        return factory;
    }
}

// === ARCHIVO: src/main/resources/application.yml ===
spring:
  application:
    name: conciliation-service
  
  datasource:
    url: jdbc:postgresql://${CONCILIATION_DB_HOST:localhost}:${CONCILIATION_DB_PORT:5432}/${CONCILIATION_DB_NAME:conciliation}
    username: ${CONCILIATION_DB_USER:conciliation_user}
    password: ${CONCILIATION_DB_PASSWORD:conciliation_pass}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
  
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: conciliation-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      properties:
        spring.deserializer.value.delegate.class: org.springframework.kafka.support.serializer.JsonDeserializer
        spring.json.trusted.packages: com.pragma.conciliation.*
        spring.json.value.default.type: com.pragma.conciliation.application.commands.ReconcileMovementCommand
        isolation.level: read_committed
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      retries: 3
      properties:
        enable.idempotence: true
        acks: all
        max.in.flight.requests.per.connection: 5
    listener:
      ack-mode: manual_immediate
      concurrency: 3
      type: single

conciliation:
  matching-window-minutes: 15
  sla-alert-minutes: 10
  sources:
    - core-bancario
    - gateway-pagos
    - sistema-liquidacion
  idempotency:
    enabled: true
    retention-days: 90
  reprocessing:
    strategy: KAFKA_REPLAY
    batch-size: 100
    max-attempts: 5
  topics:
    movements: bank-movements
    reconciled: reconciled-movements
    dead-letter: bank-movements-dlq
    retry: bank-movements-retry

server:
  port: ${SERVER_PORT:8080}
  shutdown: graceful

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers
      base-path: /actuator
  endpoint:
    health:
      show-details: when_authorized
      probes:
        enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}

resilience4j:
  circuitbreaker:
    instances:
      kafkaProducer:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
      database:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        waitDurationInOpenState: 10s
        failureRateThreshold: 50
  retry:
    instances:
      kafkaProducer:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
      database:
        maxAttempts: 3
        waitDuration: 500ms
  bulkhead:
    instances:
      kafkaProducer:
        maxConcurrentCalls: 100
        maxWaitDuration: 500ms

logging:
  level:
    root: INFO
    com.pragma.conciliation: DEBUG
    org.springframework.kafka: INFO
    org.hibernate.SQL: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/conciliation.log
    max-size: 100MB
    max-history: 30


// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/model/ReconciliationState.java ===
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/model/Reconciliation.java ===
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/events/MovementReceivedEvent.java ===
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/repository/ReconciliationRepository.java ===
package com.pragma.conciliation.domain.repository;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

public interface ReconciliationRepository {

    Mono<Reconciliation> save(Reconciliation reconciliation);

    Mono<Reconciliation> findById(String id);

    Mono<Reconciliation> findByMovementId(String movementId);

    Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version);

    Mono<Boolean> existsByEventIdAndVersion(String eventId, int version);

    Flux<Reconciliation> findByState(ReconciliationState state);

    Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime);

    Flux<Reconciliation> findAllByOrderByReceivedAtAsc();

    Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration);

    Mono<Long> countByState(ReconciliationState state);

    Mono<Reconciliation> update(Reconciliation reconciliation);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByIdempotencyKey(String idempotencyKey);
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java ===
package com.pragma.conciliation.domain.service;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.model.ReconciliationStateTransition;
import com.pragma.conciliation.domain.model.ReconciliationStateTransition;
import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private final ReconciliationRepository reconciliationRepository;
    private final Duration defaultMatchingWindow;
    private final Duration maxPendingDuration;

    public ReconciliationService(ReconciliationRepository reconciliationRepository,
                                  Duration defaultMatchingWindow,
                                  Duration maxPendingDuration) {
        this.reconciliationRepository = reconciliationRepository;
        this.defaultMatchingWindow = defaultMatchingWindow;
        this.maxPendingDuration = maxPendingDuration;
    }

    public Mono<Reconciliation> processMovement(MovementReceivedEvent event) {
        String idempotencyKey = event.getIdempotencyKey();
        log.info("Procesando movimiento con clave de idempotencia: {}", idempotencyKey);

        return reconciliationRepository.existsByEventIdAndVersion(event.getEventId(), event.getVersion())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Movimiento duplicado detectado: eventId={}, version={}",
                                event.getEventId(), event.getVersion());
                        return reconciliationRepository
                                .findByEventIdAndVersion(event.getEventId(), event.getVersion());
                    }
                    return createNewReconciliation(event);
                });
    }

    private Mono<Reconciliation> createNewReconciliation(MovementReceivedEvent event) {
        Reconciliation reconciliation = new Reconciliation(
                event.getMovementId(),
                event.getEventId(),
                event.getVersion(),
                ReconciliationState.PENDING,
                event.getReceivedAt(),
                event.getAmount(),
                event.getCurrency()
        );

        log.info("Creando nueva conciliación: id={}, movementId={}, eventId={}, version={}",
                reconciliation.getId(),
                event.getMovementId(),
                event.getEventId(),
                event.getVersion());

        return reconciliationRepository.save(reconciliation)
                .flatMap(saved -> attemptMatching(saved));
    }

    private Mono<Reconciliation> attemptMatching(Reconciliation reconciliation) {
        Instant windowStart = reconciliation.getReceivedAt().minus(defaultMatchingWindow);
        Instant windowEnd = reconciliation.getReceivedAt().plus(defaultMatchingWindow);

        log.debug("Intentando matching para {} en ventana [{}, {}]",
                reconciliation.getId(), windowStart, windowEnd);

        return findMatchingReferences(reconciliation)
                .flatMap(matchingResult -> {
                    if (matchingResult.hasAllReferences()) {
                        return transitionToMatched(reconciliation, matchingResult);
                    } else if (reconciliation.exceedsMaxPendingDuration(maxPendingDuration)) {
                        return transitionToMismatched(reconciliation,
                                "Timeout: referencias no encontradas dentro de " + maxPendingDuration);
                    }
                    return Mono.just(reconciliation);
                });
    }

    private Mono<MatchingResult> findMatchingReferences(Reconciliation reconciliation) {
        return findCoreBankReference(reconciliation.getMovementId(),
                        reconciliation.getReceivedAt().minus(defaultMatchingWindow),
                        reconciliation.getReceivedAt().plus(defaultMatchingWindow))
                .zipWith(findPaymentGatewayReference(reconciliation.getMovementId(),
                        reconciliation.getReceivedAt().minus(defaultMatchingWindow),
                        reconciliation.getReceivedAt().plus(defaultMatchingWindow)))
                .zipWith(findSettlementReference(reconciliation.getMovementId(),
                        reconciliation.getReceivedAt().minus(defaultMatchingWindow),
                        reconciliation.getReceivedAt().plus(defaultMatchingWindow)))
                .map(tuple -> {
                    String coreRef = tuple.getT1().getT1();
                    String gatewayRef = tuple.getT1().getT2();
                    String settlementRef = tuple.getT2();
                    return new MatchingResult(coreRef, gatewayRef, settlementRef);
                });
    }

    private Mono<String> findCoreBankReference(String movementId, Instant from, Instant to) {
        return Mono.just("CORE-" + movementId.substring(0, Math.min(8, movementId.length())));
    }

    private Mono<String> findPaymentGatewayReference(String movementId, Instant from, Instant to) {
        return Mono.just("GW-" + movementId.substring(0, Math.min(8, movementId.length())));
    }

    private Mono<String> findSettlementReference(String movementId, Instant from, Instant to) {
        return Mono.just("SETT-" + movementId.substring(0, Math.min(8, movementId.length())));
    }

    private Mono<Reconciliation> transitionToMatched(Reconciliation reconciliation,
                                                       MatchingResult matching) {
        reconciliation.markAsMatched(
                matching.coreBankReference(),
                matching.paymentGatewayReference(),
                matching.settlementReference()
        );

        log.info("Conciliación completada exitosamente: id={}, coreRef={}, gatewayRef={}, settlementRef={}",
                reconciliation.getId(),
                matching.coreBankReference(),
                matching.paymentGatewayReference(),
                matching.settlementReference());

        return reconciliationRepository.update(reconciliation);
    }

    private Mono<Reconciliation> transitionToMismatched(Reconciliation reconciliation, String reason) {
        reconciliation.markAsMismatched(reason);

        log.warn("Conciliación fallida: id={}, reason={}", reconciliation.getId(), reason);

        return reconciliationRepository.update(reconciliation);
    }

    public Mono<Reconciliation> resolveManually(String reconciliationId, String resolution, String processedBy) {
        return reconciliationRepository.findById(reconciliationId)
                .flatMap(reconciliation -> {
                    reconciliation.markAsManual(resolution, processedBy);
                    log.info("Resolución manual aplicada: id={}, resolution={}, processedBy={}",
                            reconciliationId, resolution, processedBy);
                    return reconciliationRepository.update(reconciliation);
                });
    }

    public Mono<List<Reconciliation>> getReconciliationsWithLag() {
        return reconciliationRepository.findPendingWithLagExceeding(maxPendingDuration);
    }

    public Flux<Reconciliation> getAllReconciliations() {
        return reconciliationRepository.findAllByOrderByReceivedAtAsc();
    }

    public Mono<Reconciliation> getReconciliationById(String id) {
        return reconciliationRepository.findById(id);
    }

    public Mono<Long> countByState(ReconciliationState state) {
        return reconciliationRepository.countByState(state);
    }

    private record MatchingResult(String coreBankReference,
                                   String paymentGatewayReference,
                                   String settlementReference) {
        boolean hasAllReferences() {
            return coreBankReference != null && !coreBankReference.isBlank()
                    && paymentGatewayReference != null && !paymentGatewayReference.isBlank()
                    && settlementReference != null && !settlementReference.isBlank();
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java ===
package com.pragma.conciliation.infrastructure.persistence;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.model.ReconciliationStateTransition;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Repository
public class PostgresReconciliationRepository implements ReconciliationRepository {

    private static final Logger log = LoggerFactory.getLogger(PostgresReconciliationRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Mono<Reconciliation> save(Reconciliation reconciliation) {
        return Mono.fromCallable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = toEntity(reconciliation);
                entityManager.persist(entity);
                entityManager.getTransaction().commit();
                log.debug("Entidad persistida: id={}", entity.getId());
                return toDomain(entity);
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                log.error("Error al persistir conciliación: {}", e.getMessage(), e);
                throw new RuntimeException("Error al guardar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Reconciliation> findById(String id) {
        return Mono.fromCallable(() -> {
            ReconciliationEntity entity = entityManager.find(ReconciliationEntity.class, id);
            return entity != null ? toDomain(entity) : null;
        });
    }

    @Override
    public Mono<Reconciliation> findByMovementId(String movementId) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.movementId = :movementId",
                    ReconciliationEntity.class);
            query.setParameter("movementId", movementId);
            query.setMaxResults(1);
            List<ReconciliationEntity> results = query.getResultList();
            return results.isEmpty() ? null : toDomain(results.get(0));
        });
    }

    @Override
    public Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    ReconciliationEntity.class);
            query.setParameter("eventId", eventId);
            query.setParameter("version", version);
            query.setMaxResults(1);
            List<ReconciliationEntity> results = query.getResultList();
            return results.isEmpty() ? null : toDomain(results.get(0));
        });
    }

    @Override
    public Mono<Boolean> existsByEventIdAndVersion(String eventId, int version) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    Long.class);
            query.setParameter("eventId", eventId);
            query.setParameter("version", version);
            return query.getSingleResult() > 0;
        });
    }

    @Override
    public Flux<Reconciliation> findByState(ReconciliationState state) {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state",
                    ReconciliationEntity.class);
            query.setParameter("state", state.name());
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime) {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state AND r.receivedAt < :cutoffTime",
                    ReconciliationEntity.class);
            query.setParameter("state", state.name());
            query.setParameter("cutoffTime", cutoffTime);
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Flux<Reconciliation> findAllByOrderByReceivedAtAsc() {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r ORDER BY r.receivedAt ASC",
                    ReconciliationEntity.class);
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration) {
        return Mono.fromCallable(() -> {
            Instant cutoffTime = Instant.now().minus(maxPendingDuration);
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state AND r.receivedAt < :cutoffTime",
                    ReconciliationEntity.class);
            query.setParameter("state", ReconciliationState.PENDING.name());
            query.setParameter("cutoffTime", cutoffTime);
            return query.getResultList().stream()
                    .map(this::toDomain)
                    .toList();
        });
    }

    @Override
    public Mono<Long> countByState(ReconciliationState state) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.state = :state",
                    Long.class);
            query.setParameter("state", state.name());
            return query.getSingleResult();
        });
    }

    @Override
    public Mono<Reconciliation> update(Reconciliation reconciliation) {
        return Mono.fromCallable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = entityManager.find(
                        ReconciliationEntity.class, reconciliation.getId());
                if (entity == null) {
                    throw new RuntimeException("Conciliación no encontrada: " + reconciliation.getId());
                }
                updateEntityFromDomain(entity, reconciliation);
                entityManager.getTransaction().commit();
                log.debug("Entidad actualizada: id={}", entity.getId());
                return toDomain(entity);
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                log.error("Error al actualizar conciliación: {}", e.getMessage(), e);
                throw new RuntimeException("Error al actualizar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = entityManager.find(ReconciliationEntity.class, id);
                if (entity != null) {
                    entityManager.remove(entity);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw new RuntimeException("Error al eliminar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Boolean> existsByIdempotencyKey(String idempotencyKey) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    Long.class);
            String[] parts = idempotencyKey.split("-");
            if (parts.length >= 2) {
                query.setParameter("eventId", parts[0]);
                query.setParameter("version", Integer.parseInt(parts[parts.length - 1]));
                return query.getSingleResult() > 0;
            }
            return false;
        });
    }

    private ReconciliationEntity toEntity(Reconciliation reconciliation) {
        ReconciliationEntity entity = new ReconciliationEntity();
        entity.setId(reconciliation.getId());
        entity.setMovementId(reconciliation.getMovementId());
        entity.setEventId(reconciliation.getEventId());
        entity.setVersion(reconciliation.getVersion());
        entity.setState(reconciliation.getState().name());
        entity.setReceivedAt(reconciliation.getReceivedAt());
        entity.setMatchedAt(reconciliation.getMatchedAt());
        entity.setCoreBankReference(reconciliation.getCoreBankReference());
        entity.setPaymentGatewayReference(reconciliation.getPaymentGatewayReference());
        entity.setSettlementReference(reconciliation.getSettlementReference());
        entity.setMovementAmount(reconciliation.getMovementAmount());
        entity.setMovementCurrency(reconciliation.getMovementCurrency());
        entity.setManualResolution(reconciliation.getManualResolution());
        entity.setProcessedBy(reconciliation.getProcessedBy());
        return entity;
    }

    private void updateEntityFromDomain(ReconciliationEntity entity, Reconciliation reconciliation) {
        entity.setState(reconciliation.getState().name());
        entity.setMatchedAt(reconciliation.getMatchedAt());
        entity.setCoreBankReference(reconciliation.getCoreBankReference());
        entity.setPaymentGatewayReference(reconciliation.getPaymentGatewayReference());
        entity.setSettlementReference(reconciliation.getSettlementReference());
        entity.setManualResolution(reconciliation.getManualResolution());
        entity.setProcessedBy(reconciliation.getProcessedBy());
    }

    private Reconciliation toDomain(ReconciliationEntity entity) {
        return new Reconciliation(
                entity.getId(),
                entity.getMovementId(),
                entity.getEventId(),
                entity.getVersion(),
                ReconciliationState.valueOf(entity.getState()),
                entity.getReceivedAt(),
                entity.getMatchedAt(),
                entity.getCoreBankReference(),
                entity.getPaymentGatewayReference(),
                entity.getSettlementReference(),
                entity.getMovementAmount(),
                entity.getMovementCurrency(),
                entity.getManualResolution(),
                entity.getProcessedBy()
        );
    }

    @Entity
    @Table(name = "reconciliations")
    private static class ReconciliationEntity {
        private String id;
        private String movementId;
        private String eventId;
        private int version;
        private String state;
        private Instant receivedAt;
        private Instant matchedAt;
        private String coreBankReference;
        private String paymentGatewayReference;
        private String settlementReference;
        private java.math.BigDecimal movementAmount;
        private String movementCurrency;
        private String manualResolution;
        private String processedBy;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getMovementId() { return movementId; }
        public void setMovementId(String movementId) { this.movementId = movementId; }
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public Instant getReceivedAt() { return receivedAt; }
        public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
        public Instant getMatchedAt() { return matchedAt; }
        public void setMatchedAt(Instant matchedAt) { this.matchedAt = matchedAt; }
        public String getCoreBankReference() { return coreBankReference; }
        public void setCoreBankReference(String coreBankReference) { this.coreBankReference = coreBankReference; }
        public String getPaymentGatewayReference() { return paymentGatewayReference; }
        public void setPaymentGatewayReference(String paymentGatewayReference) { this.paymentGatewayReference = paymentGatewayReference; }
        public String getSettlementReference() { return settlementReference; }
        public void setSettlementReference(String settlementReference) { this.settlementReference = settlementReference; }
        public java.math.BigDecimal getMovementAmount() { return movementAmount; }
        public void setMovementAmount(java.math.BigDecimal movementAmount) { this.movementAmount = movementAmount; }
        public String getMovementCurrency() { return movementCurrency; }
        public void setMovementCurrency(String movementCurrency) { this.movementCurrency = movementCurrency; }
        public String getManualResolution() { return manualResolution; }
        public void setManualResolution(String manualResolution) { this.manualResolution = manualResolution; }
        public String getProcessedBy() { return processedBy; }
        public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/service/ReprocessingStrategy.java ===
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommand.java ===
package com.pragma.conciliation.application.commands;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Comando para iniciar el proceso de conciliación de un movimiento bancario.
 * Representa la intención de conciliar un movimiento recibido desde cualquiera
 * de las tres fuentes: core bancario, gateway de pagos o sistema de liquidación.
 */
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
    private final MovementReceivedEvent.MovementType movementType;
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
            MovementReceivedEvent.MovementType movementType,
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

    public String getEventId() {
        return eventId;
    }

    public int getVersion() {
        return version;
    }

    public String getMovementId() {
        return movementId;
    }

    public String getSource() {
        return source;
    }

    public String getReference() {
        return reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public MovementReceivedEvent.MovementType getMovementType() {
        return movementType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public boolean isForceReprocessing() {
        return forceReprocessing;
    }

    public String getIdempotencyKey() {
        return eventId + "_v" + version;
    }

    @Override
    public String toString() {
        return "ReconcileMovementCommand{" +
                "eventId='" + eventId + '\'' +
                ", version=" + version +
                ", movementId='" + movementId + '\'' +
                ", source='" + source + '\'' +
                ", reference='" + reference + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", movementType=" + movementType +
                ", forceReprocessing=" + forceReprocessing +
                '}';
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommandHandler.java ===
package com.pragma.conciliation.application.commands;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.domain.service.ReprocessingStrategy;
import com.pragma.conciliation.infrastructure.messaging.IdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handler del comando de conciliación que orquesta el proceso completo.
 * Implementa el patrón CQRS para el comando de reconciliación de movimientos.
 * Coordina la validación de idempotencia, el procesamiento del matching y la
 * actualización del estado de la reconciliación.
 */
@Component
public class ReconcileMovementCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ReconcileMovementCommandHandler.class);

    private final ReconciliationRepository reconciliationRepository;
    private final ReconciliationService reconciliationService;
    private final IdempotencyStore idempotencyStore;
    private final Map<ReprocessingStrategy.StrategyType, ReprocessingStrategy> reprocessingStrategies;

    public ReconcileMovementCommandHandler(
            ReconciliationRepository reconciliationRepository,
            ReconciliationService reconciliationService,
            IdempotencyStore idempotencyStore,
            List<ReprocessingStrategy> strategies) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationService = reconciliationService;
        this.idempotencyStore = idempotencyStore;
        this.reprocessingStrategies = strategies.stream()
            .collect(Collectors.toMap(ReprocessingStrategy::getType, Function.identity()));
    }

    /**
     * Ejecuta el comando de conciliación para un movimiento específico.
     * @param command Comando con los datos del movimiento a conciliar
     * @return Mono con la reconciliación resultante
     */
    public Mono<Reconciliation> handle(ReconcileMovementCommand command) {
        log.info("Iniciando conciliación para movimiento: eventId={}, version={}, movementId={}",
                command.getEventId(), command.getVersion(), command.getMovementId());

        String idempotencyKey = command.getIdempotencyKey();

        return idempotencyStore.isProcessed(idempotencyKey)
            .flatMap(processed -> {
                if (processed && !command.isForceReprocessing()) {
                    log.warn("Movimiento ya procesado: idempotencyKey={}", idempotencyKey);
                    return reconciliationRepository.findByEventIdAndVersion(
                        command.getEventId(), command.getVersion()
                    );
                }
                return executeReconciliation(command);
            })
            .doOnSuccess(reconciliation -> {
                if (reconciliation != null) {
                    log.info("Conciliación completada: id={}, state={}, movementId={}",
                        reconciliation.getId(), reconciliation.getState(), reconciliation.getMovementId());
                }
            })
            .doOnError(error ->
                log.error("Error en conciliación: eventId={}, movementId={}, error={}",
                    command.getEventId(), command.getMovementId(), error.getMessage()));
    }

    private Mono<Reconciliation> executeReconciliation(ReconcileMovementCommand command) {
        return createOrUpdateReconciliation(command)
            .flatMap(this::performMatching)
            .flatMap(this::updateReconciliationState)
            .flatMap(this::persistReconciliation)
            .flatMap(this::markAsProcessed);
    }

    private Mono<Reconciliation> createOrUpdateReconciliation(ReconcileMovementCommand command) {
        return reconciliationRepository.findByMovementId(command.getMovementId())
            .flatMap(existing -> {
                if (existing.getVersion() < command.getVersion()) {
                    log.info("Actualizando reconciliación existente: movementId={}, oldVersion={}, newVersion={}",
                        command.getMovementId(), existing.getVersion(), command.getVersion());
                    return updateReconciliation(existing, command);
                }
                log.info("Versión obsoleta ignorada: movementId={}, existingVersion={}, newVersion={}",
                    command.getMovementId(), existing.getVersion(), command.getVersion());
                return Mono.just(existing);
            })
            .switchIfEmpty(Mono.defer(() -> createNewReconciliation(command)));
    }

    private Mono<Reconciliation> createNewReconciliation(ReconcileMovementCommand command) {
        Reconciliation reconciliation = new Reconciliation(
            command.getMovementId(),
            command.getEventId(),
            command.getVersion(),
            ReconciliationState.PENDING,
            command.getReceivedAt()
        );
        log.debug("Creando nueva reconciliación: movementId={}, eventId={}",
            command.getMovementId(), command.getEventId());
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> updateReconciliation(Reconciliation existing, ReconcileMovementCommand command) {
        existing.markAsMismatched("Versión actualizada: " + command.getVersion());
        return reconciliationRepository.save(existing)
            .flatMap(updated -> createNewReconciliation(command));
    }

    private Mono<Reconciliation> performMatching(Reconciliation reconciliation) {
        return reconciliationService.matchMovement(reconciliation)
            .flatMap(matched -> {
                if (matched) {
                    log.info("Movimiento conciliado exitosamente: movementId={}", reconciliation.getMovementId());
                    return reconciliationService.findMatchingReferences(reconciliation)
                        .flatMap(refs -> {
                            reconciliation.markAsMatched(
                                refs.get("coreBankReference"),
                                refs.get("paymentGatewayReference"),
                                refs.get("settlementReference")
                            );
                            return Mono.just(reconciliation);
                        });
                } else {
                    log.info("No se encontraron coincidencias para el movimiento: movementId={}",
                        reconciliation.getMovementId());
                    return Mono.just(reconciliation);
                }
            });
    }

    private Mono<Reconciliation> updateReconciliationState(Reconciliation reconciliation) {
        if (reconciliation.getState() == ReconciliationState.PENDING) {
            Duration pendingDuration = reconciliation.getPendingDuration();
            if (pendingDuration.toMinutes() > 30) {
                log.warn("Movimiento en estado PENDING por más de 30 minutos: movementId={}, duration={}",
                    reconciliation.getMovementId(), pendingDuration.toMinutes());
            }
        }
        return Mono.just(reconciliation);
    }

    private Mono<Reconciliation> persistReconciliation(Reconciliation reconciliation) {
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> markAsProcessed(Reconciliation reconciliation) {
        String idempotencyKey = reconciliation.getIdempotencyKey();
        return idempotencyStore.markAsProcessed(idempotencyKey)
            .thenReturn(reconciliation);
    }

    /**
     * Reprocesa movimientos utilizando la estrategia especificada.
     * @param strategyType Tipo de estrategia de reprocesamiento
     * @param since Instante desde el cual buscar movimientos
     * @return Flux de reconciliaciones reprocesadas
     */
    public reactor.core.publisher.Flux<Reconciliation> reprocess(
            ReprocessingStrategy.StrategyType strategyType,
            Instant since) {
        ReprocessingStrategy strategy = reprocessingStrategies.get(strategyType);
        if (strategy == null) {
            return reactor.core.publisher.Flux.error(
                new IllegalArgumentException("Estrategia de reprocesamiento no encontrada: " + strategyType)
            );
        }

        log.info("Iniciando reprocesamiento con estrategia: {}, desde={}", strategyType, since);

        return strategy.findPendingReconciliations(since, Instant.now())
            .flatMap(reconciliation -> {
                ReconcileMovementCommand command = ReconcileMovementCommand.fromEvent(
                    new MovementReceivedEvent(
                        reconciliation.getEventId(),
                        reconciliation.getVersion(),
                        reconciliation.getMovementId(),
                        "REPROCESS",
                        reconciliation.getCoreBankReference() != null ?
                            reconciliation.getCoreBankReference() : reconciliation.getMovementId(),
                        reconciliation.getMovementAmount(),
                        reconciliation.getMovementCurrency(),
                        Instant.now(),
                        Instant.now(),
                        reconciliation.getId(),
                        MovementReceivedEvent.MovementType.CREDIT,
                        null,
                        null
                    )
                );
                return handle(command);
            }, 10);
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumer.java ===
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
            
            if (!idempotencyStore.isProcessed(event.getEventId(), event.getVersion())) {
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/config/ReconciliationConfig.java ===
package com.pragma.conciliation.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "conciliation")
@ConfigurationPropertiesScan
public class ReconciliationConfig {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationConfig.class);
    
    private MatchingConfig matching;
    private KafkaConfig kafka;
    private MonitorConfig monitor;
    private ReprocessingConfig reprocessing;
    private ManualInterventionConfig manualIntervention;
    
    public ReconciliationConfig() {
        this.matching = new MatchingConfig();
        this.kafka = new KafkaConfig();
        this.monitor = new MonitorConfig();
        this.reprocessing = new ReprocessingConfig();
        this.manualIntervention = new ManualInterventionConfig();
    }
    
    public MatchingConfig getMatching() {
        return matching;
    }
    
    public void setMatching(MatchingConfig matching) {
        this.matching = matching;
    }
    
    public KafkaConfig getKafka() {
        return kafka;
    }
    
    public void setKafka(KafkaConfig kafka) {
        this.kafka = kafka;
    }
    
    public MonitorConfig getMonitor() {
        return monitor;
    }
    
    public void setMonitor(MonitorConfig monitor) {
        this.monitor = monitor;
    }
    
    public ReprocessingConfig getReprocessing() {
        return reprocessing;
    }
    
    public void setReprocessing(ReprocessingConfig reprocessing) {
        this.reprocessing = reprocessing;
    }
    
    public ManualInterventionConfig getManualIntervention() {
        return manualIntervention;
    }
    
    public void setManualIntervention(ManualInterventionConfig manualIntervention) {
        this.manualIntervention = manualIntervention;
    }
    
    public void validate() {
        log.info("Validando configuración de conciliación");
        
        matching.validate();
        kafka.validate();
        monitor.validate();
        reprocessing.validate();
        manualIntervention.validate();
        
        log.info("Configuración de conciliación validada exitosamente");
    }
    
    public static class MatchingConfig {
        private Duration window = Duration.ofMinutes(5);
        private Duration maxPendingDuration = Duration.ofHours(1);
        private boolean strictMode = false;
        private AmountTolerance amountTolerance = new AmountTolerance();
        
        public Duration getWindow() {
            return window;
        }
        
        public void setWindow(Duration window) {
            this.window = window;
        }
        
        public Duration getMaxPendingDuration() {
            return maxPendingDuration;
        }
        
        public void setMaxPendingDuration(Duration maxPendingDuration) {
            this.maxPendingDuration = maxPendingDuration;
        }
        
        public boolean isStrictMode() {
            return strictMode;
        }
        
        public void setStrictMode(boolean strictMode) {
            this.strictMode = strictMode;
        }
        
        public AmountTolerance getAmountTolerance() {
            return amountTolerance;
        }
        
        public void setAmountTolerance(AmountTolerance amountTolerance) {
            this.amountTolerance = amountTolerance;
        }
        
        public void validate() {
            Duration minWindow = Duration.ofMinutes(5);
            Duration maxWindow = Duration.ofHours(1);
            
            if (window == null) {
                throw new IllegalStateException("La ventana de matching no puede ser nula");
            }
            
            if (window.compareTo(minWindow) < 0) {
                throw new IllegalStateException(
                        String.format("La ventana de matching no puede ser menor a %d minutos", 
                                     minWindow.toMinutes()));
            }
            
            if (window.compareTo(maxWindow) > 0) {
                throw new IllegalStateException(
                        String.format("La ventana de matching no puede exceder %d hora(s)", 
                                     maxWindow.toHours()));
            }
            
            if (maxPendingDuration == null) {
                throw new IllegalStateException("La duración máxima pendiente no puede ser nula");
            }
            
            if (maxPendingDuration.compareTo(window) <= 0) {
                throw new IllegalStateException(
                        "La duración máxima pendiente debe ser mayor a la ventana de matching");
            }
            
            amountTolerance.validate();
            
            log.info("Configuración de matching validada: ventana={}, maxPending={}, strictMode={}",
                    window, maxPendingDuration, strictMode);
        }
        
        public static class AmountTolerance {
            private double percentage = 0.0;
            private String currency = "USD";
            
            public double getPercentage() {
                return percentage;
            }
            
            public void setPercentage(double percentage) {
                this.percentage = percentage;
            }
            
            public String getCurrency() {
                return currency;
            }
            
            public void setCurrency(String currency) {
                this.currency = currency;
            }
            
            public void validate() {
                if (percentage < 0 || percentage > 10) {
                    throw new IllegalStateException(
                            "El porcentaje de tolerancia de monto debe estar entre 0% y 10%");
                }
            }
        }
    }
    
    public static class KafkaConfig {
        private TopicsConfig topics = new TopicsConfig();
        private ConsumerConfig consumer = new ConsumerConfig();
        private ProducerConfig producer = new ProducerConfig();
        
        public TopicsConfig getTopics() {
            return topics;
        }
        
        public void setTopics(TopicsConfig topics) {
            this.topics = topics;
        }
        
        public ConsumerConfig getConsumer() {
            return consumer;
        }
        
        public void setConsumer(ConsumerConfig consumer) {
            this.consumer = consumer;
        }
        
        public ProducerConfig getProducer() {
            return producer;
        }
        
        public void setProducer(ProducerConfig producer) {
            this.producer = producer;
        }
        
        public void validate() {
            topics.validate();
            consumer.validate();
            log.info("Configuración de Kafka validada");
        }
        
        public static class TopicsConfig {
            private String movements = "movements";
            private String settlements = "settlements";
            private String deadLetter = "conciliation-dlq";
            private String retry = "conciliation-retry";
            
            public String getMovements() {
                return movements;
            }
            
            public void setMovements(String movements) {
                this.movements = movements;
            }
            
            public String getSettlements() {
                return settlements;
            }
            
            public void setSettlements(String settlements) {
                this.settlements = settlements;
            }
            
            public String getDeadLetter() {
                return deadLetter;
            }
            
            public void setDeadLetter(String deadLetter) {
                this.deadLetter = deadLetter;
            }
            
            public String getRetry() {
                return retry;
            }
            
            public void setRetry(String retry) {
                this.retry = retry;
            }
            
            public void validate() {
                List<String> emptyTopics = new ArrayList<>();
                if (movements == null || movements.isBlank()) emptyTopics.add("movements");
                if (settlements == null || settlements.isBlank()) emptyTopics.add("settlements");
                if (deadLetter == null || deadLetter.isBlank()) emptyTopics.add("deadLetter");
                if (retry == null || retry.isBlank()) emptyTopics.add("retry");
                
                if (!emptyTopics.isEmpty()) {
                    throw new IllegalStateException(
                            "Los siguientes topics de Kafka no pueden estar vacíos: " + emptyTopics);
                }
            }
        }
        
        public static class ConsumerConfig {
            private String groupId = "conciliation-group";
            private int maxPollRecords = 500;
            private Duration maxPollInterval = Duration.ofMinutes(5);
            
            public String getGroupId() {
                return groupId;
            }
            
            public void setGroupId(String groupId) {
                this.groupId = groupId;
            }
            
            public int getMaxPollRecords() {
                return maxPollRecords;
            }
            
            public void setMaxPollRecords(int maxPollRecords) {
                this.maxPollRecords = maxPollRecords;
            }
            
            public Duration getMaxPollInterval() {
                return maxPollInterval;
            }
            
            public void setMaxPollInterval(Duration maxPollInterval) {
                this.maxPollInterval = maxPollInterval;
            }
            
            public void validate() {
                if (groupId == null || groupId.isBlank()) {
                    throw new IllegalStateException("El groupId del consumidor no puede estar vacío");
                }
                if (maxPollRecords <= 0) {
                    throw new IllegalStateException("maxPollRecords debe ser mayor a 0");
                }
            }
        }
        
        public static class ProducerConfig {
            private int retries = 3;
            private Duration retryBackoff = Duration.ofSeconds(10);
            
            public int getRetries() {
                return retries;
            }
            
            public void setRetries(int retries) {
                this.retries = retries;
            }
            
            public Duration getRetryBackoff() {
                return retryBackoff;
            }
            
            public void setRetryBackoff(Duration retryBackoff) {
                this.retryBackoff = retryBackoff;
            }
        }
    }
    
    public static class MonitorConfig {
        private Duration slaThreshold = Duration.ofMinutes(10);
        private int alertThresholdPercent = 80;
        private boolean prometheusEnabled = true;
        
        public Duration getSlaThreshold() {
            return slaThreshold;
        }
        
        public void setSlaThreshold(Duration slaThreshold) {
            this.slaThreshold = slaThreshold;
        }
        
        public int getAlertThresholdPercent() {
            return alertThresholdPercent;
        }
        
        public void setAlertThresholdPercent(int alertThresholdPercent) {
            this.alertThresholdPercent = alertThresholdPercent;
        }
        
        public boolean isPrometheusEnabled() {
            return prometheusEnabled;
        }
        
        public void setPrometheusEnabled(boolean prometheusEnabled) {
            this.prometheusEnabled = prometheusEnabled;
        }
        
        public void validate() {
            if (slaThreshold == null || slaThreshold.compareTo(Duration.ofMinutes(1)) < 0) {
                throw new IllegalStateException("El SLA threshold debe ser de al menos 1 minuto");
            }
            if (alertThresholdPercent < 50 || alertThresholdPercent > 100) {
                throw new IllegalStateException(
                        "El porcentaje de alerta debe estar entre 50% y 100%");
            }
            log.info("Configuración de monitoreo validada: slaThreshold={}, alertThresholdPercent={}%",
                    slaThreshold, alertThresholdPercent);
        }
    }
    
    public static class ReprocessingConfig {
        private String strategy = "KAFKA_REPLAY";
        private Duration snapshotRetention = Duration.ofDays(7);
        private int maxReplayAttempts = 3;
        
        public String getStrategy() {
            return strategy;
        }
        
        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }
        
        public Duration getSnapshotRetention() {
            return snapshotRetention;
        }
        
        public void setSnapshotRetention(Duration snapshotRetention) {
            this.snapshotRetention = snapshotRetention;
        }
        
        public int getMaxReplayAttempts() {
            return maxReplayAttempts;
        }
        
        public void setMaxReplayAttempts(int maxReplayAttempts) {
            this.maxReplayAttempts = maxReplayAttempts;
        }
        
        public void validate() {
            List<String> validStrategies = List.of("KAFKA_REPLAY", "POSTGRES_SNAPSHOT");
            if (!validStrategies.contains(strategy)) {
                throw new IllegalStateException(
                        "Estrategia de reprocesamiento inválida. Valores válidos: " + validStrategies);
            }
            if (maxReplayAttempts <= 0 || maxReplayAttempts > 10) {
                throw new IllegalStateException("maxReplayAttempts debe estar entre 1 y 10");
            }
            log.info("Configuración de reprocesamiento validada: strategy={}, maxReplayAttempts={}",
                    strategy, maxReplayAttempts);
        }
    }
    
    public static class ManualInterventionConfig {
        private boolean enabled = true;
        private List<String> allowedRoles = List.of("ADMIN", "OPERATIONS");
        private boolean requireApproval = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public List<String> getAllowedRoles() {
            return allowedRoles;
        }
        
        public void setAllowedRoles(List<String> allowedRoles) {
            this.allowedRoles = allowedRoles;
        }
        
        public boolean isRequireApproval() {
            return requireApproval;
        }
        
        public void setRequireApproval(boolean requireApproval) {
            this.requireApproval = requireApproval;
        }
        
        public void validate() {
            if (allowedRoles == null || allowedRoles.isEmpty()) {
                throw new IllegalStateException("Debe especificar al menos un rol para intervención manual");
            }
            log.info("Configuración de intervención manual validada: enabled={}, requireApproval={}",
                    enabled, requireApproval);
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java ===
package com.pragma.conciliation.infrastructure.rest;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.infrastructure.config.ReconciliationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reconciliations")
public class ReconciliationController {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationController.class);
    
    private final ReconciliationService reconciliationService;
    private final ReconciliationConfig config;
    
    public ReconciliationController(ReconciliationService reconciliationService,
                                     ReconciliationConfig config) {
        this.reconciliationService = reconciliationService;
        this.config = config;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReconciliationResponse> getById(@PathVariable String id) {
        log.debug("Consultando conciliación por ID: {}", id);
        
        Optional<Reconciliation> reconciliation = reconciliationService.findById(id);
        
        if (reconciliation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Conciliación no encontrada: " + id);
        }
        
        Reconciliation r = reconciliation.get();
        ReconciliationResponse response = mapToResponse(r);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ReconciliationResponse>> getAll(
            @RequestParam(required = false) ReconciliationState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.debug("Consultando conciliaciones: state={}, page={}, size={}", state, page, size);
        
        List<Reconciliation> reconciliations;
        if (state != null) {
            reconciliations = reconciliationService.findByState(state, page, size);
        } else {
            reconciliations = reconciliationService.findAll(page, size);
        }
        
        List<ReconciliationResponse> responses = reconciliations.stream()
                .map(this::mapToResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<ReconciliationResponse>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.debug("Consultando conciliaciones pendientes: page={}, size={}", page, size);
        
        List<Reconciliation> pending = reconciliationService.findPending(page, size);
        
        List<ReconciliationResponse> responses = pending.stream()
                .map(this::mapToResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/stuck")
    public ResponseEntity<List<ReconciliationResponse>> getStuck(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Duration maxPending = config.getMatching().getMaxPendingDuration();
        log.debug("Consultando conciliaciones bloqueadas (mayor a {}): page={}, size={}", 
                maxPending, page, size);
        
        List<Reconciliation> stuck = reconciliationService.findStuck(maxPending, page, size);
        
        List<ReconciliationResponse> responses = stuck.stream()
                .map(this::mapToResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ReconciliationResponse> resolveManually(
            @PathVariable String id,
            @RequestBody ManualResolutionRequest request) {
        
        log.info("Resolución manual solicitada para conciliación {}: resolution={}, processedBy={}",
                id, request.resolution(), request.processedBy());
        
        if (!config.getManualIntervention().isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Intervención manual deshabilitada en la configuración");
        }
        
        Optional<Reconciliation> reconciliation = reconciliationService.findById(id);
        if (reconciliation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Conciliación no encontrada: " + id);
        }
        
        Reconciliation r = reconciliation.get();
        if (!r.getState().requiresManualReview()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "La conciliación no requiere revisión manual. Estado actual: " + r.getState());
        }
        
        Reconciliation resolved = reconciliationService.resolveManually(
                id, request.resolution(), request.processedBy());
        
        return ResponseEntity.ok(mapToResponse(resolved));
    }
    
    @PostMapping("/{id}/retry")
    public ResponseEntity<ReconciliationResponse> retryMatching(
            @PathVariable String id,
            @RequestBody(required = false) RetryRequest request) {
        
        log.info("Reintento de matching solicitado para conciliación: {}", id);
        
        Optional<Reconciliation> reconciliation = reconciliationService.findById(id);
        if (reconciliation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Conciliación no encontrada: " + id);
        }
        
        boolean force = request != null && request.force();
        Reconciliation retried = reconciliationService.retryMatching(id, force);
        
        return ResponseEntity.ok(mapToResponse(retried));
    }
    
    @PostMapping("/reprocess")
    public ResponseEntity<Map<String, Object>> reprocessAll(
            @RequestBody ReprocessRequest request) {
        
        log.info("Reprocesamiento masivo solicitado: from={}, to={}, strategy={}",
                request.from(), request.to(), request.strategy());
        
        int reprocessed = reconciliationService.reprocess(
                request.from(), request.to(), request.strategy());
        
        return ResponseEntity.ok(Map.of(
                "reprocessed", reprocessed,
                "timestamp", Instant.now().toString()
        ));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.debug("Consultando estadísticas de conciliación");
        
        Map<String, Object> stats = reconciliationService.getStatistics();
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Consultando salud del sistema de conciliación");
        
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "matchingWindow", config.getMatching().getWindow().toMinutes() + " minutes",
                "maxPendingDuration", config.getMatching().getMaxPendingDuration().toHours() + " hours",
                "manualInterventionEnabled", config.getManualIntervention().isEnabled()
        );
        
        return ResponseEntity.ok(health);
    }
    
    private ReconciliationResponse mapToResponse(Reconciliation r) {
        return new ReconciliationResponse(
                r.getId(),
                r.getMovementId(),
                r.getEventId(),
                r.getVersion(),
                r.getState().name(),
                r.getReceivedAt().toString(),
                r.getMatchedAt() != null ? r.getMatchedAt().toString() : null,
                r.getCoreBankReference(),
                r.getPaymentGatewayReference(),
                r.getSettlementReference(),
                r.getMovementAmount(),
                r.getMovementCurrency(),
                r.getManualResolution(),
                r.getProcessedBy(),
                r.getStateHistory().stream()
                        .map(t -> Map.of(
                                "from", t.from() != null ? t.from().name() : null,
                                "to", t.to().name(),
                                "reason", t.reason(),
                                "timestamp", t.timestamp().toString()
                        ))
                        .toList()
        );
    }
    
    public record ReconciliationResponse(
            String id,
            String movementId,
            String eventId,
            int version,
            String state,
            String receivedAt,
            String matchedAt,
            String coreBankReference,
            String paymentGatewayReference,
            String settlementReference,
            java.math.BigDecimal amount,
            String currency,
            String manualResolution,
            String processedBy,
            List<Map<String, Object>> stateHistory
    ) {}
    
    public record ManualResolutionRequest(
            String resolution,
            String processedBy
    ) {
        public ManualResolutionRequest {
            if (resolution == null || resolution.isBlank()) {
                throw new IllegalArgumentException("La resolución no puede estar vacía");
            }
            if (processedBy == null || processedBy.isBlank()) {
                throw new IllegalArgumentException("El procesado por no puede estar vacío");
            }
        }
    }
    
    public record RetryRequest(
            boolean force
    ) {}
    
    public record ReprocessRequest(
            Instant from,
            Instant to,
            String strategy
    ) {
        public ReprocessRequest {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Los parámetros from y to son obligatorios");
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("from debe ser anterior a to");
            }
            if (strategy == null || strategy.isBlank()) {
                strategy = "KAFKA_REPLAY";
            }
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/monitoring/ConciliationLagMonitor.java ===
package com.pragma.conciliation.infrastructure.monitoring;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConciliationLagMonitor {

    private static final Logger log = LoggerFactory.getLogger(ConciliationLagMonitor.class);

    private final ReconciliationRepository reconciliationRepository;
    private final MeterRegistry meterRegistry;

    @Value("${conciliation.monitoring.sla-minutes:10}")
    private int slaMinutes;

    @Value("${conciliation.monitoring.alert-threshold-minutes:15}")
    private int alertThresholdMinutes;

    private final AtomicLong currentLagSeconds = new AtomicLong(0);
    private final AtomicReference<Instant> lastAlertTime = new AtomicReference<>(Instant.EPOCH);
    private final AtomicLong pendingCount = new AtomicLong(0);
    private final AtomicLong matchedCount = new AtomicLong(0);
    private final AtomicLong mismatchedCount = new AtomicLong(0);
    private final AtomicLong manualReviewCount = new AtomicLong(0);

    private final Counter lagExceededCounter;
    private final Timer lagCalculationTimer;

    public ConciliationLagMonitor(
            ReconciliationRepository reconciliationRepository,
            MeterRegistry meterRegistry) {
        this.reconciliationRepository = reconciliationRepository;
        this.meterRegistry = meterRegistry;

        this.lagExceededCounter = Counter.builder("conciliation.lag.exceeded")
                .description("Número de veces que el lag de conciliación ha superado el SLA")
                .register(meterRegistry);

        this.lagCalculationTimer = Timer.builder("conciliation.lag.calculation.time")
                .description("Tiempo que tarda el cálculo del lag de conciliación")
                .register(meterRegistry);

        Gauge.builder("conciliation.lag.current.seconds", currentLagSeconds, AtomicLong::get)
                .description("Lag actual de conciliación en segundos")
                .register(meterRegistry);

        Gauge.builder("conciliation.pending.count", pendingCount, AtomicLong::get)
                .description("Número de conciliaciones pendientes")
                .register(meterRegistry);

        Gauge.builder("conciliation.matched.count", matchedCount, AtomicLong::get)
                .description("Número de conciliaciones matched")
                .register(meterRegistry);

        Gauge.builder("conciliation.mismatched.count", mismatchedCount, AtomicLong::get)
                .description("Número de conciliaciones mismatched")
                .register(meterRegistry);

        Gauge.builder("conciliation.manual.review.count", manualReviewCount, AtomicLong::get)
                .description("Número de conciliaciones en revisión manual")
                .register(meterRegistry);
    }

    @Scheduled(fixedRateString = "${conciliation.monitoring.check-interval-ms:60000}")
    public void calculateLagAndAlert() {
        lagCalculationTimer.record(() -> {
            try {
                Instant now = Instant.now();
                Duration slaDuration = Duration.ofMinutes(slaMinutes);

                List<Reconciliation> pendingReconciliations =
                        reconciliationRepository.findByStateIn(List.of(
                                ReconciliationState.PENDING,
                                ReconciliationState.MATCHED
                        ));

                long currentPending = pendingReconciliations.stream()
                        .filter(r -> r.getState() == ReconciliationState.PENDING)
                        .count();
                pendingCount.set(currentPending);

                long currentMatched = pendingReconciliations.stream()
                        .filter(r -> r.getState() == ReconciliationState.MATCHED)
                        .count();
                matchedCount.set(currentMatched);

                List<Reconciliation> mismatchedReconciliations =
                        reconciliationRepository.findByState(ReconciliationState.MISMATCHED);
                mismatchedCount.set(mismatchedReconciliations.size());

                List<Reconciliation> manualReconciliations =
                        reconciliationRepository.findByState(ReconciliationState.MANUAL_REVIEW);
                manualReviewCount.set(manualReconciliations.size());

                if (!pendingReconciliations.isEmpty()) {
                    Instant oldestPending = pendingReconciliations.stream()
                            .map(Reconciliation::getReceivedAt)
                            .min(Instant::compareTo)
                            .orElse(now);

                    Duration currentLag = Duration.between(oldestPending, now);
                    long lagSeconds = currentLag.getSeconds();
                    currentLagSeconds.set(lagSeconds);

                    log.info("Lag de conciliación actual: {} segundos (SLA: {} minutos)",
                            lagSeconds, slaMinutes);

                    if (currentLag.compareTo(slaDuration) > 0) {
                        lagExceededCounter.increment();
                        triggerAlert(currentLag, pendingReconciliations.size());
                    }
                } else {
                    currentLagSeconds.set(0);
                    log.debug("No hay conciliaciones pendientes para calcular lag");
                }

            } catch (Exception e) {
                log.error("Error al calcular el lag de conciliación", e);
            }
        });
    }

    private void triggerAlert(Duration currentLag, int pendingCount) {
        Instant now = Instant.now();
        Duration alertCooldown = Duration.ofMinutes(alertThresholdMinutes);

        Instant lastAlert = lastAlertTime.get();
        if (Duration.between(lastAlert, now).compareTo(alertCooldown) < 0) {
            log.debug("Alerta suprimida por cooldown. Última alerta hace: {} segundos",
                    Duration.between(lastAlert, now).getSeconds());
            return;
        }

        if (lastAlertTime.compareAndSet(lastAlert, now)) {
            log.warn("ALERTA: El lag de conciliación ({}) supera el SLA de {} minutos. " +
                            "Conciliaciones pendientes: {}",
                    currentLag.toMinutes(), slaMinutes, pendingCount);

            meterRegistry.counter("conciliation.alerts.triggered", "reason", "sla_exceeded")
                    .increment();
        }
    }

    public long getCurrentLagSeconds() {
        return currentLagSeconds.get();
    }

    public long getPendingCount() {
        return pendingCount.get();
    }

    public boolean isLagWithinSla() {
        return currentLagSeconds.get() <= (slaMinutes * 60L);
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/messaging/IdempotencyStore.java ===
package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class IdempotencyStore {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyStore.class);

    private final JdbcTemplate jdbcTemplate;
    private final ReconciliationRepository reconciliationRepository;

    public IdempotencyStore(JdbcTemplate jdbcTemplate,
                            ReconciliationRepository reconciliationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reconciliationRepository = reconciliationRepository;
        initializeTable();
    }

    private void initializeTable() {
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS idempotency_keys (
                    event_id VARCHAR(255) NOT NULL,
                    version INTEGER NOT NULL,
                    movement_id VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    processed_at TIMESTAMP,
                    PRIMARY KEY (event_id, version)
                )
                """);
            log.info("Tabla de idempotencia inicializada correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar la tabla de idempotencia", e);
        }
    }

    public boolean tryAcquire(String eventId, int version, String movementId) {
        try {
            jdbcTemplate.update("""
                INSERT INTO idempotency_keys (event_id, version, movement_id, created_at)
                VALUES (?, ?, ?, ?)
                """,
                    eventId,
                    version,
                    movementId,
                    Instant.now()
            );

            log.debug("Adquirido lock de idempotencia para eventId={} version={}",
                    eventId, version);
            return true;

        } catch (DataIntegrityViolationException e) {
            log.debug("Duplicado detectado para eventId={} version={}, verificando estado",
                    eventId, version);

            Optional<IdempotencyRecord> existingRecord = findByEventIdAndVersion(eventId, version);
            if (existingRecord.isPresent() && existingRecord.get().processedAt() != null) {
                log.info("Evento ya procesado: eventId={} version={}", eventId, version);
                return false;
            }

            log.warn("Evento en proceso: eventId={} version={}, rehusando procesamiento",
                    eventId, version);
            return false;
        }
    }

    public Optional<IdempotencyRecord> findByEventIdAndVersion(String eventId, int version) {
        try {
            return jdbcTemplate.query("""
                SELECT event_id, version, movement_id, created_at, processed_at
                FROM idempotency_keys
                WHERE event_id = ? AND version = ?
                """,
                (rs, rowNum) -> new IdempotencyRecord(
                        rs.getString("event_id"),
                        rs.getInt("version"),
                        rs.getString("movement_id"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("processed_at") != null
                                ? rs.getTimestamp("processed_at").toInstant()
                                : null
                ),
                eventId,
                version
            ).stream().findFirst();
        } catch (Exception e) {
            log.error("Error al buscar registro de idempotencia: eventId={} version={}",
                    eventId, version, e);
            return Optional.empty();
        }
    }

    public void markAsProcessed(String eventId, int version) {
        int updated = jdbcTemplate.update("""
            UPDATE idempotency_keys
            SET processed_at = ?
            WHERE event_id = ? AND version = ?
            """,
            Instant.now(),
            eventId,
            version
        );

        if (updated > 0) {
            log.debug("Marcado como procesado: eventId={} version={}", eventId, version);
        } else {
            log.warn("No se encontró registro para marcar como procesado: eventId={} version={}",
                    eventId, version);
        }
    }

    public void cleanupOldRecords(int retentionDays) {
        Instant cutoff = Instant.now().minusSeconds(retentionDays * 24L * 60L * 60L);

        int deleted = jdbcTemplate.update("""
            DELETE FROM idempotency_keys
            WHERE created_at < ? AND processed_at IS NOT NULL
            """,
            cutoff
        );

        log.info("Limpiados {} registros de idempotencia antiguos (mayores a {} días)",
                deleted, retentionDays);
    }

    public record IdempotencyRecord(
            String eventId,
            int version,
            String movementId,
            Instant createdAt,
            Instant processedAt
    ) {
        public boolean isProcessed() {
            return processedAt != null;
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresSnapshotReprocessor.java ===
package com.pragma.conciliation.infrastructure.persistence;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.domain.service.ReprocessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class PostgresSnapshotReprocessor implements ReprocessingStrategy {

    private static final Logger log = LoggerFactory.getLogger(PostgresSnapshotReprocessor.class);

    private final ReconciliationRepository reconciliationRepository;
    private final ReconciliationService reconciliationService;

    @Value("${conciliation.reprocessing.snapshot.batch-size:50}")
    private int batchSize;

    @Value("${conciliation.reprocessing.snapshot.max-retries:3}")
    private int maxRetries;

    @Value("${conciliation.reprocessing.snapshot.states:PENDING,MISMATCHED}")
    private List<String> targetStates;

    public PostgresSnapshotReprocessor(
            ReconciliationRepository reconciliationRepository,
            ReconciliationService reconciliationService) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationService = reconciliationService;
    }

    @Override
    public String getStrategyName() {
        return "POSTGRES_SNAPSHOT";
    }

    @Override
    public ReprocessingResult reprocess(String reconciliationId, int attemptNumber) {
        log.info("Iniciando reprocesamiento por snapshot para reconciliationId={}, intento={}",
                reconciliationId, attemptNumber);

        if (attemptNumber > maxRetries) {
            log.error("Máximo de intentos alcanzado para reconciliationId={}", reconciliationId);
            return ReprocessingResult.failure(
                    reconciliationId,
                    "Máximo de intentos alcanzado: " + maxRetries
            );
        }

        Optional<Reconciliation> reconciliationOpt =
                reconciliationRepository.findById(reconciliationId);

        if (reconciliationOpt.isEmpty()) {
            log.warn("No se encontró reconciliation para reprocesar: {}", reconciliationId);
            return ReprocessingResult.failure(reconciliationId, "Reconciliation no encontrado");
        }

        Reconciliation reconciliation = reconciliationOpt.get();

        if (!canReprocess(reconciliation)) {
            log.warn("Reconciliation no apta para reprocesamiento: {} en estado {}",
                    reconciliationId, reconciliation.getState());
            return ReprocessingResult.failure(
                    reconciliationId,
                    "Estado no apta para reprocesamiento: " + reconciliation.getState()
            );
        }

        try {
            reconciliationService.processReconciliation(reconciliation);
            log.info("Reprocesamiento exitoso para reconciliationId={}", reconciliationId);
            return ReprocessingResult.success(reconciliationId);

        } catch (Exception e) {
            log.error("Error en reprocesamiento para reconciliationId={}", reconciliationId, e);
            reconciliation.markAsMismatched("Error en reprocesamiento: " + e.getMessage());
            reconciliationRepository.save(reconciliation);

            return ReprocessingResult.failure(reconciliationId, e.getMessage());
        }
    }

    @Override
    public ReprocessingResult reprocessBatch(List<String> reconciliationIds) {
        log.info("Iniciando reprocesamiento por lote de {} elementos", reconciliationIds.size());

        int successCount = 0;
        int failureCount = 0;

        for (String reconciliationId : reconciliationIds) {
            ReprocessingResult result = reprocess(reconciliationId, 1);
            if (result.success()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        log.info("Reprocesamiento por lote completado: {} exitosos, {} fallidos",
                successCount, failureCount);

        return new ReprocessingResult(
                true,
                reconciliationIds,
                String.format("Lote: %d exitosos, %d fallidos", successCount, failureCount)
        );
    }

    @Override
    public List<Reconciliation> findReconciliationsToReprocess() {
        List<ReconciliationState> states = targetStates.stream()
                .map(ReconciliationState::valueOf)
                .toList();

        log.debug("Buscando reconciliations en estados: {} para reprocesamiento", states);

        List<Reconciliation> reconciliations = reconciliationRepository.findByStateIn(states);

        Instant cutoffTime = Instant.now().minusSeconds(30 * 60);

        return reconciliations.stream()
                .filter(r -> r.getReceivedAt().isBefore(cutoffTime))
                .filter(this::canReprocess)
                .limit(batchSize)
                .toList();
    }

    private boolean canReprocess(Reconciliation reconciliation) {
        ReconciliationState currentState = reconciliation.getState();

        if (currentState == ReconciliationState.PENDING) {
            return reconciliation.getPendingDuration().getSeconds() > 300;
        }

        if (currentState == ReconciliationState.MISMATCHED) {
            return true;
        }

        return false;
    }

    public void reprocessStaleReconciliations() {
        log.info("Iniciando reprocesamiento de conciliaciones obsoletas");

        List<Reconciliation> staleReconciliations = findReconciliationsToReprocess();

        if (staleReconciliations.isEmpty()) {
            log.info("No hay conciliaciones obsoletas para reprocesar");
            return;
        }

        log.info("Se reprocesarán {} conciliaciones obsoletas", staleReconciliations.size());

        for (Reconciliation reconciliation : staleReconciliations) {
            reprocess(reconciliation.getId(), 1);
        }
    }
}


// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaReplayReprocessor.java ===
package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.application.commands.ReconcileMovementCommand;
import com.pragma.conciliation.application.commands.ReconcileMovementCommandHandler;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.infrastructure.config.ReconciliationConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KafkaReplayReprocessor {
    private static final Logger log = LoggerFactory.getLogger(KafkaReplayReprocessor.class);
    
    private final ReconciliationConfig config;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReconcileMovementCommandHandler commandHandler;
    private final MeterRegistry meterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final Consumer<ReconcileMovementCommand> commandProcessor;
    
    private final Map<String, ProcessingMetadata> processingMetadata = new ConcurrentHashMap<>();
    private final AtomicBoolean replayInProgress = new AtomicBoolean(false);
    private final AtomicLong messagesReplayed = new AtomicLong(0);
    private final AtomicLong messagesFailed = new AtomicLong(0);
    
    private Counter replaySuccessCounter;
    private Counter replayFailureCounter;
    private Timer replayDurationTimer;
    private Timer messageProcessingTimer;
    
    private final String inputTopic;
    private final String retryTopic;
    private final String deadLetterTopic;
    private final int maxRetryAttempts;
    private final Duration replayTimeout;

    public KafkaReplayReprocessor(
            ReconciliationConfig config,
            KafkaTemplate<String, Object> kafkaTemplate,
            ReconcileMovementCommandHandler commandHandler,
            MeterRegistry meterRegistry) {
        this.config = config;
        this.kafkaTemplate = kafkaTemplate;
        this.commandHandler = commandHandler;
        this.meterRegistry = meterRegistry;
        this.circuitBreakerRegistry = buildCircuitBreakerRegistry();
        this.commandProcessor = buildCommandProcessor();
        
        this.inputTopic = config.getKafka().getInputTopic();
        this.retryTopic = config.getKafka().getRetryTopic();
        this.deadLetterTopic = config.getKafka().getDeadLetterTopic();
        this.maxRetryAttempts = config.getKafka().getMaxRetryAttempts();
        this.replayTimeout = config.getKafka().getReplayTimeout();
        
        initializeMetrics();
    }

    private void initializeMetrics() {
        this.replaySuccessCounter = Counter.builder("conciliation.replay.success")
                .description("Número de mensajes reprocesados exitosamente")
                .register(meterRegistry);
        
        this.replayFailureCounter = Counter.builder("conciliation.replay.failure")
                .description("Número de mensajes que fallaron en reprocesamiento")
                .register(meterRegistry);
        
        this.replayDurationTimer = Timer.builder("conciliation.replay.duration")
                .description("Duración total del replay")
                .register(meterRegistry);
        
        this.messageProcessingTimer = Timer.builder("conciliation.replay.message.duration")
                .description("Duración del procesamiento de cada mensaje")
                .register(meterRegistry);
    }

    private CircuitBreakerRegistry buildCircuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
        
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    private Consumer<ReconcileMovementCommand> buildCommandProcessor() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("replay-processor");
        
        return command -> {
            Timer.Sample sample = Timer.start(meterRegistry);
            try {
                Mono.fromCallable(() -> commandHandler.handle(command))
                        .transform(mono -> io.github.resilience4j.reactor.ReactorExtKt.reactor(mono, circuitBreaker))
                        .doOnSuccess(result -> {
                            sample.stop(messageProcessingTimer);
                            recordSuccess(command.getEventId());
                        })
                        .doOnError(error -> {
                            sample.stop(messageProcessingTimer);
                            recordFailure(command.getEventId(), error);
                        })
                        .subscribe();
            } catch (Exception e) {
                sample.stop(messageProcessingTimer);
                recordFailure(command.getEventId(), e);
            }
        };
    }

    public Mono<ReplayResult> replayFromOffset(long offset) {
        return replayFromOffset(offset, inputTopic);
    }

    public Mono<ReplayResult> replayFromOffset(long offset, String topic) {
        if (!replayInProgress.compareAndSet(false, true)) {
            log.warn("Repl，已在 proceso");
            return Mono.just(ReplayResult.alreadyRunning());
        }
        
        log.info("Iniciando replay desde offset {} en topic {}", offset, topic);
        Timer.Sample sample = Timer.start(meterRegistry);
        
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> consumerProps = new HashMap<>();
                consumerProps.put("bootstrap.servers", config.getKafka().getBootstrapServers());
                consumerProps.put("group.id", config.getKafka().getConsumerGroupId() + "-replay");
                consumerProps.put("auto.offset.reset", "earliest");
                consumerProps.put("enable.auto.commit", "false");
                consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                consumerProps.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
                consumerProps.put("spring.json.trusted.packages", "*");
                
                KafkaMessageListenerContainer<String, Object> container = createMessageListenerContainer(consumerProps, topic);
                container.start();
                
                BlockingQueue<ConsumerRecord<String, Object>> records = new LinkedBlockingQueue<>(100);
                container.getContainerProperties().setMessageListener((MessageListener<String, Object>) record -> {
                    if (record.offset() >= offset) {
                        records.offer(record);
                    }
                });
                
                Duration timeout = replayTimeout;
                Instant startTime = Instant.now();
                long processedCount = 0;
                long failedCount = 0;
                
                while (Duration.between(startTime, Instant.now()).compareTo(timeout) < 0) {
                    ConsumerRecord<String, Object> record = records.poll(1, java.util.concurrent.TimeUnit.SECONDS);
                    if (record != null) {
                        try {
                            processRecord(record);
                            processedCount++;
                            messagesReplayed.incrementAndGet();
                        } catch (Exception e) {
                            failedCount++;
                            messagesFailed.incrementAndGet();
                            log.error("Error procesando mensaje en offset {}: {}", record.offset(), e.getMessage());
                            sendToDeadLetter(record, e);
                        }
                    }
                }
                
                container.stop();
                return new ReplayResult(true, processedCount, failedCount, 
                        Duration.between(startTime, Instant.now()).toMillis());
                
            } catch (Exception e) {
                log.error("Error durante replay: {}", e.getMessage(), e);
                return new ReplayResult(false, 0, 0, 0, e.getMessage());
            } finally {
                replayInProgress.set(false);
                sample.stop(replayDurationTimer);
            }
        });
    }

    public Mono<ReplayResult> replayFailedMessages() {
        return replayFromOffset(0, retryTopic);
    }

    public Mono<ReplayResult> replayByTimeRange(Instant from, Instant to) {
        if (!replayInProgress.compareAndSet(false, true)) {
            log.warn("Replay ya en proceso");
            return Mono.just(ReplayResult.alreadyRunning());
        }
        
        log.info("Iniciando replay por rango de tiempo: {} a {}", from, to);
        Timer.Sample sample = Timer.start(meterRegistry);
        
        return Mono.fromCallable(() -> {
            try {
                List<ConsumerRecord<String, Object>> recordsToReplay = new ArrayList<>();
                
                Map<String, Object> consumerProps = buildConsumerProps();
                KafkaMessageListenerContainer<String, Object> container = createMessageListenerContainer(consumerProps, inputTopic);
                
                final List<ConsumerRecord<String, Object>> collectedRecords = Collections.synchronizedList(new ArrayList<>());
                container.getContainerProperties().setMessageListener((MessageListener<String, Object>) record -> {
                    Instant recordTime = Instant.ofEpochMilli(record.timestamp());
                    if (!recordTime.isBefore(from) && !recordTime.isAfter(to)) {
                        collectedRecords.add(record);
                    }
                });
                
                container.start();
                Thread.sleep(replayTimeout.toMillis());
                container.stop();
                
                long processedCount = 0;
                long failedCount = 0;
                
                for (ConsumerRecord<String, Object> record : collectedRecords) {
                    try {
                        processRecord(record);
                        processedCount++;
                        messagesReplayed.incrementAndGet();
                    } catch (Exception e) {
                        failedCount++;
                        messagesFailed.incrementAndGet();
                        log.error("Error reprocesando mensaje: {}", e.getMessage());
                    }
                }
                
                return new ReplayResult(true, processedCount, failedCount, 
                        System.currentTimeMillis());
                
            } catch (Exception e) {
                log.error("Error en replay por tiempo: {}", e.getMessage(), e);
                return new ReplayResult(false, 0, 0, 0, e.getMessage());
            } finally {
                replayInProgress.set(false);
                sample.stop(replayDurationTimer);
            }
        });
    }

    private void processRecord(ConsumerRecord<String, Object> record) {
        Object value = record.value();
        
        if (value instanceof MovementReceivedEvent event) {
            ReconcileMovementCommand command = ReconcileMovementCommand.builder()
                    .eventId(event.getEventId())
                    .version(event.getVersion())
                    .movementId(event.getMovementId())
                    .source(event.getSource())
                    .reference(event.getReference())
                    .amount(event.getAmount())
                    .currency(event.getCurrency())
                    .occurredAt(event.getOccurredAt())
                    .receivedAt(event.getReceivedAt())
                    .correlationId(event.getCorrelationId())
                    .movementType(event.getMovementType())
                    .customerId(event.getCustomerId())
                    .accountId(event.getAccountId())
                    .build();
            
            commandProcessor.accept(command);
            
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventMap = (Map<String, Object>) value;
            ReconcileMovementCommand command = mapToCommand(eventMap);
            commandProcessor.accept(command);
        } else {
            throw new IllegalArgumentException("Tipo de mensaje no soportado: " + 
                    (value != null ? value.getClass().getName() : "null"));
        }
    }

    private ReconcileMovementCommand mapToCommand(Map<String, Object> eventMap) {
        return ReconcileMovementCommand.builder()
                .eventId((String) eventMap.get("eventId"))
                .version((Integer) eventMap.get("version"))
                .movementId((String) eventMap.get("movementId"))
                .source((String) eventMap.get("source"))
                .reference((String) eventMap.get("reference"))
                .amount(new java.math.BigDecimal(eventMap.get("amount").toString()))
                .currency((String) eventMap.get("currency"))
                .occurredAt(Instant.parse((String) eventMap.get("occurredAt")))
                .receivedAt(Instant.parse((String) eventMap.get("receivedAt")))
                .correlationId((String) eventMap.get("correlationId"))
                .customerId((String) eventMap.get("customerId"))
                .accountId((String) eventMap.get("accountId"))
                .build();
    }

    private KafkaMessageListenerContainer<String, Object> createMessageListenerContainer(
            Map<String, Object> consumerProps, String topic) {
        
        org.apache.kafka.clients.consumer.ConsumerFactory<String, Object> consumerFactory = 
                new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(consumerProps);
        
        KafkaMessageListenerContainer<String, Object> container = 
                new KafkaMessageListenerContainer<>(consumerFactory, 
                        new ContainerProperties(topic));
        
        container.setCommonErrorHandler(new CommonErrorHandler() {
            @Override
            public void handle(Exception e, ConsumerRecord<String, Object> record) {
                log.error("Error en listener: {}", e.getMessage());
                sendToDeadLetter(record, e);
            }
            
            @Override
            public void handleBatch(Exception e, ConsumerRecords<String, Object> records, 
                    Consumer<?, ?> consumer, java.util.function.Function<long, Long> seekCallback) {
                log.error("Error en batch: {}", e.getMessage());
                for (ConsumerRecord<String, Object> record : records) {
                    handle(e, record);
                }
            }
            
            @Override
            public boolean isAckAfterHandle() {
                return true;
            }
        });
        
        return container;
    }

    private Map<String, Object> buildConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", config.getKafka().getBootstrapServers());
        props.put("group.id", config.getKafka().getConsumerGroupId() + "-replay-time");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.json.trusted.packages", "*");
        return props;
    }

    private void sendToDeadLetter(ConsumerRecord<String, Object> record, Exception error) {
        try {
            kafkaTemplate.send(deadLetterTopic, record.key(), record.value())
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Error enviando a dead letter topic: {}", ex.getMessage());
                        } else {
                            log.info("Mensaje enviado a dead letter topic, partition: {}, offset: {}",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error fatal enviando a dead letter: {}", e.getMessage());
        }
    }

    public void sendToRetry(ConsumerRecord<String, Object> record, int attempt) {
        if (attempt >= maxRetryAttempts) {
            log.warn("Máximo de intentos alcanzado, enviando a dead letter");
            sendToDeadLetter(record, new RuntimeException("Max retries exceeded"));
            return;
        }
        
        Map<String, Object> headers = new HashMap<>();
        headers.put("retry-attempt", attempt + 1);
        headers.put("original-offset", record.offset());
        headers.put("original-timestamp", record.timestamp());
        
        kafkaTemplate.send(retryTopic, record.key(), record.value())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error enviando a retry topic: {}", ex.getMessage());
                    } else {
                        log.info("Mensaje enviado a retry topic, intento {}", attempt + 1);
                    }
                });
    }

    private void recordSuccess(String eventId) {
        processingMetadata.put(eventId, new ProcessingMetadata(eventId, 
                ProcessingStatus.SUCCESS, Instant.now()));
        replaySuccessCounter.increment();
    }

    private void recordFailure(String eventId, Throwable error) {
        processingMetadata.put(eventId, new ProcessingMetadata(eventId, 
                ProcessingStatus.FAILED, Instant.now(), error.getMessage()));
        replayFailureCounter.increment();
    }

    public Mono<ReplayStatus> getReplayStatus() {
        return Mono.just(new ReplayStatus(
                replayInProgress.get(),
                messagesReplayed.get(),
                messagesFailed.get(),
                new ArrayList<>(processingMetadata.values()),
                Instant.now()
        ));
    }

    public record ReplayResult(
            boolean success,
            long processedCount,
            long failedCount,
            long durationMs
    ) {
        public ReplayResult(boolean success, long processedCount, long failedCount, 
                long durationMs, String errorMessage) {
            this(success, processedCount, failedCount, durationMs);
        }
        
        public static ReplayResult alreadyRunning() {
            return new ReplayResult(false, 0, 0, 0);
        }
    }

    public record ReplayStatus(
            boolean inProgress,
            long totalReplayed,
            long totalFailed,
            List<ProcessingMetadata> metadata,
            Instant queriedAt
    ) {}

    public record ProcessingMetadata(
            String eventId,
            ProcessingStatus status,
            Instant processedAt,
            String errorMessage
    ) {
        public ProcessingMetadata(String eventId, ProcessingStatus status, Instant processedAt) {
            this(eventId, status, processedAt, null);
        }
    }

    public enum ProcessingStatus {
        SUCCESS,
        FAILED,
        PROCESSING
    }
}

// === ARCHIVO: src/test/java/com/pragma/conciliation/domain/ReconciliationServiceTest.java ===
package com.pragma.conciliation.domain;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        service = new ReconciliationService(repository);
    }

    @Nested
    @DisplayName("Escenarios de idempotencia")
    class IdempotencyScenarios {

        @Test
        @DisplayName("Debería rechazar evento duplicado con misma versión")
        void shouldRejectDuplicateEventWithSameVersion() {
            MovementReceivedEvent event = createMovementEvent("MOV-001", "EVT-001", 1);
            Reconciliation existing = Reconciliation.fromEvent(event);
            existing.markAsMatched("CORE-REF", "GW-REF", "SETT-REF");
            
            when(repository.findByMovementIdAndVersion("MOV-001", 1))
                    .thenReturn(Optional.of(existing));

            Optional<Reconciliation> result = service.processMovement(event);

            assertThat(result).isEmpty();
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Debería aceptar evento con versión mayor al existente")
        void shouldAcceptEventWithHigherVersion() {
            MovementReceivedEvent existingEvent = createMovementEvent("MOV-001", "EVT-001", 1);
            Reconciliation existing = Reconciliation.fromEvent(existingEvent);
            existing.markAsMatched("CORE-REF", "GW-REF", "SETT-REF");
            
            MovementReceivedEvent newEvent = createMovementEvent("MOV-001", "EVT-001", 2);
            
            when(repository.findByMovementIdAndVersion("MOV-001", 1))
                    .thenReturn(Optional.of(existing));
            when(repository.findByMovementIdAndVersion("MOV-001", 2))
                    .thenReturn(Optional.empty());
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<Reconciliation> result = service.processMovement(newEvent);

            assertThat(result).isPresent();
            assertThat(result.get().getVersion()).isEqualTo(2);
            verify(repository).save(any(Reconciliation.class));
        }

        @Test
        @DisplayName("Debería crear nueva conciliación para evento nuevo")
        void shouldCreateNewReconciliationForNewEvent() {
            MovementReceivedEvent event = createMovementEvent("MOV-NEW", "EVT-NEW", 1);
            
            when(repository.findByMovementIdAndVersion("MOV-NEW", 1))
                    .thenReturn(Optional.empty());
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<Reconciliation> result = service.processMovement(event);

            assertThat(result).isPresent();
            assertThat(result.get().getMovementId()).isEqualTo("MOV-NEW");
            assertThat(result.get().getState()).isEqualTo(ReconciliationState.PENDING);
            verify(repository).save(any(Reconciliation.class));
        }
    }

    @Nested
    @DisplayName("Escenarios de reprocesamiento")
    class ReprocessingScenarios {

        @Test
        @DisplayName("Debería reprocesar movimiento en estado PENDING después de timeout")
        void shouldReprocessPendingMovementAfterTimeout() {
            Reconciliation pending = createPendingReconciliation("MOV-TIMEOUT");
            pending.markAsMismatched("Timeout waiting for references");
            
            when(repository.findByMovementId("MOV-TIMEOUT"))
                    .thenReturn(Optional.of(pending));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<Reconciliation> result = service.reprocessMovement("MOV-TIMEOUT");

            assertThat(result).isPresent();
            assertThat(result.get().getState()).isEqualTo(ReconciliationState.PENDING);
        }

        @Test
        @DisplayName("No debería reprocesar movimiento ya conciliado")
        void shouldNotReprocessAlreadyMatchedMovement() {
            Reconciliation matched = createMatchedReconciliation("MOV-MATCHED");
            
            when(repository.findByMovementId("MOV-MATCHED"))
                    .thenReturn(Optional.of(matched));

            Optional<Reconciliation> result = service.reprocessMovement("MOV-MATCHED");

            assertThat(result).isEmpty();
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Debería retornar vacante para movimiento inexistente en reprocesamiento")
        void shouldReturnEmptyForNonExistentMovementOnReprocess() {
            when(repository.findByMovementId("MOV-NONEXISTENT"))
                    .thenReturn(Optional.empty());

            Optional<Reconciliation> result = service.reprocessMovement("MOV-NONEXISTENT");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Transiciones de estado")
    class StateTransitions {

        @Test
        @DisplayName("Debería transitar de PENDING a MATCHED con todas las referencias")
        void shouldTransitionFromPendingToMatched() {
            Reconciliation reconciliation = createPendingReconciliation("MOV-TRANSIT");
            
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Reconciliation result = service.matchMovement(
                    reconciliation.getId(),
                    "CORE-REF-2",
                    "GW-REF-2",
                    "SETT-REF-2"
            );

            assertThat(result.getState()).isEqualTo(ReconciliationState.MATCHED);
            assertThat(result.getCoreBankReference()).isEqualTo("CORE-REF-2");
            assertThat(result.getMatchedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debería transitar a MISMATCHED cuando referencias no coinciden")
        void shouldTransitionToMismatchedOnReferenceMismatch() {
            Reconciliation reconciliation = createPendingReconciliation("MOV-MISMATCH");
            reconciliation.setMovementAmount(new BigDecimal("1000.00"));
            
            when(repository.findById(reconciliation.getId()))
                    .thenReturn(Optional.of(reconciliation));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Reconciliation result = service.mismatchMovement(
                    reconciliation.getId(),
                    "Amount mismatch: expected 500.00, got 1000.00"
            );

            assertThat(result.getState()).isEqualTo(ReconciliationState.MISMATCHED);
        }

        @Test
        @DisplayName("No debería permitir transición de MATCHED a PENDING")
        void shouldNotAllowTransitionFromMatchedToPending() {
            Reconciliation matched = createMatchedReconciliation("MOV-INVALID");
            
            when(repository.findById(matched.getId()))
                    .thenReturn(Optional.of(matched));

            assertThatThrownBy(() -> service.reprocessMovement("MOV-INVALID"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot reprocess");
        }
    }

    @Nested
    @DisplayName("Consultas de dominio")
    class DomainQueries {

        @Test
        @DisplayName("Debería listar conciliaciones pendientes mayores al timeout")
        void shouldListPendingReconciliationsExceedingTimeout() {
            Reconciliation oldPending = createPendingReconciliation("MOV-OLD");
            Reconciliation newPending = createPendingReconciliation("MOV-NEW");
            
            when(repository.findByState(ReconciliationState.PENDING))
                    .thenReturn(List.of(oldPending, newPending));

            List<Reconciliation> result = service.findPendingExceedingTimeout(
                    java.time.Duration.ofMinutes(5)
            );

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getMovementId()).isEqualTo("MOV-OLD");
        }

        @Test
        @DisplayName("Debería verificar si movimiento ya fue procesado")
        void shouldCheckIfMovementAlreadyProcessed() {
            when(repository.findByMovementIdAndVersion("MOV-CHECK", 1))
                    .thenReturn(Optional.of(createMatchedReconciliation("MOV-CHECK")));

            boolean result = service.isMovementProcessed("MOV-CHECK", 1);

            assertThat(result).isTrue();
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
                MovementReceivedEvent.MovementType.CREDIT,
                "CUST-001",
                "ACC-001"
        );
    }

    private Reconciliation createPendingReconciliation(String movementId) {
        return new Reconciliation(
                UUID.randomUUID().toString(),
                movementId,
                "EVT-" + movementId,
                1,
                ReconciliationState.PENDING,
                Instant.now().minusSeconds(600),
                null,
                null,
                null,
                null,
                new BigDecimal("1000.00"),
                "USD",
                List.of()
        );
    }

    private Reconciliation createMatchedReconciliation(String movementId) {
        Reconciliation reconciliation = createPendingReconciliation(movementId);
        reconciliation.markAsMatched("CORE-REF", "GW-REF", "SETT-REF");
        return reconciliation;
    }
}

// === ARCHIVO: src/test/java/com/pragma/conciliation/application/ReconcileMovementCommandHandlerTest.java ===
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

// === ARCHIVO: src/test/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumerTest.java ===
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

// === ARCHIVO: pom.xml ===
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.pragma</groupId>
    <artifactId>conciliation</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>conciliation</name>
    <description>Sistema de conciliación bancaria</description>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
            <version>3.4.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
            <version>3.4.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>3.4.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>1.13.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>3.4.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot2</artifactId>
            <version>2.1.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>3.4.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>1.19.8</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <version>1.19.8</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommand.java ===
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/application/commands/ReconcileMovementCommandHandler.java ===
package com.pragma.conciliation.application.commands;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.events.MovementReceivedEvent.MovementType;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.domain.service.ReprocessingStrategy;
import com.pragma.conciliation.domain.service.ReprocessingStrategy.StrategyType;
import com.pragma.conciliation.infrastructure.messaging.IdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReconcileMovementCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ReconcileMovementCommandHandler.class);

    private final ReconciliationRepository reconciliationRepository;
    private final ReconciliationService reconciliationService;
    private final IdempotencyStore idempotencyStore;
    private final Map<StrategyType, ReprocessingStrategy> reprocessingStrategies;

    public ReconcileMovementCommandHandler(
            ReconciliationRepository reconciliationRepository,
            ReconciliationService reconciliationService,
            IdempotencyStore idempotencyStore,
            List<ReprocessingStrategy> strategies) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationService = reconciliationService;
        this.idempotencyStore = idempotencyStore;
        this.reprocessingStrategies = strategies.stream()
            .collect(Collectors.toMap(ReprocessingStrategy::getType, Function.identity()));
    }

    public Mono<Reconciliation> handle(ReconcileMovementCommand command) {
        log.info("Iniciando conciliación para movimiento: eventId={}, version={}, movementId={}",
                command.getEventId(), command.getVersion(), command.getMovementId());

        String idempotencyKey = command.getIdempotencyKey();

        return idempotencyStore.findByEventIdAndVersion(command.getEventId(), command.getVersion())
            .flatMap(record -> {
                if (record != null && !command.isForceReprocessing()) {
                    log.warn("Movimiento ya procesado: idempotencyKey={}", idempotencyKey);
                    return reconciliationRepository.findByEventIdAndVersion(
                        command.getEventId(), command.getVersion()
                    );
                }
                return executeReconciliation(command);
            })
            .switchIfEmpty(Mono.defer(() -> executeReconciliation(command)))
            .doOnSuccess(reconciliation -> {
                if (reconciliation != null) {
                    log.info("Conciliación completada: id={}, state={}, movementId={}",
                        reconciliation.getId(), reconciliation.getState(), reconciliation.getMovementId());
                }
            })
            .doOnError(error ->
                log.error("Error en conciliación: eventId={}, movementId={}, error={}",
                    command.getEventId(), command.getMovementId(), error.getMessage()));
    }

    private Mono<Reconciliation> executeReconciliation(ReconcileMovementCommand command) {
        return createOrUpdateReconciliation(command)
            .flatMap(this::performMatching)
            .flatMap(this::updateReconciliationState)
            .flatMap(this::persistReconciliation)
            .flatMap(this::markAsProcessed);
    }

    private Mono<Reconciliation> createOrUpdateReconciliation(ReconcileMovementCommand command) {
        return reconciliationRepository.findByMovementId(command.getMovementId())
            .flatMap(existing -> {
                if (existing.getVersion() < command.getVersion()) {
                    log.info("Actualizando reconciliación existente: movementId={}, oldVersion={}, newVersion={}",
                        command.getMovementId(), existing.getVersion(), command.getVersion());
                    return updateReconciliation(existing, command);
                }
                log.info("Versión obsoleta ignorada: movementId={}, existingVersion={}, newVersion={}",
                    command.getMovementId(), existing.getVersion(), command.getVersion());
                return Mono.just(existing);
            })
            .switchIfEmpty(Mono.defer(() -> createNewReconciliation(command)));
    }

    private Mono<Reconciliation> createNewReconciliation(ReconcileMovementCommand command) {
        Reconciliation reconciliation = new Reconciliation(
            command.getMovementId(),
            command.getEventId(),
            command.getVersion(),
            ReconciliationState.PENDING,
            command.getReceivedAt()
        );
        log.debug("Creando nueva reconciliación: movementId={}, eventId={}",
            command.getMovementId(), command.getEventId());
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> updateReconciliation(Reconciliation existing, ReconcileMovementCommand command) {
        existing.markAsMismatched("Versión actualizada: " + command.getVersion());
        return reconciliationRepository.save(existing)
            .flatMap(updated -> createNewReconciliation(command));
    }

    private Mono<Reconciliation> performMatching(Reconciliation reconciliation) {
        return reconciliationService.processMovement(
            new MovementReceivedEvent(
                reconciliation.getEventId(),
                reconciliation.getVersion(),
                reconciliation.getMovementId(),
                "CORE_BANK",
                reconciliation.getCoreBankReference() != null ? 
                    reconciliation.getCoreBankReference() : reconciliation.getMovementId(),
                reconciliation.getMovementAmount(),
                reconciliation.getMovementCurrency(),
                Instant.now(),
                Instant.now(),
                reconciliation.getId(),
                MovementType.CREDIT,
                null,
                null
            )
        );
    }

    private Mono<Reconciliation> updateReconciliationState(Reconciliation reconciliation) {
        if (reconciliation.getState() == ReconciliationState.PENDING) {
            Duration pendingDuration = reconciliation.getPendingDuration();
            if (pendingDuration.toMinutes() > 30) {
                log.warn("Movimiento en estado PENDING por más de 30 minutos: movementId={}, duration={}",
                    reconciliation.getMovementId(), pendingDuration.toMinutes());
            }
        }
        return Mono.just(reconciliation);
    }

    private Mono<Reconciliation> persistReconciliation(Reconciliation reconciliation) {
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> markAsProcessed(Reconciliation reconciliation) {
        String idempotencyKey = reconciliation.getIdempotencyKey();
        idempotencyStore.markAsProcessed(reconciliation.getEventId(), reconciliation.getVersion());
        return Mono.just(reconciliation);
    }

    public Flux<Reconciliation> reprocess(StrategyType strategyType, Instant since) {
        ReprocessingStrategy strategy = reprocessingStrategies.get(strategyType);
        if (strategy == null) {
            return Flux.error(
                new IllegalArgumentException("Estrategia de reprocesamiento no encontrada: " + strategyType)
            );
        }

        log.info("Iniciando reprocesamiento con estrategia: {}, desde={}", strategyType, since);

        return strategy.findPendingReconciliations(since, Instant.now())
            .flatMap(reconciliation -> {
                ReconcileMovementCommand command = ReconcileMovementCommand.fromEvent(
                    new MovementReceivedEvent(
                        reconciliation.getEventId(),
                        reconciliation.getVersion(),
                        reconciliation.getMovementId(),
                        "REPROCESS",
                        reconciliation.getCoreBankReference() != null ?
                            reconciliation.getCoreBankReference() : reconciliation.getMovementId(),
                        reconciliation.getMovementAmount(),
                        reconciliation.getMovementCurrency(),
                        Instant.now(),
                        Instant.now(),
                        reconciliation.getId(),
                        MovementType.CREDIT,
                        null,
                        null
                    )
                );
                return handle(command);
            }, 10);
    }
}

// === ARCHIVO: src/test/java/com/pragma/conciliation/domain/ReconciliationServiceTest.java ===
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

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java ===
package com.pragma.conciliation.domain.service;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private final ReconciliationRepository reconciliationRepository;
    private final Duration defaultMatchingWindow;
    private final Duration maxPendingDuration;

    public ReconciliationService(ReconciliationRepository reconciliationRepository,
                                  Duration defaultMatchingWindow,
                                  Duration maxPendingDuration) {
        this.reconciliationRepository = reconciliationRepository;
        this.defaultMatchingWindow = defaultMatchingWindow;
        this.maxPendingDuration = maxPendingDuration;
    }

    public Mono<Reconciliation> processMovement(MovementReceivedEvent event) {
        String idempotencyKey = event.getIdempotencyKey();
        
        return reconciliationRepository.existsByIdempotencyKey(idempotencyKey)
            .flatMap(exists -> {
                if (exists) {
                    log.warn("Evento duplicado detectado: idempotencyKey={}", idempotencyKey);
                    return Mono.empty();
                }
                return createNewReconciliation(event);
            });
    }

    private Mono<Reconciliation> createNewReconciliation(MovementReceivedEvent event) {
        Reconciliation reconciliation = new Reconciliation(
            event.getMovementId(),
            event.getEventId(),
            event.getVersion(),
            ReconciliationState.PENDING,
            event.getReceivedAt()
        );
        
        return reconciliationRepository.save(reconciliation)
            .flatMap(saved -> attemptMatching(saved, event));
    }

    private Mono<Reconciliation> attemptMatching(Reconciliation reconciliation, MovementReceivedEvent event) {
        Instant from = event.getOccurredAt().minus(defaultMatchingWindow);
        Instant to = event.getOccurredAt().plus(defaultMatchingWindow);
        
        return findMatchingReferences(reconciliation.getMovementId(), from, to)
            .flatMap(matching -> {
                if (matching.hasAllReferences()) {
                    return transitionToMatched(reconciliation, matching);
                } else {
                    return Mono.just(reconciliation);
                }
            });
    }

    private Mono<MatchingResult> findMatchingReferences(String movementId, Instant from, Instant to) {
        return Mono.zip(
            findCoreBankReference(movementId, from, to),
            findPaymentGatewayReference(movementId, from, to),
            findSettlementReference(movementId, from, to)
        ).map(tuple -> new MatchingResult(
            tuple.getT1(),
            tuple.getT2(),
            tuple.getT3()
        ));
    }

    private Mono<String> findCoreBankReference(String movementId, Instant from, Instant to) {
        return Mono.just("CORE-" + movementId);
    }

    private Mono<String> findPaymentGatewayReference(String movementId, Instant from, Instant to) {
        return Mono.just("GW-" + movementId);
    }

    private Mono<String> findSettlementReference(String movementId, Instant from, Instant to) {
        return Mono.just("SETT-" + movementId);
    }

    private Mono<Reconciliation> transitionToMatched(Reconciliation reconciliation, MatchingResult matching) {
        reconciliation.markAsMatched(
            matching.coreBankReference(),
            matching.paymentGatewayReference(),
            matching.settlementReference()
        );
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> transitionToMismatched(Reconciliation reconciliation, String reason) {
        reconciliation.markAsMismatched(reason);
        return reconciliationRepository.save(reconciliation);
    }

    public Mono<Reconciliation> resolveManually(String reconciliationId, String resolution, String processedBy) {
        return reconciliationRepository.findById(reconciliationId)
            .flatMap(reconciliation -> {
                reconciliation.markAsManual(resolution, processedBy);
                return reconciliationRepository.save(reconciliation);
            });
    }

    public Mono<List<Reconciliation>> getReconciliationsWithLag() {
        Instant cutoffTime = Instant.now().minus(maxPendingDuration);
        return reconciliationRepository.findByStateAndReceivedAtBefore(ReconciliationState.PENDING, cutoffTime)
            .collectList();
    }

    public Flux<Reconciliation> getAllReconciliations() {
        return reconciliationRepository.findAllByOrderByReceivedAtAsc();
    }

    public Mono<Reconciliation> getReconciliationById(String id) {
        return reconciliationRepository.findById(id);
    }

    public Mono<Long> countByState(ReconciliationState state) {
        return reconciliationRepository.countByState(state);
    }

    private record MatchingResult(
        String coreBankReference,
        String paymentGatewayReference,
        String settlementReference
    ) {
        boolean hasAllReferences() {
            return coreBankReference != null && paymentGatewayReference != null && settlementReference != null;
        }
    }
}


// === ARCHIVO: pom.xml ===
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.0</version>
        <relativePath/>
    </parent>

    <groupId>com.pragma</groupId>
    <artifactId>conciliation</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>conciliation</name>
    <description>Conciliation Service</description>

    <properties>
        <java.version>21</java.version>
        <reactor.kafka.version>1.3.22</reactor.kafka.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <dependency>
            <groupId>org.reactivestreams</groupId>
            <artifactId>reactive-streams</artifactId>
        </dependency>

        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-core</artifactId>
        </dependency>

        <dependency>
            <groupId>io.projectreactor.kafka</groupId>
            <artifactId>reactor-kafka</artifactId>
            <version>${reactor.kafka.version}</version>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot2</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/repository/ReconciliationRepository.java ===
package com.pragma.conciliation.domain.repository;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface ReconciliationRepository {

    Mono<Reconciliation> save(Reconciliation reconciliation);

    Mono<Reconciliation> findById(String id);

    Mono<Reconciliation> findByMovementId(String movementId);

    Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version);

    Mono<Boolean> existsByEventIdAndVersion(String eventId, int version);

    Flux<Reconciliation> findByState(ReconciliationState state);

    Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime);

    Flux<Reconciliation> findAllByOrderByReceivedAtAsc();

    Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration);

    Mono<Long> countByState(ReconciliationState state);

    Mono<Reconciliation> update(Reconciliation reconciliation);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByIdempotencyKey(String idempotencyKey);
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/persistence/PostgresReconciliationRepository.java ===
package com.pragma.conciliation.infrastructure.persistence;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostgresReconciliationRepository implements ReconciliationRepository {

    private static final Logger log = LoggerFactory.getLogger(PostgresReconciliationRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Mono<Reconciliation> save(Reconciliation reconciliation) {
        return Mono.fromCallable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = toEntity(reconciliation);
                entityManager.persist(entity);
                entityManager.getTransaction().commit();
                log.debug("Entidad persistida: id={}", entity.getId());
                return toDomain(entity);
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                log.error("Error al persistir conciliación: {}", e.getMessage(), e);
                throw new RuntimeException("Error al guardar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Reconciliation> findById(String id) {
        return Mono.fromCallable(() -> {
            ReconciliationEntity entity = entityManager.find(ReconciliationEntity.class, id);
            return entity != null ? toDomain(entity) : null;
        });
    }

    @Override
    public Mono<Reconciliation> findByMovementId(String movementId) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.movementId = :movementId",
                    ReconciliationEntity.class);
            query.setParameter("movementId", movementId);
            query.setMaxResults(1);
            List<ReconciliationEntity> results = query.getResultList();
            return results.isEmpty() ? null : toDomain(results.get(0));
        });
    }

    @Override
    public Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    ReconciliationEntity.class);
            query.setParameter("eventId", eventId);
            query.setParameter("version", version);
            query.setMaxResults(1);
            List<ReconciliationEntity> results = query.getResultList();
            return results.isEmpty() ? null : toDomain(results.get(0));
        });
    }

    @Override
    public Mono<Boolean> existsByEventIdAndVersion(String eventId, int version) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    Long.class);
            query.setParameter("eventId", eventId);
            query.setParameter("version", version);
            return query.getSingleResult() > 0;
        });
    }

    @Override
    public Flux<Reconciliation> findByState(ReconciliationState state) {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state",
                    ReconciliationEntity.class);
            query.setParameter("state", state.name());
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime) {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state AND r.receivedAt < :cutoffTime",
                    ReconciliationEntity.class);
            query.setParameter("state", state.name());
            query.setParameter("cutoffTime", cutoffTime);
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Flux<Reconciliation> findAllByOrderByReceivedAtAsc() {
        return Flux.fromIterable(() -> {
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r ORDER BY r.receivedAt ASC",
                    ReconciliationEntity.class);
            return query.getResultList().iterator();
        }).map(this::toDomain);
    }

    @Override
    public Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration) {
        return Mono.fromCallable(() -> {
            Instant cutoffTime = Instant.now().minus(maxPendingDuration);
            var query = entityManager.createQuery(
                    "SELECT r FROM ReconciliationEntity r WHERE r.state = :state AND r.receivedAt < :cutoffTime",
                    ReconciliationEntity.class);
            query.setParameter("state", ReconciliationState.PENDING.name());
            query.setParameter("cutoffTime", cutoffTime);
            return query.getResultList().stream()
                    .map(this::toDomain)
                    .toList();
        });
    }

    @Override
    public Mono<Long> countByState(ReconciliationState state) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.state = :state",
                    Long.class);
            query.setParameter("state", state.name());
            return query.getSingleResult();
        });
    }

    @Override
    public Mono<Reconciliation> update(Reconciliation reconciliation) {
        return Mono.fromCallable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = entityManager.find(
                        ReconciliationEntity.class, reconciliation.getId());
                if (entity == null) {
                    throw new RuntimeException("Conciliación no encontrada: " + reconciliation.getId());
                }
                updateEntityFromDomain(entity, reconciliation);
                entityManager.getTransaction().commit();
                log.debug("Entidad actualizada: id={}", entity.getId());
                return toDomain(entity);
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                log.error("Error al actualizar conciliación: {}", e.getMessage(), e);
                throw new RuntimeException("Error al actualizar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> {
            entityManager.getTransaction().begin();
            try {
                ReconciliationEntity entity = entityManager.find(ReconciliationEntity.class, id);
                if (entity != null) {
                    entityManager.remove(entity);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw new RuntimeException("Error al eliminar conciliación", e);
            }
        });
    }

    @Override
    public Mono<Boolean> existsByIdempotencyKey(String idempotencyKey) {
        return Mono.fromCallable(() -> {
            var query = entityManager.createQuery(
                    "SELECT COUNT(r) FROM ReconciliationEntity r WHERE r.eventId = :eventId AND r.version = :version",
                    Long.class);
            String[] parts = idempotencyKey.split("-");
            if (parts.length >= 2) {
                query.setParameter("eventId", parts[0]);
                query.setParameter("version", Integer.parseInt(parts[parts.length - 1]));
                return query.getSingleResult() > 0;
            }
            return false;
        });
    }

    private ReconciliationEntity toEntity(Reconciliation reconciliation) {
        ReconciliationEntity entity = new ReconciliationEntity();
        entity.setId(reconciliation.getId());
        entity.setMovementId(reconciliation.getMovementId());
        entity.setEventId(reconciliation.getEventId());
        entity.setVersion(reconciliation.getVersion());
        entity.setState(reconciliation.getState().name());
        entity.setReceivedAt(reconciliation.getReceivedAt());
        entity.setMatchedAt(reconciliation.getMatchedAt());
        entity.setCoreBankReference(reconciliation.getCoreBankReference());
        entity.setPaymentGatewayReference(reconciliation.getPaymentGatewayReference());
        entity.setSettlementReference(reconciliation.getSettlementReference());
        entity.setMovementAmount(reconciliation.getMovementAmount());
        entity.setMovementCurrency(reconciliation.getMovementCurrency());
        entity.setManualResolution(reconciliation.getManualResolution());
        entity.setProcessedBy(reconciliation.getProcessedBy());
        return entity;
    }

    private void updateEntityFromDomain(ReconciliationEntity entity, Reconciliation reconciliation) {
        entity.setState(reconciliation.getState().name());
        entity.setMatchedAt(reconciliation.getMatchedAt());
        entity.setCoreBankReference(reconciliation.getCoreBankReference());
        entity.setPaymentGatewayReference(reconciliation.getPaymentGatewayReference());
        entity.setSettlementReference(reconciliation.getSettlementReference());
        entity.setManualResolution(reconciliation.getManualResolution());
        entity.setProcessedBy(reconciliation.getProcessedBy());
    }

    private Reconciliation toDomain(ReconciliationEntity entity) {
        return new Reconciliation(
                entity.getId(),
                entity.getMovementId(),
                entity.getEventId(),
                entity.getVersion(),
                ReconciliationState.valueOf(entity.getState()),
                entity.getReceivedAt(),
                entity.getMatchedAt(),
                entity.getCoreBankReference(),
                entity.getPaymentGatewayReference(),
                entity.getSettlementReference(),
                entity.getMovementAmount(),
                entity.getMovementCurrency(),
                entity.getManualResolution(),
                entity.getProcessedBy()
        );
    }

    @Entity
    @Table(name = "reconciliations")
    private static class ReconciliationEntity {
        private String id;
        private String movementId;
        private String eventId;
        private int version;
        private String state;
        private Instant receivedAt;
        private Instant matchedAt;
        private String coreBankReference;
        private String paymentGatewayReference;
        private String settlementReference;
        private BigDecimal movementAmount;
        private String movementCurrency;
        private String manualResolution;
        private String processedBy;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getMovementId() { return movementId; }
        public void setMovementId(String movementId) { this.movementId = movementId; }
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public Instant getReceivedAt() { return receivedAt; }
        public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
        public Instant getMatchedAt() { return matchedAt; }
        public void setMatchedAt(Instant matchedAt) { this.matchedAt = matchedAt; }
        public String getCoreBankReference() { return coreBankReference; }
        public void setCoreBankReference(String coreBankReference) { this.coreBankReference = coreBankReference; }
        public String getPaymentGatewayReference() { return paymentGatewayReference; }
        public void setPaymentGatewayReference(String paymentGatewayReference) { this.paymentGatewayReference = paymentGatewayReference; }
        public String getSettlementReference() { return settlementReference; }
        public void setSettlementReference(String settlementReference) { this.settlementReference = settlementReference; }
        public BigDecimal getMovementAmount() { return movementAmount; }
        public void setMovementAmount(BigDecimal movementAmount) { this.movementAmount = movementAmount; }
        public String getMovementCurrency() { return movementCurrency; }
        public void setMovementCurrency(String movementCurrency) { this.movementCurrency = movementCurrency; }
        public String getManualResolution() { return manualResolution; }
        public void setManualResolution(String manualResolution) { this.manualResolution = manualResolution; }
        public String getProcessedBy() { return processedBy; }
        public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaReplayReprocessor.java ===
package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.application.commands.ReconcileMovementCommand;
import com.pragma.conciliation.application.commands.ReconcileMovementCommandHandler;
import com.pragma.conciliation.infrastructure.config.ReconciliationConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class KafkaReplayReprocessor {
    private static final Logger log = LoggerFactory.getLogger(KafkaReplayReprocessor.class);
    
    private final ReconciliationConfig config;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReconcileMovementCommandHandler commandHandler;
    private final MeterRegistry meterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final Consumer<ReconcileMovementCommand> commandProcessor;
    
    private final Map<String, ProcessingMetadata> processingMetadata = new ConcurrentHashMap<>();
    private final AtomicBoolean replayInProgress = new AtomicBoolean(false);
    private final AtomicLong messagesReplayed = new AtomicLong(0);
    private final AtomicLong messagesFailed = new AtomicLong(0);
    
    private Counter replaySuccessCounter;
    private Counter replayFailureCounter;
    private Timer replayDurationTimer;
    private Timer messageProcessingTimer;
    
    private final String inputTopic;
    private final String retryTopic;
    private final String deadLetterTopic;
    private final int maxRetryAttempts;
    private final Duration replayTimeout;

    public KafkaReplayReprocessor(
            ReconciliationConfig config,
            KafkaTemplate<String, Object> kafkaTemplate,
            ReconcileMovementCommandHandler commandHandler,
            MeterRegistry meterRegistry) {
        this.config = config;
        this.kafkaTemplate = kafkaTemplate;
        this.commandHandler = commandHandler;
        this.meterRegistry = meterRegistry;
        this.circuitBreakerRegistry = buildCircuitBreakerRegistry();
        this.commandProcessor = buildCommandProcessor();
        
        this.inputTopic = config.getKafka().getInputTopic();
        this.retryTopic = config.getKafka().getRetryTopic();
        this.deadLetterTopic = config.getKafka().getDeadLetterTopic();
        this.maxRetryAttempts = config.getKafka().getMaxRetryAttempts();
        this.replayTimeout = config.getKafka().getReplayTimeout();
        
        initializeMetrics();
    }

    private void initializeMetrics() {
        this.replaySuccessCounter = Counter.builder("conciliation.replay.success")
                .description("Número de mensajes reprocesados exitosamente")
                .register(meterRegistry);
        
        this.replayFailureCounter = Counter.builder("conciliation.replay.failure")
                .description("Número de mensajes que fallaron en reprocesamiento")
                .register(meterRegistry);
        
        this.replayDurationTimer = Timer.builder("conciliation.replay.duration")
                .description("Duración total del replay")
                .register(meterRegistry);
        
        this.messageProcessingTimer = Timer.builder("conciliation.replay.message.duration")
                .description("Duración del procesamiento de cada mensaje")
                .register(meterRegistry);
    }

    private CircuitBreakerRegistry buildCircuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
        
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    private Consumer<ReconcileMovementCommand> buildCommandProcessor() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("replay-processor");
        
        return command -> {
            Timer.Sample sample = Timer.start(meterRegistry);
            try {
                Mono.fromCallable(() -> commandHandler.handle(command))
                        .doOnSuccess(result -> {
                            sample.stop(messageProcessingTimer);
                            recordSuccess(command.getEventId());
                        })
                        .doOnError(error -> {
                            sample.stop(messageProcessingTimer);
                            recordFailure(command.getEventId(), error);
                        })
                        .subscribe();
            } catch (Exception e) {
                sample.stop(messageProcessingTimer);
                recordFailure(command.getEventId(), e);
            }
        };
    }

    public Mono<ReplayResult> replayFromOffset(long offset) {
        return replayFromOffset(offset, inputTopic);
    }

    public Mono<ReplayResult> replayFromOffset(long offset, String topic) {
        if (!replayInProgress.compareAndSet(false, true)) {
            log.warn("Replay ya en proceso");
            return Mono.just(ReplayResult.alreadyRunning());
        }
        
        log.info("Iniciando replay desde offset {} en topic {}", offset, topic);
        Timer.Sample sample = Timer.start(meterRegistry);
        
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> consumerProps = new HashMap<>();
                consumerProps.put("bootstrap.servers", config.getKafka().getBootstrapServers());
                consumerProps.put("group.id", config.getKafka().getConsumerGroupId() + "-replay");
                consumerProps.put("auto.offset.reset", "earliest");
                consumerProps.put("enable.auto.commit", "false");
                consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                consumerProps.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
                consumerProps.put("spring.json.trusted.packages", "*");
                
                KafkaMessageListenerContainer<String, Object> container = createMessageListenerContainer(consumerProps, topic);
                container.start();
                
                BlockingQueue<ConsumerRecord<String, Object>> records = new LinkedBlockingQueue<>(100);
                container.getContainerProperties().setMessageListener((MessageListener<String, Object>) record -> {
                    if (record.offset() >= offset) {
                        records.offer(record);
                    }
                });
                
                Duration timeout = replayTimeout;
                Instant startTime = Instant.now();
                long processedCount = 0;
                long failedCount = 0;
                
                while (Duration.between(startTime, Instant.now()).compareTo(timeout) < 0) {
                    ConsumerRecord<String, Object> record = records.poll(1, java.util.concurrent.TimeUnit.SECONDS);
                    if (record != null) {
                        try {
                            processRecord(record);
                            processedCount++;
                            messagesReplayed.incrementAndGet();
                        } catch (Exception e) {
                            failedCount++;
                            messagesFailed.incrementAndGet();
                            log.error("Error procesando mensaje en offset {}: {}", record.offset(), e.getMessage());
                            sendToDeadLetter(record, e);
                        }
                    }
                }
                
                container.stop();
                return new ReplayResult(true, processedCount, failedCount, 
                        Duration.between(startTime, Instant.now()).toMillis());
                
            } catch (Exception e) {
                log.error("Error durante replay: {}", e.getMessage(), e);
                return new ReplayResult(false, 0, 0, 0, e.getMessage());
            } finally {
                replayInProgress.set(false);
                sample.stop(replayDurationTimer);
            }
        });
    }

    public Mono<ReplayResult> replayFailedMessages() {
        return replayFromOffset(0, retryTopic);
    }

    public Mono<ReplayResult> replayByTimeRange(Instant from, Instant to) {
        if (!replayInProgress.compareAndSet(false, true)) {
            log.warn("Replay ya en proceso");
            return Mono.just(ReplayResult.alreadyRunning());
        }
        
        log.info("Iniciando replay por rango de tiempo: {} a {}", from, to);
        Timer.Sample sample = Timer.start(meterRegistry);
        
        return Mono.fromCallable(() -> {
            try {
                List<ConsumerRecord<String, Object>> recordsToReplay = new ArrayList<>();
                
                Map<String, Object> consumerProps = buildConsumerProps();
                KafkaMessageListenerContainer<String, Object> container = createMessageListenerContainer(consumerProps, inputTopic);
                
                final List<ConsumerRecord<String, Object>> collectedRecords = Collections.synchronizedList(new ArrayList<>());
                container.getContainerProperties().setMessageListener((MessageListener<String, Object>) record -> {
                    Instant recordTime = Instant.ofEpochMilli(record.timestamp());
                    if (!recordTime.isBefore(from) && !recordTime.isAfter(to)) {
                        collectedRecords.add(record);
                    }
                });
                
                container.start();
                Thread.sleep(replayTimeout.toMillis());
                container.stop();
                
                long processedCount = 0;
                long failedCount = 0;
                
                for (ConsumerRecord<String, Object> record : collectedRecords) {
                    try {
                        processRecord(record);
                        processedCount++;
                        messagesReplayed.incrementAndGet();
                    } catch (Exception e) {
                        failedCount++;
                        messagesFailed.incrementAndGet();
                        log.error("Error reprocesando mensaje: {}", e.getMessage());
                    }
                }
                
                return new ReplayResult(true, processedCount, failedCount, 
                        System.currentTimeMillis());
                
            } catch (Exception e) {
                log.error("Error en replay por tiempo: {}", e.getMessage(), e);
                return new ReplayResult(false, 0, 0, 0, e.getMessage());
            } finally {
                replayInProgress.set(false);
                sample.stop(replayDurationTimer);
            }
        });
    }

    private void processRecord(ConsumerRecord<String, Object> record) {
        Object value = record.value();
        
        if (value instanceof MovementReceivedEvent event) {
            ReconcileMovementCommand command = ReconcileMovementCommand.builder()
                    .eventId(event.getEventId())
                    .version(event.getVersion())
                    .movementId(event.getMovementId())
                    .source(event.getSource())
                    .reference(event.getReference())
                    .amount(event.getAmount())
                    .currency(event.getCurrency())
                    .occurredAt(event.getOccurredAt())
                    .receivedAt(event.getReceivedAt())
                    .correlationId(event.getCorrelationId())
                    .movementType(event.getMovementType())
                    .customerId(event.getCustomerId())
                    .accountId(event.getAccountId())
                    .build();
            
            commandProcessor.accept(command);
            
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventMap = (Map<String, Object>) value;
            ReconcileMovementCommand command = mapToCommand(eventMap);
            commandProcessor.accept(command);
        } else {
            throw new IllegalArgumentException("Tipo de mensaje no soportado: " + 
                    (value != null ? value.getClass().getName() : "null"));
        }
    }

    private ReconcileMovementCommand mapToCommand(Map<String, Object> eventMap) {
        return ReconcileMovementCommand.builder()
                .eventId((String) eventMap.get("eventId"))
                .version((Integer) eventMap.get("version"))
                .movementId((String) eventMap.get("movementId"))
                .source((String) eventMap.get("source"))
                .reference((String) eventMap.get("reference"))
                .amount(new BigDecimal(eventMap.get("amount").toString()))
                .currency((String) eventMap.get("currency"))
                .occurredAt(Instant.parse((String) eventMap.get("occurredAt")))
                .receivedAt(Instant.parse((String) eventMap.get("receivedAt")))
                .correlationId((String) eventMap.get("correlationId"))
                .customerId((String) eventMap.get("customerId"))
                .accountId((String) eventMap.get("accountId"))
                .build();
    }

    private KafkaMessageListenerContainer<String, Object> createMessageListenerContainer(
            Map<String, Object> consumerProps, String topic) {
        
        org.apache.kafka.clients.consumer.ConsumerFactory<String, Object> consumerFactory = 
                new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(consumerProps);
        
        KafkaMessageListenerContainer<String, Object> container = 
                new KafkaMessageListenerContainer<>(consumerFactory, 
                        new ContainerProperties(topic));
        
        container.setCommonErrorHandler(new CommonErrorHandler() {
            @Override
            public void handle(Exception e, ConsumerRecord<String, Object> record) {
                log.error("Error en listener: {}", e.getMessage());
                sendToDeadLetter(record, e);
            }
            
            @Override
            public void handleBatch(Exception e, ConsumerRecords<String, Object> records, 
                    Consumer<?, ?> consumer, java.util.function.Function<Long, Long> seekCallback) {
                log.error("Error en batch: {}", e.getMessage());
                for (ConsumerRecord<String, Object> record : records) {
                    handle(e, record);
                }
            }
            
            @Override
            public boolean isAckAfterHandle() {
                return true;
            }
        });
        
        return container;
    }

    private Map<String, Object> buildConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", config.getKafka().getBootstrapServers());
        props.put("group.id", config.getKafka().getConsumerGroupId() + "-replay-time");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.json.trusted.packages", "*");
        return props;
    }

    private void sendToDeadLetter(ConsumerRecord<String, Object> record, Exception error) {
        try {
            kafkaTemplate.send(deadLetterTopic, record.key(), record.value())
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Error enviando a dead letter topic: {}", ex.getMessage());
                        } else {
                            log.info("Mensaje enviado a dead letter topic, partition: {}, offset: {}",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error fatal enviando a dead letter: {}", e.getMessage());
        }
    }

    public void sendToRetry(ConsumerRecord<String, Object> record, int attempt) {
        if (attempt >= maxRetryAttempts) {
            log.warn("Máximo de intentos alcanzado, enviando a dead letter");
            sendToDeadLetter(record, new RuntimeException("Max retries exceeded"));
            return;
        }
        
        Map<String, Object> headers = new HashMap<>();
        headers.put("retry-attempt", attempt + 1);
        headers.put("original-offset", record.offset());
        headers.put("original-timestamp", record.timestamp());
        
        kafkaTemplate.send(retryTopic, record.key(), record.value())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error enviando a retry topic: {}", ex.getMessage());
                    } else {
                        log.info("Mensaje enviado a retry topic, intento {}", attempt + 1);
                    }
                });
    }

    private void recordSuccess(String eventId) {
        processingMetadata.put(eventId, new ProcessingMetadata(eventId, 
                ProcessingStatus.SUCCESS, Instant.now()));
        replaySuccessCounter.increment();
    }

    private void recordFailure(String eventId, Throwable error) {
        processingMetadata.put(eventId, new ProcessingMetadata(eventId, 
                ProcessingStatus.FAILED, Instant.now(), error.getMessage()));
        replayFailureCounter.increment();
    }

    public Mono<ReplayStatus> getReplayStatus() {
        return Mono.just(new ReplayStatus(
                replayInProgress.get(),
                messagesReplayed.get(),
                messagesFailed.get(),
                new ArrayList<>(processingMetadata.values()),
                Instant.now()
        ));
    }

    public record ReplayResult(
            boolean success,
            long processedCount,
            long failedCount,
            long durationMs
    ) {
        public ReplayResult(boolean success, long processedCount, long failedCount, 
                long durationMs, String errorMessage) {
            this(success, processedCount, failedCount, durationMs);
        }
        
        public static ReplayResult alreadyRunning() {
            return new ReplayResult(false, 0, 0, 0);
        }
    }

    public record ReplayStatus(
            boolean inProgress,
            long totalReplayed,
            long totalFailed,
            List<ProcessingMetadata> metadata,
            Instant queriedAt
    ) {}

    public record ProcessingMetadata(
            String eventId,
            ProcessingStatus status,
            Instant processedAt,
            String errorMessage
    ) {
        public ProcessingMetadata(String eventId, ProcessingStatus status, Instant processedAt) {
            this(eventId, status, processedAt, null);
        }
    }

    public enum ProcessingStatus {
        SUCCESS,
        FAILED,
        PROCESSING
    }
}


// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/messaging/KafkaMovementConsumer.java ===
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


// === ARCHIVO: pom.xml ===
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.0</version>
        <relativePath/>
    </parent>

    <groupId>com.pragma</groupId>
    <artifactId>conciliation</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>conciliation</name>
    <description>Sistema de Conciliación de Movimientos</description>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
            <version>3.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>1.13.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot2</artifactId>
            <version>2.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.16</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>3.4.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>1.19.8</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <version>1.19.8</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/service/ReconciliationService.java ===
package com.pragma.conciliation.domain.service;

import com.pragma.conciliation.domain.events.MovementReceivedEvent;
import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReconciliationService {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);
    
    private final ReconciliationRepository reconciliationRepository;
    private final Duration defaultMatchingWindow;
    private final Duration maxPendingDuration;

    public ReconciliationService(ReconciliationRepository reconciliationRepository,
                                  com.pragma.conciliation.infrastructure.config.ReconciliationConfig config) {
        this.reconciliationRepository = reconciliationRepository;
        this.defaultMatchingWindow = config.getMatching().getWindow();
        this.maxPendingDuration = config.getMatching().getMaxPendingDuration();
    }

    public Mono<Reconciliation> processMovement(MovementReceivedEvent event) {
        log.debug("Procesando movimiento: eventId={}, movementId={}", event.getEventId(), event.getMovementId());
        return reconciliationRepository.findByMovementId(event.getMovementId())
                .flatMap(existing -> createNewReconciliation(event))
                .switchIfEmpty(Mono.defer(() -> createNewReconciliation(event)))
                .flatMap(this::attemptMatching);
    }

    private Mono<Reconciliation> createNewReconciliation(MovementReceivedEvent event) {
        Reconciliation reconciliation = new Reconciliation(
                event.getMovementId(),
                event.getEventId(),
                event.getVersion(),
                event.getAmount(),
                event.getCurrency(),
                Instant.now()
        );
        return reconciliationRepository.save(reconciliation);
    }

    private Mono<Reconciliation> attemptMatching(Reconciliation reconciliation) {
        Instant from = reconciliation.getReceivedAt().minus(defaultMatchingWindow);
        Instant to = reconciliation.getReceivedAt().plus(defaultMatchingWindow);

        return findMatchingReferences(reconciliation)
                .flatMap(result -> {
                    if (result.hasAllReferences()) {
                        return transitionToMatched(reconciliation, result);
                    } else {
                        return transitionToMismatched(reconciliation, "No se encontraron todas las referencias");
                    }
                });
    }

    private Mono<MatchingResult> findMatchingReferences(Reconciliation reconciliation) {
        Instant from = reconciliation.getReceivedAt().minus(defaultMatchingWindow);
        Instant to = reconciliation.getReceivedAt().plus(defaultMatchingWindow);

        return Mono.zip(
                findCoreBankReference(reconciliation.getMovementId(), from, to),
                findPaymentGatewayReference(reconciliation.getMovementId(), from, to),
                findSettlementReference(reconciliation.getMovementId(), from, to)
        ).map(tuple -> new MatchingResult(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3(),
                tuple.getT1() != null && tuple.getT2() != null && tuple.getT3() != null
        ));
    }

    private Mono<String> findCoreBankReference(String movementId, Instant from, Instant to) {
        return Mono.just("CORE-" + movementId);
    }

    private Mono<String> findPaymentGatewayReference(String movementId, Instant from, Instant to) {
        return Mono.just("PGW-" + movementId);
    }

    private Mono<String> findSettlementReference(String movementId, Instant from, Instant to) {
        return Mono.just("STL-" + movementId);
    }

    private Mono<Reconciliation> transitionToMatched(Reconciliation reconciliation,
                                                       MatchingResult result) {
        reconciliation.markAsMatched(result.coreBankReference(),
                result.paymentGatewayReference(),
                result.settlementReference());
        return reconciliationRepository.update(reconciliation);
    }

    private Mono<Reconciliation> transitionToMismatched(Reconciliation reconciliation, String reason) {
        reconciliation.markAsMismatched(reason);
        return reconciliationRepository.update(reconciliation);
    }

    public Mono<Reconciliation> resolveManually(String reconciliationId, String resolution, String processedBy) {
        return reconciliationRepository.findById(reconciliationId)
                .flatMap(reconciliation -> {
                    reconciliation.markAsManual(resolution, processedBy);
                    return reconciliationRepository.update(reconciliation);
                });
    }

    public Mono<List<Reconciliation>> getReconciliationsWithLag() {
        return reconciliationRepository.findPendingWithLagExceeding(maxPendingDuration)
                .collectList();
    }

    public Flux<Reconciliation> getAllReconciliations() {
        return reconciliationRepository.findAllByOrderByReceivedAtAsc();
    }

    public Mono<Reconciliation> getReconciliationById(String id) {
        return reconciliationRepository.findById(id);
    }

    public Mono<Long> countByState(ReconciliationState state) {
        return reconciliationRepository.countByState(state);
    }

    public Mono<Reconciliation> findById(String id) {
        return reconciliationRepository.findById(id);
    }

    public Flux<Reconciliation> findByState(ReconciliationState state) {
        return reconciliationRepository.findByState(state);
    }

    public Mono<List<Reconciliation>> findPending(int page, int size) {
        return reconciliationRepository.findByState(ReconciliationState.PENDING)
                .take(size)
                .collectList();
    }

    public Mono<List<Reconciliation>> findStuck(Duration maxPending, int page, int size) {
        Instant cutoff = Instant.now().minus(maxPending);
        return reconciliationRepository.findByStateAndReceivedAtBefore(ReconciliationState.PENDING, cutoff)
                .take(size)
                .collectList();
    }

    public Mono<Reconciliation> retryMatching(String id, boolean force) {
        return reconciliationRepository.findById(id)
                .flatMap(reconciliation -> {
                    reconciliation.markAsMismatched("Reintento de matching");
                    return attemptMatching(reconciliation);
                });
    }

    public Mono<Integer> reprocess(Instant from, Instant to, String strategy) {
        return reconciliationRepository.findByStateAndReceivedAtBefore(ReconciliationState.PENDING, to)
                .filter(r -> r.getReceivedAt().isAfter(from))
                .flatMap(reconciliation -> attemptMatching(reconciliation).then(Mono.just(1)))
                .reduce(0, Integer::sum);
    }

    public Mono<Map<String, Object>> getStatistics() {
        return Mono.zip(
                countByState(ReconciliationState.PENDING),
                countByState(ReconciliationState.MATCHED),
                countByState(ReconciliationState.MISMATCHED),
                countByState(ReconciliationState.MANUAL_REVIEW)
        ).map(tuple -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("pending", tuple.getT1());
            stats.put("matched", tuple.getT2());
            stats.put("mismatched", tuple.getT3());
            stats.put("manualReview", tuple.getT4());
            stats.put("timestamp", Instant.now().toString());
            return stats;
        });
    }

    public record MatchingResult(
            String coreBankReference,
            String paymentGatewayReference,
            String settlementReference,
            boolean hasAllReferences
    ) {}
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/domain/repository/ReconciliationRepository.java ===
package com.pragma.conciliation.domain.repository;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface ReconciliationRepository {
    Mono<Reconciliation> save(Reconciliation reconciliation);
    Mono<Reconciliation> findById(String id);
    Mono<Reconciliation> findByMovementId(String movementId);
    Mono<Reconciliation> findByEventIdAndVersion(String eventId, int version);
    Mono<Boolean> existsByEventIdAndVersion(String eventId, int version);
    Flux<Reconciliation> findByState(ReconciliationState state);
    Flux<Reconciliation> findByStateAndReceivedAtBefore(ReconciliationState state, Instant cutoffTime);
    Flux<Reconciliation> findAllByOrderByReceivedAtAsc();
    Mono<List<Reconciliation>> findPendingWithLagExceeding(Duration maxPendingDuration);
    Mono<Long> countByState(ReconciliationState state);
    Mono<Reconciliation> update(Reconciliation reconciliation);
    Mono<Void> deleteById(String id);
    Mono<Boolean> existsByIdempotencyKey(String idempotencyKey);
    
    default Flux<Reconciliation> findByStateIn(List<ReconciliationState> states) {
        return Flux.fromIterable(states)
                .flatMap(this::findByState);
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/rest/ReconciliationController.java ===
package com.pragma.conciliation.infrastructure.rest;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.service.ReconciliationService;
import com.pragma.conciliation.infrastructure.config.ReconciliationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reconciliations")
public class ReconciliationController {
    private static final Logger log = LoggerFactory.getLogger(ReconciliationController.class);
    
    private final ReconciliationService reconciliationService;
    private final ReconciliationConfig config;
    
    public ReconciliationController(ReconciliationService reconciliationService,
                                     ReconciliationConfig config) {
        this.reconciliationService = reconciliationService;
        this.config = config;
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ReconciliationResponse>> getById(@PathVariable String id) {
        log.debug("Consultando conciliación por ID: {}", id);
        
        return reconciliationService.findById(id)
                .map(r -> ResponseEntity.ok(mapToResponse(r)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public Mono<ResponseEntity<List<ReconciliationResponse>>> getAll(
            @RequestParam(required = false) ReconciliationState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.debug("Consultando conciliaciones: state={}, page={}, size={}", state, page, size);
        
        if (state != null) {
            return reconciliationService.findByState(state)
                    .take(size)
                    .collectList()
                    .map(list -> list.stream().map(this::mapToResponse).toList())
                    .map(ResponseEntity::ok);
        }
        
        return reconciliationService.getAllReconciliations()
                .take(size)
                .collectList()
                .map(list -> list.stream().map(this::mapToResponse).toList())
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/pending")
    public Mono<ResponseEntity<List<ReconciliationResponse>>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.debug("Consultando conciliaciones pendientes: page={}, size={}", page, size);
        
        return reconciliationService.findPending(page, size)
                .map(list -> list.stream().map(this::mapToResponse).toList())
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/stuck")
    public Mono<ResponseEntity<List<ReconciliationResponse>>> getStuck(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        var maxPending = config.getMatching().getMaxPendingDuration();
        log.debug("Consultando conciliaciones bloqueadas (mayor a {}): page={}, size={}", 
                maxPending, page, size);
        
        return reconciliationService.findStuck(maxPending, page, size)
                .map(list -> list.stream().map(this::mapToResponse).toList())
                .map(ResponseEntity::ok);
    }
    
    @PostMapping("/{id}/resolve")
    public Mono<ResponseEntity<ReconciliationResponse>> resolveManually(
            @PathVariable String id,
            @RequestBody ManualResolutionRequest request) {
        
        log.info("Resolución manual solicitada para conciliación {}: resolution={}, processedBy={}",
                id, request.resolution(), request.processedBy());
        
        if (!config.getManualIntervention().isEnabled()) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Intervención manual deshabilitada en la configuración"));
        }
        
        return reconciliationService.findById(id)
                .flatMap(r -> {
                    if (!r.getState().requiresManualReview()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                                "La conciliación no requiere revisión manual. Estado actual: " + r.getState()));
                    }
                    return reconciliationService.resolveManually(id, request.resolution(), request.processedBy());
                })
                .map(r -> ResponseEntity.ok(mapToResponse(r)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/retry")
    public Mono<ResponseEntity<ReconciliationResponse>> retryMatching(
            @PathVariable String id,
            @RequestBody(required = false) RetryRequest request) {
        
        log.info("Reintento de matching solicitado para conciliación: {}", id);
        
        boolean force = request != null && request.force();
        return reconciliationService.retryMatching(id, force)
                .map(r -> ResponseEntity.ok(mapToResponse(r)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/reprocess")
    public Mono<ResponseEntity<Map<String, Object>>> reprocessAll(
            @RequestBody ReprocessRequest request) {
        
        log.info("Reprocesamiento masivo solicitado: from={}, to={}, strategy={}",
                request.from(), request.to(), request.strategy());
        
        return reconciliationService.reprocess(request.from(), request.to(), request.strategy())
                .map(count -> ResponseEntity.ok(Map.of(
                        "reprocessed", count,
                        "timestamp", Instant.now().toString()
                )));
    }
    
    @GetMapping("/stats")
    public Mono<ResponseEntity<Map<String, Object>>> getStats() {
        log.debug("Consultando estadísticas de conciliación");
        
        return reconciliationService.getStatistics()
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Consultando salud del sistema de conciliación");
        
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "matchingWindow", config.getMatching().getWindow().toMinutes() + " minutes",
                "maxPendingDuration", config.getMatching().getMaxPendingDuration().toHours() + " hours",
                "manualInterventionEnabled", config.getManualIntervention().isEnabled()
        );
        
        return ResponseEntity.ok(health);
    }
    
    private ReconciliationResponse mapToResponse(Reconciliation r) {
        return new ReconciliationResponse(
                r.getId(),
                r.getMovementId(),
                r.getEventId(),
                r.getVersion(),
                r.getState().name(),
                r.getReceivedAt().toString(),
                r.getMatchedAt() != null ? r.getMatchedAt().toString() : null,
                r.getCoreBankReference(),
                r.getPaymentGatewayReference(),
                r.getSettlementReference(),
                r.getMovementAmount(),
                r.getMovementCurrency(),
                r.getManualResolution(),
                r.getProcessedBy(),
                r.getStateHistory().stream()
                        .map(t -> Map.of(
                                "from", t.from() != null ? t.from().name() : null,
                                "to", t.to().name(),
                                "reason", t.reason(),
                                "timestamp", t.timestamp().toString()
                        ))
                        .toList()
        );
    }
    
    public record ReconciliationResponse(
            String id,
            String movementId,
            String eventId,
            int version,
            String state,
            String receivedAt,
            String matchedAt,
            String coreBankReference,
            String paymentGatewayReference,
            String settlementReference,
            BigDecimal amount,
            String currency,
            String manualResolution,
            String processedBy,
            List<Map<String, Object>> stateHistory
    ) {}
    
    public record ManualResolutionRequest(
            String resolution,
            String processedBy
    ) {
        public ManualResolutionRequest {
            if (resolution == null || resolution.isBlank()) {
                throw new IllegalArgumentException("La resolución no puede estar vacía");
            }
            if (processedBy == null || processedBy.isBlank()) {
                throw new IllegalArgumentException("El procesado por no puede estar vacío");
            }
        }
    }
    
    public record RetryRequest(
            boolean force
    ) {}
    
    public record ReprocessRequest(
            Instant from,
            Instant to,
            String strategy
    ) {
        public ReprocessRequest {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Los parámetros from y to son obligatorios");
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("from debe ser anterior a to");
            }
            if (strategy == null || strategy.isBlank()) {
                strategy = "KAFKA_REPLAY";
            }
        }
        
        public String strategy() {
            return strategy;
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/monitoring/ConciliationLagMonitor.java ===
package com.pragma.conciliation.infrastructure.monitoring;

import com.pragma.conciliation.domain.model.Reconciliation;
import com.pragma.conciliation.domain.model.ReconciliationState;
import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConciliationLagMonitor {

    private static final Logger log = LoggerFactory.getLogger(ConciliationLagMonitor.class);

    private final ReconciliationRepository reconciliationRepository;
    private final MeterRegistry meterRegistry;

    @Value("${conciliation.monitoring.sla-minutes:10}")
    private int slaMinutes;

    @Value("${conciliation.monitoring.alert-threshold-minutes:15}")
    private int alertThresholdMinutes;

    private final AtomicLong currentLagSeconds = new AtomicLong(0);
    private final AtomicReference<Instant> lastAlertTime = new AtomicReference<>(Instant.EPOCH);
    private final AtomicLong pendingCount = new AtomicLong(0);
    private final AtomicLong matchedCount = new AtomicLong(0);
    private final AtomicLong mismatchedCount = new AtomicLong(0);
    private final AtomicLong manualReviewCount = new AtomicLong(0);

    private final Counter lagExceededCounter;
    private final Timer lagCalculationTimer;

    public ConciliationLagMonitor(
            ReconciliationRepository reconciliationRepository,
            MeterRegistry meterRegistry) {
        this.reconciliationRepository = reconciliationRepository;
        this.meterRegistry = meterRegistry;

        this.lagExceededCounter = Counter.builder("conciliation.lag.exceeded")
                .description("Número de veces que el lag de conciliación ha superado el SLA")
                .register(meterRegistry);

        this.lagCalculationTimer = Timer.builder("conciliation.lag.calculation.time")
                .description("Tiempo que tarda el cálculo del lag de conciliación")
                .register(meterRegistry);

        Gauge.builder("conciliation.lag.current.seconds", currentLagSeconds, AtomicLong::get)
                .description("Lag actual de conciliación en segundos")
                .register(meterRegistry);

        Gauge.builder("conciliation.pending.count", pendingCount, AtomicLong::get)
                .description("Número de conciliaciones pendientes")
                .register(meterRegistry);

        Gauge.builder("conciliation.matched.count", matchedCount, AtomicLong::get)
                .description("Número de conciliaciones matched")
                .register(meterRegistry);

        Gauge.builder("conciliation.mismatched.count", mismatchedCount, AtomicLong::get)
                .description("Número de conciliaciones mismatched")
                .register(meterRegistry);

        Gauge.builder("conciliation.manual.review.count", manualReviewCount, AtomicLong::get)
                .description("Número de conciliaciones en revisión manual")
                .register(meterRegistry);
    }

    @Scheduled(fixedRateString = "${conciliation.monitoring.check-interval-ms:60000}")
    public void calculateLagAndAlert() {
        lagCalculationTimer.record(() -> {
            try {
                Instant now = Instant.now();
                Duration slaDuration = Duration.ofMinutes(slaMinutes);

                List<ReconciliationState> statesToCheck = List.of(
                        ReconciliationState.PENDING,
                        ReconciliationState.MATCHED
                );
                
                Flux<Reconciliation> pendingReconciliationsFlux = 
                        reconciliationRepository.findByStateIn(statesToCheck);
                
                List<Reconciliation> pendingReconciliations = pendingReconciliationsFlux.collectList().block();

                long currentPending = pendingReconciliations.stream()
                        .filter(r -> r.getState() == ReconciliationState.PENDING)
                        .count();
                pendingCount.set(currentPending);

                long currentMatched = pendingReconciliations.stream()
                        .filter(r -> r.getState() == ReconciliationState.MATCHED)
                        .count();
                matchedCount.set(currentMatched);

                List<Reconciliation> mismatchedReconciliations =
                        reconciliationRepository.findByState(ReconciliationState.MISMATCHED).collectList().block();
                mismatchedCount.set(mismatchedReconciliations.size());

                List<Reconciliation> manualReconciliations =
                        reconciliationRepository.findByState(ReconciliationState.MANUAL_REVIEW).collectList().block();
                manualReviewCount.set(manualReconciliations.size());

                if (!pendingReconciliations.isEmpty()) {
                    Instant oldestPending = pendingReconciliations.stream()
                            .map(Reconciliation::getReceivedAt)
                            .min(Instant::compareTo)
                            .orElse(now);

                    Duration currentLag = Duration.between(oldestPending, now);
                    long lagSeconds = currentLag.getSeconds();
                    currentLagSeconds.set(lagSeconds);

                    log.info("Lag de conciliación actual: {} segundos (SLA: {} minutos)",
                            lagSeconds, slaMinutes);

                    if (currentLag.compareTo(slaDuration) > 0) {
                        lagExceededCounter.increment();
                        triggerAlert(currentLag, pendingReconciliations.size());
                    }
                } else {
                    currentLagSeconds.set(0);
                    log.debug("No hay conciliaciones pendientes para calcular lag");
                }

            } catch (Exception e) {
                log.error("Error al calcular el lag de conciliación", e);
            }
        });
    }

    private void triggerAlert(Duration currentLag, int pendingCount) {
        Instant now = Instant.now();
        Duration alertCooldown = Duration.ofMinutes(alertThresholdMinutes);

        Instant lastAlert = lastAlertTime.get();
        if (Duration.between(lastAlert, now).compareTo(alertCooldown) < 0) {
            log.debug("Alerta suprimida por cooldown. Última alerta hace: {} segundos",
                    Duration.between(lastAlert, now).getSeconds());
            return;
        }

        if (lastAlertTime.compareAndSet(lastAlert, now)) {
            log.warn("ALERTA: El lag de conciliación ({}) supera el SLA de {} minutos. " +
                            "Conciliaciones pendientes: {}",
                    currentLag.toMinutes(), slaMinutes, pendingCount);

            meterRegistry.counter("conciliation.alerts.triggered", "reason", "sla_exceeded")
                    .increment();
        }
    }

    public long getCurrentLagSeconds() {
        return currentLagSeconds.get();
    }

    public long getPendingCount() {
        return pendingCount.get();
    }

    public boolean isLagWithinSla() {
        return currentLagSeconds.get() <= (slaMinutes * 60L);
    }
}

// === ARCHIVO: src/main/java/com/pragma/conciliation/infrastructure/messaging/IdempotencyStore.java ===
package com.pragma.conciliation.infrastructure.messaging;

import com.pragma.conciliation.domain.repository.ReconciliationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class IdempotencyStore {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyStore.class);

    private final JdbcTemplate jdbcTemplate;
    private final ReconciliationRepository reconciliationRepository;

    public IdempotencyStore(JdbcTemplate jdbcTemplate,
                            ReconciliationRepository reconciliationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reconciliationRepository = reconciliationRepository;
        initializeTable();
    }

    private void initializeTable() {
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS idempotency_keys (
                    event_id VARCHAR(255) NOT NULL,
                    version INTEGER NOT NULL,
                    movement_id VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    processed_at TIMESTAMP,
                    PRIMARY KEY (event_id, version)
                )
                """);
            log.info("Tabla de idempotencia inicializada correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar la tabla de idempotencia", e);
        }
    }

    public boolean tryAcquire(String eventId, int version, String movementId) {
        try {
            jdbcTemplate.update("""
                INSERT INTO idempotency_keys (event_id, version, movement_id, created_at)
                VALUES (?, ?, ?, ?)
                """,
                    eventId,
                    version,
                    movementId,
                    Instant.now()
            );

            log.debug("Adquirido lock de idempotencia para eventId={} version={}",
                    eventId, version);
            return true;

        } catch (DataIntegrityViolationException e) {
            log.debug("Duplicado detectado para eventId={} version={}, verificando estado",
                    eventId, version);

            Optional<IdempotencyRecord> existingRecord = findByEventIdAndVersion(eventId, version);
            if (existingRecord.isPresent() && existingRecord.get().processedAt() != null) {
                log.info("Evento ya procesado: eventId={} version={}", eventId, version);
                return false;
            }

            log.warn("Evento en proceso: eventId={} version={}, rehusando procesamiento",
                    eventId, version);
            return false;
        }
    }

    public Optional<IdempotencyRecord> findByEventIdAndVersion(String eventId, int version) {
        try {
            return jdbcTemplate.query("""
                SELECT event_id, version, movement_id, created_at, processed_at
                FROM idempotency_keys
                WHERE event_id = ? AND version = ?
                """,
                (rs, rowNum) -> new IdempotencyRecord(
                        rs.getString("event_id"),
                        rs.getInt("version"),
                        rs.getString("movement_id"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("processed_at") != null
                                ? rs.getTimestamp("processed_at").toInstant()
                                : null
                ),
                eventId,
                version
            ).stream().findFirst();
        } catch (Exception e) {
            log.error("Error al buscar registro de idempotencia: eventId={} version={}",
                    eventId, version, e);
            return Optional.empty();
        }
    }

    public void markAsProcessed(String eventId, int version) {
        int updated = jdbcTemplate.update("""
            UPDATE idempotency_keys
            SET processed_at = ?
            WHERE event_id = ? AND version = ?
            """,
            Instant.now(),
            eventId,
            version
        );

        if (updated > 0) {
            log.debug("Marcado como procesado: eventId={} version={}", eventId, version);
        } else {
            log.warn("No se encontró registro para marcar como procesado: eventId={} version={}",
                    eventId, version);
        }
    }

    public void cleanupOldRecords(int retentionDays) {
        Instant cutoff = Instant.now().minusSeconds(retentionDays * 24L * 60L * 60L);

        int deleted = jdbcTemplate.update("""
            DELETE FROM idempotency_keys
            WHERE created_at < ? AND processed_at IS NOT NULL
            """,
            cutoff
        );

        log.info("Limpiados {} registros de idempotencia antiguos (mayores a {} días)",
                deleted, retentionDays);
    }

    public record IdempotencyRecord(
            String eventId,
            int version,
            String movementId,
            Instant createdAt,
            Instant processedAt
    ) {
        public boolean isProcessed() {
            return processedAt != null;
        }
    }
}
```
