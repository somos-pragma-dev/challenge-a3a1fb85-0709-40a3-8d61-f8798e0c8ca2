# Diseño de sistema de conciliación bancaria en tiempo real

El sistema debe conciliar movimientos bancarios en tiempo real consumiendo streams de 3 fuentes: core bancario, gateway de pagos y sistema de liquidación. Cada movimiento se reconcilia contra las 3 fuentes con tolerancia a mensajes fuera de orden y llegadas duplicadas. El desarrollador debe diseñar la máquina de estados de cada Reconciliation (Pending, Matched, Mismatched, Manual), justificar la ventana de matching (5 min vs 1 hora), definir cómo maneja idempotencia con eventId + version, y elegir entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL. Adicionalmente debe explicar cómo alertar al equipo de operaciones cuando el lag de conciliación supera un SLA de 10 minutos.

## Informacion General

| Campo | Valor |
|-------|-------|
| **Tema** | TEST-CT |
| **Nivel** | senior-l2 |
| **Tipo** | practical |
| **Tiempo estimado** | 1 semana |

## Fases del Reto

### Fase 0: Configuración del Proyecto

**Objetivo:** Obtener el proyecto base funcional enviando el Código Base a un asistente de IA, que lo analizará, corregirá errores y generará un ZIP listo para usar.

**Tiempo estimado:** 15-30 minutos

**Instrucciones:**

- Asegúrate de tener instalado para ejecutar el proyecto: JDK 17+, Maven 3.9+, IDE con soporte Java.
- Copia todo el contenido del campo **Código Base** de este reto — incluyendo el texto de instrucciones que aparece al inicio.
- Abre un asistente de IA (Claude en claude.ai, ChatGPT o Gemini — se recomienda Claude), pega el contenido copiado en el chat y envíalo.
- El asistente analizará los archivos, corregirá errores y generará un archivo ZIP descargable. Descárgalo y extráelo en la carpeta donde quieras trabajar.
- Ejecuta `mvn compile` en la raíz. Si no hay errores, estás listo.

**Entregable:** El proyecto compila/arranca sin errores.

<details>
<summary>Pistas de conocimiento</summary>

- Copia el Código Base completo incluyendo el texto de instrucciones al inicio — esas instrucciones le indican al asistente exactamente qué hacer con los archivos.
- Si el asistente no genera el ZIP automáticamente al terminar el análisis, escríbele: "genera el ZIP ahora".
- Si el proyecto tiene errores al arrancar, comparte el mensaje de error con el mismo asistente para que lo corrija.

</details>

### Fase 1: Diseño de la máquina de estados

**Objetivo:** Definir los estados y transiciones de la conciliación

**Tiempo estimado:** 2 días

**Instrucciones:**

- Identificar los estados posibles de una conciliación (Pending, Matched, Mismatched, Manual)
- Definir las transiciones entre estados basadas en eventos del dominio (recepción de movimiento, detección de discrepancia, intervención manual)
- Justificar la elección de cada estado y transición

**Entregable:** Diagrama de estados de la conciliación

<details>
<summary>Pistas de conocimiento</summary>

- Considera cómo manejar eventos fuera de orden y duplicados
- Piensa en cómo integrar la idempotencia en el diseño

</details>

### Fase 2: Definición de la ventana de matching

**Objetivo:** Establecer la ventana de tiempo para la conciliación de movimientos

**Tiempo estimado:** 1 día

**Instrucciones:**

- Evaluar las ventajas y desventajas de una ventana de 5 minutos vs 1 hora para la conciliación de movimientos
- Justificar la elección de la ventana basada en el contexto del dominio
- Definir cómo manejar movimientos que caen fuera de la ventana seleccionada

**Entregable:** Documento que describe la ventana de matching y su justificación

<details>
<summary>Pistas de conocimiento</summary>

- Considera la latencia aceptable para el negocio
- Piensa en cómo afecta la ventana de matching a la idempotencia y la consistencia

</details>

### Fase 3: Manejo de idempotencia

**Objetivo:** Implementar idempotencia en el proceso de conciliación

**Tiempo estimado:** 2 días

**Instrucciones:**

- Definir cómo se manejará la idempotencia utilizando eventId + version
- Describir el proceso para asegurar que cada movimiento se concilie una sola vez
- Identificar posibles puntos de falla y cómo mitigarlos

**Entregable:** Documento que describe el manejo de idempotencia en el proceso de conciliación

<details>
<summary>Pistas de conocimiento</summary>

- Considera cómo almacenar y verificar eventId + version
- Piensa en cómo manejar casos de reprocesamiento

</details>

### Fase 4: Estrategia de reprocesamiento

**Objetivo:** Elegir entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL

**Tiempo estimado:** 2 días

**Instrucciones:**

- Evaluar las ventajas y desventajas de reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL
- Justificar la elección basada en el contexto del dominio y las restricciones del sistema
- Describir el proceso de reprocesamiento elegido

**Entregable:** Documento que describe la estrategia de reprocesamiento elegida

<details>
<summary>Pistas de conocimiento</summary>

- Considera la latencia, consistencia y escalabilidad de cada opción
- Piensa en cómo afecta la elección a la idempotencia y la recuperación de fallos

</details>

### Fase 5: Alerta de lag de conciliación

**Objetivo:** Definir cómo alertar al equipo de operaciones cuando el lag de conciliación supera un SLA

**Tiempo estimado:** 2 días

**Instrucciones:**

- Definir el SLA para el lag de conciliación (10 minutos)
- Describir el proceso para monitorear el lag y generar alertas
- Identificar posibles causas del lag y cómo mitigarlas

**Entregable:** Documento que describe el proceso de alerta de lag de conciliación

<details>
<summary>Pistas de conocimiento</summary>

- Considera cómo integrar la monitorización en el diseño del sistema
- Piensa en cómo comunicar las alertas al equipo de operaciones

</details>

## Dimensiones Evaluadas

- **queEs**: ¿Qué es la conciliación bancaria y por qué es importante?
- **paraQueSirve**: ¿Para qué sirve la máquina de estados en el proceso de conciliación?
- **comoSeUsa**: ¿Cómo se usa la idempotencia para asegurar la consistencia en el proceso de conciliación?
- **erroresComunes**: ¿Cuáles son los errores comunes en el proceso de conciliación y cómo se pueden mitigar?
- **queDecisionesImplica**: ¿Qué decisiones implica la elección entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL?

## Criterios de Evaluacion

- Diseño de la máquina de estados de la conciliación
- Justificación de la ventana de matching
- Implementación de idempotencia utilizando eventId + version
- Elección entre reprocesamiento por replay de Kafka vs snapshot desde PostgreSQL
- Definición del proceso de alerta de lag de conciliación

## Como trabajar con un asistente de IA

Hay dos caminos, elegi uno:

- **AGENTS.md** (recomendado) — instrucciones nativas del repo. Abri esta carpeta con tu agente local (Claude Code, Cursor, Codex, Copilot, Gemini) y las carga solo. Sabe que archivos faltan y con que comando se verifica, y completa el scaffold escribiendo en disco.
- **PROMPT_MEJORA.md** — para copiar y pegar en un chat (claude.ai, ChatGPT). Devuelve un ZIP con el proyecto. Sirve si no tenes un agente en el IDE.

Ninguno de los dos resuelve las fases del reto: eso es tu trabajo.

## Verificacion

El proyecto esta listo para trabajar cuando este comando corre sin errores:

```bash
el comando de build o arranque canonico del stack elegido
```

---

*Reto generado automaticamente por Challenge Generator - Pragma*
