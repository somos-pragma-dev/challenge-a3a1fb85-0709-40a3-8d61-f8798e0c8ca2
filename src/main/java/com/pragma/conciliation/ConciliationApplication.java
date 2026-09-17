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