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