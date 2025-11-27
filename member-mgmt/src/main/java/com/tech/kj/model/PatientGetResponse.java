package com.tech.kj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response model for patient details")
public class PatientGetResponse {
    private String id;
    private String name;
    private int age;
    private String address;
    private String contactNumber;
    private String email;
}
