package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateOrderRequest() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Integer quantity = 5;

        // Act
        CreateOrderRequest request = new CreateOrderRequest(productId, quantity);

        // Assert
        assertEquals(productId, request.productId());
        assertEquals(quantity, request.quantity());
    }

    @Test
    void shouldSerializeToJson() throws Exception {
        // Arrange
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CreateOrderRequest request = new CreateOrderRequest(productId, 3);

        // Act
        String json = objectMapper.writeValueAsString(request);

        // Assert
        assertTrue(json.contains("product_id"));
        assertTrue(json.contains("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(json.contains("\"quantity\":3"));
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        // Arrange
        String json = "{\"product_id\":\"123e4567-e89b-12d3-a456-426614174000\",\"quantity\":2}";

        // Act
        CreateOrderRequest request = objectMapper.readValue(json, CreateOrderRequest.class);

        // Assert
        assertNotNull(request);
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), request.productId());
        assertEquals(2, request.quantity());
    }
}
