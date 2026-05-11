package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.InitialData;
import com.brokerage.tradeengine.application.port.out.InitialDataProvider;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Customer;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadInitialDataUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private InitialDataProvider initialDataProvider;

    @InjectMocks
    private LoadInitialDataUseCase loadInitialDataUseCase;

    @Test
    void execute_shouldPersistCustomersAndAssetsFromProvider() {
        InitialData.CustomerData customerData = new InitialData.CustomerData();
        customerData.setCustomerId("cust-1");
        customerData.setName("Alice");
        customerData.setStatus("ACTIVE");

        InitialData.AssetData assetData = new InitialData.AssetData();
        assetData.setCustomerId("cust-1");
        assetData.setAssetName("TRY");
        assetData.setSize(new BigDecimal("1000.00"));
        assetData.setUsableSize(new BigDecimal("900.00"));

        InitialData initialData = new InitialData();
        initialData.setCustomers(List.of(customerData));
        initialData.setAssets(List.of(assetData));
        when(initialDataProvider.load()).thenReturn(initialData);

        loadInitialDataUseCase.execute();

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        assertEquals("cust-1", customerCaptor.getValue().id());
        assertEquals("Alice", customerCaptor.getValue().name());

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository).save(assetCaptor.capture());
        assertEquals("TRY", assetCaptor.getValue().getAssetName());
        assertEquals(new BigDecimal("1000.00"), assetCaptor.getValue().getSize());
    }

    @Test
    void execute_shouldSkipPersistenceWhenListsAreNull() {
        InitialData initialData = new InitialData();
        when(initialDataProvider.load()).thenReturn(initialData);

        loadInitialDataUseCase.execute();

        verify(customerRepository, never()).save(any(Customer.class));
        verify(assetRepository, never()).save(any(Asset.class));
    }
}

