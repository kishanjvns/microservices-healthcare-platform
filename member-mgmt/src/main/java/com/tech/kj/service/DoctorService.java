package com.tech.kj.service;

import com.tech.kj.model.DoctorCreateRequest;
import com.tech.kj.model.DoctorGetResponse;
import com.tech.kj.model.ResponseModel;

public interface DoctorService {
    String addDoctor(DoctorCreateRequest request);
    ResponseModel<DoctorGetResponse> getDoctorById(String id);
}
