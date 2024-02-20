package com.example.HRfinalproject.service;

import com.example.HRfinalproject.dto.timesheetDto.*;
import com.example.HRfinalproject.entity.Timesheet;
import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.enums.StatusType;
import com.example.HRfinalproject.exceptions.TimesheetNotFoundException;
import com.example.HRfinalproject.repository.TimesheetRepository;
import com.example.HRfinalproject.repository.UserRepository;
import com.example.HRfinalproject.validators.TimesheetValidators;
import jakarta.persistence.EntityNotFoundException;
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

//    public ResponseEntity<SaveTimesheetRequest> createTimeSheet(SaveTimesheetRequest timesheet) {
//        Optional<User> optionalUser = userRepository.findById(timesheet.getUser().getId());
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            Timesheet newTimesheet = Timesheet.builder()
//                    .fromDate(timesheet.getSaveTimesheet().getFromDate())
//                    .toDate(timesheet.getSaveTimesheet().getToDate())
//                    .note(timesheet.getSaveTimesheet().getNote())
//                    .status(StatusType.PENDING)
//                    .user(user)
//                    .createdAt(LocalDate.now())
//                    .createdBy(user.getUsername())
//                    .modifiedAt(LocalDate.now())
//                    .modifiedBy(user.getUsername())
//                    .build();
//            timesheetRepository.save(newTimesheet);
//            return ResponseEntity.ok(timesheet);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    public ResponseEntity<TimesheetResponse> createTimeSheet(SaveTimesheetRequest timesheetRequest) {
        Optional<User> optionalUser = userRepository.findById(timesheetRequest.getUser().getId());
        if (!optionalUser.isPresent()) {
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
        long daysRequested = ChronoUnit.DAYS.between(fromDate, toDate) + 1; // +1 to include both start and end date
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

//    public ResponseEntity<UpdateTimesheetByUserRequest> updateTimesheetUser(Long id, UpdateTimesheetByUserRequest timesheet) throws TimesheetNotFoundException {
//        Optional<Timesheet> updatedTimesheet = timesheetRepository.findById(id);
//        if (updatedTimesheet.isEmpty()) {
//            throw new TimesheetNotFoundException("Time Sheet with id: " + id + " doesnt exist");
//        }
//        if (updatedTimesheet.get().getStatus()!=StatusType.PENDING){
//            throw new TimesheetNotFoundException("Only time sheets with status of \"PENDING\" can be updated");
//        }
//        updatedTimesheet.get().setFromDate(timesheet.getFromDate());
//        updatedTimesheet.get().setToDate(timesheet.getToDate());
//        updatedTimesheet.get().setNote(timesheet.getNote());
//        updatedTimesheet.get().setModifiedAt(LocalDate.now());
//        updatedTimesheet.get().setModifiedBy(timesheet.getModifiedBy());
//        timesheetRepository.save(updatedTimesheet.get());
//        return ResponseEntity.ok(timesheet);
//    }

    public ResponseEntity<UpdateTimesheetResponse> updateTimesheetUser(Long id, UpdateTimesheetByUserRequest timesheet) throws Exception {
        Optional<Timesheet> optionalUpdatedTimesheet = timesheetRepository.findById(id);
        if (optionalUpdatedTimesheet.isEmpty()) {
            throw new TimesheetNotFoundException("Time Sheet with id: " + id + " doesn't exist");
        }

        Timesheet updatedTimesheet = optionalUpdatedTimesheet.get();

        if (updatedTimesheet.getStatus() != StatusType.PENDING){
            throw new TimesheetNotFoundException("Only time sheets with status of 'PENDING' can be updated");
        }

        LocalDate fromDate = timesheet.getFromDate();
        LocalDate toDate = timesheet.getToDate();

        // Ensure toDate is after fromDate
        if (toDate.isBefore(fromDate)) {
            return ResponseEntity.badRequest().body(new UpdateTimesheetResponse(null, "ToDate must be after FromDate."));
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
        long daysRequested = ChronoUnit.DAYS.between(fromDate, toDate) + 1;

        // Ensure user has enough daysOff available (consider only approved timesheets for calculation)
        long totalDaysOffUsed = existingTimesheets.stream()
                .filter(t -> t.getStatus() == StatusType.APPROVED)
                .mapToLong(t -> ChronoUnit.DAYS.between(t.getFromDate(), t.getToDate()) + 1)
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
        updatedTimesheet.setModifiedBy(timesheet.getModifiedBy()); // Ensure this is set correctly, perhaps from the authenticated user
        timesheetRepository.save(updatedTimesheet);

        return ResponseEntity.ok(new UpdateTimesheetResponse(timesheet, null));
    }

//    public ResponseEntity<UpdateTimesheetByManagerRequest> updateTimesheetManager(Long id, UpdateTimesheetByManagerRequest timesheet) throws TimesheetNotFoundException {
//        Optional<Timesheet> updatedTimesheet = timesheetRepository.findById(id);
//        if (updatedTimesheet.isEmpty()) {
//            throw new TimesheetNotFoundException("Time Sheet with id: " + id + " doesnt exist");
//        }
//        updatedTimesheet.get().setStatus(timesheet.getStatus());
//        updatedTimesheet.get().setModifiedAt(LocalDate.now());
//        updatedTimesheet.get().setModifiedBy(timesheet.getModifiedBy());
//        timesheetRepository.save(updatedTimesheet.get());
//        return ResponseEntity.ok(timesheet);
//    }

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
            throw new EntityNotFoundException("Time Sheet with id: " + id + " doesnt exist");
        }
        timesheetRepository.deleteById(id);
        return ResponseEntity.ok().body("Timesheet Deleted");
    }

    public ResponseEntity<List<Timesheet>> getTimesheetsByUserId(Long userId){
        List<Timesheet> timesheets = timesheetRepository.findTimeSheetsByUserId(userId);
        return ResponseEntity.ok().body(timesheets);
    }

    public List<Timesheet> getAllTimesheets() {
        return timesheetRepository.findAll();
    }

//    public List<Timesheet> getTimesheetsByUserId(Long userId) {
//        User user = userRepository.findById(String.valueOf(userId)).orElse(null);
//        if (user == null) {
//            return null;
//        }
//        return timesheetRepository.findByUser(user);
//    }
//
//    public Timesheet createTimesheet(Timesheet timesheet) {
//        // Set initial status to PENDING
//        timesheet.setStatus(StatusType.PENDING);
//
//        // Set the current date as the createdAt date
//        timesheet.setCreatedAt(LocalDate.now());
//
//        // Set the createdBy field with the username of the authenticated user
//        String authenticatedUser = "authenticatedUser";
//        timesheet.setCreatedBy(authenticatedUser);
//
//        // Check for overlapping dates
//        List<Timesheet> overlappingTimesheets = timesheetRepository.findOverlappingTimesheets(
//                timesheet.getUser().getId(), timesheet.getFromDate(), timesheet.getToDate());
//        if (!overlappingTimesheets.isEmpty()) {
//            // Return an error response if there are overlapping timesheets
//            throw new OverlappingTimesheetsException("Timesheet overlaps with existing timesheets");
//        }
//
//        return timesheetRepository.save(timesheet);
//    }

//    public Timesheet createTimesheet(Timesheet timesheet) {
//        User user = timesheet.getUser();
//        if (user == null) {
//            return null; // Timesheet must be associated with a user
//        }
//
//        // Check if the user has enough days off left
//        int remainingDaysOff = user.getDaysOff() - countApprovedDaysOff(user);
//        if (remainingDaysOff < countDaysOff(timesheet)) {
//            return null; // User does not have enough days off
//        }
//
//        // Check if the user has already applied for the same dates
//        if (isDuplicateApplication(timesheet)) {
//            return null; // User has already applied for these dates
//        }
//
//        // Apply the timesheet
//        timesheet.setStatus(StatusType.PENDING);
//        return timesheetRepository.save(timesheet);
//    }

//    public Timesheet updateTimesheet(Timesheet timesheet, String note) {
//        if (timesheet == null) {
//            return null; // Timesheet must be provided
//        }
//
//        timesheet.setNote(note);
//        return timesheetRepository.save(timesheet);
//    }
//
//    public boolean deleteTimesheet(Long id) {
//        timesheetRepository.deleteById(String.valueOf(id));
//        return true;
//    }
//
//    private int countApprovedDaysOff(User user) {
//        int totalDaysOff = 0;
//        for (Timesheet timesheet : timesheetRepository.findByUser(user)) {
//            if (timesheet.getStatus() == StatusType.APPROVED) {
//                totalDaysOff += countDaysOff(timesheet);
//            }
//        }
//        return totalDaysOff;
//    }
//
//    private int countDaysOff(Timesheet timesheet) {
//        return (int) (timesheet.getToDate().toEpochDay() - timesheet.getFromDate().toEpochDay()) + 1;
//    }
//
//    public int getRemainingDaysOff(long userId) {
//        User user = userRepository.findById(String.valueOf(userId)).orElse(null);
//        if (user == null) {
//            return -1; // User not found
//        }
//
//        // Get approved timesheets for the user
//        List<Timesheet> approvedTimesheets = timesheetRepository.findByUserAndStatus(user, StatusType.APPROVED);
//
//        // Calculate total days off approved
//        int totalDaysOffApproved = 0;
//        for (Timesheet timesheet : approvedTimesheets) {
//            totalDaysOffApproved += countDaysOff(timesheet);
//        }
//
//        // Calculate remaining days off
//        return user.getDaysOff() - totalDaysOffApproved;
//    }
//
//    private boolean isDuplicateApplication(Timesheet timesheet) {
//        List<Timesheet> existingTimesheets = getTimesheetsByUserId(timesheet.getUser().getId());
//        for (Timesheet existingTimesheet : existingTimesheets) {
//            LocalDate fromDate1 = timesheet.getFromDate();
//            LocalDate toDate1 = timesheet.getToDate();
//            LocalDate fromDate2 = existingTimesheet.getFromDate();
//            LocalDate toDate2 = existingTimesheet.getToDate();
//            if (fromDate1.compareTo(fromDate2) >= 0 && toDate1.compareTo(toDate2) <= 0) {
//                return true; // User has already applied for these dates
//            }
//        }
//        return false;
//    }
//
//    public List<Timesheet> getAllTimesheetsByUser() {
//        return timesheetRepository.findByStatusAndUserRole(StatusType.PENDING, UserRoles.USER);
//    }
//
//    public Timesheet editTimesheet(Long id, String note, StatusType status) {
//        Timesheet timesheet = timesheetRepository.findById(String.valueOf(id)).orElse(null);
//        if (timesheet == null) {
//            return null; // Timesheet not found
//        }
//
//        timesheet.setNote(note);
//        timesheet.setStatus(status);
//        return timesheetRepository.save(timesheet);
//    }


}
