package com.ecmsp.e2e.dto.order;

import java.time.LocalDateTime;
import java.util.List;

public record Order(
        String orderId,
        String clientId,
        String orderStatus,
        LocalDateTime date,
        List<OrderItem> items) {
}