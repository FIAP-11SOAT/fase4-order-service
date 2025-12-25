package com.fiap.soat11.order.consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderEventType Unit Tests")
class OrderEventTypeTest {

    @Test
    @DisplayName("Deve converter nome do evento para enum")
    void testFromEventName_Success() {
        // Act & Assert
        assertEquals(OrderEventType.PAYMENT_CREATED, 
            OrderEventType.fromEventName("payment-created-event"));
        assertEquals(OrderEventType.PAYMENT_COMPLETED, 
            OrderEventType.fromEventName("payment-completed-event"));
        assertEquals(OrderEventType.PAYMENT_FAILED, 
            OrderEventType.fromEventName("payment-failed-event"));
        assertEquals(OrderEventType.PRODUCTION_STARTED, 
            OrderEventType.fromEventName("production-started-event"));
        assertEquals(OrderEventType.PRODUCTION_COMPLETED, 
            OrderEventType.fromEventName("production-completed-event"));
        assertEquals(OrderEventType.PRODUCTION_DELIVERED, 
            OrderEventType.fromEventName("production-delivered-event"));
    }

    @Test
    @DisplayName("Deve lançar exceção para nome de evento desconhecido")
    void testFromEventName_UnknownEvent() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> OrderEventType.fromEventName("unknown-event")
        );
        
        assertTrue(exception.getMessage().contains("Unknown event name"));
    }

    @Test
    @DisplayName("Deve retornar o nome correto do evento")
    void testGetEventName_Success() {
        // Assert
        assertEquals("payment-created-event", OrderEventType.PAYMENT_CREATED.getEventName());
        assertEquals("payment-completed-event", OrderEventType.PAYMENT_COMPLETED.getEventName());
        assertEquals("payment-failed-event", OrderEventType.PAYMENT_FAILED.getEventName());
        assertEquals("production-started-event", OrderEventType.PRODUCTION_STARTED.getEventName());
        assertEquals("production-completed-event", OrderEventType.PRODUCTION_COMPLETED.getEventName());
        assertEquals("production-delivered-event", OrderEventType.PRODUCTION_DELIVERED.getEventName());
    }
}
