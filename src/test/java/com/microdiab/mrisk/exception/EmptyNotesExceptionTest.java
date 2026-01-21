package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmptyNotesExceptionTest {

    @Test
    public void testEmptyNotesException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        EmptyNotesException exception = assertThrows(
                EmptyNotesException.class,
                () -> { throw new EmptyNotesException(expectedMessage); }
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
