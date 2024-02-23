package com.example.HRfinalproject.service;

import com.example.HRfinalproject.dto.timesheetDto.*;
import com.example.HRfinalproject.entity.Timesheet;
import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.enums.StatusType;
import com.example.HRfinalproject.exceptions.TimesheetNotFoundException;
import com.example.HRfinalproject.repository.TimesheetRepository;
import com.example.HRfinalproject.repository.UserRepository;
import com.example.HRfinalproject.validators.TimesheetValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TimesheetService {
    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<TimesheetResponse> createTimeSheet(SaveTimesheetRequest timesheetRequest) {
        Optional<User> optionalUser = userRepository.findById(timesheetRequest.getUser().getId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        LocalDate fromDate = timesheetRequest.getSaveTimesheet().getFromDate();
        LocalDate toDate = timesheetRequest.getSaveTimesheet().getToDate();

        // Ensure toDate is after fromDate
        if (toDate.isBefore(fromDate)) {
            return ResponseEntity.badRequest().body(new TimesheetResponse(null, "ToDate must be after FromDate."));
        }

        // Ensure timesheet dates are within the current year
        LocalDate now = LocalDate.now();
        if (fromDate.getYear() != now.getYear() || toDate.getYear() != now.getYear()) {
            return ResponseEntity.badRequest().body(new TimesheetResponse(null, "Timesheet dates must be within the current year."));
        }

        // Ensure timesheet does not overlap with existing timesheets
        List<Timesheet> existingTimesheets = timesheetRepository.findByUserId(user.getId());
        for (Timesheet existing : existingTimesheets) {
            if (fromDate.isBefore(existing.getToDate()) && toDate.isAfter(existing.getFromDate())) {
                return ResponseEntity.badRequest().body(new TimesheetResponse(null, "New timesheet overlaps with an existing timesheet."));
            }
        }

        // Ensure user has enough daysOff available
        int daysRequested = TimesheetValidators.getBusinessDaysBetween(fromDate, toDate);
        if (user.getDaysOff() < daysRequested) {
            return ResponseEntity.badRequest().body(new TimesheetResponse(null, "Not enough days off available."));
        }

        // Proceed with timesheet creation without updating daysOff
        Timesheet newTimesheet = Timesheet.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .note(timesheetRequest.getSaveTimesheet().getNote())
                .status(StatusType.PENDING)
                .user(user)
                .createdAt(now)
                .createdBy(user.getUsername())
                .modifiedAt(now)
                .modifiedBy(user.getUsername())
                .build();

        timesheetRepository.save(newTimesheet);

        return ResponseEntity.ok(new TimesheetResponse(timesheetRequest, null));
    }

    public ResponseEntity<UpdateTimesheetResponse> updateTimesheetUser(Long id, UpdateTimesheetByUserRequest timesheet) throws Exception {
        Optional<Timesheet> optionalUpdatedTimesheet = timesheetRepository.findById(id);

        if (optionalUpdatedTimesheet.isEmpty()) {
            throw new TimesheetNotFoundException("Time Sheet with id: " + id + " doesn't exist");
        }

        Timesheet updatedTimesheet = optionalUpdatedTimesheet.get();

        if (updatedTimesheet.getStatus() != StatusType.PENDING){
            return ResponseEntity.badRequest().body(new UpdateTimesheetResponse(null, "Only time sheets with status of 'PENDING' can be updated"));
        }

        LocalDate fromDate = timesheet.getFromDate();
        LocalDate toDate = timesheet.getToDate();

        // Ensure toDate is after fromDate
        if (toDate.isBefore(fromDate)) {
            return ResponseEntity.badRequest().body(new UpdateTimesheetResponse(null, "To-Date must be after From-Date."));
        }

        // Ensure timesheet dates are within the current year
        if (fromDate.getYear() != 2024 || toDate.getYear() != 2024) {
            return ResponseEntity.badRequest().body(new UpdateTimesheetResponse(null, "Timesheet dates must be within the current year 2024."));
        }

        // Ensure timesheet does not overlap with existing timesheets
        List<Timesheet> existingTimesheets = timesheetRepository.findByUserId(updatedTimesheet.getUser().getId());
        for (Timesheet existing : existingTimesheets) {
            if (existing.getId() != id && fromDate.isBefore(existing.getToDate()) && toDate.isAfter(existing.getFromDate())) {
                return ResponseEntity.badRequest().body(new UpdateTimesheetResponse(null, "Updated timesheet overlaps with an existing timesheet."));
            }
        }

        // Calculate the number of days requested for the updated timesheet
        int daysRequested = TimesheetValidators.getBusinessDaysBetween(fromDate, toDate);

        // Ensure user has enough daysOff available (consider only approved timesheets for calculation)
        int totalDaysOffUsed = existingTimesheets.stream()
                .filter(t -> t.getStatus() == StatusType.APPROVED)
                .mapToInt(t -> TimesheetValidators.getBusinessDaysBetween(t.getFromDate(), t.getToDate()))
                .sum();
        User user = updatedTimesheet.getUser();
        if (user.getDaysOff() < (totalDaysOffUsed + daysRequested)) {
            return ResponseEntity.badRequest().body(new UpdateTimesheetResponse(null, "Not enough days off available."));
        }

        // Proceed with timesheet update
        updatedTimesheet.setFromDate(fromDate);
        updatedTimesheet.setToDate(toDate);
        updatedTimesheet.setNote(timesheet.getNote());
        updatedTimesheet.setModifiedAt(LocalDate.now());
        updatedTimesheet.setModifiedBy(timesheet.getModifiedBy());
        timesheetRepository.save(updatedTimesheet);

        return ResponseEntity.ok(new UpdateTimesheetResponse(timesheet, null));
    }

    public ResponseEntity<UpdateTimesheetByManagerRequest> updateTimesheetManager(Long id, UpdateTimesheetByManagerRequest timesheet) throws Exception {
        Optional<Timesheet> optionalUpdatedTimesheet = timesheetRepository.findById(id);
        if (optionalUpdatedTimesheet.isEmpty()) {
            throw new TimesheetNotFoundException("Time Sheet with id: " + id + " doesn't exist");
        }

        Timesheet updatedTimesheet = optionalUpdatedTimesheet.get();
        StatusType originalStatus = updatedTimesheet.getStatus();
        updatedTimesheet.setStatus(timesheet.getStatus());
        updatedTimesheet.setModifiedAt(LocalDate.now());
        updatedTimesheet.setModifiedBy(timesheet.getModifiedBy());
        timesheetRepository.save(updatedTimesheet);

        // Deduct days off if the status is changed to APPROVED
        if (originalStatus != StatusType.APPROVED && timesheet.getStatus() == StatusType.APPROVED) {
            LocalDate fromDate = updatedTimesheet.getFromDate();
            LocalDate toDate = updatedTimesheet.getToDate();
            int businessDays = TimesheetValidators.getBusinessDaysBetween(fromDate, toDate);

            User user = updatedTimesheet.getUser();
            if (user.getDaysOff() >= businessDays) {
                user.setDaysOff(user.getDaysOff() - businessDays);
                userRepository.save(user);
            }
        }

        return ResponseEntity.ok(timesheet);
    }

    public ResponseEntity deleteTimesheet(Long id) throws TimesheetNotFoundException {
        Optional<Timesheet> deleteTimesheet = timesheetRepository.findById(id);
        if (deleteTimesheet.isEmpty()) {
            throw new TimesheetNotFoundException("Time Sheet with id: " + id + " doesnt exist");
        }
        timesheetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<Timesheet>> getTimesheetsByUserId(Long userId){
        List<Timesheet> timesheets = timesheetRepository.findTimeSheetsByUserId(userId);
        return ResponseEntity.ok().body(timesheets);
    }

    public List<Timesheet> getAllTimesheets() {
        return timesheetRepository.findAll();
    }
}
