package com.microdiab.mrisk.controller;

import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.service.RiskService;
import com.microdiab.mrisk.tracing.TracingHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskControllerTest {

    @Mock
    private RiskService riskService;

    @Mock
    private TracingHelper tracing;

    @InjectMocks
    private RiskController riskController;

    private RiskLevel mockRiskLevel;

    @BeforeEach
    void setUp() {
        mockRiskLevel = new RiskLevel();
    }

    @Test
    void getRiskLevel_ShouldReturnRiskLevel_WhenPatIdIsValid() {
        // Arrange
        Long patId = 1L;
        when(riskService.calculateRisk(patId)).thenReturn(mockRiskLevel);

        // Act
        ResponseEntity<RiskLevel> response = riskController.getRiskLevel(patId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRiskLevel, response.getBody());
        verify(riskService, times(1)).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnRiskLevel_WhenPatIdIsZero() {
        // Arrange
        Long patId = 0L;
        when(riskService.calculateRisk(patId)).thenReturn(mockRiskLevel);

        // Act
        ResponseEntity<RiskLevel> response = riskController.getRiskLevel(patId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRiskLevel, response.getBody());
        verify(riskService, times(1)).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnRiskLevel_WhenPatIdIsNegative() {
        // Arrange
        Long patId = -1L;
        when(riskService.calculateRisk(patId)).thenReturn(mockRiskLevel);

        // Act
        ResponseEntity<RiskLevel> response = riskController.getRiskLevel(patId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRiskLevel, response.getBody());
        verify(riskService, times(1)).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldCallServiceOnce_WhenCalled() {
        // Arrange
        Long patId = 123L;
        when(riskService.calculateRisk(patId)).thenReturn(mockRiskLevel);

        // Act
        riskController.getRiskLevel(patId);

        // Assert
        verify(riskService, times(1)).calculateRisk(patId);
        verifyNoMoreInteractions(riskService);
    }

    @Test
    void getRiskLevel_ShouldReturnNullBody_WhenServiceReturnsNull() {
        // Arrange
        Long patId = 1L;
        when(riskService.calculateRisk(patId)).thenReturn(null);

        // Act
        ResponseEntity<RiskLevel> response = riskController.getRiskLevel(patId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(riskService, times(1)).calculateRisk(patId);
    }
}