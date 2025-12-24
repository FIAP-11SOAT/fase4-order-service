package com.fiap.soat11.order.entity;

public enum OrderStatusEventEnum {
    ORDER_CREATED,
    AWAITING_PAYMENT,
    PAYMENT_APPROVED,
    PAYMENT_FAILED,
    AWAITING_PREPARATION,
    PREPARATION_IN_PROGRESS,
    FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}