package com.microdiab.mrisk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception thrown when an internal server error occurs.
 * This exception is annotated with {@link ResponseStatus} to return a 500 (Internal Server Error) HTTP status.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerErrorException extends RuntimeException {

    /**
     * Logger for this class, used to log warning messages when the exception is thrown.
     */
    private static final Logger log = LoggerFactory.getLogger(ServerErrorException.class);

    /**
     * Constructs a new {@code ServerErrorException} with the specified detail message.
     *
     * @param message The detail message explaining the exception.
     */
    public ServerErrorException(String message) {
        super(message);
        log.warn("*****  THROW Exception : {} - message : {}", getClass().getName(), getMessage());
    }
}
