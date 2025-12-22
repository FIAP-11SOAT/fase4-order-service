package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record ConsumerData (
    ConsumerMeta meta,
    JsonNode payload
) {}
