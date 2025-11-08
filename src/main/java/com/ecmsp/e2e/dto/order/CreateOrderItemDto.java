package com.ecmsp.e2e.dto.order;

public record CreateOrderItemDto(
        String itemId,
        String variantId,
        String name,
        int quantity,
        double price,
        String imageUrl,
        String description,
        boolean isReturnable
) {
}
