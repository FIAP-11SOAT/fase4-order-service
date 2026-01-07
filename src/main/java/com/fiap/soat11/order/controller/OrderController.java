package com.fiap.soat11.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.soat11.order.dto.CreateOrderRequest;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.service.OrderService;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMERS', 'EMPLOYEES', 'SERVICES')")
    public List<Order> orders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMERS', 'EMPLOYEES', 'SERVICES')")
    public Order getOrderById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMERS', 'EMPLOYEES', 'SERVICES')")
    public Order createOrder(
        @RequestBody List<CreateOrderRequest> request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getClaims().get("sub").toString();
        String customerName = jwt.getClaims().get("name").toString();
        return orderService.createOrder(request, userId, customerName);
    }

}
