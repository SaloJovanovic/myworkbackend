package com.example.mywork.services;

import com.example.mywork.models.Account;
import com.example.mywork.models.AccountLogInCred;
import com.example.mywork.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    private PasswordEncoder passwordEncoder;

    public Account createAccount(Account accountInfo) {
        Account account = Account.builder()
                .name(accountInfo.getName())
                .username(accountInfo.getUsername())
                .password(passwordEncoder.encode(accountInfo.getPassword()))
                .role(accountInfo.getRole())
                .build();
        try { return accountRepository.save(account); }
        catch (DataIntegrityViolationException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }
    }

    public Account getAccountByUsername(String username) {
        Account account = accountRepository.findByUsername(username).orElse(null);
        return account;
    }

    public AccountLogInCred accountLogInCreds(String username, String attemptedPassword) {
        Account acc = accountRepository.findByUsername(username).orElse(null);
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

    public Account getAccountById(String id) {
        return accountRepository.findById(id).get();
    }

    public boolean changePassword(String id, String oldPassword, String newPassword) {
        Account account = accountRepository.findById(id).orElse(null);

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
}
