package com.ecmsp.e2e.dto.returns;

public record ItemToReturnDetails(String itemId, String variantId, Integer quantity, String reason) {
}

