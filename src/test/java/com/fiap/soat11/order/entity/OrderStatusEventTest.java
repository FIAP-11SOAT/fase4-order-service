package com.fiap.soat11.order.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderStatusEventTest {

    @Test
    void shouldCreateOrderStatusEventWithFactoryMethod() {
        OrderStatusEventEnum status = OrderStatusEventEnum.ORDER_CREATED;
        String description = "Order was created successfully";

        OrderStatusEvent event = OrderStatusEvent.create(status, description);

        assertNotNull(event);
        assertEquals(status, event.getStatus());
        assertEquals(description, event.getDescription());
        assertNull(event.getId());
        assertNull(event.getCreatedAt());
        assertNull(event.getOrder());
    }

    @Test
    void shouldSetAndGetAllFields() {
        OrderStatusEvent event = new OrderStatusEvent();
        UUID id = UUID.randomUUID();
        OrderStatusEventEnum status = OrderStatusEventEnum.PAYMENT_APPROVED;
        String description = "Payment approved";
        Order order = new Order();

        event.setId(id);
        event.setStatus(status);
        event.setDescription(description);
        event.setOrder(order);

        assertEquals(id, event.getId());
        assertEquals(status, event.getStatus());
        assertEquals(description, event.getDescription());
        assertEquals(order, event.getOrder());
    }

    @Test
    void shouldHandleNullDescription() {
        OrderStatusEvent event = OrderStatusEvent.create(
            OrderStatusEventEnum.ORDER_CREATED,
            null
        );

        assertNotNull(event);
        assertEquals(OrderStatusEventEnum.ORDER_CREATED, event.getStatus());
        assertNull(event.getDescription());
    }

    @Test
    void shouldCreateEventForDifferentStatuses() {
        OrderStatusEventEnum[] statuses = {
            OrderStatusEventEnum.ORDER_CREATED,
            OrderStatusEventEnum.AWAITING_PAYMENT,
            OrderStatusEventEnum.PAYMENT_APPROVED,
            OrderStatusEventEnum.PAYMENT_FAILED,
            OrderStatusEventEnum.AWAITING_PREPARATION,
            OrderStatusEventEnum.PREPARATION_IN_PROGRESS,
            OrderStatusEventEnum.FOR_DELIVERY,
            OrderStatusEventEnum.DELIVERED,
            OrderStatusEventEnum.CANCELLED
        };

        for (OrderStatusEventEnum status : statuses) {
            OrderStatusEvent event = OrderStatusEvent.create(status, "Test description");
            assertNotNull(event);
            assertEquals(status, event.getStatus());
        }
    }
}
