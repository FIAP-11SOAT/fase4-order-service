package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerPayloadTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateConsumerPayloadWithProduction() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        ConsumerProductionData production = new ConsumerProductionData(orderId);

        // Act
        ConsumerPayload payload = new ConsumerPayload(production, null);

        // Assert
        assertNotNull(payload.production());
        assertNull(payload.payment());
        assertEquals(orderId, payload.production().orderID());
    }

    @Test
    void shouldCreateConsumerPayloadWithPayment() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        ConsumerPaymentData payment = new ConsumerPaymentData(orderId, paymentId);

        // Act
        ConsumerPayload payload = new ConsumerPayload(null, payment);

        // Assert
        assertNull(payload.production());
        assertNotNull(payload.payment());
        assertEquals(orderId, payload.payment().orderID());
        assertEquals(paymentId, payload.payment().paymentID());
    }

    @Test
    void shouldSerializeToJsonWithProduction() throws Exception {
        // Arrange
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ConsumerProductionData production = new ConsumerProductionData(orderId);
        ConsumerPayload payload = new ConsumerPayload(production, null);

        // Act
        String json = objectMapper.writeValueAsString(payload);

        // Assert
        assertTrue(json.contains("production"));
        assertTrue(json.contains("order_id"));
        assertTrue(json.contains("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void shouldSerializeToJsonWithPayment() throws Exception {
        // Arrange
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID paymentId = UUID.fromString("987e6543-e89b-12d3-a456-426614174000");
        ConsumerPaymentData payment = new ConsumerPaymentData(orderId, paymentId);
        ConsumerPayload payload = new ConsumerPayload(null, payment);

        // Act
        String json = objectMapper.writeValueAsString(payload);

        // Assert
        assertTrue(json.contains("payment"));
        assertTrue(json.contains("order_id"));
        assertTrue(json.contains("payment_id"));
        assertTrue(json.contains("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(json.contains("987e6543-e89b-12d3-a456-426614174000"));
    }

    @Test
    void shouldDeserializeFromJsonWithProduction() throws Exception {
        // Arrange
        String json = "{\"production\":{\"order_id\":\"123e4567-e89b-12d3-a456-426614174000\"},\"payment\":null}";

        // Act
        ConsumerPayload payload = objectMapper.readValue(json, ConsumerPayload.class);

        // Assert
        assertNotNull(payload);
        assertNotNull(payload.production());
        assertNull(payload.payment());
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), payload.production().orderID());
    }

    @Test
    void shouldDeserializeFromJsonWithPayment() throws Exception {
        // Arrange
        String json = "{\"production\":null,\"payment\":{\"order_id\":\"123e4567-e89b-12d3-a456-426614174000\",\"payment_id\":\"987e6543-e89b-12d3-a456-426614174000\"}}";

        // Act
        ConsumerPayload payload = objectMapper.readValue(json, ConsumerPayload.class);

        // Assert
        assertNotNull(payload);
        assertNull(payload.production());
        assertNotNull(payload.payment());
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), payload.payment().orderID());
        assertEquals(UUID.fromString("987e6543-e89b-12d3-a456-426614174000"), payload.payment().paymentID());
    }
}
