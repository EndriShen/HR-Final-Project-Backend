package com.example.HRfinalproject.exceptions;

public class TimesheetNotFoundException extends Exception{
    private String message;

    public TimesheetNotFoundException(String message){
        super(message);
        this.message = message;
    }
}