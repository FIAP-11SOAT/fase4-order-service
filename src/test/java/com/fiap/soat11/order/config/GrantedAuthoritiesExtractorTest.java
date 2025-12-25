package com.fiap.soat11.order.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GrantedAuthoritiesExtractorTest {

    private GrantedAuthoritiesExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new GrantedAuthoritiesExtractor();
    }

    @Test
    void testConvertWithValidUserType() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_type", "admin");
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );

        AbstractAuthenticationToken token = extractor.convert(jwt);

        assertNotNull(token, "Token should not be null");
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertEquals(1, authorities.size(), "Should have one authority");
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
            "Should contain ROLE_ADMIN");
    }

    @Test
    void testConvertWithMultipleUserTypes() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_type", "admin user customer");
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );

        AbstractAuthenticationToken token = extractor.convert(jwt);

        assertNotNull(token);
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertEquals(3, authorities.size(), "Should have three authorities");
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    void testConvertWithNullUserType() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );

        AbstractAuthenticationToken token = extractor.convert(jwt);

        assertNotNull(token);
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertTrue(authorities.isEmpty(), "Should have no authorities when user_type is null");
    }

    @Test
    void testConvertWithEmptyUserType() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_type", "");
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );

        AbstractAuthenticationToken token = extractor.convert(jwt);

        assertNotNull(token);
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertTrue(authorities.isEmpty(), "Should have no authorities when user_type is empty");
    }

    @Test
    void testConvertWithBlankUserType() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_type", "   ");
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );

        AbstractAuthenticationToken token = extractor.convert(jwt);

        assertNotNull(token);
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertTrue(authorities.isEmpty(), "Should have no authorities when user_type is blank");
    }

    @Test
    void testConvertWithUserTypeContainingExtraSpaces() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_type", "  admin   user  ");
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );

        AbstractAuthenticationToken token = extractor.convert(jwt);

        assertNotNull(token);
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertEquals(2, authorities.size(), "Should handle extra spaces correctly");
    }
}
