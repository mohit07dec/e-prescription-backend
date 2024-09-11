package com.crusaders.eprescriptionserver.repository;

import com.crusaders.eprescriptionserver.entity.User;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    void deleteByEmail(String email);
}

