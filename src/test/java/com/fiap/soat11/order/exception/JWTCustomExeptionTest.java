package com.fiap.soat11.order.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWTCustomExeptionTest {

    @Test
    void testExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        JWTCustomExeption exception = new JWTCustomExeption("JWT error occurred", cause);
        
        assertEquals("JWT error occurred", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(JWTCustomExeption.class, () -> {
            throw new JWTCustomExeption("Test error", new Exception("Cause"));
        });
    }
}
