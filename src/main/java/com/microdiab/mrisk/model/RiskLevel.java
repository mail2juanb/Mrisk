package com.microdiab.mrisk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the risk level associated with a patient in the MicroDiab system.
 * This class encapsulates the patient ID and the calculated risk level,
 * which can be used for diabetes risk assessment.
 * <p>The {@code RiskLevel} class is a model that stores the risk level of a patient.
 * It includes the patient ID and the risk level as a string.</p>
 */
public class RiskLevel {

    /**
     * The unique identifier of the patient.
     * This field is mandatory and cannot be null.
     */
    @NotNull(message = "patId cannot be null")
    private Long patId;

    /**
     * The risk level of the patient.
     * This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "riskLevel is mandatory")
    private String riskLevel;

    /**
     * Default constructor for the {@code RiskLevel} class.
     */
    public RiskLevel() {
    }

    /**
     * Constructs a new {@code RiskLevel} with the specified risk level and patient ID.
     *
     * @param riskLevel The risk level of the patient.
     * @param patId     The unique identifier of the patient.
     */
    public RiskLevel(String riskLevel, Long patId) {
        this.riskLevel = riskLevel;
        this.patId = patId;
    }

    /**
     * Gets the risk level of the patient.
     *
     * @return The risk level as a string.
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * Sets the risk level of the patient.
     *
     * @param riskLevel The risk level to set.
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    /**
     * Gets the unique identifier of the patient.
     *
     * @return The patient ID.
     */
    public Long getPatId() {
        return patId;
    }

    /**
     * Sets the unique identifier of the patient.
     *
     * @param patId The patient ID to set.
     */
    public void setPatId(Long patId) {
        this.patId = patId;
    }

    /**
     * Returns a string representation of the {@code RiskLevel} object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "RiskLevel{" +
                "patId=" + patId +
                ", riskLevel='" + riskLevel + '\'' +
                '}';
    }
}

