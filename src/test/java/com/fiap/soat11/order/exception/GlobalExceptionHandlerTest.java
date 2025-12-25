package com.fiap.soat11.order.exception;

import com.fiap.soat11.order.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar AuthorizationDeniedException")
    void testHandleAuthorizationDeniedException() {
        // Arrange
        AuthorizationDecision decision = new AuthorizationDecision(false);
        AuthorizationDeniedException exception = new AuthorizationDeniedException("Access denied", decision);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthorizationDeniedException(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("AuthorizationDeniedException"));
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
    }

    @Test
    @DisplayName("Deve tratar AccessDeniedException")
    void testHandleAccessDeniedException() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("AccessDeniedException"));
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
    }

    @Test
    @DisplayName("Deve tratar AuthenticationException")
    void testHandleAuthenticationException() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Authentication failed") {};

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("AuthenticationException"));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatus());
    }

    @Test
    @DisplayName("Deve tratar ResponseStatusException")
    void testHandleResponseStatusException() {
        // Arrange
        ResponseStatusException exception = new ResponseStatusException(
            HttpStatus.NOT_FOUND, 
            "Resource not found"
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResponseStatusException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
    }
}
