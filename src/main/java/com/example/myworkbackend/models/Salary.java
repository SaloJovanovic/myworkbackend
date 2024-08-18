package com.example.myworkbackend.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
@Builder
public class Salary {
    @Id
    private String id;
    private LocalDate date;
    private String accountId;
    private Double salary;
    private String name;
    private String username;
    private String role;
    private Integer active;
    private Double hourlyRate;
    private Double fixedSalary;
}
