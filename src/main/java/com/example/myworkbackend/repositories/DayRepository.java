package com.example.myworkbackend.repositories;

import com.example.myworkbackend.models.Day;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DayRepository extends MongoRepository<Day, String> {
    Optional<Day> findByDate(LocalDate date);

    List<Day> findByDateBetween(LocalDate weekStartDate, LocalDate weekEndDate);
    List<Day> findByDateGreaterThanEqual(LocalDate inputDate);
}
