package com.fiap.soat11.order.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidStatusTransitionExceptionTest {

    @Test
    void testExceptionWithMessage() {
        InvalidStatusTransitionException exception = 
            new InvalidStatusTransitionException("Invalid status transition");
        
        assertEquals("Invalid status transition", exception.getMessage());
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(InvalidStatusTransitionException.class, () -> {
            throw new InvalidStatusTransitionException("Cannot transition from COMPLETED to PENDING");
        });
    }

    @Test
    void testExceptionIsRuntimeException() {
        InvalidStatusTransitionException exception = 
            new InvalidStatusTransitionException("Test");
        
        assertInstanceOf(RuntimeException.class, exception);
    }
}
