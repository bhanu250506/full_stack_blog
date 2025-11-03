package com.example.demo.exception;


import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex){
        return new ResponseEntity<>(
                Map.of("error", "A user with this email already exists"),
                HttpStatus.CONFLICT
        );
    }
}
