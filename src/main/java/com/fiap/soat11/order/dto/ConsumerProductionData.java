package com.fiap.soat11.order.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsumerProductionData(
    @JsonProperty("order_id")
    UUID orderID
) {}
