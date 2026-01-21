package com.microdiab.mrisk.service;

import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import com.microdiab.mrisk.exception.PatientNotFoundException;
import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.proxy.MicroservicesProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * Service class for calculating the diabetes risk level of a patient.
 * This class interacts with microservices to fetch patient and note data,
 * and applies business rules to determine the risk level.
 * <p>The {@code RiskService} class provides methods to calculate the diabetes risk level
 * of a patient based on their notes and demographic data.</p>
 */
@Service
public class RiskService {

    private static final Logger logger = LoggerFactory.getLogger(RiskService.class);

    @Autowired
    private MicroservicesProxy microservicesProxy;


    /**
     * Returns the list of trigger terms used for risk calculation.
     *
     * @return A list of trigger terms.
     */
    private static List<String> getTriggerTerms() {
        return List.of("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur", "Fumeuse",
                "Anormal", "Cholestérol", "Vertiges", "Rechute", "Réaction", "Anticorps");
    }

    /**
     * Calculates the diabetes risk level for a patient based on their notes and demographic data.
     *
     * @param patId The unique identifier of the patient.
     * @return The calculated risk level for the patient.
     * @throws PatientNotFoundException If the patient is not found.
     */
    public RiskLevel calculateRisk(Long patId) {

        // Retrieval of the patient concerned from mPatient
        Optional<PatientBean> patient = microservicesProxy.getPatientById(patId);
        if (patient.isEmpty()) {
            throw new PatientNotFoundException("Patient not found with ID: " + patId);
        }

        // Retrieve the patient's age and gender
        int patientAge = patient.get().getAge();
        String patientGender = patient.get().getGender();


        // Retrieve the list of patient notes from mNotes
        List<NoteBean> notes = microservicesProxy.getNotesByPatId(patId);
        if (notes.isEmpty()) {
            // NOTE : No exceptions are made because it is possible that there are no marks yet.
            logger.warn("No notes retrieved for Patient with ID: {}. Risk level: Undefined", patId);
            return new RiskLevel("Undefined", patId);
        }


        // Retrieve the list of trigger terms
        List<String> triggerTerms = getTriggerTerms();


        // Count the number of unique trigger terms present in the notes
        long triggerCount = notes.stream()
                .flatMap(note ->
                        triggerTerms.stream()
                                .filter(term ->
                                        note.getNote().toLowerCase().contains(term.toLowerCase())
                                )
                )
                // NOTE : If distinct is enabled, then the results requested by the client are not obtained.
                //.distinct()
                .count();

        // Determine the level of risk
        if (triggerCount == 0) {
            // No trigger terms found. Risk level = None
            return new RiskLevel("None", patId);
        } else {
            if (patientAge > 30) {
                // Patient over 30 years of age.
                if (triggerCount >= 2 && triggerCount <= 5) {
                    // Number of trigger terms between 2 and 5. Risk level: Borderline
                    return new RiskLevel("Borderline", patId);
                } else if (triggerCount >= 6 && triggerCount <= 7) {
                    // Number of trigger terms between 6 and 7. Risk level: In Danger
                    return new RiskLevel("In Danger", patId);
                } else if (triggerCount >= 8) {
                    // Number of trigger terms greater than or equal to 8. Risk level: Early onset
                    return new RiskLevel("Early onset", patId);
                }
            } else {
                // Patient aged 30 years or younger.
                if (patientGender.equalsIgnoreCase("M")) {
                    if (triggerCount >= 3 && triggerCount <= 4) {
                        // Male under 30 with 3 or 4 trigger terms. Risk level: In Danger
                        return new RiskLevel("In Danger", patId);
                    } else if (triggerCount >= 5) {
                        // Male under 30 years of age with 5 or more trigger terms. Risk level: Early onset
                        return new RiskLevel("Early onset", patId);
                    }
                } else if (patientGender.equalsIgnoreCase("F")) {
                    if (triggerCount >= 4 && triggerCount <= 6) {
                        // Women under 30 with 4 to 6 trigger terms. Risk level: At risk
                        return new RiskLevel("In Danger", patId);
                    } else if (triggerCount >= 7) {
                        // Woman under 30 with 7 or more trigger terms. Risk level: Early onset
                        return new RiskLevel("Early onset", patId);
                    }
                }
            }
        }

        // If none of the criteria are met
        logger.warn("No risk criteria match for patient ID = {}. Calculated risk level = {}.", patId, triggerCount);
        return new RiskLevel("None", patId);
    }
}
