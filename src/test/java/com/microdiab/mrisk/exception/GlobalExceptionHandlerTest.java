package com.microdiab.mrisk.exception;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        // Configuration du logger pour capturer les logs
        logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void handlePatientNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Patient not found";
        PatientNotFoundException exception = new PatientNotFoundException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handlePatientNotFoundException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);

        // Vérifier le log
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("PatientNotFoundException", errorMessage);
    }

    @Test
    void handleEmptyNotesException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Notes are empty";
        EmptyNotesException exception = new EmptyNotesException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handleEmptyNotesException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);

        // Vérifier le log
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("EmptyNotesException", errorMessage);
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handleNotFoundException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);

        // Vérifier le log
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("NotFoundException", errorMessage);
    }

    @Test
    void handleBadRequestException_ShouldReturnBadRequestResponse() {
        // Given
        String errorMessage = "Invalid request";
        BadRequestException exception = new BadRequestException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handleBadRequestException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);

        // Vérifier le log
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("BadRequestException", errorMessage);
    }

    @Test
    void handleConflictException_ShouldReturnConflictResponse() {
        // Given
        String errorMessage = "Resource conflict";
        ConflictException exception = new ConflictException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handleConflictException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);

        // Vérifier le log
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("ConflictException", errorMessage);
    }

    @Test
    void handleServerErrorException_ShouldReturnInternalServerErrorResponse() {
        // Given
        String errorMessage = "Server error occurred";
        ServerErrorException exception = new ServerErrorException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handleServerErrorException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);

        // Vérifier le log (niveau ERROR)
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("ServerErrorException", errorMessage);
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Given
        String errorMessage = "Unexpected exception";
        Exception exception = new RuntimeException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = handler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("An unexpected error occurred");

        // Vérifier le log (niveau ERROR avec stack trace)
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1);
        assertThat(logsList.getFirst().getLevel()).isEqualTo(Level.WARN);
        assertThat(logsList.getFirst().getFormattedMessage()).contains("Unexpected error", errorMessage);
        assertThat(logsList.getFirst().getThrowableProxy()).isNotNull();
    }
}