package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class SendPaymentPayloadOrderTest {

    @Test
    void shouldCreateSendPaymentPayloadOrder() {
        UUID id = UUID.randomUUID();
        Double amount = 99.99;
        String customerName = "John Doe";

        SendPaymentPayloadOrder order = new SendPaymentPayloadOrder(id, amount, customerName);

        assertNotNull(order);
        assertEquals(id, order.id());
        assertEquals(amount, order.amount());
        assertEquals(customerName, order.customerName());
    }

    @Test
    void shouldHandleNullValues() {
        SendPaymentPayloadOrder order = new SendPaymentPayloadOrder(null, null, null);

        assertNotNull(order);
        assertEquals(null, order.id());
        assertEquals(null, order.amount());
        assertEquals(null, order.customerName());
    }

    @Test
    void shouldHandleZeroAmount() {
        SendPaymentPayloadOrder order = new SendPaymentPayloadOrder(
            UUID.randomUUID(), 
            0.0, 
            "Customer"
        );

        assertNotNull(order);
        assertEquals(0.0, order.amount());
    }

    @Test
    void shouldHandleAnonymousCustomer() {
        SendPaymentPayloadOrder order = new SendPaymentPayloadOrder(
            UUID.randomUUID(), 
            50.0, 
            "anonymous"
        );

        assertNotNull(order);
        assertEquals("anonymous", order.customerName());
    }
}
