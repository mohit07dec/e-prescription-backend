package com.crusaders.eprescriptionserver.repository;

import com.crusaders.eprescriptionserver.entity.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Doctor findByUserName(String username);
}
