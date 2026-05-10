package com.brokerage.tradeengine.application.port.in;

import com.brokerage.tradeengine.application.dto.request.CreateOrderRequest;
import com.brokerage.tradeengine.application.dto.response.CreateOrderResponse;

public interface CreateOrderInputPort {

    CreateOrderResponse execute(CreateOrderRequest request);
}
