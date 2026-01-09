package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ConsumerProductionDataTest {

    @Test
    void shouldCreateConsumerProductionData() {
        UUID orderId = UUID.randomUUID();

        ConsumerProductionData data = new ConsumerProductionData(orderId);

        assertNotNull(data);
        assertEquals(orderId, data.orderID());
    }

    @Test
    void shouldHandleNullOrderId() {
        ConsumerProductionData data = new ConsumerProductionData(null);

        assertNotNull(data);
        assertEquals(null, data.orderID());
    }
}
