package com.fiap.soat11.order.dto;

public record SendPaymentMessageData(
    ConsumerMeta meta,
    SendPaymentPayload payload
) {}
