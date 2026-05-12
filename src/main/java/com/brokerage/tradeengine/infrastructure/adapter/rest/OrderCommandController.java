package com.brokerage.tradeengine.infrastructure.adapter.rest;

import com.brokerage.tradeengine.application.dto.request.CancelOrderRequest;
import com.brokerage.tradeengine.application.dto.request.CreateOrderRequest;
import com.brokerage.tradeengine.application.dto.response.CreateOrderResponse;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.application.port.in.CancelOrderInputPort;
import com.brokerage.tradeengine.application.port.in.CreateOrderInputPort;
import com.brokerage.tradeengine.application.port.in.MatchOrderInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management - Commands", description = "Order command operations")
public class OrderCommandController {

    private final CreateOrderInputPort createOrderInputPort;
    private final CancelOrderInputPort cancelOrderInputPort;
    private final MatchOrderInputPort matchOrderInputPort;
    private final CustomerIdResolver customerIdResolver;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order", description = "Creates a new buy or sell order for a customer.")
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String customerId = customerIdResolver.resolve(request.customerId());
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                customerId,
                request.assetName(),
                request.side(),
                request.size(),
                request.price()
        );
        return createOrderInputPort.execute(createOrderRequest);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Cancel an order", description = "Cancels an existing order by its ID.")
    public OrderItemResponse cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String customerId
    ) {
        String effectiveCustomerId = customerIdResolver.resolve(customerId);
        return cancelOrderInputPort.execute(new CancelOrderRequest(orderId, effectiveCustomerId));
    }

    @PostMapping("/match")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Match orders", description = "Initiates the order matching process.")
    public void matchOrder() {
        matchOrderInputPort.execute();
    }
}
