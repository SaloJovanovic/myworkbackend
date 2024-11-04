package com.example.myworkbackend.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Document
@Builder
public class Day2 {
    @Id
    private String id;

    @Indexed(unique = true) // Set unique to true to ensure date uniqueness
    private LocalDate date;

    private List<String> employeesIds;
    private List<String> employeesNames;
    private String[] shifts;
    private String[] startTimes;
    private String[] endTimes;
    private Double[] hourlyRates;
    private String weekNote;
    private String dayNote;
}
