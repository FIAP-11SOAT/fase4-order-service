package com.fiap.soat11.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.soat11.order.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtDecoder jwtDecoder;

    public SecurityConfig(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    @SuppressWarnings("java:S4502") // CSRF protection disabled safely for stateless JWT-based REST API
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(new GrantedAuthoritiesExtractor()))
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Unauthorized: " + authException.getMessage(),
                                    HttpServletResponse.SC_UNAUTHORIZED);
                            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
                        }))
                // CSRF protection is disabled because this is a stateless REST API using JWT
                // tokens.
                // CSRF attacks are only relevant for session-based authentication with cookies.
                // JWT tokens are sent in the Authorization header and are not susceptible to
                // CSRF attacks.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));

        return http.build();
    }

}