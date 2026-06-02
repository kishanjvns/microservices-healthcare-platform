package com.tech.kj.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response model for notification status query by appointment ID.
 * Shows overall status and per-channel delivery details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatusResponse {

    /**
     * Appointment ID queried
     */
    private String appointmentId;

    /**
     * Overall status across all channels:
     * - PASS: All channels delivered successfully
     * - PARTIAL_FAILURE: Some channels failed
     * - FAILED: All channels failed
     * - PENDING: Some channels still pending
     */
    private String overallStatus;

    /**
     * Total number of notification channels sent
     */
    private int totalChannels;

    /**
     * Number of successfully delivered channels
     */
    private int successfulChannels;

    /**
     * Number of failed channels
     */
    private int failedChannels;

    /**
     * Per-channel status details
     */
    private List<ChannelNotificationStatus> channels;

    /**
     * Summary message
     */
    private String message;
}
