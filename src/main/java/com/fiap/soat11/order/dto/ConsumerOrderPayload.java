package com.fiap.soat11.order.dto;

import java.util.List;

record Item(
    String name,
    Integer quantity
) {}

record Customer(
    String name
) {}

public record ConsumerOrderPayload(
    String id,
    List<Item> itens,
    Customer customer
) {}
