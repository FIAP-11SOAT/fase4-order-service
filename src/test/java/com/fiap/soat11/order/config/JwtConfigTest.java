package com.fiap.soat11.order.config;

import com.fiap.soat11.order.exception.JWTCustomExeption;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    @Test
    void testJwtDecoderBeanIsCreatedWithValidJwk() {
        JwtConfig config = new JwtConfig();
        
        // Valid RSA public key in JWK format
        String validJwk = "{\"kty\":\"RSA\",\"e\":\"AQAB\",\"n\":\"xGOr-H7A-PWfZQcqJdTRBtLvEm5cB8FvvL_SAF0eYPYh20X8HyNrXmFvHKQ31xO3WcXNhZmmGRrTNc0p-8gYPSP6HLX8BNz7-c0WXH08IkQTVXv7MYKzNxDHMM7OJJCHfHpJU6KnVjlvkqvDHqxRElHLbxMW8CgG1b0xQ-0cB-9vq6CiZ6kqQPNhBYPVYVEqQJ3vl4HxnJIGOGGnWLLCsxCy8QY9jxEXa0PQpSO6pTmBqCW9hW-dxf_KQ6SdZv8xOLH0XPl7p6eVDNQJRZ_fxT6L5b_8J5bHhzQ_8GwBb7EZcWqPB8N3B2J5C3q8p5H0J8Nq6pB5M\"}";
        
        try {
            var field = JwtConfig.class.getDeclaredField("jwkJson");
            field.setAccessible(true);
            field.set(config, validJwk);
            
            JwtDecoder decoder = config.jwtDecoder();
            assertNotNull(decoder, "JwtDecoder should be created with valid JWK");
        } catch (Exception e) {
            fail("Should create JwtDecoder with valid JWK: " + e.getMessage());
        }
    }

    @Test
    void testJwtConfigWithInvalidJwk() {
        JwtConfig config = new JwtConfig();
        
        try {
            var field = JwtConfig.class.getDeclaredField("jwkJson");
            field.setAccessible(true);
            field.set(config, "invalid-jwk");
            
            assertThrows(JWTCustomExeption.class, config::jwtDecoder,
                "Should throw JWTCustomExeption for invalid JWK");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }

    @Test
    void testJwtConfigWithEmptyJwk() {
        JwtConfig config = new JwtConfig();
        
        try {
            var field = JwtConfig.class.getDeclaredField("jwkJson");
            field.setAccessible(true);
            field.set(config, "");
            
            assertThrows(JWTCustomExeption.class, config::jwtDecoder,
                "Should throw JWTCustomExeption for empty JWK");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
}
