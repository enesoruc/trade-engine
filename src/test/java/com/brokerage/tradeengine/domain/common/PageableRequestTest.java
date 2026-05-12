package com.brokerage.tradeengine.domain.common;

import com.brokerage.tradeengine.domain.exception.PageNumberMustBeValidException;
import com.brokerage.tradeengine.domain.exception.PageSizeMustBeValidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageableRequestTest {

    @Test
    void shouldCreatePageableRequestWithValidParameters() {
        PageableRequest request = new PageableRequest(0, 10);
        assertNotNull(request);
        assertEquals(0, request.pageNumber());
        assertEquals(10, request.pageSize());
    }

    @Test
    void shouldThrowPageNumberMustBeValidExceptionWhenPageNumberIsNegative() {
        assertThrows(PageNumberMustBeValidException.class, () -> new PageableRequest(-1, 10));
    }

    @Test
    void shouldThrowPageSizeMustBeValidExceptionWhenPageSizeIsLessThanOne() {
        assertThrows(PageSizeMustBeValidException.class, () -> new PageableRequest(0, 0));
    }

    @Test
    void shouldThrowPageSizeMustBeValidExceptionWhenPageSizeIsGreaterThanOneHundred() {
        assertThrows(PageSizeMustBeValidException.class, () -> new PageableRequest(0, 101));
    }

    @Test
    void ofMethodShouldAdjustPageNumberCorrectly() {
        PageableRequest request = PageableRequest.of(5, 20);
        assertNotNull(request);
        assertEquals(4, request.pageNumber()); // 5 - 1 = 4
        assertEquals(20, request.pageSize());
    }

    @Test
    void ofMethodShouldThrowPageNumberMustBeValidExceptionWhenPageNumberIsNegativeAfterAdjustment() {
        assertThrows(PageNumberMustBeValidException.class, () -> PageableRequest.of(0, 10)); // 0 - 1 = -1
    }

    @Test
    void ofMethodShouldThrowPageSizeMustBeValidExceptionWhenPageSizeIsLessThanOne() {
        assertThrows(PageSizeMustBeValidException.class, () -> PageableRequest.of(1, 0));
    }

    @Test
    void ofMethodShouldThrowPageSizeMustBeValidExceptionWhenPageSizeIsGreaterThanOneHundred() {
        assertThrows(PageSizeMustBeValidException.class, () -> PageableRequest.of(1, 101));
    }
}
