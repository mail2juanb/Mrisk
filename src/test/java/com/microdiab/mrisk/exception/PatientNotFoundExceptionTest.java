package com.microdiab.mrisk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PatientNotFoundExceptionTest {

    @Test
    public void testServerErrorException_Message() {
        // Arrange
        String expectedMessage = "Expected message";

        // Act & Assert
        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> { throw new PatientNotFoundException(expectedMessage); }
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
