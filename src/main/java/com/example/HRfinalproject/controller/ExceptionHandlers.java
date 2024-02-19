package com.example.HRfinalproject.controller;

import com.example.HRfinalproject.exceptions.ApiError;
import com.example.HRfinalproject.exceptions.OverlappingTimesheetsException;
import com.example.HRfinalproject.exceptions.TimesheetNotFoundException;
import com.example.HRfinalproject.exceptions.UserNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(0)
public class ExceptionHandlers {
    @ExceptionHandler({UserNotFoundException.class})
    private ResponseEntity<ApiError> handleMethodArgumentNotValidException(UserNotFoundException notFoundException){
        return new ResponseEntity<>(
                new ApiError(notFoundException.getMessage(), "404"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({TimesheetNotFoundException.class})
    private ResponseEntity<ApiError> handleMethodArgumentNotValidException(TimesheetNotFoundException notFoundException){
        return new ResponseEntity<>(
                new ApiError(notFoundException.getMessage(), "404"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({OverlappingTimesheetsException.class})
    private ResponseEntity<ApiError> handleOverlappingTimesheetsException(OverlappingTimesheetsException overlappingException){
        return new ResponseEntity<>(
                new ApiError(overlappingException.getMessage(), "400"),
                HttpStatus.BAD_REQUEST
        );
    }
}
