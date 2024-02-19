package com.example.HRfinalproject.exceptions;

import lombok.Data;

@Data
public class ApiError {
    private String message;
    private String code;

    public ApiError(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
