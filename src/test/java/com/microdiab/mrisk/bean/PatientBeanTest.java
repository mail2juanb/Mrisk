package com.microdiab.mrisk.bean;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PatientBeanTest {

    @Test
    void testConstructorsAndGettersSetters() {
        // Manufacturer test without arguments
        PatientBean patient1 = new PatientBean();
        patient1.setLastname("Dupont");
        patient1.setFirstname("Jean");
        patient1.setDateofbirth(LocalDate.of(1990, 5, 15));
        patient1.setGender("M");
        patient1.setAddress("123 Rue de Paris");
        patient1.setPhone("0123456789");

        assertThat(patient1.getLastname()).isEqualTo("Dupont");
        assertThat(patient1.getFirstname()).isEqualTo("Jean");
        assertThat(patient1.getDateofbirth()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(patient1.getGender()).isEqualTo("M");
        assertThat(patient1.getAddress()).isEqualTo("123 Rue de Paris");
        assertThat(patient1.getPhone()).isEqualTo("0123456789");

        // Manufacturer test with arguments
        PatientBean patient2 = new PatientBean("Martin", "Marie", LocalDate.of(1985, 8, 20), "F", "456 Rue de Lyon", "0987654321");
        assertThat(patient2.getLastname()).isEqualTo("Martin");
        assertThat(patient2.getFirstname()).isEqualTo("Marie");
        assertThat(patient2.getDateofbirth()).isEqualTo(LocalDate.of(1985, 8, 20));
        assertThat(patient2.getGender()).isEqualTo("F");
        assertThat(patient2.getAddress()).isEqualTo("456 Rue de Lyon");
        assertThat(patient2.getPhone()).isEqualTo("0987654321");
    }

    @Test
    void testGetAge() {
        PatientBean patient = new PatientBean();
        patient.setDateofbirth(LocalDate.of(1990, 1, 1)); // Birthday already passed

        int expectedAge = LocalDate.now().getYear() - 1990;
        assertThat(patient.getAge()).isEqualTo(expectedAge);
    }

    @Test
    void testGetAge_WhenDateofbirthIsNull_ThrowsIllegalStateException() {
        PatientBean patient = new PatientBean();
        patient.setDateofbirth(null);

        // Check that an exception is raised if the date of birth is null.
        assertThatThrownBy(patient::getAge)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The patient's date of birth is required to calculate their age.");
    }

    @Test
    void testToString() {
        PatientBean patient = new PatientBean("Dupont", "Jean", LocalDate.of(1990, 5, 15), "M", "123 Rue de Paris", "0123456789");
        String toString = patient.toString();

        assertThat(toString).contains("Dupont", "Jean", "1990-05-15", "M", "123 Rue de Paris", "0123456789");
    }

    @Test
    void testGetIdAndSetId() {
        PatientBean patient = new PatientBean();

        assertThat(patient.getId()).isNull();

        patient.setId(123L);
        assertThat(patient.getId()).isEqualTo(123L);

        patient.setId(456L);
        assertThat(patient.getId()).isEqualTo(456L);
    }
}
