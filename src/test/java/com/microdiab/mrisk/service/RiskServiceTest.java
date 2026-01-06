package com.microdiab.mrisk.service;

import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import com.microdiab.mrisk.exception.PatientNotFoundException;
import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.proxy.MicroservicesProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires RiskService")
class RiskServiceTest {

    @Mock
    private MicroservicesProxy microservicesProxy;

    @InjectMocks
    private RiskService riskService;

    private PatientBean patientOver30;
    private PatientBean patientMaleUnder30;
    private PatientBean patientFemaleUnder30;

    @BeforeEach
    void setUp() {
        // Patient de plus de 30 ans (né il y a 35 ans)
        patientOver30 = new PatientBean();
        patientOver30.setId(1L);
        patientOver30.setLastname("Doe");
        patientOver30.setDateofbirth(LocalDate.now().minusYears(35));
        patientOver30.setGender("M");

        // Homme de moins de 30 ans (né il y a 25 ans)
        patientMaleUnder30 = new PatientBean();
        patientMaleUnder30.setId(2L);
        patientMaleUnder30.setLastname("Smith");
        patientMaleUnder30.setDateofbirth(LocalDate.now().minusYears(25));
        patientMaleUnder30.setGender("M");

        // Femme de moins de 30 ans (née il y a 28 ans)
        patientFemaleUnder30 = new PatientBean();
        patientFemaleUnder30.setId(3L);
        patientFemaleUnder30.setLastname("Johnson");
        patientFemaleUnder30.setDateofbirth(LocalDate.now().minusYears(28));
        patientFemaleUnder30.setGender("F");
    }

    @Nested
    @DisplayName("Tests d'exception")
    class ExceptionTests {

        @Test
        @DisplayName("Devrait lancer PatientNotFoundException si le patient n'existe pas")
        void shouldThrowPatientNotFoundException_whenPatientNotFound() {
            // Arrange
            Long patId = 999L;
            when(microservicesProxy.getPatientById(patId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> riskService.calculateRisk(patId))
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessage("Patient not found with ID: " + patId);

            verify(microservicesProxy).getPatientById(patId);
            verifyNoMoreInteractions(microservicesProxy);
        }
    }

    @Nested
    @DisplayName("Tests niveau de risque: None/Undefined")
    class NoneRiskTests {

        @Test
        @DisplayName("Devrait retourner 'Undefined' si aucune note n'existe")
        void shouldReturnUndefined_whenNoNotes() {
            // Arrange
            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(new ArrayList<>());

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Undefined");
            assertThat(result.getPatId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Devrait retourner 'None' si aucun terme déclencheur n'est présent")
        void shouldReturnNone_whenNoTriggerTerms() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Consultation de routine"),
                    createNote(2L, "Patient en bonne santé")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
            assertThat(result.getPatId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Devrait retourner 'None' pour patient >30 ans avec 1 seul déclencheur")
        void shouldReturnNone_whenOver30WithOneTrigger() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient présente du Cholestérol")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Devrait retourner 'None' pour homme <30 ans avec moins de 3 déclencheurs")
        void shouldReturnNone_whenMaleUnder30WithLessThan3Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient fumeur avec du Cholestérol")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Devrait retourner 'None' pour femme <30 ans avec 2 déclencheurs")
        void shouldReturnNone_whenFemaleUnder30With2Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patiente fumeuse avec Cholestérol")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Devrait retourner 'None' pour femme <30 ans avec 3 déclencheurs")
        void shouldReturnNone_whenFemaleUnder30With3Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patiente fumeuse avec Cholestérol et Vertiges")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Devrait retourner 'None' pour patient <30 ans avec genre non-binaire et déclencheurs")
        void shouldReturnNone_whenUnder30WithOtherGenderAndTriggers() {
            // Arrange
            PatientBean patientOtherGender = new PatientBean();
            patientOtherGender.setId(6L);
            patientOtherGender.setLastname("Other");
            patientOtherGender.setDateofbirth(LocalDate.now().minusYears(25));
            patientOtherGender.setGender("X");

            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient avec Cholestérol, Fumeur, Vertiges, Anormal, Poids")
            );

            when(microservicesProxy.getPatientById(6L)).thenReturn(Optional.of(patientOtherGender));
            when(microservicesProxy.getNotesByPatId(6L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(6L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }
    }

    @Nested
    @DisplayName("Tests niveau de risque: Borderline")
    class BorderlineRiskTests {

        @Test
        @DisplayName("Devrait retourner 'Borderline' pour patient >30 ans avec 2 déclencheurs")
        void shouldReturnBorderline_whenOver30With2Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient fumeur avec Cholestérol élevé")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Devrait retourner 'Borderline' pour patient >30 ans avec 3 déclencheurs")
        void shouldReturnBorderline_whenOver30With3Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient fumeur avec Cholestérol et Vertiges")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Devrait retourner 'Borderline' pour patient >30 ans avec 4 déclencheurs")
        void shouldReturnBorderline_whenOver30With4Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient fumeur avec Cholestérol, Vertiges et Poids anormal")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Devrait retourner 'Borderline' pour patient >30 ans avec 5 déclencheurs")
        void shouldReturnBorderline_whenOver30With5Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient fumeur avec Cholestérol, Poids élevé, Vertiges et Rechute")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }
    }

    @Nested
    @DisplayName("Tests niveau de risque: In Danger")
    class InDangerRiskTests {

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour patient >30 ans avec 6 déclencheurs")
        void shouldReturnInDanger_whenOver30With6Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Poids, Anormal, Vertiges, Rechute")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour patient >30 ans avec 7 déclencheurs")
        void shouldReturnInDanger_whenOver30With7Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Poids, Anormal, Vertiges, Rechute, Réaction")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour homme <30 ans avec 3 déclencheurs")
        void shouldReturnInDanger_whenMaleUnder30With3Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur avec Cholestérol et Vertiges")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour homme <30 ans avec 4 déclencheurs")
        void shouldReturnInDanger_whenMaleUnder30With4Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Vertiges, Anormal")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour femme <30 ans avec 4 déclencheurs")
        void shouldReturnInDanger_whenFemaleUnder30With4Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeuse, Cholestérol, Vertiges, Anormal")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour femme <30 ans avec 5 déclencheurs")
        void shouldReturnInDanger_whenFemaleUnder30With5Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeuse, Cholestérol, Vertiges, Anormal, Poids")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait retourner 'In Danger' pour femme <30 ans avec 6 déclencheurs")
        void shouldReturnInDanger_whenFemaleUnder30With6Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeuse, Cholestérol, Vertiges, Anormal, Poids, Rechute")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }
    }

    @Nested
    @DisplayName("Tests niveau de risque: Early onset")
    class EarlyOnsetRiskTests {

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour patient >30 ans avec 8 déclencheurs")
        void shouldReturnEarlyOnset_whenOver30With8Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Poids, Anormal, Vertiges, Rechute, Réaction, Anticorps")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour patient >30 ans avec 9 déclencheurs")
        void shouldReturnEarlyOnset_whenOver30With9Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Poids, Anormal, Vertiges, Rechute, Réaction, Anticorps, Taille")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour patient >30 ans avec plus de 8 déclencheurs")
        void shouldReturnEarlyOnset_whenOver30WithMoreThan8Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Poids, Anormal, Vertiges, Rechute, Réaction, Anticorps, Taille, Microalbumine")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour homme <30 ans avec 5 déclencheurs")
        void shouldReturnEarlyOnset_whenMaleUnder30With5Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Vertiges, Anormal, Poids")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour homme <30 ans avec 6 déclencheurs")
        void shouldReturnEarlyOnset_whenMaleUnder30With6Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Vertiges, Anormal, Poids, Rechute")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour homme <30 ans avec plus de 5 déclencheurs")
        void shouldReturnEarlyOnset_whenMaleUnder30WithMoreThan5Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Vertiges, Anormal, Poids, Rechute, Réaction")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour femme <30 ans avec 7 déclencheurs")
        void shouldReturnEarlyOnset_whenFemaleUnder30With7Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeuse, Cholestérol, Vertiges, Anormal, Poids, Rechute, Réaction")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour femme <30 ans avec 8 déclencheurs")
        void shouldReturnEarlyOnset_whenFemaleUnder30With8Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeuse, Cholestérol, Vertiges, Anormal, Poids, Rechute, Réaction, Anticorps")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait retourner 'Early onset' pour femme <30 ans avec plus de 7 déclencheurs")
        void shouldReturnEarlyOnset_whenFemaleUnder30WithMoreThan7Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeuse, Cholestérol, Vertiges, Anormal, Poids, Rechute, Réaction, Anticorps, Taille")
            );

            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(3L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }
    }

    @Nested
    @DisplayName("Tests de sensibilité à la casse et tous les termes déclencheurs")
    class TriggerTermsTests {

        @Test
        @DisplayName("Devrait détecter les termes déclencheurs indépendamment de la casse")
        void shouldDetectTriggersRegardlessOfCase() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient FUMEUR avec cholestérol et VERTIGES")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Devrait détecter tous les termes déclencheurs possibles")
        void shouldDetectAllPossibleTriggerTerms() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Hémoglobine A1C élevée"),
                    createNote(2L, "Microalbumine détectée"),
                    createNote(3L, "Taille et Poids mesurés"),
                    createNote(4L, "Patient Fumeur"),
                    createNote(5L, "Patiente Fumeuse"),
                    createNote(6L, "Résultat Anormal"),
                    createNote(7L, "Cholestérol élevé"),
                    createNote(8L, "Vertiges signalés"),
                    createNote(9L, "Rechute observée"),
                    createNote(10L, "Réaction allergique"),
                    createNote(11L, "Anticorps détectés")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Devrait détecter Fumeur et Fumeuse comme termes différents")
        void shouldDetectFumeurAndFumeuseAsSeparateTerms() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Le patient est fumeur et sa conjointe est fumeuse")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline"); // 2 déclencheurs
        }

        @Test
        @DisplayName("Devrait filtrer correctement les termes dans les notes (test filter)")
        void shouldFilterTermsInNotesCorrectly() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "HÉMOGLOBINE A1C"),
                    createNote(2L, "microalbumine"),
                    createNote(3L, "TaIlLe")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline"); // 3 déclencheurs
        }
    }

    @Nested
    @DisplayName("Tests cas limites âge 30 ans")
    class EdgeCaseAge30Tests {

        @Test
        @DisplayName("Devrait traiter un patient de 30 ans comme <=30 ans (homme avec 3 déclencheurs)")
        void shouldTreatAge30AsUnder30_maleWith3Triggers() {
            // Arrange
            PatientBean patient30 = new PatientBean();
            patient30.setId(4L);
            patient30.setLastname("Edge");
            patient30.setDateofbirth(LocalDate.now().minusYears(30));
            patient30.setGender("M");

            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur avec Cholestérol et Vertiges")
            );

            when(microservicesProxy.getPatientById(4L)).thenReturn(Optional.of(patient30));
            when(microservicesProxy.getNotesByPatId(4L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(4L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Devrait traiter un patient de 31 ans comme >30 ans (6 déclencheurs)")
        void shouldTreatAge31AsOver30_with6Triggers() {
            // Arrange
            PatientBean patient31 = new PatientBean();
            patient31.setId(5L);
            patient31.setLastname("Edge");
            patient31.setDateofbirth(LocalDate.now().minusYears(31));
            patient31.setGender("M");

            List<NoteBean> notes = List.of(
                    createNote(1L, "Fumeur, Cholestérol, Poids, Anormal, Vertiges, Rechute")
            );

            when(microservicesProxy.getPatientById(5L)).thenReturn(Optional.of(patient31));
            when(microservicesProxy.getNotesByPatId(5L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(5L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }
    }

    private NoteBean createNote(Long id, String noteContent) {
        NoteBean note = new NoteBean();
        note.setPatId(id);
        note.setNote(noteContent);
        return note;
    }
}