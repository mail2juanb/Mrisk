package com.microdiab.mrisk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(BadRequestException.class);

    public BadRequestException(String message) {
        super(message);
        log.warn("*****  THROW Exception : {} - message : {}", getClass().getName(), getMessage());
    }
}
