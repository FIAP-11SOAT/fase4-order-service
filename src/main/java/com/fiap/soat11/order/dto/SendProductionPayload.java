package com.fiap.soat11.order.dto;

import java.util.List;
import java.util.UUID;

public record SendProductionPayload(
    UUID id,
    List<SendProductionItemData> itens,
    SendProductionCustomerData customer
) {}
