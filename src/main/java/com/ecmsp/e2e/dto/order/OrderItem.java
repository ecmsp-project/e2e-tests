package com.ecmsp.e2e.dto.order;

//TODO: it needs to have variantId as if it lacks then we can't make return without calling procduct service
public record OrderItem(
    String itemId,
    int quantity
) {
}
