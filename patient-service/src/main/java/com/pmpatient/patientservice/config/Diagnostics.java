package com.pmpatient.patientservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Diagnostics {

    @Bean
    @Profile("dev")
    public CommandLineRunner run() {
        return (args) -> System.out.println("Hello from dev");
    }

    @Bean
    @Profile("prod")
    public CommandLineRunner runProduction() {
        return (args) -> System.out.println("Hello from production");
    }
}
