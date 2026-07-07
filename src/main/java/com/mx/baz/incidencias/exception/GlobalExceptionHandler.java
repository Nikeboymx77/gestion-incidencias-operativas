package com.mx.baz.incidencias.exception;

import com.mx.baz.incidencias.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> manejarBusinessException(
            BusinessException ex) {

        ErrorResponse response = ErrorResponse.builder()
                .codigo("BUSINESS-001")
                .mensaje(ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}