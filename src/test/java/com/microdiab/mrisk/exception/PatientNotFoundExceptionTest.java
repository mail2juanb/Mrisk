package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PatientNotFoundExceptionTest {

    // Vérifier que l'exception est bien levée avec le bon message.
    @Test
    public void testServerErrorException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> { throw new PatientNotFoundException(expectedMessage); }
        );

        // Vérifie que le message est correct
        assertEquals(expectedMessage, exception.getMessage());
    }
}
