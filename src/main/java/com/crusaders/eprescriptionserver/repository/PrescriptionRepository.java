package com.crusaders.eprescriptionserver.repository;

import com.crusaders.eprescriptionserver.dto.PrescriptionRequest;
import com.crusaders.eprescriptionserver.entity.Prescription;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrescriptionRepository extends MongoRepository<Prescription, ObjectId> {

    List<Prescription> findByPatientEmailOrDoctorEmail(String patientEmail, String doctorEmail);
    //Prescription saveFromRequest(PrescriptionRequest request);
}
