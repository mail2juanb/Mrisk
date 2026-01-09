package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handlePatientNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Patient not found";
        PatientNotFoundException exception = new PatientNotFoundException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handlePatientNotFoundException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);
    }

    @Test
    void handleEmptyNotesException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Notes are empty";
        EmptyNotesException exception = new EmptyNotesException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handleEmptyNotesException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handleNotFoundException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);
    }

    @Test
    void handleBadRequestException_ShouldReturnBadRequestResponse() {
        // Given
        String errorMessage = "Invalid request";
        BadRequestException exception = new BadRequestException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handleBadRequestException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);
    }

    @Test
    void handleConflictException_ShouldReturnConflictResponse() {
        // Given
        String errorMessage = "Resource conflict";
        ConflictException exception = new ConflictException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handleConflictException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);
    }

    @Test
    void handleServerErrorException_ShouldReturnInternalServerErrorResponse() {
        // Given
        String errorMessage = "Server error occurred";
        ServerErrorException exception = new ServerErrorException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handleServerErrorException(exception);

        // Then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error"))
                .isEqualTo(errorMessage);
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Given
        Exception exception = new RuntimeException("Unexpected exception");

        // When
        ResponseEntity<Map<String, String>> response =
                handler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error"))
                .isEqualTo("An unexpected error occurred");
    }
}
