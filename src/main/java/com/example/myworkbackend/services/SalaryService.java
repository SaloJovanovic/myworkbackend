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

import java.time.DayOfWeek;
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
        List<Salary> salaries = new ArrayList<>();

        System.out.println("QWERQWERQWERQWER - " + date.plusMonths(1).withDayOfMonth(1).plusDays(1));

        if (LocalDate.now().isBefore(date.plusMonths(1).withDayOfMonth(1).plusDays(1))) {
            for (Account account : accounts) {
                Salary salary = createOrUpdateSalary(date, account.getId());
                salaries.add(salary);
            }
        }
        else {
            salaries = getAllSalariesForMonth(date);
        }

        return salaries;
    }

    public List<Salary> createOrUpdateAllSalariesInCase(LocalDate date) {
        List<Account> accounts = accountRepository.findAll();
        List<Salary> salaries = new ArrayList<>();

        for (Account account : accounts) {
            Salary salary = createOrUpdateSalary(date, account.getId());
            salaries.add(salary);
        }

        return salaries;
    }

    public Salary createOrUpdateSalary(LocalDate date, String accountId) {
        List<Salary> existingSalaries = salaryRepository.findAllByAccountId(accountId);
        List<Salary> salariesForMonth = existingSalaries.stream()
                .filter(salary -> salary.getDate().getMonth() == date.getMonth())
                .toList();

        Account account = accountRepository.findById(accountId).orElse(null);
        System.out.println("??????????????????????????????? " + account.getUsername() + " " + account.getRole());

        if (!salariesForMonth.isEmpty()) {
            Salary existingSalary = salariesForMonth.get(0);
            double updatedSalaryValue = calculateTotalSalary(date, accountId);
            existingSalary.setSalary(updatedSalaryValue);
            return salaryRepository.save(existingSalary);
        } else {
            double totalSalaryValue = calculateTotalSalary(date, accountId);
            assert account != null;
            System.out.println("################################ " + account.getUsername() + " " + account.getRole());
            Salary newSalary = Salary.builder()
                    .date(date)
                    .accountId(accountId)
                    .salary(totalSalaryValue)
                    .name(account.getName())
                    .username(account.getUsername())
                    .role(account.getRole())
                    .active(account.getActive())
                    .hourlyRate(account.getHourlyRate())
                    .fixedSalary(account.getFixedSalary())
                    .build();
            return salaryRepository.save(newSalary);
        }
    }

    private double calculateTotalSalary(LocalDate date, String accountId) {
        double totalSalaryValue = 0.0;

        System.out.println("Duzina dana: " + dayRepository.findByDate(date.withDayOfMonth(date.lengthOfMonth())));

        List<Day> daysInMonth = dayRepository.findByDateBetween(
                date.withDayOfMonth(1),
                date.withDayOfMonth(date.lengthOfMonth()).plusDays(1)
        );

        System.out.println("Svi dani u mesecu: " + daysInMonth);

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account != null) {
            System.out.println("Osoba: " + account.getUsername());
            Double fixedSalary = account.getFixedSalary();

            for (Day day : daysInMonth) {
                List<String> employeeIds = day.getEmployeesIds();
                List<Double> hourlyRates = day.getHourlyRates() != null ? List.of(day.getHourlyRates()) : new ArrayList<>();
                List<String> startTimes = day.getStartTimes() != null ? List.of(day.getStartTimes()) : new ArrayList<>();
                List<String> endTimes = day.getEndTimes() != null ? List.of(day.getEndTimes()) : new ArrayList<>();
                List<String> shifts = day.getShifts() != null ? List.of(day.getShifts()) : new ArrayList<>();

                for (int i = 1; i < employeeIds.size(); i++) {
                    if (employeeIds.get(i).equals(accountId)) {
                        System.out.println(day.getDate());
                        double dailySalary = calculateDailySalary(startTimes.get(i-1), endTimes.get(i-1), hourlyRates.get(i-1), shifts.get(i-1), fixedSalary);
//                        double dailySalary = calculateDailySalary(
//                                i < startTimes.size() ? startTimes.get(i-1) : null,
//                                i < endTimes.size() ? endTimes.get(i-1) : null,
//                                i < hourlyRates.size() ? hourlyRates.get(i-1) : 0.0,
//                                i < shifts.size() ? shifts.get(i-1) : null,
//                                fixedSalary
//                        );
                        totalSalaryValue += dailySalary;
                    }
                }
            }

            if (fixedSalary != null && fixedSalary > 0) {
                totalSalaryValue += fixedSalary;
            }
        } else {
            log.error("Account with ID {} not found.", accountId);
        }

        System.out.println("NALOG: " + accountRepository.findById(accountId).get().getUsername() + " - " + totalSalaryValue);

        return totalSalaryValue;
    }



    private double calculateDailySalary(String startTime, String endTime, double hourlyRate, String shift, Double fixedSalary) {
        double totalHours = 0;

        try {
            if (startTime != null && !startTime.equals("0") && endTime != null && !endTime.equals("0")) {
                LocalTime start = LocalTime.parse(startTime);
                LocalTime end = LocalTime.parse(endTime);

                // Ako je endTime manje od startTime, dodaj 24 sata na endTime
                if (end.isBefore(start)) {
                    end = end.plusHours(24);
                }

                double hoursDif = 0;
                if (end.getHour() <= start.getHour()) {
                    hoursDif = (end.getHour()+24) - start.getHour();
                }
                else {
                    hoursDif = end.getHour() - start.getHour();
                }
                hoursDif *= 60;
                double minsDif = end.getMinute() - start.getMinute();
                hoursDif += minsDif;

                // Izračunaj trajanje između start i end vremena
                Duration duration = Duration.between(start, end);

                // Izračunaj ukupan broj radnih sati
                totalHours = hoursDif / 60.0;
                if (totalHours > 15)
                    return 0;
                System.out.println("    broj sati: " + start + " - " + end + "; " + totalHours);
            } else {
                log.warn("Invalid startTime or endTime: startTime={}, endTime={}", startTime, endTime);
            }
        } catch (Exception e) {
            log.error("Error parsing time: " + e.getMessage());
        }

        // Izračunaj platu na osnovu smene
        if ("O".equals(shift)) {
            if (fixedSalary == null || fixedSalary <= 0) {
                return totalHours * hourlyRate * 0.6;
            } else {
                return -fixedSalary / 26 * 0.6;
            }
        } else {
            return totalHours * hourlyRate;
        }
    }


    public List<Salary> getAllSalariesForMonth(LocalDate inputDate) {
        LocalDate startDate = inputDate.withDayOfMonth(1);
        LocalDate endDate = inputDate.withDayOfMonth(inputDate.lengthOfMonth());
        return salaryRepository.findByDateBetween(startDate, endDate);
    }
}