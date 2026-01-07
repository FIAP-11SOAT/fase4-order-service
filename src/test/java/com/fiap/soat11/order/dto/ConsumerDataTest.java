package com.fiap.soat11.order.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateConsumerDataWithProduction() {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-123",
            "2024-12-25T10:00:00Z",
            "production-service",
            "order-service",
            "production.completed"
        );
        UUID orderId = UUID.randomUUID();
        ConsumerProductionData production = new ConsumerProductionData(orderId);
        ConsumerPayload payload = new ConsumerPayload(production, null);

        // Act
        ConsumerData data = new ConsumerData(meta, payload);

        // Assert
        assertNotNull(data);
        assertEquals(meta, data.meta());
        assertEquals(payload, data.payload());
        assertNotNull(data.payload().production());
        assertEquals(orderId, data.payload().production().orderID());
    }

    @Test
    void shouldCreateConsumerDataWithPayment() {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-456",
            "2024-12-25T11:00:00Z",
            "payment-service",
            "order-service",
            "payment.completed"
        );
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        ConsumerPaymentData payment = new ConsumerPaymentData(orderId, paymentId);
        ConsumerPayload payload = new ConsumerPayload(null, payment);

        // Act
        ConsumerData data = new ConsumerData(meta, payload);

        // Assert
        assertNotNull(data);
        assertEquals(meta, data.meta());
        assertEquals(payload, data.payload());
        assertNotNull(data.payload().payment());
        assertEquals(orderId, data.payload().payment().orderID());
        assertEquals(paymentId, data.payload().payment().paymentID());
    }

    @Test
    void shouldSerializeToJsonWithProduction() throws Exception {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-789",
            "2024-12-25T12:00:00Z",
            "production-service",
            "order-service",
            "production.started"
        );
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ConsumerProductionData production = new ConsumerProductionData(orderId);
        ConsumerPayload payload = new ConsumerPayload(production, null);
        ConsumerData data = new ConsumerData(meta, payload);

        // Act
        String json = objectMapper.writeValueAsString(data);

        // Assert
        assertTrue(json.contains("meta"));
        assertTrue(json.contains("payload"));
        assertTrue(json.contains("production"));
        assertTrue(json.contains("event_id"));
        assertTrue(json.contains("order_id"));
    }

    @Test
    void shouldSerializeToJsonWithPayment() throws Exception {
        // Arrange
        ConsumerMeta meta = new ConsumerMeta(
            "event-999",
            "2024-12-25T13:00:00Z",
            "payment-service",
            "order-service",
            "payment.completed"
        );
        UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID paymentId = UUID.fromString("987e6543-e89b-12d3-a456-426614174000");
        ConsumerPaymentData payment = new ConsumerPaymentData(orderId, paymentId);
        ConsumerPayload payload = new ConsumerPayload(null, payment);
        ConsumerData data = new ConsumerData(meta, payload);

        // Act
        String json = objectMapper.writeValueAsString(data);

        // Assert
        assertTrue(json.contains("meta"));
        assertTrue(json.contains("payload"));
        assertTrue(json.contains("payment"));
        assertTrue(json.contains("event_id"));
        assertTrue(json.contains("order_id"));
        assertTrue(json.contains("payment_id"));
    }

    @Test
    void shouldDeserializeFromJsonWithProduction() throws Exception {
        // Arrange
        String json = "{\"meta\":{\"event_id\":\"event-111\",\"event_type\":\"2024-12-25T14:00:00Z\",\"event_source\":\"production-service\",\"event_target\":\"order-service\",\"event_name\":\"production.started\"},\"payload\":{\"production\":{\"order_id\":\"123e4567-e89b-12d3-a456-426614174000\"},\"payment\":null}}";

        // Act
        ConsumerData data = objectMapper.readValue(json, ConsumerData.class);

        // Assert
        assertNotNull(data);
        assertNotNull(data.meta());
        assertNotNull(data.payload());
        assertNotNull(data.payload().production());
        assertEquals("event-111", data.meta().eventId());
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), data.payload().production().orderID());
    }

    @Test
    void shouldDeserializeFromJsonWithPayment() throws Exception {
        // Arrange
        String json = "{\"meta\":{\"event_id\":\"event-222\",\"event_type\":\"2024-12-25T15:00:00Z\",\"event_source\":\"payment-service\",\"event_target\":\"order-service\",\"event_name\":\"payment.completed\"},\"payload\":{\"production\":null,\"payment\":{\"order_id\":\"123e4567-e89b-12d3-a456-426614174000\",\"payment_id\":\"987e6543-e89b-12d3-a456-426614174000\"}}}";

        // Act
        ConsumerData data = objectMapper.readValue(json, ConsumerData.class);

        // Assert
        assertNotNull(data);
        assertNotNull(data.meta());
        assertNotNull(data.payload());
        assertNotNull(data.payload().payment());
        assertEquals("event-222", data.meta().eventId());
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), data.payload().payment().orderID());
        assertEquals(UUID.fromString("987e6543-e89b-12d3-a456-426614174000"), data.payload().payment().paymentID());
    }
}
