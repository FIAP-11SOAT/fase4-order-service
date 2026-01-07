package com.fiap.soat11.order.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsumerPaymentData(
    @JsonProperty("order_id")
    UUID orderID,
    @JsonProperty("payment_id")
    UUID paymentID
) {}
