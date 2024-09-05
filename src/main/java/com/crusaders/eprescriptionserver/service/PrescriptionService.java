package com.crusaders.eprescriptionserver.service;

import com.crusaders.eprescriptionserver.entity.Medication;
import com.crusaders.eprescriptionserver.entity.Prescription;
import com.crusaders.eprescriptionserver.repository.PrescriptionRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Prescription createPrescription(String doctorEmail, String patientEmail, List<Medication> medications) {
        Prescription prescription = new Prescription();
        prescription.setDoctorEmail(doctorEmail);
        prescription.setPatientEmail(patientEmail);
        prescription.setMedications(medications);
        prescription.setCreatedAt(LocalDateTime.now());

        return prescriptionRepository.save(prescription);
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

        // Adding prescription header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Font regularFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

        // Title
        Paragraph title = new Paragraph("Prescription", headerFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Patient and doctor info
        Paragraph doctorInfo = new Paragraph("Doctor: " + prescription.getDoctorEmail(), regularFont);
        Paragraph patientInfo = new Paragraph("Patient: " + prescription.getPatientEmail(), regularFont);
        Paragraph dateInfo = new Paragraph("Date: " + prescription.getCreatedAt().toString(), regularFont);

        document.add(doctorInfo);
        document.add(patientInfo);
        document.add(dateInfo);

        document.add(new Paragraph(" ")); // Empty space

        // Table for medications
        PdfPTable table = new PdfPTable(3); // 3 columns: Medication Name, Dosage, Frequency
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{4f, 2f, 2f});

        // Table headers
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Medication Name", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Dosage", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Frequency", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        table.addCell(cell);

        // Adding medication details
        for (Medication medication : prescription.getMedications()) {
            PdfPCell medNameCell = new PdfPCell(new Phrase(medication.getName(), regularFont));
            medNameCell.setPadding(10);
            table.addCell(medNameCell);

            PdfPCell dosageCell = new PdfPCell(new Phrase(medication.getDosage(), regularFont));
            dosageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dosageCell.setPadding(10);
            table.addCell(dosageCell);

            PdfPCell frequencyCell = new PdfPCell(new Phrase(medication.getFrequency(), regularFont));
            frequencyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            frequencyCell.setPadding(10);
            table.addCell(frequencyCell);
        }

        document.add(table);

        document.add(new Paragraph(" ")); // Empty space

        // Footer information
        Paragraph footer = new Paragraph("Generated by the ePrescription System", regularFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
        return new ByteArrayResource(outputStream.toByteArray());
    }
}
