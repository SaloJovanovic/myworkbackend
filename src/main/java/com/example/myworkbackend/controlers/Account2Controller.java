package com.example.myworkbackend.controlers;

import com.example.myworkbackend.models.Account2;
import com.example.myworkbackend.models.AccountLogInCred;
import com.example.myworkbackend.services.Account2Service;
import com.example.myworkbackend.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/2/account")
public class Account2Controller {
    private final Account2Service accountService;

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE)
                .build();
    }

    @PostMapping("/create")
    @CrossOrigin
    public Account2 createAccount(@RequestBody Account2 accountInfo) {
        return accountService.createAccount(accountInfo);
    }

    @PostMapping("/login")
    @CrossOrigin
    public AccountLogInCred accountLogInCreds(@RequestParam String username, @RequestParam String attemptedPassword) {
        return accountService.accountLogInCreds(username, attemptedPassword);
    }

    @GetMapping("/get-token")
    @CrossOrigin
    public String getToken(@RequestParam String id) {
        return accountService.getToken(id);
    }

    @GetMapping("/get-account")
    @CrossOrigin
    public Account2 getAccountById(@RequestParam String id) {
        return accountService.getAccountById(id);
    }

    @PutMapping("/change-password")
    @CrossOrigin
    public boolean changePassword(@RequestParam String id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return accountService.changePassword(id, oldPassword, newPassword);
    }

    @PutMapping("/change-username")
    @CrossOrigin
    public Account2 changeUsername(@RequestParam String id, @RequestParam String newName) {
        return accountService.changeUsername(id, newName);
    }

    @PutMapping("/changeHourlyRate")
    @CrossOrigin
    public Account2 changeHourlyRate(@RequestParam String id, @RequestParam Double newHourlyRate) {
        return  accountService.changeHourlyRate(id, newHourlyRate);
    }

    @PutMapping("/change-fixedSalary")
    @CrossOrigin
    public Account2 changeFixedSalary(@RequestParam String id, @RequestParam Double newFixedSalary) {
        return accountService.changeFixedSalary(id, newFixedSalary);
    }
}
