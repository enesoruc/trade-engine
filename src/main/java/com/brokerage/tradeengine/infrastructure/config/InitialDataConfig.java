package com.brokerage.tradeengine.infrastructure.config;

import com.brokerage.tradeengine.application.usecase.InitializeDataFromJsonUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig {

    private final InitializeDataFromJsonUseCase initializeDataFromJsonUseCase;

    @Bean
    public CommandLineRunner initialDataRunner() {
        return args -> initializeDataFromJsonUseCase.execute();
    }
}

