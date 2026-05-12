package com.brokerage.tradeengine.domain.common;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int totalPages,
        long totalElements
) {
    public static <T> PagedResult<T> of(List<T> content, int totalPages, long totalElements) {
        return new PagedResult<>(
                content,
                totalPages,
                totalElements
        );
    }
}
