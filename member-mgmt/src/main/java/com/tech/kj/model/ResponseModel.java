package com.tech.kj.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseModel <T> {
    private int status;
    private String message;
    private T data;
}