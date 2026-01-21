package com.microdiab.mrisk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;


/**
 * Global exception handler for mRisk application.
 * This class uses Spring's {@link RestControllerAdvice} to centralize exception handling
 * across all controllers. It maps specific exceptions to appropriate HTTP responses
 * and ensures consistent error formatting.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * Handles {@link PatientNotFoundException} by returning a 404 (Not Found) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing the error message.
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundException(PatientNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }


    /**
     * Handles {@link EmptyNotesException} by returning a 404 (Not Found) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing the error message.
     */
    @ExceptionHandler(EmptyNotesException.class)
    public ResponseEntity<Map<String, String>> handleEmptyNotesException(EmptyNotesException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }


    /**
     * Handles {@link NotFoundException} by returning a 404 (Not Found) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing the error message.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }


    /**
     * Handles {@link BadRequestException} by returning a 400 (Bad Request) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing the error message.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }


    /**
     * Handles {@link ConflictException} by returning a 409 (Conflict) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing the error message.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }


    /**
     * Handles {@link ServerErrorException} by returning a 500 (Internal Server Error) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing the error message.
     */
    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<Map<String, String>> handleServerErrorException(ServerErrorException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }


    /**
     * Handles generic exceptions by returning a 500 (Internal Server Error) HTTP response.
     *
     * @param ex The exception to handle.
     * @return A {@link ResponseEntity} containing a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
