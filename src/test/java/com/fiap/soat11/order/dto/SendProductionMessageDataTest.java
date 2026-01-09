package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SendProductionMessageDataTest {

    @Test
    void shouldCreateSendProductionMessageData() {
        ConsumerMeta meta = ConsumerMeta.create(
            "2024-01-01T10:00:00",
            "order-service",
            "production-service",
            "order-paid-event"
        );
        SendProductionPayload payload = new SendProductionPayload(
            java.util.UUID.randomUUID(),
            java.util.List.of(new SendProductionItemData("Product", 1)),
            new SendProductionCustomerData("customer-123", "John Doe")
        );

        SendProductionMessageData message = new SendProductionMessageData(meta, payload);

        assertNotNull(message);
        assertEquals(meta, message.meta());
        assertEquals(payload, message.payload());
    }

    @Test
    void shouldHandleNullValues() {
        SendProductionMessageData message = new SendProductionMessageData(null, null);

        assertNotNull(message);
        assertEquals(null, message.meta());
        assertEquals(null, message.payload());
    }
}
