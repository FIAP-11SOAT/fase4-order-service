package com.fiap.soat11.order.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HealthController {
    
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public String status() {
        return "Order Service is running";
    }

    @GetMapping("/health")
    public String health() {
        return "Order Service is healthy";
    }

}
