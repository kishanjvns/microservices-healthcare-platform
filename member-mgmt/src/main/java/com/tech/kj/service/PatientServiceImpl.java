package com.tech.kj.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tech.kj.entity.PatientEntity;
import com.tech.kj.model.PatientCreateRequest;
import com.tech.kj.model.PatientGetResponse;
import com.tech.kj.model.ResponseModel;
import com.tech.kj.repository.PatientRepository;
import com.tech.kj.web.controller.exception.NotFoundException;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private ModelMapperStrategy modelMapperStrategy;

    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public String addPatient(PatientCreateRequest request) {
        var patientEntity = modelMapperStrategy.convert(request, PatientEntity.class);
        var savedEntity = patientRepository.save(patientEntity);
        return savedEntity.getId().toString();
    }

    @Override
    public ResponseModel<PatientGetResponse> getPatientById(String id) throws NotFoundException {
        PatientGetResponse patientResponse = patientRepository.findById(UUID.fromString(id))
            .map(entity -> modelMapperStrategy.convert(entity, PatientGetResponse.class))
            .orElseThrow(NotFoundException::new);

        return ResponseModel.<PatientGetResponse>builder()
            .status(200)
            .message("Patient found successfully")
            .data(patientResponse)
            .build();
    }
    
}
