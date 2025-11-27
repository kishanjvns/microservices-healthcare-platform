package com.tech.kj.service;

import com.tech.kj.model.PatientCreateRequest;
import com.tech.kj.model.PatientGetResponse;
import com.tech.kj.model.ResponseModel;

public interface PatientService {
    String addPatient(PatientCreateRequest request);
    ResponseModel<PatientGetResponse> getPatientById(String id);
}
