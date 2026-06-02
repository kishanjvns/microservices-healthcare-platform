package com.tech.kj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.kj.command.NotificationCommandInvoker;
import com.tech.kj.constants.ChannelType;
import com.tech.kj.model.NotificationRequestModel;
import com.tech.kj.model.Payload;
import com.tech.kj.model.debezium.DebeziumEvent;
import com.tech.kj.model.debezium.OutboxRecord;
import com.tech.kj.repository.NotificationRepo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationCommandInvoker notificationCommandInvoker;

    @Autowired
    private NotificationRepo notificationRepo;

    @KafkaListener(topics = "notification_topic", groupId = "notification-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            log.info("Received Notification Message from Kafka: {}", record.value());

            // Parse Debezium event
            DebeziumEvent debeziumEvent = objectMapper.readValue(record.value(), DebeziumEvent.class);

            if (debeziumEvent == null || debeziumEvent.getAfter() == null) {
                log.warn("Invalid Debezium event structure (missing 'after' field), skipping");
                acknowledgment.acknowledge();
                return;
            }

            OutboxRecord outboxRecord = debeziumEvent.getAfter();
            String outboxEventId = outboxRecord.getId();

            log.info("Processing event: outboxEventId={}, eventType={}, aggregateId={}",
                    outboxEventId, outboxRecord.getEventType(), outboxRecord.getAggregateId());

            // Parse notification request from outbox payload
            String payloadJson = outboxRecord.getPayload();
            NotificationRequestModel notificationRequest =
                    objectMapper.readValue(payloadJson, NotificationRequestModel.class);

            // Set idempotency key from outbox event
            notificationRequest.setOutboxEventId(outboxEventId);

            // Determine command action based on channel type
            String action = determineCommandAction(notificationRequest.getNotificationType().name(),
                    getChannelType(notificationRequest));

            // Create command payload
            Payload payload = new Payload();
            payload.setAction(action);
            payload.setData(objectMapper.writeValueAsString(notificationRequest));

            // Process notification async in thread pool
            notificationCommandInvoker.invokeAsync(payload);

            // TODO: Implement better ACK strategy

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
            // Acknowledge to prevent poison pill blocking the consumer
            acknowledgment.acknowledge();
        }
    }

    /**
     * Determine command bean name based on notification type and channel.
     */
    private String determineCommandAction(String notificationType, ChannelType channelType) {
        return switch (channelType) {
            case EMAIL -> "sendEmailCommand";
            case SMS -> "sendSmsCommand";
            case POPUP -> "sendPopupCommand";
        };
    }

    /**
     * Get channel type from notification request.
     * Defaults to EMAIL if not specified.
     */
    private ChannelType getChannelType(NotificationRequestModel request) {
        // Assuming channel type is derived from notification type
        // Or it could be a field in the request model
        // For now, defaulting to EMAIL - adjust based on your business logic
        return ChannelType.EMAIL;
    }
}
