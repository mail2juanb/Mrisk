package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConflictExceptionTest {

    // Vérifier que l'exception est bien levée avec le bon message.
    @Test
    public void testConflictException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> { throw new ConflictException(expectedMessage); }
        );

        // Vérifie que le message est correct
        assertEquals(expectedMessage, exception.getMessage());
    }
}
