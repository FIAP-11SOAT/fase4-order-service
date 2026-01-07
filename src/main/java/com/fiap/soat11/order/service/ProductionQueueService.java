package com.fiap.soat11.order.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.soat11.order.dto.ConsumerMeta;
import com.fiap.soat11.order.dto.SendProductionCustomerData;
import com.fiap.soat11.order.dto.SendProductionItemData;
import com.fiap.soat11.order.dto.SendProductionMessageData;
import com.fiap.soat11.order.dto.SendProductionPayload;
import com.fiap.soat11.order.entity.Order;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Service
public class ProductionQueueService {
    
    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;
    
    public ProductionQueueService(SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void sendOrderPaidEvent(Order order) {
        List<SendProductionItemData> items = order.getItems().stream()
            .map(item -> new SendProductionItemData(
                item.getProductName(),
                item.getQuantity()
            ))
            .collect(Collectors.toList());
        
        SendProductionCustomerData customer = new SendProductionCustomerData(
            order.getCustomerId() != null ? order.getCustomerId() : "anonymous",
            "anonymous"
        );
        
        SendProductionPayload payload = new SendProductionPayload(
            order.getId(),
            items,
            customer
        );
        
        SendProductionMessageData message = new SendProductionMessageData(
            ConsumerMeta.create(
                getCurrentTimestamp(),
                "order-service",
                "production-service",
                "order-paid-event"
            ),
            payload
        );
        
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            sqsTemplate.send("fase4-production-service-queue", messageJson);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error serializing production message", 
                e
            );
        }
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
