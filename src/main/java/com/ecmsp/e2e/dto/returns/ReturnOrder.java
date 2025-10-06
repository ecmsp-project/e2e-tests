package com.ecmsp.e2e.dto.returns;

import java.time.LocalDateTime;
import java.util.List;

public record ReturnOrder(
        String returnId,
        String orderId,
        List<ItemToReturnDetails> itemsToReturn,
        String status,
        LocalDateTime createdAt
) {
}
