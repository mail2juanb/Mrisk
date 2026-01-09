package com.microdiab.mrisk.config;

import com.microdiab.mrisk.exception.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Feign exception handling in the *MicroDiab* project.
 * This class provides a custom error decoder bean to handle exceptions
 * thrown during Feign client communication with the backend (e.g., mPatient or mNotes microservice).
 */
@Configuration
public class FeignExceptionConfig {

    /**
     * Creates a {@link CustomErrorDecoder} bean for Feign clients.
     * This decoder customizes the handling of HTTP errors (e.g., 4xx, 5xx)
     * returned by the backend microservices.
     *
     * @return A configured {@link CustomErrorDecoder} instance.
     */
    @Bean
    public CustomErrorDecoder mCustomErrorDecoder() {
        return new CustomErrorDecoder();
    }
}
