package com.fiap.soat11.order.dto;

public record ConsumerPayload(
    ConsumerProductionData production,
    ConsumerPaymentData payment
) {}
