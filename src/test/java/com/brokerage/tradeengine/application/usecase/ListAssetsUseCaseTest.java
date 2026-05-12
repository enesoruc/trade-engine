package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;
import com.brokerage.tradeengine.domain.common.PageableRequest;
import com.brokerage.tradeengine.domain.common.PagedResult;
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
        when(assetRepository.findByCustomerId(customerId, PageableRequest.of(1, 10)))
                .thenReturn(PagedResult.of(assets, 1, 2));

        PagedResult<AssetListItemResponse> responses = listAssetsUseCase.execute(customerId, 1, 10);

        assertEquals(2, responses.content().size());
        assertEquals("TRY", responses.content().getFirst().assetName());
        assertEquals(new BigDecimal("800.00"), responses.content().getFirst().usableSize());
        assertEquals("AAPL", responses.content().get(1).assetName());
        verify(assetRepository).findByCustomerId(customerId, PageableRequest.of(1, 10));
    }
}
