package com.tech.kj.web.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tech.kj.config.aop.LogTime;
import com.tech.kj.model.PatientCreateRequest;
import com.tech.kj.model.PatientGetResponse;
import com.tech.kj.model.ResponseModel;
import com.tech.kj.service.PatientService;

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
@RequestMapping("/patient")
@Slf4j
public class PatientController {
    
    @Autowired
    private PatientService patientService;
   
   
    @PostMapping()
    @LogTime
    @Operation(summary = "Creates a new user account.", 
               description = "Checks business rules and persists the new user to the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created."),
        @ApiResponse(responseCode = "400", description = "Invalid input or user already exists.")
    })
    public ResponseEntity<ResponseModel<String>> postMethodName(@RequestBody @Valid PatientCreateRequest request) {
        String id = patientService.addPatient(request);
        return ResponseEntity.ok(
            ResponseModel.<String>builder()
                .status(201)
                .message("Patient created successfully")
                .data(Map.of("patientId", id).toString())
                .build()
        );
    }

    @GetMapping("/{id}")
    @LogTime
    @Operation(summary = "Get patient by ID",
               description = "Retrieves a patient record by their unique identifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient found."),
        @ApiResponse(responseCode = "404", description = "Patient not found.")
    })
    public ResponseEntity<ResponseModel<PatientGetResponse>> getPatientById(@PathVariable String id) {
        ResponseModel<PatientGetResponse> response = patientService.getPatientById(id);
        return ResponseEntity.ok(response);
    }

}
