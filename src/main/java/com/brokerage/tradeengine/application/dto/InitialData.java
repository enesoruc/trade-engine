package com.brokerage.tradeengine.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class InitialData {

    private List<CustomerData> customers;
    private List<AssetData> assets;

    @Setter
    @Getter
    public static class CustomerData {
        private String customerId;
        private String name;
        private String status;

    }

    @Setter
    @Getter
    public static class AssetData {
        private String customerId;
        private String assetName;
        private BigDecimal size;
        private BigDecimal usableSize;

    }
}

