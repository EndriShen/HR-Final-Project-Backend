package com.example.HRfinalproject.dto.timesheetDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTimesheetResponse {
    private UpdateTimesheetByUserRequest timesheet;
    private String errorMessage;

}
