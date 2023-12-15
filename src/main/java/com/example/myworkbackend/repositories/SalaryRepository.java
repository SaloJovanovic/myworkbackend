package com.example.myworkbackend.repositories;

import com.example.myworkbackend.models.Salary;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalaryRepository extends MongoRepository<Salary, String> {
    List<Salary> findAllByAccountId(String accountId);

    List<Salary> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
