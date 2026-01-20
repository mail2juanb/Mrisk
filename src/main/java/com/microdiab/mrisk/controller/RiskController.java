package com.microdiab.mrisk.controller;

import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.service.RiskService;
import com.microdiab.mrisk.tracing.TracingHelper;
import io.micrometer.tracing.annotation.NewSpan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing risk-related operations in the *MicroDiab* project.
 * This controller exposes endpoints to calculate and retrieve the risk level
 * of a patient, typically used by the front-end (clientui).
 */
@RestController
@Tag(name = "Risk Management", description = "Endpoints for calculating and retrieving patient risk levels")
public class RiskController {

    /**
     * Service responsible for calculating the risk level of a patient.
     */
    @Autowired
    private RiskService riskService;

    /**
     * Tracing Service.
     */
    @Autowired
    private TracingHelper tracing;

    /**
     * Retrieves the risk level for a patient identified by their ID.
     *
     * @param patId The ID of the patient.
     * @return A {@link ResponseEntity} containing the calculated {@link RiskLevel}.
     */
    @Operation(
        summary = "Get risk level for a patient",
        description = "Calculates and returns the risk level of the patient identified by the provided ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the risk level",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = RiskLevel.class))),
        @ApiResponse(responseCode = "400", description = "Invalid patient ID supplied"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error while calculating risk level")
    })
    @GetMapping("/risk/{patId}")
    @NewSpan("mrisk-get-risk-level")
    public ResponseEntity<RiskLevel> getRiskLevel(@PathVariable Long patId) {

        tracing.tag("endpoint", "/risk/{patId}");
        tracing.tag("patient.id", patId);
        tracing.event("Calculating risk level for patient");

        RiskLevel riskLevel = riskService.calculateRisk(patId);
        tracing.event("Risk level calculated successfully");

        return ResponseEntity.ok(riskLevel);
    }
}
