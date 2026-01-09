package com.microdiab.mrisk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for handling bad requests (HTTP 400).
 * This exception is thrown when a client sends an invalid or malformed request.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(BadRequestException.class);

    /**
     * Constructs a new {@link BadRequestException} with the specified detail message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public BadRequestException(String message) {
        super(message);
        log.warn("*****  THROW Exception : {} - message : {}", getClass().getName(), getMessage());
    }
}
