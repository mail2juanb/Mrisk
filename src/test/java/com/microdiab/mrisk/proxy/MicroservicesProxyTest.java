package com.microdiab.mrisk.proxy;

import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MicroservicesProxyTest {

    @Mock
    private MicroservicesProxy microservicesProxy;


    @Test
    void getPatientById_shouldReturnPatient_whenPatientExists() {
        // Arrange
        Long patientId = 1L;
        PatientBean expectedPatient = new PatientBean();
        expectedPatient.setId(patientId);
        expectedPatient.setLastname("Dupont");

        when(microservicesProxy.getPatientById(patientId))
                .thenReturn(Optional.of(expectedPatient));

        // Act
        Optional<PatientBean> result = microservicesProxy.getPatientById(patientId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(patientId);
        assertThat(result.get().getLastname()).isEqualTo("Dupont");
    }

    @Test
    void getPatientById_shouldReturnEmpty_whenPatientDoesNotExist() {
        // Arrange
        Long patientId = 999L;
        when(microservicesProxy.getPatientById(patientId))
                .thenReturn(Optional.empty());

        // Act
        Optional<PatientBean> result = microservicesProxy.getPatientById(patientId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getNotesByPatId_shouldReturnNotes_whenNotesExist() {
        // Arrange
        Long patId = 1L;
        List<NoteBean> expectedNotes = Arrays.asList(
                new NoteBean(patId, "Patient 1", "note patient 1"),
                new NoteBean(patId, "Patient 2", "note patient 2")
        );

        when(microservicesProxy.getNotesByPatId(patId))
                .thenReturn(expectedNotes);

        // Act
        List<NoteBean> result = microservicesProxy.getNotesByPatId(patId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNote()).isEqualTo("note patient 1");
        assertThat(result.get(1).getPatId()).isEqualTo(patId);
    }

    @Test
    void getNotesByPatId_shouldReturnEmptyList_whenNoNotesExist() {
        // Arrange
        Long patId = 999L;
        when(microservicesProxy.getNotesByPatId(patId))
                .thenReturn(List.of());

        // Act
        List<NoteBean> result = microservicesProxy.getNotesByPatId(patId);

        // Assert
        assertThat(result).isEmpty();
    }
}
