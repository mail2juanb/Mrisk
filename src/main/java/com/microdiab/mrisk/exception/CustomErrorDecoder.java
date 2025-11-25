package com.microdiab.mrisk.exception;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode (String invoqueur, Response response) {
        log.info("CustomErrorDecoder appelé avec status : {}", response.status());
//        if (response.status() == 400) {
//            return new PatientBadRequestException("Requête incorrecte..." + response.body() + "....." + response.status() + "...." + response.reason());
//        }
        if (response.status() == 404) {
            // Vérifie le corps de la requête, ce doit être via le body pour différencier les erreurs.
            // Pas via la provenance
            try {
                String responseBody = Util.toString(new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8));
                log.info("Corps de la réponse : {}", responseBody);
            } catch (IOException e) {
                log.error("Erreur lors de la lecture du corps de la réponse", e);
                return new RuntimeException("Erreur inconnue lors de la récupération des données du corps de la réponse.");
            }


            if (invoqueur.contains("/notes")) {
                return new EmptyNotesException("Les notes du patient sont vides.");
            } else if (invoqueur.contains("/patient/{id}")) {
                return new PatientNotFoundException("Le Patient demandé n'existe pas.");
            } else {
                return new NotFoundException("Cas non géré de la réponse 404");
            }

        }
        
//        if (response.status() == 409) {
//            return new PatientDuplicateException("Un patient avec les mêmes informations existe déjà.");
//        }
        log.info("CustomErrorDecoder fin de la méthode...");
        return defaultErrorDecoder.decode(invoqueur, response);
    }
}
