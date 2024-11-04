package com.example.myworkbackend.repositories;

import com.example.myworkbackend.models.Salary2;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface Salary2Repository extends MongoRepository<Salary2, String> {
    List<Salary2> findAllByAccountId(String accountId);

    List<Salary2> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
