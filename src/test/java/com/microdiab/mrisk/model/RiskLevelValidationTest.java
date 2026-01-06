package com.microdiab.mrisk.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class RiskLevelValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenRiskLevelIsNull_thenValidationFails() {
        RiskLevel riskLevel = new RiskLevel(null, 123L);
        var violations = validator.validate(riskLevel);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("riskLevel is mandatory"));
    }

    @Test
    void whenPatIdIsNull_thenValidationFails() {
        RiskLevel riskLevel = new RiskLevel("HIGH", null);
        var violations = validator.validate(riskLevel);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("patId cannot be null"));
    }
}

