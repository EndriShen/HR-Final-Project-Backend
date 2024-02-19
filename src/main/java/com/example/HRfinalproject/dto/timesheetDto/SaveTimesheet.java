package com.example.HRfinalproject.dto.timesheetDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SaveTimesheet {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String note;
}
