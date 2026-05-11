package com.brokerage.tradeengine.infrastructure.config;

import com.brokerage.tradeengine.application.usecase.LoadInitialDataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig {

    private final LoadInitialDataUseCase loadInitialDataUseCase;

    @Bean
    public CommandLineRunner initialDataRunner() {
        return args -> loadInitialDataUseCase.execute();
    }
}

