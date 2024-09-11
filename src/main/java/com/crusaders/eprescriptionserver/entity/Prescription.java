package com.crusaders.eprescriptionserver.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "prescriptions")
@NoArgsConstructor
public class Prescription {

    @Id
    private ObjectId id;
    @NonNull
    private String patientEmail;
    @NonNull
    private String DoctorName;
    @NonNull
    private String patientName;
    @NonNull
    private String patientAge;
    private String patientGender;
    private String doctorEmail;
    private String otp;

    private List<Medication> medications = new ArrayList<>();;  // List of Medication objects

    private LocalDateTime createdAt;

}
