package com.tech.kj.model;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class BaseModelClass {
    // Common fields
    private String action;
    private UUID userId;
}
