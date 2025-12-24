package com.fiap.soat11.order.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsumerPayload(
    @JsonProperty("order_id")
    UUID orderID
) {}
