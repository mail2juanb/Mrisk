package com.microdiab.mrisk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerErrorException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(ServerErrorException.class);

    public ServerErrorException(String message) {
        super(message);
        log.warn("*****  THROW Exception : {} - message : {}", getClass().getName(), getMessage());
    }
}
