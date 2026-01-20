package com.microdiab.mrisk.controller;

import com.microdiab.mrisk.exception.PatientNotFoundException;
import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.service.RiskService;
import com.microdiab.mrisk.tracing.TracingHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskController.class)
@WithMockUser(roles = {"INTERNAL"})
class RiskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskService riskService;

    @MockitoBean
    private TracingHelper tracing;


    @Test
    void getRiskLevel_ShouldReturnNone_WhenNoRisk() throws Exception {
        // Arrange
        Long patId = 1L;
        RiskLevel riskLevel = new RiskLevel("None", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("None"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnBorderline_WhenModerateRisk() throws Exception {
        // Arrange
        Long patId = 2L;
        RiskLevel riskLevel = new RiskLevel("Borderline", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("Borderline"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnInDanger_WhenHighRisk() throws Exception {
        // Arrange
        Long patId = 3L;
        RiskLevel riskLevel = new RiskLevel("In Danger", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("In Danger"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnEarlyOnset_WhenCriticalRisk() throws Exception {
        // Arrange
        Long patId = 4L;
        RiskLevel riskLevel = new RiskLevel("Early onset", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("Early onset"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnUndefined_WhenNoNotes() throws Exception {
        // Arrange
        Long patId = 5L;
        RiskLevel riskLevel = new RiskLevel("Undefined", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("Undefined"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldHandleNullResponse_WhenServiceReturnsNull() throws Exception {
        // Arrange
        Long patId = 999L;
        when(riskService.calculateRisk(patId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturn404_WhenPatientNotFound() throws Exception {
        // Arrange
        Long patId = 999L;
        when(riskService.calculateRisk(patId))
                .thenThrow(new PatientNotFoundException("Patient not found with ID: " + patId));

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isNotFound()) // 404 au lieu de 500
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Patient not found with ID: " + patId));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldHandleException_WhenServiceThrowsRuntimeException() throws Exception {
        // Arrange
        Long patId = 5L;
        when(riskService.calculateRisk(anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().is5xxServerError());

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldAcceptZeroPatientId() throws Exception {
        // Arrange
        Long patId = 0L;
        RiskLevel riskLevel = new RiskLevel("None", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("None"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldAcceptLargePatientId() throws Exception {
        // Arrange
        Long patId = Long.MAX_VALUE;
        RiskLevel riskLevel = new RiskLevel("Borderline", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("Borderline"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldAcceptNegativePatientId() throws Exception {
        // Arrange
        Long patId = -1L;
        RiskLevel riskLevel = new RiskLevel("None", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("None"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldReturnCorrectContentType() throws Exception {
        // Arrange
        Long patId = 10L;
        RiskLevel riskLevel = new RiskLevel("None", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));

        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldCallServiceExactlyOnce() throws Exception {
        // Arrange
        Long patId = 11L;
        RiskLevel riskLevel = new RiskLevel("Borderline", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk());

        // Assert
        verify(riskService).calculateRisk(patId);
    }

    @Test
    void getRiskLevel_ShouldHandleInvalidPathVariable_WhenNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"INTERNAL"})
    void getRiskLevel_ShouldWork_WithDifferentUsername() throws Exception {
        // Arrange
        Long patId = 12L;
        RiskLevel riskLevel = new RiskLevel("None", patId);
        when(riskService.calculateRisk(patId)).thenReturn(riskLevel);

        // Act & Assert
        mockMvc.perform(get("/risk/{patId}", patId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patId").value(patId))
                .andExpect(jsonPath("$.riskLevel").value("None"));

        verify(riskService).calculateRisk(patId);
    }
}