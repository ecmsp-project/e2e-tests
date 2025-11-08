package com.ecmsp.e2e.dto.order;

import java.util.List;

public record CreateOrderRequestDto(
        List<CreateOrderItemDto> items
) {
}
