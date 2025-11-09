package com.ecmsp.e2e.dto.order;

public record FailedReservationVariantDto(
        String variantId,
        int requestedQuantity,
        int availableQuantity
) {
}
