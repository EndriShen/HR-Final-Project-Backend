package com.example.HRfinalproject.validators;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class TimesheetValidators {
    public static int getBusinessDaysBetween(LocalDate startDay, LocalDate endDay) {
        int businessDays = 0;
        LocalDate date = startDay;

        while (!date.isAfter(endDay)) {
            if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                businessDays++;
            }
            date = date.plusDays(1);
        }

        return businessDays;
    }
}
