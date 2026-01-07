package com.fiap.soat11.order.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.soat11.order.dto.ConsumerMeta;
import com.fiap.soat11.order.dto.CreateOrderRequest;
import com.fiap.soat11.order.dto.SendPaymentMessageData;
import com.fiap.soat11.order.dto.SendPaymentPayload;
import com.fiap.soat11.order.dto.SendPaymentPayloadOrder;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderItem;
import com.fiap.soat11.order.helpers.client.catalog.CatalogClient;
import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;
import com.fiap.soat11.order.repository.OrderRepository;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient, SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
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

    public Order createOrder(List<CreateOrderRequest> request, String userId, String customerName) {
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

        SendPaymentMessageData paymentMessage = new SendPaymentMessageData(
            ConsumerMeta.create(
                getCurrentTimestamp(),
                "order-service",
                "payment-service",
                "order-payment-requested-event"),
            new SendPaymentPayload(
                new SendPaymentPayloadOrder(
                    order.getId(),
                    Double.valueOf(order.getTotalAmount().toString()), 
                    customerName)
            )
        );

        try {
            String messageJson = objectMapper.writeValueAsString(paymentMessage);
            this.sqsTemplate.send("fase4-payment-service-queue", messageJson);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing payment message", e);
        }

        return order;
    }


    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}