package com.microdiab.mrisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * Main application class for the Mrisk microservice.
 * This class initializes the Spring Boot application and enables service discovery and Feign clients.
 * <p>The {@code MriskApplication} class is the entry point for the Mrisk microservice.
 * It enables service discovery and Feign clients for inter-microservice communication.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MriskApplication {

    /**
     * The main method to start the Mrisk microservice.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(MriskApplication.class, args);
    }

}
