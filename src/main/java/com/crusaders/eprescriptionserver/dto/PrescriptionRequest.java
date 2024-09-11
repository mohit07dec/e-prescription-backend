package com.crusaders.eprescriptionserver.dto;

import com.crusaders.eprescriptionserver.entity.Medication;
import com.mongodb.lang.NonNull;

import lombok.Data;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class PrescriptionRequest {
    @NonNull
    private String patientEmail;
    @NonNull
    private String patientName;
    @NonNull
    private String patientAge = "N/A";
    private String patientGender = "N/A";

    @DBRef
    private List<Medication> medications;
}