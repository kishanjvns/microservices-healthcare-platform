package com.tech.kj.web.controller.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tech.kj.model.ResponseModel;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionMapper {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseModel> handleValidationException(MethodArgumentNotValidException ex) {
          String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(ResponseModel.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(message)
        .build());
    }

     @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseModel> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ResponseModel.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(ex.getMessage())
        .build());
    }
}
