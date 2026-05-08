package com.brokerage.tradeengine.application.port;

import com.brokerage.tradeengine.application.dto.InitialData;

public interface InitialDataProvider {

    InitialData load();
}

