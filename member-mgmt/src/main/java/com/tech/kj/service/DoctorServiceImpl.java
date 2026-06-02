package com.tech.kj.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tech.kj.entity.DoctorEntity;
import com.tech.kj.model.DoctorCreateRequest;
import com.tech.kj.model.DoctorGetResponse;
import com.tech.kj.model.ResponseModel;
import com.tech.kj.repository.DoctorRepository;
import com.tech.kj.web.controller.exception.NotFoundException;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private ModelMapperStrategy modelMapperStrategy;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public String addDoctor(DoctorCreateRequest request) {
        var doctorEntity = modelMapperStrategy.convert(request, DoctorEntity.class);
        var savedEntity = doctorRepository.save(doctorEntity);
        return savedEntity.getId().toString();
    }

    @Override
    public ResponseModel<DoctorGetResponse> getDoctorById(String id) throws NotFoundException {
        DoctorGetResponse doctorResponse = doctorRepository.findById(UUID.fromString(id))
            .map(entity -> modelMapperStrategy.convert(entity, DoctorGetResponse.class))
            .orElseThrow(NotFoundException::new);

        return ResponseModel.<DoctorGetResponse>builder()
            .status(200)
            .message("Doctor found successfully")
            .data(doctorResponse)
            .build();
    }
}
