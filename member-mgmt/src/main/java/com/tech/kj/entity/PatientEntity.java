package com.tech.kj.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "patients")
@Data
public class PatientEntity {
    @Id
    @GeneratedValue(strategy  = GenerationType.UUID)
    private UUID id;
    private String name;
    private int age;
    private String address;
    private String contactNumber;
    private String email;
}
