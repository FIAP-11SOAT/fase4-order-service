package com.fiap.soat11.order.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class OrderConsumerExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String errorMessage = "Error processing order consumer message";

        OrderConsumerException exception = new OrderConsumerException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfRuntimeException() {
        OrderConsumerException exception = new OrderConsumerException("Test error");

        assertNotNull(exception);
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }

    @Test
    void shouldHandleEmptyMessage() {
        OrderConsumerException exception = new OrderConsumerException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void shouldHandleNullMessage() {
        OrderConsumerException exception = new OrderConsumerException(null);

        assertNotNull(exception);
        assertEquals(null, exception.getMessage());
    }
}
