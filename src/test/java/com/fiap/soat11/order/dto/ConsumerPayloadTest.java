package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerPayloadTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateConsumerPayload() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Act
        ConsumerPayload payload = new ConsumerPayload(orderId);

        // Assert
        assertEquals(orderId, payload.orderID());
    }

    @Test
    void shouldSerializeToJson() throws Exception {
        // Arrange
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ConsumerPayload payload = new ConsumerPayload(orderId);

        // Act
        String json = objectMapper.writeValueAsString(payload);

        // Assert
        assertTrue(json.contains("order_id"));
        assertTrue(json.contains("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        // Arrange
        String json = "{\"order_id\":\"123e4567-e89b-12d3-a456-426614174000\"}";

        // Act
        ConsumerPayload payload = objectMapper.readValue(json, ConsumerPayload.class);

        // Assert
        assertNotNull(payload);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), payload.orderID());
    }
}
