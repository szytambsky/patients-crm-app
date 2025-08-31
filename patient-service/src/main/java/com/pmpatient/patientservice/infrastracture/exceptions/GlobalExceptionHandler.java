package com.pmpatient.patientservice.infrastracture.exceptions;

import com.pmpatient.patientservice.infrastracture.exceptions.patient.EmailAlreadyExistsException;
import com.pmpatient.patientservice.infrastracture.exceptions.patient.PatientNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Pair<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Email already in use: {}", ex.getMessage());
        Pair<String, String> pair = Pair.of("message", "Email already in use.");
        return ResponseEntity.badRequest().body(pair);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFound(PatientNotFoundException ex) {
        log.warn("Patient not found: {}", ex.getPatientId());
        Map<String, String> errors = new HashMap<>();
        String message = ex.getMessage();
        UUID notFoundPatientId = ex.getPatientId();
        errors.put(message, notFoundPatientId.toString());
        return ResponseEntity.badRequest().body(errors);
    }
}
