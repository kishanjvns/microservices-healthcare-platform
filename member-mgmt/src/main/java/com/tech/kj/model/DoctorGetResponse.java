package com.tech.kj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response model for doctor details")
public class DoctorGetResponse {
    private String id;
    private String name;
    private String specialization;
    private String contactNumber;
    private String email;
    private String department;
}
