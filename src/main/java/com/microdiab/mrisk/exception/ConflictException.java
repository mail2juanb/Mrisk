package com.microdiab.mrisk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for handling conflict situations (HTTP 409).
 * This exception is thrown when a request conflicts with the current state of the server,
 * such as duplicate entries or concurrent modifications.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(ConflictException.class);

    /**
     * Constructs a new {@link ConflictException} with the specified detail message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public ConflictException(String message) {
        super(message);
        log.warn("*****  THROW Exception : {} - message : {}", getClass().getName(), getMessage());
    }
}
