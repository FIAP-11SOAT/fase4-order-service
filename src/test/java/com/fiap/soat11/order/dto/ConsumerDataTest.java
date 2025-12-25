package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateConsumerData() {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-123",
            "2024-12-25T10:00:00Z",
            "payment-service",
            "order-service",
            "payment.completed"
        );
        UUID orderId = UUID.randomUUID();
        ConsumerPayload payload = new ConsumerPayload(orderId);

        // Act
        ConsumerData data = new ConsumerData(meta, payload);

        // Assert
        assertNotNull(data);
        assertEquals(meta, data.meta());
        assertEquals(payload, data.payload());
        assertEquals(orderId, data.payload().orderID());
    }

    @Test
    void shouldSerializeToJson() throws Exception {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-456",
            "2024-12-25T11:00:00Z",
            "production-service",
            "order-service",
            "production.completed"
        );
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ConsumerPayload payload = new ConsumerPayload(orderId);
        ConsumerData data = new ConsumerData(meta, payload);

        // Act
        String json = objectMapper.writeValueAsString(data);

        // Assert
        assertTrue(json.contains("meta"));
        assertTrue(json.contains("payload"));
        assertTrue(json.contains("event_id"));
        assertTrue(json.contains("order_id"));
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        // Arrange
        String json = "{\"meta\":{\"event_id\":\"event-789\",\"event_type\":\"2024-12-25T12:00:00Z\",\"event_source\":\"payment-service\",\"event_target\":\"order-service\",\"event_name\":\"payment.created\"},\"payload\":{\"order_id\":\"123e4567-e89b-12d3-a456-426614174000\"}}";

        // Act
        ConsumerData data = objectMapper.readValue(json, ConsumerData.class);

        // Assert
        assertNotNull(data);
        assertNotNull(data.meta());
        assertNotNull(data.payload());
        assertEquals("event-789", data.meta().eventId());
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), data.payload().orderID());
    }
}
