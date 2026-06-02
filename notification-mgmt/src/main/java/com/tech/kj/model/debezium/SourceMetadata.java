package com.tech.kj.model.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Debezium source metadata.
 * Contains information about where the change originated.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceMetadata {

    @JsonProperty("version")
    private String version;

    @JsonProperty("connector")
    private String connector;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ts_ms")
    private Long timestampMs;

    @JsonProperty("snapshot")
    private String snapshot;

    @JsonProperty("db")
    private String database;

    @JsonProperty("schema")
    private String schema;

    @JsonProperty("table")
    private String table;
}
