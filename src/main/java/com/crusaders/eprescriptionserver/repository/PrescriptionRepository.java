package com.crusaders.eprescriptionserver.repository;

import com.crusaders.eprescriptionserver.entity.Prescription;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrescriptionRepository extends MongoRepository<Prescription, ObjectId> {
}
