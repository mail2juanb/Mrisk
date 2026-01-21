package com.microdiab.mrisk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception thrown when a requested patient is not found in the system.
 * This exception is annotated with {@link ResponseStatus} to return a 404 (Not Found) HTTP status.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {

    /**
     * Logger for this class, used to log warning messages when the exception is thrown.
     */
    private static final Logger log = LoggerFactory.getLogger(PatientNotFoundException.class);

    /**
     * Constructs a new {@code PatientNotFoundException} with the specified detail message.
     *
     * @param message The detail message explaining the exception.
     */
    public PatientNotFoundException(String message) {
        super(message);
        log.warn("*****  THROW Exception : {} - message : {}", getClass().getName(), getMessage());
    }
}
