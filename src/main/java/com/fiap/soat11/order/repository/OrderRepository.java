package com.fiap.soat11.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.soat11.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    
}
