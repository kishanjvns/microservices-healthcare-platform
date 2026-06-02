package com.tech.kj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.kj.command.NotificationCommandInvoker;
import com.tech.kj.constants.ChannelType;
import com.tech.kj.constants.DeliveryStatus;
import com.tech.kj.entity.NotificationDelivery;
import com.tech.kj.entity.NotificationEntity;
import com.tech.kj.exceptions.RecordNotFoundException;
import com.tech.kj.model.*;
import com.tech.kj.repository.NotificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for querying notification status and retrying failed notifications.
 * Used by support team to monitor and retry notifications by appointment ID.
 */
@Service
@Slf4j
public class NotificationRetryService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private NotificationCommandInvoker notificationCommandInvoker;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Get notification status for a specific appointment.
     * Shows overall status (PASS/PARTIAL_FAILURE/FAILED) and per-channel details.
     *
     * @param appointmentId The appointment ID to query
     * @return NotificationStatusResponse with per-channel status
     */
    public NotificationStatusResponse getNotificationStatus(String appointmentId) {
        log.info("Querying notification status for appointmentId={}", appointmentId);

        // Find all notifications for this appointment
        List<NotificationEntity> notifications = notificationRepo.findByAppointmentId(appointmentId);

        if (notifications.isEmpty()) {
            throw new RecordNotFoundException("No notifications found for appointmentId: " + appointmentId);
        }

        // Build per-channel status
        List<ChannelNotificationStatus> channelStatuses = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (NotificationEntity notification : notifications) {
            NotificationDelivery delivery = notification.getDeliveryStatus();

            ChannelNotificationStatus channelStatus = ChannelNotificationStatus.builder()
                    .channelType(notification.getChannelType())
                    .status(delivery != null ? delivery.getStatus() : DeliveryStatus.PENDING)
                    .recipientAddress(notification.getRecipientAddress())
                    .errorReason(delivery != null ? delivery.getErrorMessage() : null)
                    .errorCode(delivery != null ? delivery.getErrorCode() : null)
                    .retryCount(delivery != null ? delivery.getRetry() : 0)
                    .maxRetries(delivery != null ? delivery.getMaxRetries() : 0)
                    .deliveredAt(delivery != null && DeliveryStatus.DELIVERED.equals(delivery.getStatus())
                            ? delivery.getCreatedAt() : null)
                    .lastAttemptAt(delivery != null ? delivery.getLastModifiedAt() : null)
                    .vendorStatus(delivery != null ? delivery.getVendorStatus() : null)
                    .notificationId(notification.getId().toString())
                    .build();

            channelStatuses.add(channelStatus);

            // Count successes and failures
            if (delivery != null && DeliveryStatus.DELIVERED.equals(delivery.getStatus())) {
                successCount++;
            } else if (delivery != null && DeliveryStatus.FAILED.equals(delivery.getStatus())) {
                failCount++;
            }
        }

        // Determine overall status
        String overallStatus;
        String message;
        if (successCount == notifications.size()) {
            overallStatus = "PASS";
            message = "All notifications delivered successfully";
        } else if (failCount == notifications.size()) {
            overallStatus = "FAILED";
            message = "All notifications failed";
        } else if (failCount > 0) {
            overallStatus = "PARTIAL_FAILURE";
            message = String.format("%d of %d channels failed", failCount, notifications.size());
        } else {
            overallStatus = "PENDING";
            message = "Some notifications still pending";
        }

        log.info("Notification status for appointmentId={}: overallStatus={}, successCount={}, failCount={}",
                appointmentId, overallStatus, successCount, failCount);

        return NotificationStatusResponse.builder()
                .appointmentId(appointmentId)
                .overallStatus(overallStatus)
                .totalChannels(notifications.size())
                .successfulChannels(successCount)
                .failedChannels(failCount)
                .channels(channelStatuses)
                .message(message)
                .build();
    }

    /**
     * Retry failed notifications for a specific appointment.
     * Can optionally retry only a specific channel type.
     *
     * @param retryRequest Contains appointment ID and optional channel type
     * @return Response with retry results
     */
    public NotificationRetryResponse retryNotifications(NotificationRetryRequest retryRequest) {
        String appointmentId = retryRequest.getAppointmentId();
        ChannelType channelType = retryRequest.getChannelType();

        log.info("Manual retry requested for appointmentId={}, channelType={}",
                appointmentId, channelType);

        // Find all notifications for this appointment
        List<NotificationEntity> notifications = notificationRepo.findByAppointmentId(appointmentId);

        if (notifications.isEmpty()) {
            throw new RecordNotFoundException("No notifications found for appointmentId: " + appointmentId);
        }

        // Filter to only failed notifications
        List<NotificationEntity> failedNotifications = notifications.stream()
                .filter(n -> n.getDeliveryStatus() != null
                        && DeliveryStatus.FAILED.equals(n.getDeliveryStatus().getStatus()))
                .collect(Collectors.toList());

        // If specific channel type provided, filter further
        if (channelType != null) {
            failedNotifications = failedNotifications.stream()
                    .filter(n -> channelType.equals(n.getChannelType()))
                    .collect(Collectors.toList());

            if (failedNotifications.isEmpty()) {
                String msg = String.format("No failed notifications found for channel %s", channelType);
                log.info(msg);
                return NotificationRetryResponse.builder()
                        .appointmentId(appointmentId)
                        .channelType(channelType)
                        .eligibleForRetry(0)
                        .retriedCount(0)
                        .retriedChannels(new ArrayList<>())
                        .success(true)
                        .message(msg)
                        .build();
            }
        }

        if (failedNotifications.isEmpty()) {
            String msg = "No failed notifications found for retry";
            log.info(msg);
            return NotificationRetryResponse.builder()
                    .appointmentId(appointmentId)
                    .channelType(channelType)
                    .eligibleForRetry(0)
                    .retriedCount(0)
                    .retriedChannels(new ArrayList<>())
                    .success(true)
                    .message(msg)
                    .build();
        }

        // Submit each failed notification for retry
        List<ChannelType> retriedChannels = new ArrayList<>();
        int successCount = 0;

        for (NotificationEntity notification : failedNotifications) {
            try {
                // Reconstruct notification request from stored data
                NotificationRequestModel notificationRequest = reconstructNotificationRequest(notification);

                // Determine command action based on channel type
                String action = determineCommandAction(notification.getChannelType());

                // Create payload
                Payload payload = new Payload();
                payload.setAction(action);
                payload.setData(objectMapper.writeValueAsString(notificationRequest));

                // Submit to async processing
                notificationCommandInvoker.invokeAsync(payload);

                retriedChannels.add(notification.getChannelType());
                successCount++;
                log.info("Submitted notification {} (channel={}) for retry",
                        notification.getId(), notification.getChannelType());

            } catch (Exception e) {
                log.error("Failed to submit notification {} for retry: {}",
                        notification.getId(), e.getMessage(), e);
                // TODO: Implement support team alert via outbox table + Debezium
            }
        }

        String message = String.format(
                "Found %d failed notifications, successfully submitted %d for retry",
                failedNotifications.size(), successCount);

        log.info("Manual retry completed: {}", message);

        return NotificationRetryResponse.builder()
                .appointmentId(appointmentId)
                .channelType(channelType)
                .eligibleForRetry(failedNotifications.size())
                .retriedCount(successCount)
                .retriedChannels(retriedChannels)
                .success(successCount > 0)
                .message(message)
                .build();
    }

    /**
     * Reconstruct NotificationRequestModel from persisted notification entity.
     *
     * NOTE: This is a simplified reconstruction that only includes basic fields.
     * The original outbox event will be replayed, triggering a new notification
     * with the same outboxEventId (which will be caught by idempotency check).
     *
     * For true retry, we need to generate a NEW outboxEventId or modify the
     * idempotency logic to allow retries.
     */
    private NotificationRequestModel reconstructNotificationRequest(NotificationEntity notification) {
        NotificationRequestModel request = new NotificationRequestModel();

        // Only these fields are available from NotificationEntity
        request.setOutboxEventId(notification.getOutboxEventId());
        request.setAppointmentId(notification.getAppointmentId());

        // TODO: NotificationEntity doesn't store the following fields needed for retry:
        // - notificationType
        // - recipientName, recipientEmail, recipientContact
        // - clinicName, doctorName, clinicAddress
        // - appointmentDate, appointmentTime
        // - emailSubject
        //
        // Current limitation: Retry will be blocked by idempotency check since
        // we're reusing the same outboxEventId.
        //
        // Solutions:
        // 1. Store the original NotificationRequestModel JSON in NotificationEntity
        // 2. Generate a new outboxEventId for retry (e.g., original + "_RETRY_1")
        // 3. Trigger a new outbox event from the source service

        return request;
    }

    /**
     * Determine command bean name based on channel type.
     */
    private String determineCommandAction(ChannelType channelType) {
        return switch (channelType) {
            case EMAIL -> "sendEmailCommand";
            case SMS -> "sendSmsCommand";
            case POPUP -> "sendPopupCommand";
        };
    }
}
