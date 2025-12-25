package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerMetaTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateConsumerMeta() {
        // Arrange & Act
        ConsumerMeta meta = new ConsumerMeta(
            "event-123",
            "2024-12-25T10:00:00Z",
            "payment-service",
            "order-service",
            "payment.completed"
        );

        // Assert
        assertEquals("event-123", meta.eventId());
        assertEquals("2024-12-25T10:00:00Z", meta.eventDate());
        assertEquals("payment-service", meta.eventSource());
        assertEquals("order-service", meta.eventTarget());
        assertEquals("payment.completed", meta.eventName());
    }

    @Test
    void shouldSerializeToJson() throws Exception {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-456",
            "2024-12-25T11:00:00Z",
            "production-service",
            "order-service",
            "production.started"
        );

        // Act
        String json = objectMapper.writeValueAsString(meta);

        // Assert
        assertTrue(json.contains("event_id"));
        assertTrue(json.contains("event-456"));
        assertTrue(json.contains("event_type"));
        assertTrue(json.contains("event_source"));
        assertTrue(json.contains("event_target"));
        assertTrue(json.contains("event_name"));
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        // Arrange
        String json = "{\"event_id\":\"event-789\",\"event_type\":\"2024-12-25T12:00:00Z\",\"event_source\":\"payment-service\",\"event_target\":\"order-service\",\"event_name\":\"payment.failed\"}";

        // Act
        ConsumerMeta meta = objectMapper.readValue(json, ConsumerMeta.class);

        // Assert
        assertNotNull(meta);
        assertEquals("event-789", meta.eventId());
        assertEquals("2024-12-25T12:00:00Z", meta.eventDate());
        assertEquals("payment-service", meta.eventSource());
        assertEquals("order-service", meta.eventTarget());
        assertEquals("payment.failed", meta.eventName());
    }
}
