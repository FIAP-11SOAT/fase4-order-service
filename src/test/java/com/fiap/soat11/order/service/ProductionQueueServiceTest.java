package com.fiap.soat11.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderItem;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@ExtendWith(MockitoExtension.class)
class ProductionQueueServiceTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductionQueueService productionQueueService;

    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId("customer-123");

        orderItem = OrderItem.create(
            UUID.randomUUID(),
            "Product 1",
            new BigDecimal("10.00"),
            2
        );
        orderItem.setOrder(order);
        order.setItems(List.of(orderItem));
    }

    @Test
    void shouldSendOrderPaidEventSuccessfully() throws JsonProcessingException {
        String expectedJson = "{\"meta\":{},\"payload\":{}}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

        productionQueueService.sendOrderPaidEvent(order);

        verify(objectMapper, times(1)).writeValueAsString(any());
        verify(sqsTemplate, times(1)).send(eq("fase4-production-service-queue"), anyString());
    }

    @Test
    void shouldSendOrderPaidEventWithAnonymousCustomer() throws JsonProcessingException {
        order.setCustomerId(null);
        String expectedJson = "{\"meta\":{},\"payload\":{}}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

        productionQueueService.sendOrderPaidEvent(order);

        verify(objectMapper, times(1)).writeValueAsString(any());
        verify(sqsTemplate, times(1)).send(eq("fase4-production-service-queue"), anyString());
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenJsonProcessingFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any()))
            .thenThrow(new JsonProcessingException("Error") {});

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> productionQueueService.sendOrderPaidEvent(order)
        );

        assertEquals("500 INTERNAL_SERVER_ERROR \"Error serializing production message\"", 
            exception.getMessage());
    }

    @Test
    void shouldHandleMultipleItemsInOrder() throws JsonProcessingException {
        OrderItem orderItem2 = OrderItem.create(
            UUID.randomUUID(),
            "Product 2",
            new BigDecimal("20.00"),
            3
        );
        orderItem2.setOrder(order);
        order.setItems(List.of(orderItem, orderItem2));

        String expectedJson = "{\"meta\":{},\"payload\":{}}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

        productionQueueService.sendOrderPaidEvent(order);

        verify(objectMapper, times(1)).writeValueAsString(any());
        verify(sqsTemplate, times(1)).send(eq("fase4-production-service-queue"), anyString());
    }
}
