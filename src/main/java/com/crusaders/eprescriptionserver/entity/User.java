package com.crusaders.eprescriptionserver.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private ObjectId id;
    private ZonedDateTime createdAt;
    @Indexed(unique = true)
    @NonNull
    private String email;
    
    @NonNull
    private String password;

    @NonNull
    private String fullName;

    private List<Prescription> prescriptions = new ArrayList<>();
    
    private List<String> roles;

    public User orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    } 
}
