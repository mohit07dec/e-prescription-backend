package com.crusaders.eprescriptionserver.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "doctors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    @NonNull
    private String userName;
    @NonNull
    private String password;
    private String doctorEmail;
    private String fullname;
    private String specialization;
    private String phoneNumber;

    private String roles = "DOCTOR";  // Default role is doctor, can be extended to admin if needed

    @DBRef
    private List<Prescription> prescriptions;  // References to all prescriptions by this doctor
}
