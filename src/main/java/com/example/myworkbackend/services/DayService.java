package com.example.myworkbackend.services;

import com.example.myworkbackend.models.Account;
import com.example.myworkbackend.models.Day;
import com.example.myworkbackend.repositories.AccountRepository;
import com.example.myworkbackend.repositories.DayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class DayService {
    private final DayRepository dayRepository;
    private final AccountRepository accountRepository;

    public List<String> getAllAccountsIds() {
        List<Account> accounts = accountRepository.findAll();
        List<String> accountsIds = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getActive().equals(1))
                accountsIds.add(accounts.get(i).getId());
        }
        return accountsIds;
    }

    public List<String> getAllAccountsNames() {
        List<Account> accounts = accountRepository.findAll();
        List<String> accountsNames = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getActive().equals(1))
                accountsNames.add(accounts.get(i).getName());
        }
        return accountsNames;
    }

    public LocalDate getTodaysDate() {
        ZoneId cetTimeZone = ZoneId.of("Europe/Warsaw"); // You can use other CET time zones as needed

        // Get the current date in the CET time zone
        LocalDate cetDate = ZonedDateTime.now(cetTimeZone).toLocalDate();

        return cetDate;
    }

    @Transactional
    public List<Day> createWeek(Day dayInfo) {
        LocalDate inputDate = dayInfo.getDate();

        // Calculate the start and end dates for the entire week (Monday to Sunday)
        LocalDate weekStartDate = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDate = inputDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Day> weekDays = new ArrayList<>();

        // Check for existing days first
        for (LocalDate date = weekStartDate; !date.isAfter(weekEndDate); date = date.plusDays(1)) {
            Optional<Day> existingDay = dayRepository.findByDate(date);
            if (existingDay.isPresent()) {
                weekDays.add(existingDay.get());
            }
        }

        // Create missing days
        for (LocalDate date = weekStartDate; !date.isAfter(weekEndDate); date = date.plusDays(1)) {
            Optional<Day> existingDay = dayRepository.findByDate(date);
            if (!existingDay.isPresent()) {
                List<String> employeeIds = getAllAccountsIds();
                List<String> employeeNames = getAllAccountsNames();

                String[] emptyShifts = new String[employeeIds.size() - 1];
                Arrays.fill(emptyShifts, "0");

                String[] emptyStartTimes = new String[employeeIds.size() - 1];
                Arrays.fill(emptyStartTimes, "0");

                Double[] hourlyRates = new Double[employeeIds.size() - 1];
                for (int i = 0; i < hourlyRates.length; i++) {
                    hourlyRates[i] = accountRepository.findById(employeeIds.get(i+1)).get().getHourlyRate();
                }

                String[] emptyEndTimes = new String[employeeNames.size() - 1];
                Arrays.fill(emptyEndTimes, "0");

                Day day = Day.builder()
                        .date(date)
                        .employeesIds(employeeIds)
                        .employeesNames(employeeNames)
                        .shifts(emptyShifts)
                        .startTimes(emptyStartTimes)
                        .hourlyRates(hourlyRates)
                        .endTimes(emptyEndTimes)
                        .weekNote("")
                        .dayNote("")
                        .build();

                try {
                    weekDays.add(dayRepository.save(day));
                } catch (DataIntegrityViolationException exception) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }
        }

        return weekDays;
    }

    public void updateHourlyRates() {
        List<Day> daysToUpdate = dayRepository.findByDateGreaterThanEqual(getTodaysDate());

        for (Day currentDay : daysToUpdate) {
            Double[] hourlyRates = currentDay.getHourlyRates();
            Double[] newHourlyRates = new Double[hourlyRates.length];
            List<String> employeeIds = currentDay.getEmployeesIds();
            for (int i = 0; i < hourlyRates.length; i++) {
                newHourlyRates[i] = accountRepository.findById(employeeIds.get(i+1)).get().getHourlyRate();
            }
            currentDay.setHourlyRates(newHourlyRates);
            dayRepository.save(currentDay);
        }
    }

    @Transactional
    public List<Day> updateWeek(LocalDate inputDate, String updatedAccountId, int previousAccountNum) {
        // Calculate the start and end dates for the entire week (Monday to Sunday)
        LocalDate weekStartDate = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDate = inputDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Day> updatedWeekDays = new ArrayList<>();

        List<String> employeeIds = getAllAccountsIds();
        List<String> employeeNames = getAllAccountsNames();
        Account changedAccount = accountRepository.findById(updatedAccountId).get();

        int updatedAccountNum = 0;

        for (int i = 0; i < employeeIds.size(); i++) {
            if (employeeIds.get(i).equals(updatedAccountId))
                updatedAccountNum = i - 1;
        }

        for (LocalDate date = weekStartDate; !date.isAfter(weekEndDate); date = date.plusDays(1)) {
            Optional<Day> existingDay = dayRepository.findByDate(date);

            if (existingDay.isPresent()) {
                // Update the existing day
                Day dayToUpdate = existingDay.get();

                // Perform other necessary updates based on your requirements

                ArrayList<String> shifts = new ArrayList<>(Arrays.asList(dayToUpdate.getShifts()));
                ArrayList<String> startTimes = new ArrayList<>(Arrays.asList(dayToUpdate.getStartTimes()));
                ArrayList<Double> hourlyRates = new ArrayList<>(Arrays.asList(dayToUpdate.getHourlyRates()));
                ArrayList<String> endTimes = new ArrayList<>(Arrays.asList(dayToUpdate.getEndTimes()));
                if (changedAccount.getActive().equals(1)) {

                    shifts.add(updatedAccountNum, "0");

                    double hourlyRate = accountRepository.findById(updatedAccountId).get().getHourlyRate();
                    hourlyRates.add(updatedAccountNum, hourlyRate);

                    startTimes.add(updatedAccountNum, "0");

                    endTimes.add(updatedAccountNum, "0");

                    dayToUpdate.setEmployeesIds(employeeIds);
                    dayToUpdate.setEmployeesNames(employeeNames);
                    dayToUpdate.setShifts(shifts.toArray(new String[0]));
                    dayToUpdate.setStartTimes(startTimes.toArray(new String[0]));
                    dayToUpdate.setHourlyRates(hourlyRates.toArray(new Double[0]));
                    dayToUpdate.setEndTimes(endTimes.toArray(new String[0]));
                }
                else {
                    String[] newShifts = new String[shifts.toArray().length - 1];
                    String[] newStartTimes = new String[shifts.toArray().length - 1];
                    Double[] newHourlyRates = new Double[shifts.toArray().length - 1];
                    String[] newEndTimes = new String[shifts.toArray().length - 1];
                    if (previousAccountNum >= 0 && previousAccountNum < shifts.toArray().length) {
                        int newArrayIndex = 0;
                        for (int i = 0; i < shifts.toArray().length; i++) {
                            if (i != previousAccountNum) {
                                newShifts[newArrayIndex] = shifts.get(i);
                                newStartTimes[newArrayIndex] = startTimes.get(i);
                                newHourlyRates[newArrayIndex] = hourlyRates.get(i);
                                newEndTimes[newArrayIndex] = endTimes.get(i);
                                newArrayIndex++;
                            }
                        }
                        dayToUpdate.setEmployeesIds(employeeIds);
                        dayToUpdate.setEmployeesNames(employeeNames);
                        dayToUpdate.setShifts(newShifts);
                        dayToUpdate.setStartTimes(newStartTimes);
                        dayToUpdate.setHourlyRates(newHourlyRates);
                        dayToUpdate.setEndTimes(newEndTimes);
                    }
                }
                try {
                    updatedWeekDays.add(dayRepository.save(dayToUpdate));
                } catch (DataIntegrityViolationException exception) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }
        }

        return updatedWeekDays;
    }

    public Account changeStatus(String id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            if (account.getActive().equals(1))
                account.setActive(0);
            else
                account.setActive(1);
            accountRepository.save(account);
//            Day day = Day.builder()
//                    .date(getTodaysDate())
//                    .build();
//            createWeek(day);
            Day day = dayRepository.findByDate(getTodaysDate()).get();
            List<String> employeeIds = day.getEmployeesIds();
            int updatedAccountNum = 0;
            for (int i = 0; i < employeeIds.size(); i++) {
                if (employeeIds.get(i).equals(account.getId()))
                    updatedAccountNum = i - 1;
            }
            updateWeek(getTodaysDate(), account.getId(), updatedAccountNum);
            LocalDate nextWeekDate = getTodaysDate().plusWeeks(1);
            if (dayRepository.findByDate(nextWeekDate) != null)
                updateWeek(nextWeekDate, account.getId(), updatedAccountNum);
            return account;
        }
        return null;
    }


    public List<Day> getWeek(LocalDate inputDate) {
        List<Day> weekDays = new ArrayList<>();

        // Calculate the start date of the week (Monday)
        LocalDate weekStartDate = inputDate.with(DayOfWeek.MONDAY);

        // Generate Day objects for all 7 days of the week
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);

            // Check if a Day object exists for the current date
            Optional<Day> existingDay = dayRepository.findByDate(currentDate);

            if (existingDay.isPresent()) {
                weekDays.add(existingDay.get());
            }
        }

        return weekDays;
    }

    public Optional<Day> getDayByDate(LocalDate date) {
        return dayRepository.findByDate(date);
    }

    public Day updateShift(LocalDate date, String id, String newShift) {
        Day day = dayRepository.findByDate(date).orElse(null); // Use orElse(null) to handle case where day is not found
        System.out.println("day " + day);
        if (day != null) {
            String[] shifts = day.getShifts();
            int r = -1;

            for (int i = 1; i < day.getEmployeesIds().size(); i++) {
                if (day.getEmployeesIds().get(i).equals(id)) {
                    r = i - 1;
                    break;
                }
            }

            if (r != -1) {
                shifts[r] = newShift;
                day.setShifts(shifts);
                dayRepository.save(day); // Save the modified day object
                return day;
            }
        }

        return null; // Return null if the day is not found or the employee ID is not found in the shifts array
    }

    public Day updateTime(LocalDate date, String id, String startTime, String endTime) {
        Day day = dayRepository.findByDate(date).orElse(null); // Use orElse(null) to handle case where day is not found
        System.out.println("day " + day);
        if (day != null) {
            String[] startTimes = day.getStartTimes();
            String[] endTimes = day.getEndTimes();
            int r = -1;

            for (int i = 1; i < day.getEmployeesIds().size(); i++) {
                if (day.getEmployeesIds().get(i).equals(id)) {
                    r = i - 1;
                    break;
                }
            }

            if (r != -1) {
                startTimes[r] = startTime;
                day.setStartTimes(startTimes);
                endTimes[r] = endTime;
                day.setEndTimes(endTimes);
                dayRepository.save(day); // Save the modified day object
                return day;
            }
        }

        return null; // Return null if the day is not found or the employee ID is not found in the shifts array
    }

    public Day updateDayNote(LocalDate date, String newDayNote) {
        Day day = dayRepository.findByDate(date).orElse(null);

        if (day != null) {
            day.setDayNote(newDayNote);
            dayRepository.save(day);
            return day;
        }

        return null;
    }

    public Day updateWeekNote(LocalDate date, String newWeekNote) {
        Day day = dayRepository.findByDate(date).orElse(null);
        System.out.print(date + " " + newWeekNote);

        if (day != null) {
            day.setWeekNote(newWeekNote);
            dayRepository.save(day);
            return day;
        }

        return null;
    }

    public Day updateDay(LocalDate date, Day newDay) {
        Day day = dayRepository.findByDate(date).orElse(null);
        day.setShifts(newDay.getShifts());
        day.setStartTimes(newDay.getStartTimes());
        day.setEndTimes(newDay.getEndTimes());
        day.setWeekNote(newDay.getWeekNote());
        day.setDayNote(newDay.getDayNote());
        dayRepository.save(day);
        return day;
    }

//    public Day updateDayNote(LocalDate date, String note)
}
