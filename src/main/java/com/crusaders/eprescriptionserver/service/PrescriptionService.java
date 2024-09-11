package com.crusaders.eprescriptionserver.service;

import com.crusaders.eprescriptionserver.dto.PrescriptionRequest;
import com.crusaders.eprescriptionserver.entity.Medication;
import com.crusaders.eprescriptionserver.entity.Prescription;
import com.crusaders.eprescriptionserver.entity.User;
import com.crusaders.eprescriptionserver.repository.PrescriptionRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Service
public class PrescriptionService {

        @Autowired
        private PrescriptionRepository prescriptionRepository;

        @Autowired
        private JavaMailSender mailSender;

        @Autowired
        private UserService userService;

        public List<Prescription> getAll() {
            return prescriptionRepository.findAll();
        }

        public Prescription createPrescription(PrescriptionRequest prescriptionRequest, String email) {
            try {
                User user = userService.findByEmail(email);

                Prescription prescription = new Prescription();
                prescription.setPatientEmail(prescriptionRequest.getPatientEmail());
                prescription.setDoctorName(user.getFullName());
                prescription.setPatientName(prescriptionRequest.getPatientName());
                prescription.setPatientAge(prescriptionRequest.getPatientAge());
                prescription.setPatientGender(prescriptionRequest.getPatientGender());
                prescription.setDoctorEmail(user.getEmail());
                prescription.setMedications(prescriptionRequest.getMedications());
                ZoneId istZone = ZoneId.of("Asia/Kolkata");
                prescription.setCreatedAt(ZonedDateTime.now(istZone).toLocalDateTime());
                Prescription saved = prescriptionRepository.save(prescription);
            
                user.getPrescriptions().add(saved);
                userService.saveUser(user);
                return saved;
            
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while saving the entry.", e);
            }
        }
    

        public void sendPrescriptionEmail(Prescription prescription, String patientEmail) throws MessagingException, DocumentException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(patientEmail);
            helper.setSubject("Your Prescription");
            helper.setText("Please find your prescription attached as a PDF.", true);

            ByteArrayResource pdf = generatePdf(prescription);
            helper.addAttachment("prescription.pdf", pdf);

            mailSender.send(message);
        }

            private ByteArrayResource generatePdf(Prescription prescription) throws DocumentException {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
            Font regularFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            // Title
            Paragraph title = new Paragraph("Prescription", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Doctor Information
            document.add(new Paragraph("Doctor Information", headerFont));
            document.add(new Paragraph("Name: " + prescription.getDoctorName(), regularFont));
            document.add(new Paragraph("Email: " + prescription.getDoctorEmail(), regularFont));
            document.add(new Paragraph("\n"));

            // Patient Information
            document.add(new Paragraph("Patient Information", headerFont));
            document.add(new Paragraph("Name: " + prescription.getPatientName(), regularFont));
            document.add(new Paragraph("Email: " + prescription.getPatientEmail(), regularFont));
            document.add(new Paragraph("Age: " + prescription.getPatientAge(), regularFont));
            document.add(new Paragraph("Gender: " + prescription.getPatientGender(), regularFont));
            document.add(new Paragraph("\n"));

            // Prescription Details
            document.add(new Paragraph("Prescription Details", headerFont));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = prescription.getCreatedAt().format(formatter);
            document.add(new Paragraph("Date: " + formattedDate, regularFont));            
            document.add(new Paragraph("\n"));

            // Medications Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Stream.of("Medication", "Dosage", "Frequency", "Duration")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.DARK_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, tableHeaderFont));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    header.setPadding(5);
                    table.addCell(header);
                });

            for (Medication medication : prescription.getMedications()) {
                table.addCell(new Phrase(medication.getName(), regularFont));
                table.addCell(new Phrase(medication.getDosage(), regularFont));
                table.addCell(new Phrase(medication.getFrequency(), regularFont));
                table.addCell(new Phrase(medication.getDuration(), regularFont));
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("© 2024 Code Crusaders | Software currently under development.", regularFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return new ByteArrayResource(outputStream.toByteArray());
        }


        public List<Prescription> getPrescriptionsByEmail(String email) {
            return prescriptionRepository.findByPatientEmailOrDoctorEmail(email, email);
        }

        public Prescription updatePrescription(ObjectId id, PrescriptionRequest prescriptionRequest) {
            Prescription existingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        
            existingPrescription.setPatientEmail(prescriptionRequest.getPatientEmail());
            existingPrescription.setPatientName(prescriptionRequest.getPatientName());
            existingPrescription.setPatientAge(prescriptionRequest.getPatientAge());
            existingPrescription.setPatientGender(prescriptionRequest.getPatientGender());
            existingPrescription.setMedications(prescriptionRequest.getMedications());

            return prescriptionRepository.save(existingPrescription);
        }

        public boolean sendOtpForPrescription(String prescriptionId) {
            Prescription prescription = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
            if (prescription == null) {
                return false;
            }

            String otp = generateOtp();
            // Store OTP (you might want to use a cache or database for this)
            prescription.setOtp(otp);
            prescriptionRepository.save(prescription);

            try {
                sendOtpEmail(prescription.getPatientEmail(), otp);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }

        public Prescription verifyOtpAndGetPrescription(String prescriptionId, String otp) {
            Prescription prescription = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
            if (prescription != null && prescription.getOtp().equals(otp)) {
                prescription.setOtp(null); // Clear OTP after successful verification
                prescriptionRepository.save(prescription);
                return prescription;
            }
            return null;
        }

        private String generateOtp() {
            Random random = new Random();
            return String.format("%06d", random.nextInt(1000000));
        }

        private void sendOtpEmail(String email, String otp) throws MessagingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Your Prescription OTP");
            helper.setText("Your OTP for viewing the prescription is: " + otp, true);

            mailSender.send(message);
        }
}