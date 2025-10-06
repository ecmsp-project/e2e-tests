package com.ecmsp.e2e.dto.returns;

import java.util.List;

public record ReturnToCreate(
        String orderId,
        List<ItemToReturnDetails> itemsToReturn
) {}
