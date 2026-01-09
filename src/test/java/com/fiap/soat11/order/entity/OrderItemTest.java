package com.fiap.soat11.order.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemWithFactoryMethod() {
        UUID productId = UUID.randomUUID();
        String productName = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        Integer quantity = 5;

        OrderItem orderItem = OrderItem.create(productId, productName, price, quantity);

        assertNotNull(orderItem);
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(price, orderItem.getPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertNull(orderItem.getId());
        assertNull(orderItem.getCreatedAt());
        assertNull(orderItem.getUpdatedAt());
        assertNull(orderItem.getOrder());
    }

    @Test
    void shouldSetAndGetAllFields() {
        OrderItem orderItem = new OrderItem();
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Product Test";
        BigDecimal price = new BigDecimal("50.00");
        Integer quantity = 3;
        Order order = new Order();

        orderItem.setId(id);
        orderItem.setProductId(productId);
        orderItem.setProductName(productName);
        orderItem.setPrice(price);
        orderItem.setQuantity(quantity);
        orderItem.setOrder(order);

        assertEquals(id, orderItem.getId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(price, orderItem.getPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void shouldHandleDecimalPrices() {
        BigDecimal price = new BigDecimal("10.50");
        OrderItem orderItem = OrderItem.create(
            UUID.randomUUID(),
            "Product",
            price,
            1
        );

        assertEquals(price, orderItem.getPrice());
        assertEquals(0, price.compareTo(orderItem.getPrice()));
    }

    @Test
    void shouldHandleZeroQuantity() {
        OrderItem orderItem = OrderItem.create(
            UUID.randomUUID(),
            "Product",
            new BigDecimal("10.00"),
            0
        );

        assertEquals(0, orderItem.getQuantity());
    }
}
