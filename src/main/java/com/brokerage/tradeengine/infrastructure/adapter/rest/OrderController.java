package com.brokerage.tradeengine.infrastructure.adapter.rest;

import com.brokerage.tradeengine.application.dto.request.CancelOrderRequest;
import com.brokerage.tradeengine.application.dto.request.CreateOrderRequest;
import com.brokerage.tradeengine.application.dto.response.CreateOrderResponse;
import com.brokerage.tradeengine.application.dto.response.ListOrdersResponse;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.application.port.in.CancelOrderInputPort;
import com.brokerage.tradeengine.application.port.in.CreateOrderInputPort;
import com.brokerage.tradeengine.application.port.in.ListOrdersInputPort;
import com.brokerage.tradeengine.application.port.in.MatchOrderInputPort;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management", description = "Order operations")
public class OrderController {

    private final ListOrdersInputPort listOrdersUseCase;
    private final CreateOrderInputPort createOrderUseCase;
    private final CancelOrderInputPort cancelOrderUseCase;
    private final MatchOrderInputPort matchOrderUseCase;
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
        return createOrderUseCase.execute(createOrderRequest);
    }

    @GetMapping
    @Operation(summary = "List customer orders", description = "Returns the orders of the logged-in customer.")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ListOrdersResponse listOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) OrderSide side,
            @RequestParam(required = false) String assetName
    ) {
        String effectiveCustomerId = customerIdResolver.resolve(customerId);
        List<OrderItemResponse> orders = listOrdersUseCase.execute(effectiveCustomerId, startDate, endDate, status, side, assetName);
        return new ListOrdersResponse(orders);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Cancel an order", description = "Cancels an existing order by its ID.")
    public OrderItemResponse cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String customerId
    ) {
        String effectiveCustomerId = customerIdResolver.resolve(customerId);
        return cancelOrderUseCase.execute(new CancelOrderRequest(orderId, effectiveCustomerId));
    }

    @PostMapping("/match")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Match orders", description = "Initiates the order matching process.")
    public void matchOrder() {
        matchOrderUseCase.execute();
    }
}
