package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerErrorExceptionTest {

    // Vérifier que l'exception est bien levée avec le bon message.
    @Test
    public void testServerErrorException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        ServerErrorException exception = assertThrows(
                ServerErrorException.class,
                () -> { throw new ServerErrorException(expectedMessage); }
        );

        // Vérifie que le message est correct
        assertEquals(expectedMessage, exception.getMessage());
    }
}
