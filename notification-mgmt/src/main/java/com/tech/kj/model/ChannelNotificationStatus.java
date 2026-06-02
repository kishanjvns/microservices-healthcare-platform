package com.tech.kj.model;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.constants.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the status of a notification for a specific channel.
 * Used in NotificationStatusResponse to show per-channel delivery status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelNotificationStatus {

    /**
     * Notification channel type (EMAIL, SMS, POPUP)
     */
    private ChannelType channelType;

    /**
     * Delivery status (DELIVERED, FAILED, PENDING, etc.)
     */
    private DeliveryStatus status;

    /**
     * Recipient address (email, phone number, or user ID for popup)
     */
    private String recipientAddress;

    /**
     * Error reason if failed
     */
    private String errorReason;

    /**
     * Error code if failed
     */
    private String errorCode;

    /**
     * Number of retry attempts made
     */
    private Integer retryCount;

    /**
     * Maximum retries allowed
     */
    private Integer maxRetries;

    /**
     * Timestamp when delivered (if successful)
     */
    private LocalDateTime deliveredAt;

    /**
     * Timestamp of last attempt
     */
    private LocalDateTime lastAttemptAt;

    /**
     * Vendor status response
     */
    private String vendorStatus;

    /**
     * Notification ID
     */
    private String notificationId;
}
