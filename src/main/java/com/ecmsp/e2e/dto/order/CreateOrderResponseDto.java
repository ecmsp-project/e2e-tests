package com.ecmsp.e2e.dto.order;

import java.util.List;

public record CreateOrderResponseDto(
        boolean isSuccess,
        String orderId,
        List<String> reservedVariantIds,
        List<FailedReservationVariantDto> failedVariants
) {
}
