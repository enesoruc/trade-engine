package com.brokerage.tradeengine.domain.exception;

public class AssetNotFoundException extends DomainException {

    public AssetNotFoundException(String assetName) {
        super(
                "ASSET_NOT_FOUND",
                "error.asset.not-found",
                DomainErrorType.NOT_FOUND,
                "Asset not found: " + assetName,
                assetName
        );
    }
}
