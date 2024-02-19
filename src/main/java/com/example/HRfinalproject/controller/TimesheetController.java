package com.example.HRfinalproject.controller;

import com.example.HRfinalproject.dto.timesheetDto.SaveTimesheetRequest;
import com.example.HRfinalproject.dto.timesheetDto.UpdateTimesheetByManagerRequest;
import com.example.HRfinalproject.dto.timesheetDto.UpdateTimesheetByUserRequest;
import com.example.HRfinalproject.entity.Timesheet;
import com.example.HRfinalproject.entity.User;
import com.example.HRfinalproject.exceptions.TimesheetNotFoundException;
import com.example.HRfinalproject.exceptions.UserNotFoundException;
import com.example.HRfinalproject.service.TimesheetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timesheet")
@CrossOrigin(origins = "*")
public class TimesheetController {

    @Autowired
    private TimesheetService timesheetService;

    @PostMapping("/create")
    public ResponseEntity<SaveTimesheetRequest> createTimesheet(
            @RequestBody SaveTimesheetRequest timesheet){
        return timesheetService.createTimeSheet(timesheet);
    }

    @PatchMapping("/updateByUser/{id}")
    public ResponseEntity<UpdateTimesheetByUserRequest> updateTimesheetUser(
            @PathVariable("id") Long id,@RequestBody UpdateTimesheetByUserRequest timesheet) throws TimesheetNotFoundException {
        return timesheetService.updateTimesheetUser(id, timesheet);
    }

    @PatchMapping("/updateByManager/{id}")
    public ResponseEntity<UpdateTimesheetByManagerRequest> updateTimesheetManager(
            @PathVariable("id") Long id,@RequestBody UpdateTimesheetByManagerRequest timesheet) throws TimesheetNotFoundException {
        return timesheetService.updateTimesheetManager(id, timesheet);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteTimesheet(@PathVariable("id") Long id) throws TimesheetNotFoundException {
        return timesheetService.deleteTimesheet(id);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Timesheet>> getTimeSheetsByUserId(@PathVariable("id")Long userId){
        return timesheetService.getTimesheetsByUserId(userId);
    }

//    @PostMapping("/create")
//    public Timesheet createTimesheet(@RequestBody Timesheet timesheet) {
//        return timesheetService.createTimesheet(timesheet);
//    }
//
//    @GetMapping("/{userId}")
//    public List<Timesheet> getTimesheetsByUserId(@PathVariable Long userId) throws UserNotFoundException {
//        return timesheetService.getTimesheetsByUserId(userId);
//    }
}
