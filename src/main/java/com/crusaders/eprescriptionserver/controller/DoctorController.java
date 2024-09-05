package com.crusaders.eprescriptionserver.controller;

import com.crusaders.eprescriptionserver.dto.PrescriptionRequest;
import com.crusaders.eprescriptionserver.entity.Prescription;
import com.crusaders.eprescriptionserver.service.PrescriptionService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/create-prescription")
    public ResponseEntity<String> createPrescription(@RequestBody PrescriptionRequest prescriptionRequest) {

        Prescription prescription = prescriptionService.createPrescription(
                prescriptionRequest.getDoctorEmail(),
                prescriptionRequest.getPatientEmail(),
                prescriptionRequest.getMedications());

        try {
            prescriptionService.sendPrescriptionEmail(prescription, prescriptionRequest.getPatientEmail());
            return ResponseEntity.ok("Prescription created and emailed to patient.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email.");
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
