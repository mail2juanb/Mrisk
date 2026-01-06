package com.microdiab.mrisk.bean;


public class NoteBean {


    private Long patId;     // Clé de correspondance avec la base SQL
    private String patient; // Nom du patient
    private String note;    // Champ texte pour la note (supporte les retours à la ligne)

    // Constructors
    public NoteBean() {
    }

    public NoteBean(Long patId, String patient, String note) {
        this.patId = patId;
        this.patient = patient;
        this.note = note;
    }


    // Getters Setters
    public Long getPatId() {
        return patId;
    }

    public void setPatId(Long patId) {
        this.patId = patId;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    @Override
    public String toString() {
        return "NoteBean{" +
                "patId=" + patId +
                ", patient='" + patient + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
