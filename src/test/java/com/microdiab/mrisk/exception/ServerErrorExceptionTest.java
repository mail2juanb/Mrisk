package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerErrorExceptionTest {

    @Test
    public void testServerErrorException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        ServerErrorException exception = assertThrows(
                ServerErrorException.class,
                () -> { throw new ServerErrorException(expectedMessage); }
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
