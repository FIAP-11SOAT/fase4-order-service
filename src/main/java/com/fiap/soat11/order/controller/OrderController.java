package com.fiap.soat11.order.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fiap.soat11.order.dto.CreateOrderRequest;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderItem;
import com.fiap.soat11.order.helpers.client.catalog.CatalogClient;
import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;
import com.fiap.soat11.order.repository.OrderRepository;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderRepository orderRepository;
    private CatalogClient catalogClient;

    public OrderController(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMERS')")
    public List<Order> orders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMERS')")
    public Order getOrderById(
        @PathVariable UUID id
    ) {
        Optional<Order> item = orderRepository.findById(id);
        
        if (item.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        
        return item.get();
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMERS')")
    public Order createOrder(
        @RequestBody List<CreateOrderRequest> request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        
        List<UUID> productIds = request.stream()
            .map(CreateOrderRequest::productId)
            .toList();

        List<ProductResponse> products = catalogClient.getProductsByIds(productIds);
        
        Order order = Order.create(
            jwt.getClaims().get("sub").toString()
        );
        
        for (ProductResponse product : products) {

            Integer quantity = request.stream()
                .filter(r -> r.productId().equals(product.id()))
                .findFirst()
                .map(CreateOrderRequest::quantity)
                .orElse(0);

            OrderItem item = OrderItem.create(
                product.id(), 
                product.name(), 
                product.price(), 
                quantity);

            order.addItem(item);
        }
        
        order.calculateTotalAmount();
        orderRepository.save(order);
        return order;
    }

}
