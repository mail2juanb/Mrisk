package com.microdiab.mrisk.service;

import com.microdiab.mrisk.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class RiskService {

    public RiskLevel calculateRisk(Long patId) {
        return new RiskLevel("caca", 1L);
    }
}
