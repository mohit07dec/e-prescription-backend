package com.crusaders.eprescriptionserver.service;

import com.crusaders.eprescriptionserver.entity.User;
import com.crusaders.eprescriptionserver.repository.UserRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("DOCTOR"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER", "ADMIN"));
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }    
}

