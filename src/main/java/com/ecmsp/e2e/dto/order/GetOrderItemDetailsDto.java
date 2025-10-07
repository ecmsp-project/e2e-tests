package com.ecmsp.e2e.dto.order;


public record GetOrderItemDetailsDto(
        String itemId,
        String variantId,
        int quantity,
        double price,
        String imageUrl,
        String description,
        boolean isReturnable
) {
}

