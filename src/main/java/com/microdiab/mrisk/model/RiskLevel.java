package com.microdiab.mrisk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class RiskLevel {

    @NotNull(message = "patId cannot be null")
    private Long patId;

    @NotBlank(message = "riskLevel is mandatory")
    private String riskLevel;

    // Constructors
    public RiskLevel() {
    }

    public RiskLevel(String riskLevel, Long patId) {
        this.riskLevel = riskLevel;
        this.patId = patId;
    }


    // Getters Setters
    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Long getPatId() {
        return patId;
    }

    public void setPatId(Long patId) {
        this.patId = patId;
    }



    @Override
    public String toString() {
        return "RiskLevel{" +
                "patId=" + patId +
                ", riskLevel='" + riskLevel + '\'' +
                '}';
    }
}

