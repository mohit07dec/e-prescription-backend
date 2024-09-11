package com.crusaders.eprescriptionserver.controller;

import com.crusaders.eprescriptionserver.dto.PrescriptionRequest;
import com.crusaders.eprescriptionserver.entity.Prescription;
import com.crusaders.eprescriptionserver.entity.User;
import com.crusaders.eprescriptionserver.repository.UserRepository;
import com.crusaders.eprescriptionserver.service.UserService;
import com.crusaders.eprescriptionserver.service.PrescriptionService;
import com.itextpdf.text.DocumentException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPrescription(@RequestBody PrescriptionRequest prescriptionRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User doctor = userService.findByEmail(email);
            Prescription prescription = prescriptionService.createPrescription(prescriptionRequest, email);
            
            prescriptionService.sendPrescriptionEmail(prescription, prescriptionRequest.getPatientEmail());
            
            return ResponseEntity.ok("Prescription created and emailed to patient.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Prescription created but failed to send email.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create prescription: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        List<Prescription> all = user.getPrescriptions();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePrescription(@PathVariable ObjectId id, @RequestBody PrescriptionRequest prescriptionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            Prescription updatedPrescription = prescriptionService.updatePrescription(id, prescriptionRequest);
            
            prescriptionService.sendPrescriptionEmail(updatedPrescription, updatedPrescription.getPatientEmail());
            
            return ResponseEntity.ok("Prescription updated and emailed to patient.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Prescription updated but failed to send email.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update prescription: " + e.getMessage());
        }
    }  
}
