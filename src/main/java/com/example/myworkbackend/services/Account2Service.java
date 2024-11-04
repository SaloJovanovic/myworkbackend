package com.example.myworkbackend.services;

import com.example.myworkbackend.models.Account2;
import com.example.myworkbackend.models.AccountLogInCred;
import com.example.myworkbackend.models.Day2;
import com.example.myworkbackend.repositories.Account2Repository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class Account2Service {
    private final Account2Repository accountRepository;

    private PasswordEncoder passwordEncoder;
    private final DayService2 dayService;

    public Account2 createAccount(Account2 accountInfo) {
        System.out.println("AAAAAAAAAAA");
        Optional<Account2> existingAccount = accountRepository.findByUsername(accountInfo.getUsername());
        System.out.println("BBBBBBBBBBBB");
        if (existingAccount.isPresent()) {
            // Username already exists, throw a FORBIDDEN exception
            System.out.println("ASDFASDFASFD");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username already exists");
        }
        System.out.println(accountInfo.getUsername());
        Account2 account = Account2.builder()
                .name(accountInfo.getName())
                .username(accountInfo.getUsername())
                .password(passwordEncoder.encode(accountInfo.getPassword()))
                .role(accountInfo.getRole())
                .active(0)
                .hourlyRate(accountInfo.getHourlyRate())
                .fixedSalary(accountInfo.getFixedSalary())
                .build();
        System.out.println(account);
        try {
            accountRepository.save(account);
            dayService.changeStatus(accountRepository.findByUsername(accountInfo.getUsername()).get().getId());
            return account;
        }
        catch (DataIntegrityViolationException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }
    }

    public Account2 getAccountByUsername(String username) {
        Account2 account = accountRepository.findByUsername(username).orElse(null);
        return account;
    }

    public AccountLogInCred accountLogInCreds(String username, String attemptedPassword) {
        Account2 acc = accountRepository.findByUsername(username).orElse(null);
        if(acc == null) {
            return AccountLogInCred.builder().success(false).id(null).build();
        }
        else
        {
            if(passwordEncoder.matches(attemptedPassword, acc.getPassword())){
                return AccountLogInCred.builder().success(true).id(acc.getId()).token(acc.getPassword()).build();
            }
            else
            {
                return AccountLogInCred.builder().success(false).id(null).build();
            }
        }
    }

    public String getToken(String id) {
        return accountRepository.findById(id).get().getPassword();
    }

    public Account2 getAccountById(String id) {
        return accountRepository.findById(id).get();
    }

    public boolean changePassword(String id, String oldPassword, String newPassword) {
        Account2 account = accountRepository.findById(id).orElse(null);

        if (account == null) {
            // Account not found
            return false;
        }

        if (passwordEncoder.matches(oldPassword, account.getPassword())) {
            // The old password matches the stored password
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            account.setPassword(encodedNewPassword);
            accountRepository.save(account);
            return true;
        } else {
            // Old password doesn't match
            return false;
        }
    }

    public Account2 changeUsername(String id, String newName) {
        Account2 account = accountRepository.findById(id).orElse(null);

        if (account != null) {
            // Update the name and set Name to the lowercase version of newName
            account.setUsername(newName.toLowerCase());
            account.setName(newName);
            // Save the updated account in the database
            return accountRepository.save(account);
        } else {
            // Account not found
            return null;
        }
    }

    public Account2 changeHourlyRate(String id, Double newHourlyRate) {
        Account2 account = accountRepository.findById(id).orElse(null);

        if (account != null) {
            account.setHourlyRate(newHourlyRate);
            accountRepository.save(account);
            dayService.updateHourlyRates();
            return account;
        } else {
            return null;
        }
    }

    public Account2 changeFixedSalary(String id, Double newFixedSalary) {
        Account2 account = accountRepository.findById(id).orElse(null);

        if (account != null) {
            account.setFixedSalary(newFixedSalary);
            accountRepository.save(account);
            return account;
        } else {
            return null;
        }
    }
}
