package com.fiap.soat11.order.helpers.client.catalog;

import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    private CatalogClientImpl catalogClient;

    private final String baseUrl = "http://api-gateway.test";

    @BeforeEach
    void setUp() {
        catalogClient = new CatalogClientImpl(restTemplate, baseUrl);
    }

    @Test
    void shouldGetAllProducts() {
        // Arrange
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductResponse product1 = new ProductResponse(
            productId1,
            "Product 1",
            "Description 1",
            new BigDecimal("10.00"),
            "http://image1.jpg",
            10,
            categoryId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        ProductResponse product2 = new ProductResponse(
            productId2,
            "Product 2",
            "Description 2",
            new BigDecimal("20.00"),
            "http://image2.jpg",
            15,
            categoryId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        List<ProductResponse> expectedProducts = Arrays.asList(product1, product2);
        ResponseEntity<List<ProductResponse>> responseEntity = ResponseEntity.ok(expectedProducts);

        when(restTemplate.exchange(
            eq(baseUrl + "/products"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<ProductResponse> result = catalogClient.getProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).name());
        assertEquals("Product 2", result.get(1).name());
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void shouldGetProductsByIds() {
        // Arrange
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductResponse product1 = new ProductResponse(
            productId1,
            "Product 1",
            "Description 1",
            new BigDecimal("10.00"),
            "http://image1.jpg",
            10,
            categoryId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        ProductResponse product2 = new ProductResponse(
            productId2,
            "Product 2",
            "Description 2",
            new BigDecimal("20.00"),
            "http://image2.jpg",
            15,
            categoryId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        ProductResponse product3 = new ProductResponse(
            productId3,
            "Product 3",
            "Description 3",
            new BigDecimal("30.00"),
            "http://image3.jpg",
            20,
            categoryId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        List<ProductResponse> allProducts = Arrays.asList(product1, product2, product3);
        ResponseEntity<List<ProductResponse>> responseEntity = ResponseEntity.ok(allProducts);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<UUID> productIds = Arrays.asList(productId1, productId3);

        // Act
        List<ProductResponse> result = catalogClient.getProductsByIds(productIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.id().equals(productId1)));
        assertTrue(result.stream().anyMatch(p -> p.id().equals(productId3)));
        assertFalse(result.stream().anyMatch(p -> p.id().equals(productId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsMatchIds() {
        // Arrange
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductResponse product1 = new ProductResponse(
            productId1,
            "Product 1",
            "Description 1",
            new BigDecimal("10.00"),
            "http://image1.jpg",
            10,
            categoryId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        List<ProductResponse> allProducts = Arrays.asList(product1);
        ResponseEntity<List<ProductResponse>> responseEntity = ResponseEntity.ok(allProducts);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<UUID> productIds = Arrays.asList(productId2);

        // Act
        List<ProductResponse> result = catalogClient.getProductsByIds(productIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
