package com.microdiab.mrisk.bean;

/**
 * The {@code NoteBean} class represents a note associated with a patient in the MicroDiab application.
 * It is used to store and manage patient notes, including the patient's unique identifier, name, and the note content.
 * This class is part of the microservice architecture for the MicroDiab project, which focuses on diabetes analysis.
 *
 * <p>This bean is designed to facilitate the synchronization between SQL and MongoDB databases
 * by using the {@code patId} field as a key for correspondence.</p>
 *
 * <p>In the 'mnotes' microservice, this class is annotated with validation constraints to ensure data integrity:</p>
 * <ul>
 *   <li>{@code patId} must not be null and must be a positive number.</li>
 *   <li>{@code patient} must not be blank.</li>
 *   <li>{@code note} must not be blank.</li>
 * </ul>
 *
 * @see com.microdiab.mrisk
 */
public class NoteBean {

    /**
     * Unique identifier of the patient, used as a key for correspondence with the SQL database.
     * In the 'mnotes' microservice, this field is annotated with:
     * {@code @NotNull(message = "patId cannot be null")} and
     * {@code @Positive(message = "patId must be a positive number")}.
     */
    private Long patId;

    /**
     * Name of the patient associated with the note.
     * In the 'mnotes' microservice, this field is annotated with:
     * {@code @NotBlank(message = "patient is mandatory")}.
     */
    private String patient;

    /**
     * Text content of the note. Supports multiline text.
     * In the 'mnotes' microservice, this field is annotated with:
     * {@code @NotBlank(message = "note is mandatory")}.
     */
    private String note;

    /**
     * Default constructor for the {@code NoteBean} class.
     */
    public NoteBean() {
    }


    /**
     * Parameterized constructor for the {@code NoteBean} class.
     *
     * @param patId   The unique identifier of the patient. Must be a positive number.
     * @param patient The name of the patient. Must not be blank.
     * @param note    The text content of the note. Must not be blank.
     */
    public NoteBean(Long patId, String patient, String note) {
        this.patId = patId;
        this.patient = patient;
        this.note = note;
    }


    /**
     * Gets the unique identifier of the patient.
     *
     * @return The patient's unique identifier.
     */
    public Long getPatId() {
        return patId;
    }

    /**
     * Sets the unique identifier of the patient.
     *
     * @param patId The patient's unique identifier. Must be a positive number.
     */
    public void setPatId(Long patId) {
        this.patId = patId;
    }

    /**
     * Gets the name of the patient.
     *
     * @return The patient's name.
     */
    public String getPatient() {
        return patient;
    }

    /**
     * Sets the name of the patient.
     *
     * @param patient The patient's name. Must not be blank.
     */
    public void setPatient(String patient) {
        this.patient = patient;
    }

    /**
     * Gets the text content of the note.
     *
     * @return The note's text content.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the text content of the note.
     *
     * @param note The note's text content. Must not be blank.
     */
    public void setNote(String note) {
        this.note = note;
    }


    /**
     * Returns a string representation of the {@code NoteBean} object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "NoteBean{" +
                "patId=" + patId +
                ", patient='" + patient + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
