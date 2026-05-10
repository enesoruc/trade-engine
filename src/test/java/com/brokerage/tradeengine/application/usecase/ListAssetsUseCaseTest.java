package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAssetsUseCaseTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private ListAssetsUseCase listAssetsUseCase;

    @Test
    void execute_shouldMapAssetsToResponseList() {
        String customerId = "cust-1";
        List<Asset> assets = List.of(
                new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("800.00")),
                new Asset("cust-1", "AAPL", new BigDecimal("10.00"), new BigDecimal("6.50"))
        );
        when(assetRepository.findByCustomerId(customerId)).thenReturn(assets);

        List<AssetListItemResponse> responses = listAssetsUseCase.execute(customerId);

        assertEquals(2, responses.size());
        assertEquals("TRY", responses.get(0).assetName());
        assertEquals(new BigDecimal("800.00"), responses.get(0).usableSize());
        assertEquals("AAPL", responses.get(1).assetName());
        verify(assetRepository).findByCustomerId(customerId);
    }
}
