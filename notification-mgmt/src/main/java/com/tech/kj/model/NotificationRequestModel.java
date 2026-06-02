package com.tech.kj.model;

import com.tech.kj.constants.NotificationType;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationRequestModel extends BaseModelClass {
    /**
     * Idempotency key from Debezium outbox event.
     * Used to prevent duplicate processing.
     */
    private String outboxEventId;

    private String appointmentId;
    private NotificationType notificationType;
    private String recipientName;
    private String recipientEmail;
    private String recipientContact;
    private String clinicName;
    private String doctorName;
    private String clinicAddress;
    private String appointmentDate;
    private String appointmentTime;
    private String emailSubject;
}
