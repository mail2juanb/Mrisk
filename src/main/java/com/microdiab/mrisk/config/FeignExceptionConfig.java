package com.microdiab.mrisk.config;

import com.microdiab.mrisk.exception.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignExceptionConfig {

    @Bean
    public CustomErrorDecoder mCustomErrorDecoder() {
        return new CustomErrorDecoder();
    }
}
