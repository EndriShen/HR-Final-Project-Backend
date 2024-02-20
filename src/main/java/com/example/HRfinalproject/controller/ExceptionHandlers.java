package com.example.HRfinalproject.controller;

import com.example.HRfinalproject.exceptions.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(0)
public class ExceptionHandlers {

    @ExceptionHandler({NotUniqueException.class})
    private ResponseEntity<ApiError> handleNotUniqueException(NotUniqueException exception) {
        return new ResponseEntity<>(
                new ApiError(exception.getMessage(), "405"),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler({UserNotFoundException.class})
    private ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException notFoundException){
        return new ResponseEntity<>(
                new ApiError(notFoundException.getMessage(), "404"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({TimesheetNotFoundException.class})
    private ResponseEntity<ApiError> handleTimesheetNotFoundException(TimesheetNotFoundException notFoundException){
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
