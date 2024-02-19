package com.example.HRfinalproject.dto.timesheetDto;

import com.example.HRfinalproject.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveTimesheetRequest {
    private SaveTimesheet saveTimesheet;
    private User user;
}
