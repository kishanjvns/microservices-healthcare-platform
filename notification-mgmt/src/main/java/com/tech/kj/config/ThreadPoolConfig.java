package com.tech.kj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread pool configuration for async notification processing.
 * Allows parallel processing of notifications after Kafka consumption.
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * Creates a fixed thread pool for notification processing.
     * Size can be adjusted based on load and system resources.
     *
     * @return ExecutorService for async notification processing
     */
    @Bean(name = "notificationExecutor", destroyMethod = "shutdown")
    public ExecutorService notificationExecutor() {
        // Fixed thread pool with 10 threads
        // TODO: Make thread pool size configurable via application.properties
        return Executors.newFixedThreadPool(10);
    }
}
