package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BadRequestExceptionTest {

    // Vérifier que l'exception est bien levée avec le bon message.
    @Test
    public void testBadRequestException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> { throw new BadRequestException(expectedMessage); }
        );

        // Vérifie que le message est correct
        assertEquals(expectedMessage, exception.getMessage());
    }
}
