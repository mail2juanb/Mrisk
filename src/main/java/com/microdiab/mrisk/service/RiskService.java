package com.microdiab.mrisk.service;

import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import com.microdiab.mrisk.exception.EmptyNotesException;
import com.microdiab.mrisk.exception.PatientNotFoundException;
import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.proxy.MicroservicesProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RiskService {

    private static final Logger logger = LoggerFactory.getLogger(RiskService.class);

    @Autowired
    private MicroservicesProxy microservicesProxy;


/*    Un patient pourra avoir l'un des 4 niveaux de risque suivants :
            ● aucun risque (None) ;
            ● risque limité (Borderline) ;
            ● danger (In Danger) ;
            ● apparition précoce (Early onset).*/

/*    Les règles pour déterminer les niveaux de risque sont les suivantes :
            ● aucun risque (None) : Le dossier du patient ne contient aucune note du médecin
                contenant les déclencheurs (terminologie) ;
            ● risque limité (Borderline) : Le dossier du patient contient entre deux et cinq
                déclencheurs et le patient est âgé de plus de 30 ans ;
            ● danger (In Danger) : Dépend de l'âge et du sexe du patient. Si le patient est un homme
                de moins de 30 ans, alors trois termes déclencheurs doivent être présents. Si le patient
                est une femme et a moins de 30 ans, il faudra quatre termes déclencheurs. Si le patient
                a plus de 30 ans, alors il en faudra six ou sept ;
            ● apparition précoce (Early onset) : Encore une fois, cela dépend de l'âge et du sexe. Si
                le patient est un homme de moins de 30 ans, alors au moins cinq termes déclencheurs
                sont nécessaires. Si le patient est une femme et a moins de 30 ans, il faudra au moins
                sept termes déclencheurs. Si le patient a plus de 30 ans, alors il en faudra huit ou plus.*/

/*    Les termes déclencheurs à rechercher dans les notes du prestataire de santé sont les suivants :
            ● Hémoglobine A1C ;
            ● Microalbumine ;
            ● Taille ;
            ● Poids ;
            ● Fumeur, Fumeuse ;
            ● Anormal ;
            ● Cholestérol …
            ● Vertiges ;
            ● Rechute ;
            ● Réaction ;
            ● Anticorps.*/


    /**
     * Retourne la liste des termes déclencheurs pour le calcul du risque.
     *
     * @return Liste des termes déclencheurs.
     */
    private static List<String> getTriggerTerms() {
        return List.of("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur", "Fumeuse",
                "Anormal", "Cholestérol", "Vertiges", "Rechute", "Réaction", "Anticorps");
    }


    public RiskLevel calculateRisk(Long patId) {
        logger.info("Appel de calculateRisk(Long patId = {})", patId.toString());

        // Valider l'ID du patient. si il existe.
        // Récupération du Patient concerné depuis mPatient
        Optional<PatientBean> patient = microservicesProxy.getPatientById(patId);
        if (patient.isEmpty()) {
            logger.info("Aucun Patient récupéré");
            throw new PatientNotFoundException("Patient non trouvé avec l'ID : " + patId);
        }

        logger.info("Patient ID récupéré = {}", patient.get().getId());
        logger.info("Patient Lastname récupéré = {}", patient.get().getLastname());


        // Récupérer l'âge et le sexe du patient
        int patientAge = patient.get().getAge();
        String patientGender = patient.get().getGender();


        // Récupérer la liste des notes du patient depuis mNotes
        List<NoteBean> notes = microservicesProxy.getNotesByPatId(patId);
        if (notes.isEmpty()) {
            logger.info("Aucune note récupérée");
            throw new EmptyNotesException("Aucune note récupérée pour le Patient avec l'ID : " + patId);
        } else {
            logger.info("ID = {} ; Nombre de notes récupérées = {}", patId, notes.size());
        }


        // Récupérer la liste des termes déclencheurs
        List<String> triggerTerms = getTriggerTerms();


        // Compter le nombre de termes déclencheurs uniques présents dans les notes
        long triggerCount = notes.stream()
                .flatMap(note ->
                        triggerTerms.stream()
                                .filter(term ->
                                        note.getNote().toLowerCase().contains(term.toLowerCase())
                                )
                )
                //.distinct()
                .count();

        // Déterminer le niveau de risque
        logger.info("Début de la détermination du niveau de risque pour le patient ID = {} (triggerCount = {}, age = {}, gender = {})",
                patId, triggerCount, patientAge, patientGender);

        if (triggerCount == 0) {
            logger.info("Aucun terme déclencheur trouvé. Niveau de risque : None");
            return new RiskLevel("None", patId);
        } else {
            if (patientAge > 30) {
                logger.info("Patient âgé de plus de 30 ans. Évaluation du risque en fonction du nombre de termes déclencheurs.");
                if (triggerCount >= 2 && triggerCount <= 5) {
                    logger.info("Nombre de termes déclencheurs entre 2 et 5. Niveau de risque : Borderline");
                    return new RiskLevel("Borderline", patId);
                } else if (triggerCount >= 6 && triggerCount <= 7) {
                    logger.info("Nombre de termes déclencheurs entre 6 et 7. Niveau de risque : In Danger");
                    return new RiskLevel("In Danger", patId);
                } else if (triggerCount >= 8) {
                    logger.info("Nombre de termes déclencheurs supérieur ou égal à 8. Niveau de risque : Early onset");
                    return new RiskLevel("Early onset", patId);
                }
            } else {
                logger.info("Patient âgé de 30 ans ou moins. Évaluation du risque en fonction du genre et du nombre de termes déclencheurs.");
                if (patientGender.equalsIgnoreCase("M")) {
                    if (triggerCount >= 3 && triggerCount <= 4) {
                        logger.info("Homme de moins de 30 ans avec 3 ou 4 termes déclencheurs. Niveau de risque : In Danger");
                        return new RiskLevel("In Danger", patId);
                    } else if (triggerCount >= 5) {
                        logger.info("Homme de moins de 30 ans avec 5 termes déclencheurs ou plus. Niveau de risque : Early onset");
                        return new RiskLevel("Early onset", patId);
                    }
                } else if (patientGender.equalsIgnoreCase("F")) {
                    if (triggerCount >= 4 && triggerCount <= 6) {
                        logger.info("Femme de moins de 30 ans avec 4 à 6 termes déclencheurs. Niveau de risque : In Danger");
                        return new RiskLevel("In Danger", patId);
                    } else if (triggerCount >= 7) {
                        logger.info("Femme de moins de 30 ans avec 7 termes déclencheurs ou plus. Niveau de risque : Early onset");
                        return new RiskLevel("Early onset", patId);
                    }
                }
            }
        }

        // Si aucun critère n'est rempli
        logger.warn("Aucun critère de risque ne correspond pour le patient ID = {}. Niveau de risque calculé = {}.", patId, triggerCount);
        return new RiskLevel("None", patId);
    }
}
