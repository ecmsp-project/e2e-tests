package com.ecmsp.e2e.dto.order;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponseDto(
        String orderId,
        String orderStatus,
        LocalDateTime date,
        List<GetOrderItemDetailsDto> items) {
}