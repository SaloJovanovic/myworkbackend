package com.example.mywork.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

@Data
@Document
@Builder
public class Day {
    @Id
    private String id;

    @Indexed(unique = true) // Set unique to true to ensure date uniqueness
    private LocalDate date;

    private String[] employeesIds;
    private String[] employeesNames;
    private String[] shifts;
    private String[] startTimes;
    private String[] endTimes;
    private String weekNote;
    private String dayNote;
}
