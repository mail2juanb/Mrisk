package com.microdiab.mrisk.bean;

import java.time.LocalDate;
import java.time.Period;

/**
 * The {@code PatientBean} class represents a patient in the MicroDiab application.
 * It is used to store and manage patient information, including personal details such as name, date of birth,
 * gender, address, and phone number. This class is part of the microservice architecture for the MicroDiab project,
 * which focuses on diabetes analysis.
 *
 * <p>This bean is designed to facilitate the synchronization between SQL and MongoDB databases
 * and is used across multiple microservices, including 'mpatient' and 'mpatient'.</p>
 *
 * @see com.microdiab.mrisk
 */
public class PatientBean {

    /** Unique identifier of the patient. */
    private Long id;

    /**
     * Last name of the patient.
     * Note: In the 'mpatient' microservice, this field is annotated with {@code @NotBlank(message = "lastname is mandatory")}.
     */
    private String lastname;

    /**
     * First name of the patient.
     * Note: In the 'mpatient' microservice, this field is annotated with {@code @NotBlank(message = "firstname is mandatory")}.
     */
    private String firstname;

    /**
     * Date of birth of the patient.
     * Note: In the 'mpatient' microservice, this field is annotated with:
     * {@code @NotNull(message = "dateofbirth is mandatory")} and
     * {@code @Past(message = "dateofbirth must be in the past")}.
     */
    private LocalDate dateofbirth;

    /**
     * Gender of the patient.
     * Note: In the 'mpatient' microservice, this field is annotated with {@code @NotBlank(message = "gender is mandatory")}.
     */
    private String gender;

    /** Address of the patient. */
    private String address;

    /** Phone number of the patient. */
    private String phone;

    /**
     * Default constructor for the {@code PatientBean} class.
     */
    public PatientBean() {
    }

    /**
     * Parameterized constructor for the {@code PatientBean} class.
     *
     * @param lastname    The last name of the patient.
     * @param firstname   The first name of the patient.
     * @param dateofbirth The date of birth of the patient.
     * @param gender      The gender of the patient.
     * @param address     The address of the patient.
     * @param phone       The phone number of the patient.
     */
    public PatientBean(String lastname, String firstname, LocalDate dateofbirth, String gender, String address, String phone) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.dateofbirth = dateofbirth;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
    }

    /**
     * Gets the unique identifier of the patient.
     *
     * @return The patient's unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the patient.
     *
     * @param id The patient's unique identifier.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the last name of the patient.
     *
     * @return The patient's last name.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the last name of the patient.
     *
     * @param lastname The patient's last name.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Gets the first name of the patient.
     *
     * @return The patient's first name.
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the first name of the patient.
     *
     * @param firstname The patient's first name.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets the date of birth of the patient.
     *
     * @return The patient's date of birth.
     */
    public LocalDate getDateofbirth() {
        return dateofbirth;
    }

    /**
     * Sets the date of birth of the patient.
     *
     * @param dateofbirth The patient's date of birth.
     */
    public void setDateofbirth(LocalDate dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    /**
     * Gets the gender of the patient.
     *
     * @return The patient's gender.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender of the patient.
     *
     * @param gender The patient's gender.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the address of the patient.
     *
     * @return The patient's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the patient.
     *
     * @param address The patient's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the phone number of the patient.
     *
     * @return The patient's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the patient.
     *
     * @param phone The patient's phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns a string representation of the {@code PatientBean} object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "PatientBean{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", dateofbirth=" + dateofbirth +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }


    /**
     * Calculates the age of the patient based on their date of birth.
     *
     * @return The patient's age in years.
     * @throws IllegalStateException if the date of birth is not set.
     */
    public int getAge() {
        if (this.dateofbirth == null) {
            throw new IllegalStateException("The patient's date of birth is required to calculate their age.");
        }
        return Period.between(this.dateofbirth, LocalDate.now()).getYears();
    }
}
