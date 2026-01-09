package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ConsumerPaymentDataTest {

    @Test
    void shouldCreateConsumerPaymentData() {
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        ConsumerPaymentData data = new ConsumerPaymentData(orderId, paymentId);

        assertNotNull(data);
        assertEquals(orderId, data.orderID());
        assertEquals(paymentId, data.paymentID());
    }

    @Test
    void shouldHandleNullValues() {
        ConsumerPaymentData data = new ConsumerPaymentData(null, null);

        assertNotNull(data);
        assertEquals(null, data.orderID());
        assertEquals(null, data.paymentID());
    }
}
