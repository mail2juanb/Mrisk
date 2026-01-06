package com.microdiab.mrisk.exception;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode (String invoqueur, Response response) {
        //log.info("CustomErrorDecoder appelé avec status : {}", response.status());

        // Lire le corps de la réponse
        String body = null;

        if (response.body() != null) {
            try {
                body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                //log.info("Corps de la réponse : {}", body);
            } catch (IOException e) {
                log.error("Error reading the response body", e);
            }
        }

        // Reconstruire la réponse pour qu'elle reste lisible
        Response newResponse = response.toBuilder()
                .body(body, StandardCharsets.UTF_8)
                .build();

        // Gestion des erreurs spécifiques
        if (response.status() == 404) {
            // Vérifie le corps de la requête, ce doit être via le body pour différencier les erreurs.
            // Pas via la provenance
            if (invoqueur.contains("/notes")) {
                return new EmptyNotesException("The patient's notes are empty.");
            } else if (invoqueur.contains("/patient/{id}")) {
                return new PatientNotFoundException("The requested patient does not exist.");
            } else {
                return new NotFoundException("Resource not found: " + invoqueur);
            }
        } else if (response.status() == 400) {
                return new BadRequestException("Incorrect request : " + body);
            }
            else if (response.status() == 409) {
                return new ConflictException("Conflict detected : " + body);
            }
            else if (response.status() >= 500) {
                return new ServerErrorException("Server error : " + body);
            }

        //log.info("CustomErrorDecoder fin de la méthode...");

        // Par défaut, déléguer à l'ErrorDecoder par défaut
        return defaultErrorDecoder.decode(invoqueur, response);
    }
}
