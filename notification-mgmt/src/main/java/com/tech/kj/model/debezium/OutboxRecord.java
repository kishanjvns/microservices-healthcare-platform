package com.tech.kj.model.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents an outbox table record from Debezium.
 * Maps to columns in the source outbox table.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboxRecord {

    /**
     * Unique ID of the outbox event (primary key).
     * This serves as the idempotency key for notification processing.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Business entity ID (e.g., appointment ID)
     */
    @JsonProperty("aggregate_id")
    private String aggregateId;

    /**
     * Type of business entity (e.g., "APPOINTMENT")
     */
    @JsonProperty("aggregate_type")
    private String aggregateType;

    /**
     * Event type (e.g., "APPOINTMENT_CREATED", "APPOINTMENT_RESCHEDULED")
     */
    @JsonProperty("event_type")
    private String eventType;

    /**
     * JSON payload containing notification details.
     * This will be deserialized into NotificationRequestModel.
     */
    @JsonProperty("payload")
    private String payload;

    /**
     * Timestamp when the outbox record was created
     */
    @JsonProperty("created_at")
    private Long createdAt;
}
