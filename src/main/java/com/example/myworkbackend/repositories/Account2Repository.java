package com.example.myworkbackend.repositories;

import com.example.myworkbackend.models.Account2;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface Account2Repository extends MongoRepository<Account2, String> {
    Optional<Account2> findByUsername (String username);
    Optional<List<Account2>> findAllByActive (Integer active);
}
