package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConflictExceptionTest {

    @Test
    public void testConflictException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> { throw new ConflictException(expectedMessage); }
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
