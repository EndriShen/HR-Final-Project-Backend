package com.example.HRfinalproject.dto.timesheetDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimesheetResponse {
    private SaveTimesheetRequest timesheetRequest;
    private String errorMessage;
}
