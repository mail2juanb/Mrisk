package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NotFoundExceptionTest {

    @Test
    public void testNotFoundException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> { throw new NotFoundException(expectedMessage); }
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
