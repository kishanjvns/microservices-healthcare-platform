package com.tech.kj.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tech.kj.model.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class NotificationCommandInvoker implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final ExecutorService notificationExecutor;

    public NotificationCommandInvoker(@Qualifier("notificationExecutor") ExecutorService notificationExecutor) {
        this.notificationExecutor = notificationExecutor;
    }

    /**
     * Synchronous invocation (kept for compatibility, but generally not used).
     */
    public void invoke(Payload payload) throws JsonProcessingException {
        String action = payload.getAction();
        NotificationCommand notificationCommand = applicationContext.getBean(action, NotificationCommand.class);
        notificationCommand.setData(payload.getData());
        notificationCommand.execute();
    }

    /**
     * Asynchronous invocation using thread pool with prototype-scoped commands.
     * Each invocation gets a new command instance and processes it in a separate thread.
     *
     * @param payload The notification payload containing action and data
     */
    public void invokeAsync(Payload payload) {
        notificationExecutor.submit(() -> {
            try {
                String action = payload.getAction();
                // Get new prototype-scoped command instance for each invocation
                NotificationCommand notificationCommand = applicationContext.getBean(action, NotificationCommand.class);
                notificationCommand.setData(payload.getData());
                notificationCommand.execute();
                log.info("Successfully processed notification async: action={}", action);
            } catch (JsonProcessingException e) {
                log.error("JSON processing error in async notification: {}", e.getMessage(), e);
                // TODO: Implement support team alert via outbox table + Debezium
            } catch (Exception e) {
                log.error("Error processing notification async: {}", e.getMessage(), e);
                // TODO: Implement support team alert via outbox table + Debezium
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
