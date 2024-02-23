package com.example.HRfinalproject.dto.timesheetDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTimesheetByUserRequest {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String note;
    private String modifiedAt;
    private String modifiedBy;
}
