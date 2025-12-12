package com.fiap.soat11.order.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOrderRequest(
    @JsonProperty("product_id")
    UUID productId,
    Integer quantity
) {
    
}
