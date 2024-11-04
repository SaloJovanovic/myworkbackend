package com.example.myworkbackend.controlers;

import com.example.myworkbackend.models.Salary2;
import com.example.myworkbackend.services.SalaryService2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/2/salary")
public class Salary2Controller {
    private final SalaryService2 salaryService;

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE)
                .build();
    }

    @PostMapping("/create")
    @CrossOrigin
    public Salary2 createSalary(@RequestParam LocalDate date, @RequestParam String accountId) {
        return salaryService.createOrUpdateSalary(date, accountId);
    }

    @PostMapping("/createAll")
    @CrossOrigin
    public List<Salary2> createAllSalaries(@RequestParam LocalDate date) {
        return salaryService.createOrUpdateAllSalaries(date);
    }

    @PostMapping("/inCase")
    @CrossOrigin
    public List<Salary2> inCase(@RequestParam LocalDate date) {
        return salaryService.createOrUpdateAllSalariesInCase(date);
    }

    @GetMapping("/get")
    @CrossOrigin
    public List<Salary2> getAllSalariesForMonth(@RequestParam LocalDate date) {
        return salaryService.getAllSalariesForMonth(date);
    }
}
