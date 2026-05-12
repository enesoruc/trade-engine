package com.brokerage.tradeengine.domain.common;

import com.brokerage.tradeengine.domain.exception.PageNumberMustBeValidException;
import com.brokerage.tradeengine.domain.exception.PageSizeMustBeValidException;

public record PageableRequest(
        int pageNumber,
        int pageSize
) {
    public PageableRequest {
        if (pageNumber < 0) {
            throw new PageNumberMustBeValidException();
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new PageSizeMustBeValidException();
        }
    }

    public static PageableRequest of(int pageNumber, int pageSize) {
        return new PageableRequest(pageNumber - 1, pageSize);
    }
}
