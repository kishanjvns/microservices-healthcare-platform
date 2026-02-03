package com.tech.kj.config;

import java.util.Map;
import static java.util.Map.entry;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.apache.kafka.common.serialization.StringDeserializer;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public Map<String, Object> consumerConfigProps() {
        return Map.ofEntries(
                entry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:19092,localhost:29092"),
                entry(ConsumerConfig.GROUP_ID_CONFIG, "notification-consumer-group"),
                entry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                        "org.apache.kafka.common.serialization.StringDeserializer"),
                entry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        "org.apache.kafka.common.serialization.StringDeserializer"),
                entry(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"),
                entry(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false),
                entry(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10000),
                entry(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000),
                entry(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000),
                entry(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000),
                entry(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1),
                entry(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500));
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigProps(), new StringDeserializer(), new StringDeserializer());
    }

     /**
     * Step 3: Create KafkaListenerContainerFactory
     * This factory creates listener containers that manage the lifecycle of consumers
     * 
     * ConcurrentKafkaListenerContainerFactory: 
     * - Creates multiple KafkaMessageListenerContainer instances
     * - Each container runs in its own thread
     * - Enables concurrent message processing
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        // Set the consumer factory
        factory.setConsumerFactory(consumerFactory());
        
        // CONCURRENCY: Number of concurrent consumers (threads) to create
        // If topic has 6 partitions and concurrency=3:
        // - 3 consumer threads will be created
        // - Each thread handles 2 partitions
        factory.setConcurrency(3);
        
        // ACK_MODE: When to commit offsets
        // RECORD - Commit after each record is processed (safest, slowest)
        // BATCH - Commit after entire batch is processed (balanced)
        // MANUAL - You control when to commit (most control)
        // MANUAL_IMMEDIATE - Commit immediately when you call ack()
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        
        // POLL_TIMEOUT: How long to wait in poll() if no data available
        factory.getContainerProperties().setPollTimeout(3000);
        
        return factory;
    }

}
