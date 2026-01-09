package com.fiap.soat11.order.dto;

public record SendProductionMessageData(
    ConsumerMeta meta,
    SendProductionPayload payload
) {}
