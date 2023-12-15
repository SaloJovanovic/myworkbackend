package com.example.myworkbackend.controlers;

import com.example.myworkbackend.models.Account;
import com.example.myworkbackend.models.Day;
import com.example.myworkbackend.services.DayService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/day")
public class DayController {
    private final DayService dayService;

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE)
                .build();
    }

    @PostMapping("/create")
    @CrossOrigin
    public List<Day> createDay(@RequestBody Day dayCreationInfo) {
        return dayService.createWeek(dayCreationInfo);
    }

    @GetMapping("/get")
    @CrossOrigin
    public Optional<Day> getDayByDate(@RequestParam LocalDate date) {
        return dayService.getDayByDate(date);
    }

    @PutMapping("/updateShift")
    @CrossOrigin
    public Day updateShift(@RequestParam LocalDate date, @RequestParam String id, @RequestParam String newShift) {
        return dayService.updateShift(date, id, newShift);
    }

    @PutMapping("/updateTime")
    @CrossOrigin
    public Day updateTime(@RequestParam LocalDate date, @RequestParam String id, @RequestParam String startTime, @RequestParam String endTime) {
        return dayService.updateTime(date, id, startTime, endTime);
    }

    @PutMapping("/updateDay")
    @CrossOrigin
    public Day updateDay(@RequestParam LocalDate date, @RequestBody Day newDay) {
        return dayService.updateDay(date, newDay);
    }


    @PutMapping("/updateDayNote")
    @CrossOrigin
    public Day updateDayNote(@RequestParam LocalDate date, @RequestParam String newDayNote) {
        return dayService.updateDayNote(date, newDayNote);
    }

    @PutMapping("/updateWeekNote")
    @CrossOrigin
    public Day updateWeekNote(@RequestParam LocalDate date, @RequestParam String newWeekNote) {
        return dayService.updateWeekNote(date, newWeekNote);
    }

    @GetMapping("/get-todays-date")
    @CrossOrigin
    public LocalDate getTodaysDate() {
        return dayService.getTodaysDate();
    }

    @GetMapping("/get-week")
    @CrossOrigin
    public List<Day> getWeek(@RequestParam LocalDate inputDate) {
        return dayService.getWeek(inputDate);
    }

    @PutMapping("/changeStatus")
    @CrossOrigin
    public Account changeStatus(@RequestParam String accountId) {
        return dayService.changeStatus(accountId);
    }

    @PutMapping("/updateHourlyRates")
    @CrossOrigin
    public void updateHourlyRates() {
        dayService.updateHourlyRates();
    }

//    @PutMapping("/updateWeek")
//    @CrossOrigin
//    public List<Day> updateWeek(@RequestParam LocalDate date, String accountId) {
//        return dayService.updateWeek(date, accountId);
//    }
}
