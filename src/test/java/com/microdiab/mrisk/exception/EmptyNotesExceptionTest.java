package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmptyNotesExceptionTest {

    // Vérifier que l'exception est bien levée avec le bon message.
    @Test
    public void testEmptyNotesException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        EmptyNotesException exception = assertThrows(
                EmptyNotesException.class,
                () -> { throw new EmptyNotesException(expectedMessage); }
        );

        // Vérifie que le message est correct
        assertEquals(expectedMessage, exception.getMessage());
    }
}
