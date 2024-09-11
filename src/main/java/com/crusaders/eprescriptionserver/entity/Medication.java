package com.crusaders.eprescriptionserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Medication {
    @Id
    private String id;
    private String name;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}
