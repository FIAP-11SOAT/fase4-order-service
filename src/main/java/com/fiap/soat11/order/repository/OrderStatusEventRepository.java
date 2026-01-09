package com.fiap.soat11.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.soat11.order.entity.OrderStatusEvent;

public interface OrderStatusEventRepository extends JpaRepository<OrderStatusEvent, UUID> {
    
    List<OrderStatusEvent> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
    
}
