package com.example.HRfinalproject.dto.timesheetDto;

import com.example.HRfinalproject.enums.StatusType;
import lombok.Data;

@Data
public class UpdateTimesheetByManagerRequest {
    private StatusType status;
    private String modifiedAt;
    private String modifiedBy;
}
