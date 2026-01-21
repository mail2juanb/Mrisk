package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BadRequestExceptionTest {


    @Test
    public void testBadRequestException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> { throw new BadRequestException(expectedMessage); }
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
