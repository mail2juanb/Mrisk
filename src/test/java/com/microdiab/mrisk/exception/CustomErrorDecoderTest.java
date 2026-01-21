package com.microdiab.mrisk.exception;

import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CustomErrorDecoderTest {

    private CustomErrorDecoder customErrorDecoder;
    private Request request;

    @BeforeEach
    void setUp() {
        customErrorDecoder = new CustomErrorDecoder();

        // Create a dummy query for testing purposes
        request = Request.create(
                Request.HttpMethod.GET,
                "/test",
                new HashMap<>(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }

    @Test
    void testDecode_404_EmptyNotesException() {
        // Arrange
        String methodKey = "PatientClient#getNotes(Long)/notes";
        String body = "Notes not found";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(EmptyNotesException.class, exception);
        assertEquals("The patient's notes are empty.", exception.getMessage());
    }

    @Test
    void testDecode_404_EmptyNotesException_WithEmpty() {
        // Arrange
        String methodKey = "PatientClient#getNotes(Long)";
        String body = "The notes are empty";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(EmptyNotesException.class, exception);
        assertEquals("The patient's notes are empty.", exception.getMessage());
    }

    @Test
    void testDecode_404_EmptyNotesException_CaseInsensitive() {
        // Arrange
        String methodKey = "PatientClient#getNotes(Long)";
        String body = "NOTES NOT FOUND";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(EmptyNotesException.class, exception);
        assertEquals("The patient's notes are empty.", exception.getMessage());
    }

    @Test
    void testDecode_404_EmptyNotesException_CaseInsensitiveEmpty() {
        // Arrange
        String methodKey = "PatientClient#getNotes(Long)";
        String body = "The data is EMPTY";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(EmptyNotesException.class, exception);
        assertEquals("The patient's notes are empty.", exception.getMessage());
    }

    @Test
    void testDecode_404_PatientNotFoundException() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)/patient/{id}";
        String body = "Patient not found";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(PatientNotFoundException.class, exception);
        assertEquals("The requested patient does not exist.", exception.getMessage());
    }

    @Test
    void testDecode_404_PatientNotFoundException_CaseInsensitive() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        String body = "PATIENT does not exist";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(PatientNotFoundException.class, exception);
        assertEquals("The requested patient does not exist.", exception.getMessage());
    }

    @Test
    void testDecode_404_NotFoundException() {
        // Arrange
        String methodKey = "SomeClient#getResource()";
        String body = "Resource not found";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(NotFoundException.class, exception);
        assertTrue(exception.getMessage().contains("Resource not found"));
        assertTrue(exception.getMessage().contains(methodKey));
    }

    @Test
    void testDecode_404_NotFoundException_WithNullBody() {
        // Arrange
        String methodKey = "SomeClient#getResource()";
        Response response = createResponseWithoutBody(404);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(NotFoundException.class, exception);
        assertEquals("Resource not found: " + methodKey, exception.getMessage());
    }

    @Test
    void testDecode_404_NotFoundException_WithEmptyBody() {
        // Arrange
        String methodKey = "SomeClient#getResource()";
        Response response = createResponse(404, "");

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(NotFoundException.class, exception);
        assertEquals("Resource not found: " + methodKey, exception.getMessage());
    }

    @Test
    void testDecode_404_NotFoundException_WithBodyNotContainingNotesOrPatient() {
        // Arrange
        String methodKey = "SomeClient#getResource()";
        String body = "Something else not found";
        Response response = createResponse(404, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(NotFoundException.class, exception);
        assertEquals("Resource not found: " + methodKey, exception.getMessage());
    }

    @Test
    void testDecode_400_BadRequestException() {
        // Arrange
        String methodKey = "PatientClient#createPatient(Patient)";
        String body = "Invalid patient data";
        Response response = createResponse(400, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(BadRequestException.class, exception);
        assertTrue(exception.getMessage().contains("Incorrect request"));
        assertTrue(exception.getMessage().contains(body));
    }

    @Test
    void testDecode_409_ConflictException() {
        // Arrange
        String methodKey = "PatientClient#updatePatient(Patient)";
        String body = "Patient already exists";
        Response response = createResponse(409, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(ConflictException.class, exception);
        assertTrue(exception.getMessage().contains("Conflict detected"));
        assertTrue(exception.getMessage().contains(body));
    }

    @Test
    void testDecode_500_ServerErrorException() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        String body = "Internal server error";
        Response response = createResponse(500, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(ServerErrorException.class, exception);
        assertTrue(exception.getMessage().contains("Server error"));
        assertTrue(exception.getMessage().contains(body));
    }

    @Test
    void testDecode_503_ServerErrorException() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        String body = "Service unavailable";
        Response response = createResponse(503, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(ServerErrorException.class, exception);
        assertTrue(exception.getMessage().contains("Server error"));
        assertTrue(exception.getMessage().contains(body));
    }

    @Test
    void testDecode_501_ServerErrorException() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        String body = "Not implemented";
        Response response = createResponse(501, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(ServerErrorException.class, exception);
        assertTrue(exception.getMessage().contains("Server error"));
        assertTrue(exception.getMessage().contains(body));
    }

    @Test
    void testDecode_OtherStatus_DefaultDecoder() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        String body = "Unauthorized";
        Response response = createResponse(401, body);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertNotNull(exception);
        // The CustomErrorDecoder returns a FeignException.
        assertTrue(exception.getClass().getName().contains("feign"));
    }

    @Test
    void testDecode_NullBody() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        Response response = createResponseWithoutBody(400);

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(BadRequestException.class, exception);
        assertTrue(exception.getMessage().contains("Incorrect request"));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void testDecode_EmptyBody() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";
        Response response = createResponse(400, "");

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(BadRequestException.class, exception);
        assertTrue(exception.getMessage().contains("Incorrect request"));
    }

    @Test
    void testDecode_IOExceptionWhileReadingBody() {
        // Arrange
        String methodKey = "PatientClient#getPatient(Long)";

        // Create a response with a body that will throw an IOException when read
        Response.Body body = new Response.Body() {
            @Override
            public Integer length() {
                return null;
            }

            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public java.io.InputStream asInputStream() throws java.io.IOException {
                throw new java.io.IOException("Simulated IO error");
            }

            @Override
            public java.io.Reader asReader(java.nio.charset.Charset charset) throws java.io.IOException {
                throw new java.io.IOException("Simulated IO error");
            }

            @Override
            public void close() throws java.io.IOException {
                // Nothing to close
            }
        };

        Response response = Response.builder()
                .status(400)
                .reason("Bad Request")
                .request(request)
                .headers(new HashMap<>())
                .body(body)
                .build();

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        // Even with an IOException, the code continues and returns an appropriate exception.
        assertInstanceOf(BadRequestException.class, exception);
        assertTrue(exception.getMessage().contains("Incorrect request"));
        assertTrue(exception.getMessage().contains("null")); // The body will be null because it is unreadable.
    }

    @Test
    void testDecode_IOExceptionWhileReadingBody_404() {
        // Arrange - Test IOException pour un 404
        String methodKey = "PatientClient#getPatient(Long)";

        Response.Body body = new Response.Body() {
            @Override
            public Integer length() {
                return null;
            }

            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public java.io.InputStream asInputStream() throws java.io.IOException {
                throw new java.io.IOException("Simulated IO error");
            }

            @Override
            public java.io.Reader asReader(java.nio.charset.Charset charset) throws java.io.IOException {
                throw new java.io.IOException("Simulated IO error");
            }

            @Override
            public void close() throws java.io.IOException {
                // Nothing to close
            }
        };

        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .headers(new HashMap<>())
                .body(body)
                .build();

        // Act
        Exception exception = customErrorDecoder.decode(methodKey, response);

        // Assert
        assertInstanceOf(NotFoundException.class, exception);
        assertEquals("Resource not found: " + methodKey, exception.getMessage());
    }

    // Useful methods for creating test responses

    private Response createResponse(int status, String body) {
        return Response.builder()
                .status(status)
                .reason("Test Reason")
                .request(request)
                .headers(new HashMap<>())
                .body(body, StandardCharsets.UTF_8)
                .build();
    }

    private Response createResponseWithoutBody(int status) {
        return Response.builder()
                .status(status)
                .reason("Test Reason")
                .request(request)
                .headers(new HashMap<>())
                .build();
    }
}