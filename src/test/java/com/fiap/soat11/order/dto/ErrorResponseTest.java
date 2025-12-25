package com.fiap.soat11.order.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithConstructor() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse("Test error message", 400);

        // Assert
        assertEquals("Test error message", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus());
    }

    @Test
    void shouldSetMessage() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse("Initial message", 500);

        // Act
        errorResponse.setMessage("Updated message");

        // Assert
        assertEquals("Updated message", errorResponse.getMessage());
    }

    @Test
    void shouldSetStatus() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse("Error", 400);

        // Act
        errorResponse.setStatus(404);

        // Assert
        assertEquals(404, errorResponse.getStatus());
    }

    @Test
    void shouldHandleNullMessage() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse(null, 500);

        // Assert
        assertNull(errorResponse.getMessage());
        assertEquals(500, errorResponse.getStatus());
    }
}
