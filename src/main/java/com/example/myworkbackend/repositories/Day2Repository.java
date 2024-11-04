package com.example.myworkbackend.repositories;

import com.example.myworkbackend.models.Day2;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface Day2Repository extends MongoRepository<Day2, String> {
    Optional<Day2> findByDate(LocalDate date);

    List<Day2> findByDateBetween(LocalDate weekStartDate, LocalDate weekEndDate);
    List<Day2> findByDateGreaterThanEqual(LocalDate inputDate);
}
