package com.example.myworkbackend.controlers;

import com.example.myworkbackend.models.Salary;
import com.example.myworkbackend.services.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/salary")
public class SalaryController {
    private final SalaryService salaryService;

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE)
                .build();
    }

    @PostMapping("/create")
    @CrossOrigin
    public Salary createSalary(@RequestParam LocalDate date, @RequestParam String accountId) {
        return salaryService.createOrUpdateSalary(date, accountId);
    }

    @PostMapping("/createAll")
    @CrossOrigin
    public List<Salary> createAllSalaries(@RequestParam LocalDate date) {
        return salaryService.createOrUpdateAllSalaries(date);
    }

    @PostMapping("/inCase")
    @CrossOrigin
    public List<Salary> inCase(@RequestParam LocalDate date) {
        return salaryService.createOrUpdateAllSalariesInCase(date);
    }

    @GetMapping("/get")
    @CrossOrigin
    public List<Salary> getAllSalariesForMonth(@RequestParam LocalDate date) {
        return salaryService.getAllSalariesForMonth(date);
    }
}
