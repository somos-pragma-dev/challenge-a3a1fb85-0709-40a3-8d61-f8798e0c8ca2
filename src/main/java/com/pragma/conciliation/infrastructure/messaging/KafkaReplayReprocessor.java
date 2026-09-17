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