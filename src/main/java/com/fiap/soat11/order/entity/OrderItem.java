package com.fiap.soat11.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@JsonPropertyOrder({"id", "productId", "productName", "price", "quantity", "createdAt", "updatedAt"})
public class OrderItem {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME) // UUIDv7
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @JsonProperty("product_id")
    @Column(name = "product_id", columnDefinition = "uuid", nullable = false)
    private UUID productId;

    @JsonProperty("product_name")
    @Column(name = "product_name", length = 150)
    private String productName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public static OrderItem create(UUID productId, String productName, BigDecimal price, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setProductName(productName);
        item.setPrice(price);
        item.setQuantity(quantity);
        return item;
    }

}