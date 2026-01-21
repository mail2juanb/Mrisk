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
        // Patient over 30 years of age (born 35 years ago)
        patientOver30 = new PatientBean();
        patientOver30.setId(1L);
        patientOver30.setLastname("Doe");
        patientOver30.setDateofbirth(LocalDate.now().minusYears(35));
        patientOver30.setGender("M");

        // Man under 30 years old (born 25 years ago)
        patientMaleUnder30 = new PatientBean();
        patientMaleUnder30.setId(2L);
        patientMaleUnder30.setLastname("Smith");
        patientMaleUnder30.setDateofbirth(LocalDate.now().minusYears(25));
        patientMaleUnder30.setGender("M");

        // Woman under 30 (born 28 years ago)
        patientFemaleUnder30 = new PatientBean();
        patientFemaleUnder30.setId(3L);
        patientFemaleUnder30.setLastname("Johnson");
        patientFemaleUnder30.setDateofbirth(LocalDate.now().minusYears(28));
        patientFemaleUnder30.setGender("F");
    }

    @Nested
    class ExceptionTests {

        @Test
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
    class NoneRiskTests {

        @Test
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
    class BorderlineRiskTests {

        @Test
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
    class InDangerRiskTests {

        @Test
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
    class EarlyOnsetRiskTests {

        @Test
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
    class TriggerTermsTests {

        @Test
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
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
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
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }
    }

    @Nested
    class EdgeCaseAge30Tests {

        @Test
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

    @Nested
    class AdditionalCoverageTests {

        @Test
        @DisplayName("Should count duplicate trigger terms in multiple notes")
        void shouldCountDuplicateTriggersAcrossMultipleNotes() {
            // Arrange - Teste le comportement sans .distinct()
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient fumeur"),
                    createNote(2L, "Toujours fumeur avec Cholestérol"),
                    createNote(3L, "Fumeur avec Vertiges")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Should handle trigger terms with accents correctly")
        void shouldHandleAccentedTriggerTerms() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Hémoglobine A1C élevée, Cholestérol élevé")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Should handle trigger terms in middle of words")
        void shouldDetectTriggersEvenInMiddleOfWords() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Nonfumeur avec anticholestérol")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Should return None for male under 30 with exactly 2 triggers")
        void shouldReturnNone_whenMaleUnder30WithExactly2Triggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Cholestérol et Vertiges")
            );

            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(2L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Should handle very long notes with multiple trigger terms")
        void shouldHandleLongNotesWithMultipleTriggers() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Consultation du patient. Observations: Hémoglobine A1C élevée, " +
                            "présence de Microalbumine, Taille et Poids enregistrés, patient Fumeur, " +
                            "résultats Anormal pour Cholestérol, patient rapporte Vertiges fréquents, " +
                            "Rechute observée, Réaction allergique aux médicaments, Anticorps détectés")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Early onset"); // 11 triggers
        }

        @Test
        @DisplayName("Should handle empty note content")
        void shouldHandleEmptyNoteContent() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, ""),
                    createNote(2L, "   "),
                    createNote(3L, "Rien de particulier")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Should handle special characters in notes")
        void shouldHandleSpecialCharactersInNotes() {
            // Arrange
            List<NoteBean> notes = List.of(
                    createNote(1L, "Patient: fumeur!!! Cholestérol??? Vertiges... (Anormal)")
            );

            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(1L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("Borderline"); // 4 triggers
        }

        @Test
        @DisplayName("Should verify microservicesProxy is called with correct parameters")
        void shouldVerifyCorrectProxyInteractions() {
            // Arrange
            Long patId = 1L;
            when(microservicesProxy.getPatientById(patId)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(patId)).thenReturn(List.of(
                    createNote(1L, "Test")
            ));

            // Act
            riskService.calculateRisk(patId);

            // Assert
            verify(microservicesProxy, times(1)).getPatientById(patId);
            verify(microservicesProxy, times(1)).getNotesByPatId(patId);
            verifyNoMoreInteractions(microservicesProxy);
        }

        @Test
        @DisplayName("Should return correct patId in RiskLevel for all risk levels")
        void shouldReturnCorrectPatIdInRiskLevel() {
            // Arrange
            Long expectedPatId = 42L;
            PatientBean patient = new PatientBean();
            patient.setId(expectedPatId);
            patient.setDateofbirth(LocalDate.now().minusYears(35));
            patient.setGender("M");

            List<NoteBean> notes = List.of(
                    createNote(expectedPatId, "Fumeur, Cholestérol")
            );

            when(microservicesProxy.getPatientById(expectedPatId)).thenReturn(Optional.of(patient));
            when(microservicesProxy.getNotesByPatId(expectedPatId)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(expectedPatId);

            // Assert
            assertThat(result.getPatId()).isEqualTo(expectedPatId);
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Should handle gender case variations (m, M, f, F)")
        void shouldHandleGenderCaseVariations() {
            PatientBean patientLowercaseM = new PatientBean();
            patientLowercaseM.setId(10L);
            patientLowercaseM.setDateofbirth(LocalDate.now().minusYears(25));
            patientLowercaseM.setGender("m");

            List<NoteBean> notes = List.of(
                    createNote(10L, "Fumeur, Cholestérol, Vertiges")
            );

            when(microservicesProxy.getPatientById(10L)).thenReturn(Optional.of(patientLowercaseM));
            when(microservicesProxy.getNotesByPatId(10L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(10L);

            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Should handle exactly 30 years old female with 4 triggers")
        void shouldHandleAge30FemaleWith4Triggers() {
            // Arrange
            PatientBean patientFemale30 = new PatientBean();
            patientFemale30.setId(11L);
            patientFemale30.setDateofbirth(LocalDate.now().minusYears(30));
            patientFemale30.setGender("F");

            List<NoteBean> notes = List.of(
                    createNote(11L, "Fumeuse, Cholestérol, Vertiges, Anormal")
            );

            when(microservicesProxy.getPatientById(11L)).thenReturn(Optional.of(patientFemale30));
            when(microservicesProxy.getNotesByPatId(11L)).thenReturn(notes);

            // Act
            RiskLevel result = riskService.calculateRisk(11L);

            // Assert
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }
    }

    @Nested
    class BoundaryValueTests {

        @Test
        @DisplayName("Boundary: Over 30 with exactly 1 trigger should be None")
        void testOver30With1Trigger() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeur"));
            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(1L);
            assertThat(result.getRiskLevel()).isEqualTo("None");
        }

        @Test
        @DisplayName("Boundary: Over 30 with exactly 2 triggers should be Borderline")
        void testOver30With2Triggers() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeur, Cholestérol"));
            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(1L);
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Boundary: Over 30 with exactly 5 triggers should be Borderline")
        void testOver30With5Triggers() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeur, Cholestérol, Vertiges, Poids, Anormal"));
            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(1L);
            assertThat(result.getRiskLevel()).isEqualTo("Borderline");
        }

        @Test
        @DisplayName("Boundary: Over 30 with exactly 6 triggers should be In Danger")
        void testOver30With6Triggers() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeur, Cholestérol, Vertiges, Poids, Anormal, Rechute"));
            when(microservicesProxy.getPatientById(1L)).thenReturn(Optional.of(patientOver30));
            when(microservicesProxy.getNotesByPatId(1L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(1L);
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Boundary: Male under 30 with exactly 3 triggers should be In Danger")
        void testMaleUnder30With3Triggers() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeur, Cholestérol, Vertiges"));
            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(2L);
            assertThat(result.getRiskLevel()).isEqualTo("In Danger");
        }

        @Test
        @DisplayName("Boundary: Male under 30 with exactly 5 triggers should be Early onset")
        void testMaleUnder30With5Triggers() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeur, Cholestérol, Vertiges, Poids, Anormal"));
            when(microservicesProxy.getPatientById(2L)).thenReturn(Optional.of(patientMaleUnder30));
            when(microservicesProxy.getNotesByPatId(2L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(2L);
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }

        @Test
        @DisplayName("Boundary: Female under 30 with exactly 7 triggers should be Early onset")
        void testFemaleUnder30With7Triggers() {
            List<NoteBean> notes = List.of(createNote(1L, "Fumeuse, Cholestérol, Vertiges, Poids, Anormal, Rechute, Réaction"));
            when(microservicesProxy.getPatientById(3L)).thenReturn(Optional.of(patientFemaleUnder30));
            when(microservicesProxy.getNotesByPatId(3L)).thenReturn(notes);

            RiskLevel result = riskService.calculateRisk(3L);
            assertThat(result.getRiskLevel()).isEqualTo("Early onset");
        }
    }
}