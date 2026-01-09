package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class SendPaymentPayloadTest {

    @Test
    void shouldCreateSendPaymentPayload() {
        SendPaymentPayloadOrder order = new SendPaymentPayloadOrder(
            UUID.randomUUID(),
            99.99,
            "John Doe"
        );

        SendPaymentPayload payload = new SendPaymentPayload(order);

        assertNotNull(payload);
        assertEquals(order, payload.order());
    }

    @Test
    void shouldHandleNullOrder() {
        SendPaymentPayload payload = new SendPaymentPayload(null);

        assertNotNull(payload);
        assertEquals(null, payload.order());
    }
}
