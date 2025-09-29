package com.ecmsp.e2e.dto.order;

import java.math.BigDecimal;

public record OrderItem(
    String itemId,
    int quantity,
    BigDecimal priceAtTimeOfOrder,
    boolean isReturnable
) {
}
