package com.brokerage.tradeengine.infrastructure.config;

import com.brokerage.tradeengine.application.dto.mapper.CreateOrderResponseMapper;
import com.brokerage.tradeengine.application.dto.mapper.OrderItemResponseMapper;
import com.brokerage.tradeengine.application.port.out.InitialDataProvider;
import com.brokerage.tradeengine.application.port.in.CancelOrderInputPort;
import com.brokerage.tradeengine.application.port.in.CreateOrderInputPort;
import com.brokerage.tradeengine.application.port.in.ListAssetsInputPort;
import com.brokerage.tradeengine.application.port.in.ListOrdersInputPort;
import com.brokerage.tradeengine.application.port.in.MatchOrderInputPort;
import com.brokerage.tradeengine.application.service.CustomerIdResolutionService;
import com.brokerage.tradeengine.application.usecase.CancelOrderUseCase;
import com.brokerage.tradeengine.application.usecase.CreateOrderUseCase;
import com.brokerage.tradeengine.application.usecase.ListAssetsUseCase;
import com.brokerage.tradeengine.application.usecase.ListOrdersUseCase;
import com.brokerage.tradeengine.application.usecase.LoadInitialDataUseCase;
import com.brokerage.tradeengine.application.usecase.MatchOrderUseCase;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.CustomerRepository;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    CreateOrderInputPort createOrderUseCase(
            AssetRepository assetRepository,
            OrderRepository orderRepository,
            CreateOrderResponseMapper mapper,
            OrderBalanceService orderBalanceService
    ) {
        return new CreateOrderUseCase(assetRepository, orderRepository, mapper, orderBalanceService);
    }

    @Bean
    CancelOrderInputPort cancelOrderUseCase(
            OrderRepository orderRepository,
            AssetRepository assetRepository,
            OrderItemResponseMapper orderMapper,
            OrderBalanceService orderBalanceService
    ) {
        return new CancelOrderUseCase(orderRepository, assetRepository, orderMapper, orderBalanceService);
    }

    @Bean
    ListOrdersInputPort listOrdersUseCase(
            OrderRepository orderRepository,
            OrderItemResponseMapper orderMapper
    ) {
        return new ListOrdersUseCase(orderRepository, orderMapper);
    }

    @Bean
    ListAssetsInputPort listAssetsUseCase(AssetRepository assetRepository) {
        return new ListAssetsUseCase(assetRepository);
    }

    @Bean
    MatchOrderInputPort matchOrderUseCase(
            OrderRepository orderRepository,
            AssetRepository assetRepository,
            OrderBalanceService orderBalanceService,
            TransactionTemplate transactionTemplate
    ) {
        return new MatchOrderUseCase(orderRepository, assetRepository, orderBalanceService, transactionTemplate);
    }

    @Bean
    LoadInitialDataUseCase loadInitialDataUseCase(
            CustomerRepository customerRepository,
            AssetRepository assetRepository,
            InitialDataProvider initialDataProvider
    ) {
        return new LoadInitialDataUseCase(customerRepository, assetRepository, initialDataProvider);
    }

    @Bean
    CustomerIdResolutionService customerIdResolutionService() {
        return new CustomerIdResolutionService();
    }
}
