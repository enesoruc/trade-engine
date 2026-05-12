package com.brokerage.tradeengine.infrastructure.adapter.rest;

import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.application.port.in.ListOrdersInputPort;
import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management - Queries", description = "Order query operations")
public class OrderQueryController {

    private final ListOrdersInputPort listOrdersInputPort;
    private final CustomerIdResolver customerIdResolver;

    @GetMapping
    @Operation(summary = "List customer orders", description = "Returns the orders of the logged-in customer.")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public PagedResult<OrderItemResponse> listOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) OrderSide side,
            @RequestParam(required = false) String assetName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String effectiveCustomerId = customerIdResolver.resolve(customerId);
        return listOrdersInputPort.execute(effectiveCustomerId, startDate, endDate, status, side, assetName, page, size);
    }
}
