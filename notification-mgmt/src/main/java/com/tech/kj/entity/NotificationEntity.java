package com.tech.kj.entity;

import java.util.UUID;

import com.tech.kj.constants.ChannelType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_entity", indexes = {
    @Index(name = "idx_outbox_event_id", columnList = "outbox_event_id", unique = true),
    @Index(name = "idx_appointment_id", columnList = "appointment_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Idempotency key from Debezium outbox event.
     * Prevents duplicate notification processing.
     */
    @Column(name = "outbox_event_id", unique = true, nullable = false, length = 100)
    private String outboxEventId;

    /**
     * Business identifier for appointment.
     * Used for manual retry lookup.
     */
    @Column(name = "appointment_id", length = 100)
    private String appointmentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private ChannelType channelType;
    
    @Column(name = "recipient_address", nullable = false)
    private String recipientAddress;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "rendered_content", columnDefinition = "TEXT", nullable = false)
    private String renderedContent;
    
    @Column(name = "content_type")
    private String contentType;
    
    @OneToOne(mappedBy = "notificationEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private NotificationDelivery deliveryStatus;
    
}
