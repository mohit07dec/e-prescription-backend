package com.crusaders.eprescriptionserver.controller;

import com.crusaders.eprescriptionserver.entity.User;
import com.crusaders.eprescriptionserver.repository.UserRepository;
import com.crusaders.eprescriptionserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User userInDb = userService.findByEmail(email);
        userInDb.setEmail(user.getEmail());
        userInDb.setPassword(user.getPassword());
        userService.registerUser(userInDb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByEmail(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
