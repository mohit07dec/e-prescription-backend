package com.crusaders.eprescriptionserver.dto;

import com.crusaders.eprescriptionserver.entity.Medication;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    private String doctorEmail;
    private String patientEmail;
    private List<Medication> medications;
}