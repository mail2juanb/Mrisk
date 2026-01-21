package com.microdiab.mrisk.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class RiskLevelTest {

    @Test
    void testNoArgsConstructor() {
        RiskLevel riskLevel = new RiskLevel();
        assertThat(riskLevel).isNotNull();
        assertThat(riskLevel.getPatId()).isNull();
        assertThat(riskLevel.getRiskLevel()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        Long patId = 123L;
        String riskLevelValue = "HIGH";
        RiskLevel riskLevel = new RiskLevel(riskLevelValue, patId);

        assertThat(riskLevel.getPatId()).isEqualTo(patId);
        assertThat(riskLevel.getRiskLevel()).isEqualTo(riskLevelValue);
    }

    @Test
    void testGettersAndSetters() {
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
