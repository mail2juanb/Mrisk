package com.microdiab.mrisk.exception;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Custom implementation of Feign's {@link ErrorDecoder} to handle HTTP error responses.
 * This class provides specific exception handling
 * for common HTTP status codes (e.g., 404, 400, 409, 500+) and custom exceptions based on response body content.
 *
 * <p>It logs errors and delegates to the default error decoder for unhandled cases.
 */
public class CustomErrorDecoder implements ErrorDecoder {

    /**
     * Logger for this class, used to log error details and debugging information.
     */
    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);

    /**
     * Default error decoder used as a fallback for unhandled exceptions.
     */
    private final ErrorDecoder defaultErrorDecoder = new Default();


    /**
     * Decodes the HTTP response and throws an appropriate exception based on the status code
     * and response body.
     *
     * @param invoqueur The method key that triggered the request.
     * @param response  The HTTP response to decode.
     * @return An exception corresponding to the HTTP status code and response body.
     */
    @Override
    public Exception decode (String invoqueur, Response response) {

        // Lire le corps de la réponse
        String body = null;

        if (response.body() != null) {
            try {
                body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            } catch (IOException e) {
                log.error("Error reading the response body", e);
            }
        }

        // Reconstruire la réponse pour qu'elle reste lisible
//        Response newResponse = response.toBuilder()
//                .body(body, StandardCharsets.UTF_8)
//                .build();

        // Gestion des erreurs spécifiques
        if (response.status() == 404) {
            // Vérifie le corps de la réponse pour différencier les erreurs
            if (body != null) {
                if (body.toLowerCase().contains("notes") || body.toLowerCase().contains("empty")) {
                    return new EmptyNotesException("The patient's notes are empty.");
                } else if (body.toLowerCase().contains("patient")) {
                    return new PatientNotFoundException("The requested patient does not exist.");
                }
            }
            return new NotFoundException("Resource not found: " + invoqueur);
        } else if (response.status() == 400) {
                return new BadRequestException("Incorrect request : " + body);
            }
            else if (response.status() == 409) {
                return new ConflictException("Conflict detected : " + body);
            }
            else if (response.status() >= 500) {
                return new ServerErrorException("Server error : " + body);
            }

        // Par défaut, déléguer à l'ErrorDecoder par défaut
        return defaultErrorDecoder.decode(invoqueur, response);
    }
}
