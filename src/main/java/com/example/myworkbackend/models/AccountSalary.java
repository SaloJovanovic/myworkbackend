package com.example.myworkbackend.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class AccountSalary {
    private String accountId;
    private String name;
    private String username;
    private String role;
    private Double hourlyRate;
    private Integer active;
    private Double salary;
}
