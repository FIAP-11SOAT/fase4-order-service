package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SendProductionPayloadTest {

    @Test
    void shouldCreateSendProductionPayload() {
        UUID id = UUID.randomUUID();
        List<SendProductionItemData> items = List.of(
            new SendProductionItemData("Product 1", 2),
            new SendProductionItemData("Product 2", 3)
        );
        SendProductionCustomerData customer = new SendProductionCustomerData("customer-123", "John Doe");

        SendProductionPayload payload = new SendProductionPayload(id, items, customer);

        assertNotNull(payload);
        assertEquals(id, payload.id());
        assertEquals(items, payload.itens());
        assertEquals(customer, payload.customer());
        assertEquals(2, payload.itens().size());
    }

    @Test
    void shouldHandleEmptyItemsList() {
        UUID id = UUID.randomUUID();
        List<SendProductionItemData> items = List.of();
        SendProductionCustomerData customer = new SendProductionCustomerData("customer-123", "John Doe");

        SendProductionPayload payload = new SendProductionPayload(id, items, customer);

        assertNotNull(payload);
        assertEquals(0, payload.itens().size());
    }

    @Test
    void shouldHandleNullValues() {
        SendProductionPayload payload = new SendProductionPayload(null, null, null);

        assertNotNull(payload);
        assertEquals(null, payload.id());
        assertEquals(null, payload.itens());
        assertEquals(null, payload.customer());
    }
}
