package com.tech.kj.model;

import com.tech.kj.constants.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for manual retry of failed notifications.
 * Support team can use appointment ID and optionally channel type to trigger retry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRetryRequest {

    /**
     * Appointment ID to retry notifications for (required).
     */
    private String appointmentId;

    /**
     * Optional: Specific channel type to retry (EMAIL, SMS, POPUP).
     * If not provided, retries all failed channels.
     */
    private ChannelType channelType;
}
