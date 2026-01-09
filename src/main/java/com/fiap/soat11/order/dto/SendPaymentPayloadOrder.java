package com.fiap.soat11.order.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendPaymentPayloadOrder(
    UUID id,
    Double amount,
    @JsonProperty("customer_name")
    String customerName) {
    
}
