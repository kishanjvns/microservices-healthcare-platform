package com.tech.kj.model;

import com.tech.kj.constants.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response model for manual retry API.
 * Contains results of retry operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRetryResponse {

    /**
     * Appointment ID
     */
    private String appointmentId;

    /**
     * Channel type that was retried (if specific channel was requested)
     */
    private ChannelType channelType;

    /**
     * Number of failed notifications found eligible for retry
     */
    private int eligibleForRetry;

    /**
     * Number of notifications successfully submitted for retry
     */
    private int retriedCount;

    /**
     * List of channel types that were retried
     */
    private List<ChannelType> retriedChannels;

    /**
     * Success status of retry operation
     */
    private boolean success;

    /**
     * Message describing the result
     */
    private String message;
}
