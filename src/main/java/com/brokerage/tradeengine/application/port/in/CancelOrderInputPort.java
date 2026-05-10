package com.brokerage.tradeengine.application.port.in;

import com.brokerage.tradeengine.application.dto.request.CancelOrderRequest;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;

public interface CancelOrderInputPort {

    OrderItemResponse execute(CancelOrderRequest request);
}
