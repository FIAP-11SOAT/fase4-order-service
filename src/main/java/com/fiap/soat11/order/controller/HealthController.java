package com.fiap.soat11.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.soat11.order.decorators.PermitAll;

@RestController
@RequestMapping
public class HealthController {
    
    @GetMapping
    @PermitAll
    public String status() {
        return "Order Service is running";
    }

    
    @GetMapping("/health")
    @PermitAll
    public String health() {
        return "Order Service is healthy";
    }

}
