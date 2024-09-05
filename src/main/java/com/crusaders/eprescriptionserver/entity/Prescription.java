package com.crusaders.eprescriptionserver.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private ObjectId id;
   // @Indexed(unique = true)
    private String userName;
    private String patientEmail;
    private String doctorEmail;

    @DBRef
    private List<Medication> medications;  // List of Medication objects

    private LocalDateTime createdAt;

}
