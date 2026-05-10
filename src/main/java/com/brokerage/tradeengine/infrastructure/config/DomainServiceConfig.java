package com.brokerage.tradeengine.infrastructure.config;

import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    OrderBalanceService orderBalanceService() {
        return new OrderBalanceService();
    }
}
