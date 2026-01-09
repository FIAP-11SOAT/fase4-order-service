package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class SendPaymentMessageDataTest {

    @Test
    void shouldCreateSendPaymentMessageData() {
        ConsumerMeta meta = ConsumerMeta.create(
            "2024-01-01T10:00:00",
            "order-service",
            "payment-service",
            "order-created-event"
        );
        SendPaymentPayloadOrder order = new SendPaymentPayloadOrder(
            UUID.randomUUID(),
            99.99,
            "John Doe"
        );
        SendPaymentPayload payload = new SendPaymentPayload(order);

        SendPaymentMessageData message = new SendPaymentMessageData(meta, payload);

        assertNotNull(message);
        assertEquals(meta, message.meta());
        assertEquals(payload, message.payload());
    }

    @Test
    void shouldHandleNullValues() {
        SendPaymentMessageData message = new SendPaymentMessageData(null, null);

        assertNotNull(message);
        assertEquals(null, message.meta());
        assertEquals(null, message.payload());
    }
}
