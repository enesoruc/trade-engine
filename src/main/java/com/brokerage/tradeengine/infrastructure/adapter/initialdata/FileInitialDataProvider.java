package com.brokerage.tradeengine.infrastructure.adapter.initialdata;

import com.brokerage.tradeengine.application.dto.InitialData;
import com.brokerage.tradeengine.application.exception.InitialDataLoadException;
import com.brokerage.tradeengine.application.port.out.InitialDataProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class FileInitialDataProvider implements InitialDataProvider {

    private final ObjectMapper objectMapper;

    @Value("${tradeengine.initial-data.path:initial-data.json}")
    private String path;

    @Override
    public InitialData load() {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream is = resource.getInputStream()) {
                return objectMapper.readValue(is, InitialData.class);
            }
        } catch (IOException e) {
            throw new InitialDataLoadException(path, e);
        }
    }
}

