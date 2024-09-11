package com.crusaders.eprescriptionserver.controller;

import com.crusaders.eprescriptionserver.entity.Prescription;
import com.crusaders.eprescriptionserver.entity.User;
import com.crusaders.eprescriptionserver.service.PrescriptionService;
import com.crusaders.eprescriptionserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping("/all-doctors")
    public ResponseEntity<?> getAllUsers() {
        List<User> all = userService.getAll();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/all-prescriptions")
        public ResponseEntity<?> getAllPrescriptions() {
            List<Prescription> all = prescriptionService.getAll();
            if (all != null && !all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    
}
