package com.brokerage.tradeengine.infrastructure.adapter.rest;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;
import com.brokerage.tradeengine.application.usecase.ListAssetsUseCase;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assets")
@Tag(name = "Asset Management", description = "Asset listing operations")
public class AssetController {

    private final ListAssetsUseCase listAssetsUseCase;
    private final CustomerIdResolver customerIdResolver;

    @GetMapping
    @Operation(summary = "List customer assets", description = "Returns all assets belonging to the specified customer ID.")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public List<AssetListItemResponse> listAssets( @RequestParam(required = false) String customerId) {
        String effectiveCustomerId = customerIdResolver.resolve(customerId);
        return listAssetsUseCase.execute(effectiveCustomerId);
    }
}
