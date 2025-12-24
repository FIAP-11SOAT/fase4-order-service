package com.fiap.soat11.order.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fiap.soat11.order.dto.CreateOrderRequest;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderItem;
import com.fiap.soat11.order.helpers.client.catalog.CatalogClient;
import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;
import com.fiap.soat11.order.repository.OrderRepository;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(UUID id) {
        Optional<Order> item = orderRepository.findById(id);
        
        if (item.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        
        return item.get();
    }

    public Order createOrder(List<CreateOrderRequest> request, String userId) {
        List<UUID> productIds = request.stream()
            .map(CreateOrderRequest::productId)
            .toList();

        List<ProductResponse> products = catalogClient.getProductsByIds(productIds);
        
        Order order = Order.create(userId);
        
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
                quantity
            );

            order.addItem(item);
        }
        
        order.calculateTotalAmount();
        orderRepository.save(order);
        return order;
    }
}
