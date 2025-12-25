package com.fiap.soat11.order.helpers.client.catalog.schemas;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void shouldCreateProductResponse() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        // Act
        ProductResponse product = new ProductResponse(
            productId,
            "Burger",
            "Delicious burger",
            new BigDecimal("25.50"),
            "http://image.jpg",
            15,
            categoryId,
            now,
            now
        );

        // Assert
        assertEquals(productId, product.id());
        assertEquals("Burger", product.name());
        assertEquals("Delicious burger", product.description());
        assertEquals(new BigDecimal("25.50"), product.price());
        assertEquals("http://image.jpg", product.imageUrl());
        assertEquals(15, product.preparationTime());
        assertEquals(categoryId, product.categoryId());
        assertEquals(now, product.createdAt());
        assertEquals(now, product.updatedAt());
    }

    @Test
    void shouldCreateProductResponseWithAllFields() {
        // Arrange
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID categoryId = UUID.fromString("987e6543-e21b-12d3-a456-426614174000");
        OffsetDateTime createdAt = OffsetDateTime.parse("2024-12-25T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2024-12-25T11:00:00Z");

        // Act
        ProductResponse product = new ProductResponse(
            productId,
            "Pizza",
            "Italian pizza",
            new BigDecimal("30.00"),
            "http://pizza.jpg",
            20,
            categoryId,
            createdAt,
            updatedAt
        );

        // Assert
        assertNotNull(product);
        assertEquals(productId, product.id());
        assertEquals("Pizza", product.name());
        assertEquals("Italian pizza", product.description());
        assertEquals(0, new BigDecimal("30.00").compareTo(product.price()));
        assertEquals("http://pizza.jpg", product.imageUrl());
        assertEquals(20, product.preparationTime());
        assertEquals(categoryId, product.categoryId());
        assertEquals(createdAt, product.createdAt());
        assertEquals(updatedAt, product.updatedAt());
    }

    @Test
    void shouldHandleNullValues() {
        // Arrange & Act
        ProductResponse product = new ProductResponse(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        // Assert
        assertNotNull(product);
        assertNull(product.id());
        assertNull(product.name());
        assertNull(product.description());
        assertNull(product.price());
        assertNull(product.imageUrl());
        assertNull(product.preparationTime());
        assertNull(product.categoryId());
        assertNull(product.createdAt());
        assertNull(product.updatedAt());
    }
}
