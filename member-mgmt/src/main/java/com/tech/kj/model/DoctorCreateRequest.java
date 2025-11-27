package com.tech.kj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request model for creating a new doctor")
public class DoctorCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Specialization is required")
    private String specialization;
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
    private String email;
    private String department;
}
