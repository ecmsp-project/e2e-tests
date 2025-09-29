package com.ecmsp.e2e.dto.order;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    PAID,
    FAILED,
    CANCELLED,
    UNSPECIFIED // Added to match the protobuf enum
}