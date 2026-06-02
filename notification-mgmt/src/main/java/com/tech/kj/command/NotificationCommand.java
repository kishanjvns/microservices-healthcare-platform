package com.tech.kj.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.kj.constants.DeliveryStatus;
import com.tech.kj.entity.NotificationDelivery;
import com.tech.kj.entity.NotificationEntity;
import com.tech.kj.model.DeliveryResult;
import com.tech.kj.model.NotificationRequestModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for all notification commands.
 * Uses Template Method Pattern to allow subclasses to define their own idempotency logic.
 *
 * This design allows different commands to:
 * - Check different tables (NotificationEntity, OrderEntity, etc.)
 * - Use different idempotency keys
 * - Implement custom idempotency strategies
 */
@Slf4j
public abstract class NotificationCommand {
    private String data;
    protected ObjectMapper objectMapper;

    public NotificationCommand() {
        this.objectMapper = new ObjectMapper();
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public abstract void execute() throws JsonProcessingException;

    /**
     * Template method for idempotency check.
     * Calls the abstract existsByOutboxEventId() which each command must implement.
     *
     * This allows each command to check its own table/repository.
     *
     * @param outboxEventId The outbox event ID to check
     * @return true if already processed, false if new
     */
    protected boolean isAlreadyProcessed(String outboxEventId) {
        boolean exists = existsByOutboxEventId(outboxEventId);
        if (exists) {
            log.info("Idempotency check: Event with outboxEventId={} already processed, skipping", outboxEventId);
        }
        return exists;
    }

    /**
     * Abstract method for checking if an event has already been processed.
     * Each command implementation must provide its own logic.
     *
     * Examples:
     * - NotificationCommand: Check NotificationRepo.existsByOutboxEventId()
     * - OrderCommand: Check OrderRepo.existsByOutboxEventId()
     * - AnalyticsCommand: Return false (never skip, always record)
     *
     * @param outboxEventId The outbox event ID to check
     * @return true if already processed, false if new
     */
    protected abstract boolean existsByOutboxEventId(String outboxEventId);

    /**
     * Creates NotificationEntity with proper bidirectional relationship to NotificationDelivery.
     * This method ensures both sides of the relationship are properly set before persistence.
     */
    protected NotificationEntity createNotificationWithDelivery(int retry,int maxRetried, DeliveryResult deliveryResult, NotificationRequestModel notificationRequestModel) {
        // Create the parent entity first
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .outboxEventId(notificationRequestModel.getOutboxEventId())
                .appointmentId(notificationRequestModel.getAppointmentId())
                .channelType(deliveryResult.getChannelType())
                .recipientAddress(notificationRequestModel.getRecipientEmail())
                .subject(notificationRequestModel.getEmailSubject())
                .contentType("text/html")
                .renderedContent(deliveryResult.getRenderedContent())
                .userId(notificationRequestModel.getUserId())
                .build();

        // Create the child entity with reference to parent
        NotificationDelivery notificationDelivery = NotificationDelivery.builder()
                .retry(retry)
                .errorMessage(deliveryResult.getErrorMessage())
                .status(deliveryResult.getDeliveryStatus())
                .errorCode(deliveryResult.getErrorCode())
                .maxRetries(maxRetried)
                .vendorResponse(deliveryResult.getVendorResponse())
                .vendorStatus(deliveryResult.getVendorStatus())
                .notificationEntity(notificationEntity)  // Set the owning side FK reference
                .build();

        // Set the child on parent (complete bidirectional relationship)
        notificationEntity.setDeliveryStatus(notificationDelivery);

        return notificationEntity;
    }

}
