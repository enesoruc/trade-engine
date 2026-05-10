package com.brokerage.tradeengine.domain.exception;

import com.brokerage.tradeengine.domain.model.AssetSymbol;

public class TryAssetNotAllowedException extends DomainException {

    public TryAssetNotAllowedException() {
        super(
                "ASSET_NAME_NOT_ALLOWED",
                "error.order.asset-name-not-allowed",
                DomainErrorType.VALIDATION,
                AssetSymbol.TRY.name() + " cannot be used as assetName for orders",
                AssetSymbol.TRY.name()
        );
    }
}

