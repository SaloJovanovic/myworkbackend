package com.example.myworkbackend.services;

import com.example.myworkbackend.models.Account;
import com.example.myworkbackend.models.AccountSalary;
import com.example.myworkbackend.models.Day;
import com.example.myworkbackend.models.Salary;
import com.example.myworkbackend.repositories.AccountRepository;
import com.example.myworkbackend.repositories.DayRepository;
import com.example.myworkbackend.repositories.SalaryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final DayRepository dayRepository;
    private final AccountRepository accountRepository;

    public List<Salary> createOrUpdateAllSalaries(LocalDate date) {
        List<Account> accounts = accountRepository.findAll();

        // Now 'accounts' contains all Account entities, and you can extract IDs as needed.
//        List<String> accountIds = accounts.stream()
//                .map(Account::getId)
//                .collect(Collectors.toList());

        List<Salary> salaries = new ArrayList<>();

        for (int i = 1; i < accounts.size(); i++) {
            System.out.println(i + " - " + accounts.get(i));
            salaries.add(createOrUpdateSalary(date, accounts.get(i).getId()));
        }

        return salaries;
    }

    public Salary createOrUpdateSalary(LocalDate date, String accountId) {
        // Check if a salary entry for the given month and accountId already exists
        System.out.println("11111111111111111111111111\n");

        List<Salary> existingSalaries = salaryRepository.findAllByAccountId(accountId);

        System.out.println("22222222222222222222222222\n");

        // Filter salaries for the given month
        List<Salary> salariesForMonth = existingSalaries.stream()
                .filter(salary -> salary.getDate().getMonth() == date.getMonth())
                .toList();
        System.out.println("33333333333333333333333333\n");

        if (!salariesForMonth.isEmpty()) {
            // Update existing salary entries
//            for (Salary existingSalary : salariesForMonth) {
            double updatedSalaryValue = calculateTotalSalary(salariesForMonth.get(0).getDate(), accountId);
            salariesForMonth.get(0).setSalary(updatedSalaryValue);
            return salaryRepository.save(salariesForMonth.get(0));
//            }
        } else {
            // No existing salary entry found for the month, create a new one
            System.out.println("44444444444444444444444444\n");

            double totalSalaryValue = calculateTotalSalary(date, accountId);
            System.out.println("55555555555555555555555555\n");

            // Create the Salary object and save it
            Salary salary = Salary.builder()
                    .date(date)
                    .accountId(accountId)
                    .salary(totalSalaryValue)
                    .build();
            System.out.println("66666666666666666666666666\n");

            return salaryRepository.save(salary);
        }

        // You can return a meaningful result here if needed
    }

    private double calculateTotalSalary(LocalDate date, String accountId) {
        double totalSalaryValue = 0.0;

        // Assuming you have a method to retrieve all days in the given month
        List<Day> daysInMonth = dayRepository.findByDateBetween(
                date.withDayOfMonth(1),
                date.withDayOfMonth(date.lengthOfMonth())
        );

        // Assuming you have a method to retrieve the account details by accountId
        Account account = accountRepository.findById(accountId).orElse(null);

        if (account != null) {
            for (Day day : daysInMonth) {
                // Assuming employeesIds list contains the IDs of employees who worked on that day
                List<String> employeeIds = day.getEmployeesIds();
                Double[] hourlyRates = day.getHourlyRates();
                String[] startTimes = day.getStartTimes();
                String[] endTimes = day.getEndTimes();

                for (int i = 0; i < hourlyRates.length; i++) {
                    System.out.print(hourlyRates[i] + " " + startTimes[i] + " " + endTimes[i] + "\n");
                }

//                System.out.println(employeeIds + "\n" + hourlyRates + "\n" + startTimes + "\n" + endTimes + "\n" + day);

                for (int i = 1; i < employeeIds.size(); i++) {
                    System.out.println(employeeIds.get(i) + " " + accountId + "\n");
                    if (employeeIds.get(i).equals(accountId)) {
                        double hourlyRate = hourlyRates[i-1];
                        String startTime = startTimes[i-1];
                        String endTime = endTimes[i-1];
                        System.out.println("asdfasdfasdfasdf " + startTime + " " + endTime);

                        if (!startTime.equals(endTime)) {
                            System.out.println("qwerqwerqwer");
                            totalSalaryValue += calculateDailySalary(startTime, endTime, hourlyRate);
                        }
                    }
                }
            }
        } else {
            log.error("Account with ID {} not found.", accountId);
        }

        return totalSalaryValue;
    }

    private double calculateDailySalary(String startTime, String endTime, double hourlyRate) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // Calculate the duration between start and end times
        Duration duration = Duration.between(start, end);

        // Calculate total hours worked
        double totalHours = duration.toMinutes() / 60.0;

        // Multiply total hours by hourly rate to get the total salary
        return totalHours * hourlyRate;
    }

    public List<AccountSalary> getAllSalariesForMonth(LocalDate inputDate) {
        LocalDate startDate = inputDate.withDayOfMonth(1);
        LocalDate endDate = inputDate.withDayOfMonth(inputDate.lengthOfMonth());

        // Fetch all salaries for the specified month
        List<Salary> allSalaries = salaryRepository.findByDateBetween(startDate, endDate);

        List<AccountSalary> accountSalaries = new ArrayList<>();

        for (Salary salary : allSalaries) {
            Account account = accountRepository.findById(salary.getAccountId()).orElse(null);
            if (account != null) {
                AccountSalary accountSalary = AccountSalary.builder()
                        .username(account.getUsername())
                        .accountId(salary.getAccountId())
                        .role(account.getRole())
                        .name(account.getName())
                        .username(account.getUsername())
                        .hourlyRate(account.getHourlyRate())
                        .active(account.getActive())
                        .salary(salary.getSalary())
                        .build();
                accountSalaries.add(accountSalary);
            }
        }

        return accountSalaries;
    }
}