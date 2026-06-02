package com.tech.kj.web.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tech.kj.config.aop.LogTime;
import com.tech.kj.model.DoctorCreateRequest;
import com.tech.kj.model.DoctorGetResponse;
import com.tech.kj.model.ResponseModel;
import com.tech.kj.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/doctor")
@Slf4j
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping()
    @LogTime
    @Operation(summary = "Creates a new doctor.",
               description = "Checks business rules and persists the new doctor to the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor successfully created."),
        @ApiResponse(responseCode = "400", description = "Invalid input or doctor already exists.")
    })
    public ResponseEntity<ResponseModel<String>> createDoctor(@RequestBody @Valid DoctorCreateRequest request) {
        String id = doctorService.addDoctor(request);
        return ResponseEntity.ok(
            ResponseModel.<String>builder()
                .status(201)
                .message("Doctor created successfully")
                .data(Map.of("doctorId", id).toString())
                .build()
        );
    }

    @GetMapping("/{id}")
    @LogTime
    @Operation(summary = "Get doctor by ID",
               description = "Retrieves a doctor record by their unique identifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found."),
        @ApiResponse(responseCode = "404", description = "Doctor not found.")
    })
    public ResponseEntity<ResponseModel<DoctorGetResponse>> getDoctorById(@PathVariable String id) {
        ResponseModel<DoctorGetResponse> response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(response);
    }
}
