package com.microdiab.mrisk.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class RiskLevelTest {

    @Test
    void testNoArgsConstructor() {
        // Test du constructeur sans arguments
        RiskLevel riskLevel = new RiskLevel();
        assertThat(riskLevel).isNotNull();
        assertThat(riskLevel.getPatId()).isNull();
        assertThat(riskLevel.getRiskLevel()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Test du constructeur avec arguments
        Long patId = 123L;
        String riskLevelValue = "HIGH";
        RiskLevel riskLevel = new RiskLevel(riskLevelValue, patId);

        assertThat(riskLevel.getPatId()).isEqualTo(patId);
        assertThat(riskLevel.getRiskLevel()).isEqualTo(riskLevelValue);
    }

    @Test
    void testGettersAndSetters() {
        // Test des getters et setters
        RiskLevel riskLevel = new RiskLevel();
        Long patId = 456L;
        String riskLevelValue = "LOW";

        riskLevel.setPatId(patId);
        riskLevel.setRiskLevel(riskLevelValue);

        assertThat(riskLevel.getPatId()).isEqualTo(patId);
        assertThat(riskLevel.getRiskLevel()).isEqualTo(riskLevelValue);
    }

    @Test
    void testToString() {
        // Test de la méthode toString()
        Long patId = 789L;
        String riskLevelValue = "MEDIUM";
        RiskLevel riskLevel = new RiskLevel(riskLevelValue, patId);

        String toStringResult = riskLevel.toString();
        assertThat(toStringResult)
                .contains("RiskLevel{")
                .contains("patId=" + patId)
                .contains("riskLevel='" + riskLevelValue + "'")
                .endsWith("}");
    }
}
