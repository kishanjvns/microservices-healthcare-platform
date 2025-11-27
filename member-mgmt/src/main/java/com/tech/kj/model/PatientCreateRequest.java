package com.tech.kj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request model for creating a new patient")
public class PatientCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @Min(value = 0, message = "Age must be non-negative")
    private int age;
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
    private String email;
}
