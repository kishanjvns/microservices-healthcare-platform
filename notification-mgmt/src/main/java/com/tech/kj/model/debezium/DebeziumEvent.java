package com.tech.kj.model.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a Debezium CDC event.
 * Maps to the structure produced by Debezium when capturing database changes.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumEvent {

    /**
     * Record state before the change (null for inserts)
     */
    @JsonProperty("before")
    private OutboxRecord before;

    /**
     * Record state after the change (null for deletes)
     */
    @JsonProperty("after")
    private OutboxRecord after;

    /**
     * Operation type: c=create, u=update, d=delete, r=read (snapshot)
     */
    @JsonProperty("op")
    private String operation;

    /**
     * Timestamp when the change occurred
     */
    @JsonProperty("ts_ms")
    private Long timestampMs;

    /**
     * Source metadata (database, table, etc.)
     */
    @JsonProperty("source")
    private SourceMetadata source;
}
