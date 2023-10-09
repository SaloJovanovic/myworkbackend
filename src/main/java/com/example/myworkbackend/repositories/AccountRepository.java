package com.example.myworkbackend.repositories;

import com.example.myworkbackend.models.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByUsername (String username);
}
