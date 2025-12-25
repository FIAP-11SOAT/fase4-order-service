package com.fiap.soat11.order.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Entity Unit Tests")
class OrderTest {

    @Test
    @DisplayName("Deve criar um pedido com valores padrão")
    void testCreateOrder_DefaultValues() {
        // Arrange & Act
        Order order = Order.create("user-123");

        // Assert
        assertNotNull(order);
        assertEquals("user-123", order.getCustomerId());
        assertEquals(OrderStatusEnum.RECEBIDO, order.getStatus());
        assertEquals(OrderStatusEventEnum.ORDER_CREATED, order.getStatusEvent());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
        assertEquals(1, order.getStatusEvents().size());
    }

    @Test
    @DisplayName("Deve adicionar item ao pedido")
    void testAddItem_Success() {
        // Arrange
        Order order = Order.create("user-123");
        OrderItem item = OrderItem.create(
            UUID.randomUUID(), 
            "Produto", 
            new BigDecimal("10.00"), 
            2
        );

        // Act
        order.addItem(item);

        // Assert
        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());
    }

    @Test
    @DisplayName("Deve calcular o valor total do pedido")
    void testCalculateTotalAmount_Success() {
        // Arrange
        Order order = Order.create("user-123");
        OrderItem item1 = OrderItem.create(UUID.randomUUID(), "Produto 1", new BigDecimal("10.00"), 2);
        OrderItem item2 = OrderItem.create(UUID.randomUUID(), "Produto 2", new BigDecimal("5.00"), 3);
        
        order.addItem(item1);
        order.addItem(item2);

        // Act
        order.calculateTotalAmount();

        // Assert
        assertEquals(new BigDecimal("35.00"), order.getTotalAmount()); // (10*2) + (5*3)
    }

    @Test
    @DisplayName("Deve atualizar status do evento")
    void testUpdateStatusEvent_Success() {
        // Arrange
        Order order = Order.create("user-123");
        int initialEventsCount = order.getStatusEvents().size();

        // Act
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PAYMENT, "Aguardando pagamento");

        // Assert
        assertEquals(OrderStatusEventEnum.AWAITING_PAYMENT, order.getStatusEvent());
        assertEquals(initialEventsCount + 1, order.getStatusEvents().size());
    }

    @Test
    @DisplayName("Deve adicionar evento de status")
    void testAddStatusEvent_Success() {
        // Arrange
        Order order = Order.create("user-123");
        OrderStatusEvent event = OrderStatusEvent.create(
            OrderStatusEventEnum.PAYMENT_APPROVED, 
            "Pagamento aprovado"
        );

        // Act
        order.addStatusEvent(event);

        // Assert
        assertEquals(2, order.getStatusEvents().size());
        assertEquals(order, event.getOrder());
    }

    @Test
    @DisplayName("Deve calcular total zero quando não há itens")
    void testCalculateTotalAmount_NoItems() {
        // Arrange
        Order order = Order.create("user-123");

        // Act
        order.calculateTotalAmount();

        // Assert
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    @Test
    @DisplayName("Deve atualizar status do pedido")
    void testSetStatus_Success() {
        // Arrange
        Order order = Order.create("user-123");

        // Act
        order.setStatus(OrderStatusEnum.EM_PRPARACAO);

        // Assert
        assertEquals(OrderStatusEnum.EM_PRPARACAO, order.getStatus());
    }
}
