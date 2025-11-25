package com.microdiab.mrisk.controller;

import com.microdiab.mrisk.model.RiskLevel;
import com.microdiab.mrisk.service.RiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/risk")
public class RiskController {

    private static final Logger logger = LoggerFactory.getLogger(RiskController.class);

    @Autowired
    private RiskService riskService;

    @GetMapping("/")
    public String showHome() {
        logger.info("Appel de showHome() - L'application est active !");
        return "Hello, this is patient home !!";
    }

    @GetMapping("/risk/{patId}")
    public RiskLevel getRiskLevel(@PathVariable Long patId) {
        logger.info("Appel de getRiskLevel(@PathVariable Long patId = {})", patId.toString());
        // Appeler le service pour récupérer les données nécessaires.
        return riskService.calculateRisk(patId);
    }
}
