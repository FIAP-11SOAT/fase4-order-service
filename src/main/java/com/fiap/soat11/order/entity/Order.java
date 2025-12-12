package com.fiap.soat11.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@JsonPropertyOrder({"id", "orderDate", "customerId", "status", "totalAmount", "paymentId", "items", "createdAt", "updatedAt"})
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME) // Gera UUIDv7 no Hibernate 6.4+
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @JsonProperty("order_date")
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @JsonProperty("costumer_id")
    @Column(name = "customer_id", length = 255)
    private String customerId;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.RECEBIDO;

    @Column(name = "status_event", length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatusEvent statusEvent;

    @JsonProperty("total_amount")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @JsonProperty("payment_id")
    @Column(name = "payment_id")
    private String paymentId;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public static Order create(String customerId) {
        Order order = new Order();
        order.setCustomerId(customerId);
        return order;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

