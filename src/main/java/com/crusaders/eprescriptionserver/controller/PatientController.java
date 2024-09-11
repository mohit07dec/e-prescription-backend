package com.crusaders.eprescriptionserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crusaders.eprescriptionserver.entity.Prescription;
import com.crusaders.eprescriptionserver.service.PrescriptionService;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/request-prescription")
    public ResponseEntity<?> requestPrescription(@RequestParam String prescriptionId) {
        boolean otpSent = prescriptionService.sendOtpForPrescription(prescriptionId);
        if (otpSent) {
            return ResponseEntity.ok("OTP sent to your registered email");
        }
        return ResponseEntity.badRequest().body("Invalid prescription ID or unable to send OTP");
    }

    @PostMapping("/view-prescription")
    public ResponseEntity<?> viewPrescription(@RequestParam String prescriptionId, @RequestParam String otp) {
        Prescription prescription = prescriptionService.verifyOtpAndGetPrescription(prescriptionId, otp);
        if (prescription != null) {
            return ResponseEntity.ok(prescription);
        }
        return ResponseEntity.badRequest().body("Invalid OTP or prescription ID");
    }
}

