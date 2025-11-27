package com.tech.kj.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tech.kj.entity.*;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    
}
