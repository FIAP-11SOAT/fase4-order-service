package com.fiap.soat11.order.service;

import com.fiap.soat11.order.dto.CreateOrderRequest;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderStatusEnum;
import com.fiap.soat11.order.entity.OrderStatusEventEnum;
import com.fiap.soat11.order.helpers.client.catalog.CatalogClient;
import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;
import com.fiap.soat11.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;
    private UUID productId1;
    private UUID productId2;
    private String userId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
        userId = "user-123";
    }

    @Test
    @DisplayName("Deve retornar todos os pedidos")
    void testFindAll_Success() {
        // Arrange
        Order order1 = Order.create(userId);
        Order order2 = Order.create("user-456");
        List<Order> expectedOrders = Arrays.asList(order1, order2);

        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pedidos")
    void testFindAll_EmptyList() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Order> result = orderService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um pedido por ID")
    void testFindById_Success() {
        // Arrange
        Order expectedOrder = Order.create(userId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        // Act
        Order result = orderService.findById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedOrder, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não for encontrado")
    void testFindById_NotFound() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> orderService.findById(orderId)
        );

        assertEquals("Order not found", exception.getReason());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve criar um pedido com sucesso")
    void testCreateOrder_Success() {
        // Arrange
        CreateOrderRequest request1 = new CreateOrderRequest(productId1, 2);
        CreateOrderRequest request2 = new CreateOrderRequest(productId2, 1);
        List<CreateOrderRequest> requests = Arrays.asList(request1, request2);

        ProductResponse product1 = new ProductResponse(
            productId1,
            "Hambúrguer",
            "Delicioso hambúrguer",
            new BigDecimal("25.00"),
            "image1.jpg",
            15,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        ProductResponse product2 = new ProductResponse(
            productId2,
            "Refrigerante",
            "Bebida gelada",
            new BigDecimal("5.00"),
            "image2.jpg",
            5,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        List<ProductResponse> products = Arrays.asList(product1, product2);

        when(catalogClient.getProductsByIds(anyList())).thenReturn(products);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(requests, userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getCustomerId());
        assertEquals(OrderStatusEnum.RECEBIDO, result.getStatus());
        assertEquals(OrderStatusEventEnum.ORDER_CREATED, result.getStatusEvent());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("55.00"), result.getTotalAmount()); // (25.00 * 2) + (5.00 * 1)

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        verify(catalogClient, times(1)).getProductsByIds(anyList());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(2, savedOrder.getItems().size());
    }

    @Test
    @DisplayName("Deve criar um pedido com um único item")
    void testCreateOrder_SingleItem() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(productId1, 3);
        List<CreateOrderRequest> requests = Arrays.asList(request);

        ProductResponse product = new ProductResponse(
            productId1,
            "Pizza",
            "Pizza grande",
            new BigDecimal("40.00"),
            "pizza.jpg",
            30,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(catalogClient.getProductsByIds(anyList())).thenReturn(Arrays.asList(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(requests, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("120.00"), result.getTotalAmount()); // 40.00 * 3
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve criar um pedido mesmo quando produto não tem quantidade especificada")
    void testCreateOrder_WithZeroQuantity() {
        // Arrange
        CreateOrderRequest request1 = new CreateOrderRequest(productId1, 1);
        UUID productId3 = UUID.randomUUID();
        CreateOrderRequest request2 = new CreateOrderRequest(productId3, 2);
        List<CreateOrderRequest> requests = Arrays.asList(request1, request2);

        ProductResponse product1 = new ProductResponse(
            productId1,
            "Produto 1",
            "Descrição",
            new BigDecimal("10.00"),
            "image.jpg",
            10,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        ProductResponse product2 = new ProductResponse(
            productId2,
            "Produto 2",
            "Descrição",
            new BigDecimal("15.00"),
            "image.jpg",
            10,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(catalogClient.getProductsByIds(anyList())).thenReturn(Arrays.asList(product1, product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(requests, userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve verificar que o pedido tem status inicial correto")
    void testCreateOrder_InitialStatus() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(productId1, 1);
        List<CreateOrderRequest> requests = Arrays.asList(request);

        ProductResponse product = new ProductResponse(
            productId1,
            "Produto",
            "Descrição",
            new BigDecimal("20.00"),
            "image.jpg",
            10,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(catalogClient.getProductsByIds(anyList())).thenReturn(Arrays.asList(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(requests, userId);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatusEnum.RECEBIDO, result.getStatus());
        assertEquals(OrderStatusEventEnum.ORDER_CREATED, result.getStatusEvent());
        assertFalse(result.getStatusEvents().isEmpty());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CatalogClient falha")
    void testCreateOrder_CatalogClientFailure() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(productId1, 1);
        List<CreateOrderRequest> requests = Arrays.asList(request);

        when(catalogClient.getProductsByIds(anyList())).thenThrow(new RuntimeException("Catalog service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(requests, userId);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve criar pedido com lista vazia de produtos retornada pelo catálogo")
    void testCreateOrder_EmptyProductList() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(productId1, 1);
        List<CreateOrderRequest> requests = Arrays.asList(request);

        when(catalogClient.getProductsByIds(anyList())).thenReturn(Arrays.asList());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(requests, userId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lidar com valores decimais no cálculo do total")
    void testCreateOrder_DecimalCalculation() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(productId1, 3);
        List<CreateOrderRequest> requests = Arrays.asList(request);

        ProductResponse product = new ProductResponse(
            productId1,
            "Produto",
            "Descrição",
            new BigDecimal("33.33"),
            "image.jpg",
            10,
            UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(catalogClient.getProductsByIds(anyList())).thenReturn(Arrays.asList(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(requests, userId);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("99.99"), result.getTotalAmount());
    }
}
