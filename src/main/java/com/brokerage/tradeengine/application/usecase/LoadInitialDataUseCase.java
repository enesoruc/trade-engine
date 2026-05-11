package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.InitialData;
import com.brokerage.tradeengine.application.port.out.InitialDataProvider;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Customer;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class LoadInitialDataUseCase {

    private final CustomerRepository customerRepository;
    private final AssetRepository assetRepository;
    private final InitialDataProvider initialDataProvider;

    @Transactional
    public void execute() {
        InitialData data = initialDataProvider.load();

        if (data.getCustomers() != null) {
            data.getCustomers().forEach(c -> {
                Customer customer = new Customer(
                        c.getCustomerId(),
                        c.getName(),
                        c.getStatus()
                );
                customerRepository.save(customer);
            });
        }

        if (data.getAssets() != null) {
            data.getAssets().forEach(a -> {
                Asset asset = new Asset(
                        a.getCustomerId(),
                        a.getAssetName(),
                        a.getSize(),
                        a.getUsableSize()
                );
                assetRepository.save(asset);
            });
        }
    }
}
