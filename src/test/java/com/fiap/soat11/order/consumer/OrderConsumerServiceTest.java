package com.fiap.soat11.order.consumer;

import com.fiap.soat11.order.dto.ConsumerData;
import com.fiap.soat11.order.dto.ConsumerMeta;
import com.fiap.soat11.order.dto.ConsumerPayload;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderStatusEnum;
import com.fiap.soat11.order.entity.OrderStatusEventEnum;
import com.fiap.soat11.order.exception.InvalidStatusTransitionException;
import com.fiap.soat11.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderConsumerService Unit Tests")
class OrderConsumerServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderConsumerService orderConsumerService;

    private UUID orderId;
    private Order order;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        order = Order.create("user-123");
    }

    private ConsumerData createConsumerData(String eventName) {
        ConsumerMeta meta = new ConsumerMeta(
            UUID.randomUUID().toString(),
            "2024-12-24T10:00:00Z",
            "payment-service",
            "order-service",
            eventName
        );
        ConsumerPayload payload = new ConsumerPayload(orderId);
        return new ConsumerData(meta, payload);
    }

    @Test
    @DisplayName("Deve processar evento PAYMENT_CREATED com sucesso")
    void testHandlerPaymentCreated_Success() {
        // Arrange
        ConsumerData data = createConsumerData("payment-created-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderConsumerService.handler(data);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatusEventEnum.AWAITING_PAYMENT, savedOrder.getStatusEvent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao processar PAYMENT_CREATED com status inválido")
    void testHandlerPaymentCreated_InvalidStatus() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.PAYMENT_APPROVED, "Status inválido");
        ConsumerData data = createConsumerData("payment-created-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            orderConsumerService.handler(data);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve processar evento PAYMENT_COMPLETED com sucesso")
    void testHandlerPaymentCompleted_Success() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PAYMENT, "Aguardando pagamento");
        ConsumerData data = createConsumerData("payment-completed-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderConsumerService.handler(data);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatusEventEnum.AWAITING_PREPARATION, savedOrder.getStatusEvent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao processar PAYMENT_COMPLETED com status inválido")
    void testHandlerPaymentCompleted_InvalidStatus() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.PREPARATION_IN_PROGRESS, "Status inválido");
        ConsumerData data = createConsumerData("payment-completed-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            orderConsumerService.handler(data);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve processar evento PAYMENT_FAILED com sucesso")
    void testHandlerPaymentFailed_Success() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PAYMENT, "Aguardando pagamento");
        ConsumerData data = createConsumerData("payment-failed-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderConsumerService.handler(data);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatusEventEnum.CANCELLED, savedOrder.getStatusEvent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve processar evento PRODUCTION_STARTED com sucesso")
    void testHandlerProductionStarted_Success() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PAYMENT, "Aguardando pagamento");
        order.updateStatusEvent(OrderStatusEventEnum.PAYMENT_APPROVED, "Pagamento aprovado");
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PREPARATION, "Aguardando preparação");
        ConsumerData data = createConsumerData("production-started-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderConsumerService.handler(data);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatusEventEnum.PREPARATION_IN_PROGRESS, savedOrder.getStatusEvent());
        assertEquals(OrderStatusEnum.EM_PRPARACAO, savedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao processar PRODUCTION_STARTED com status inválido")
    void testHandlerProductionStarted_InvalidStatus() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.PAYMENT_FAILED, "Status inválido");
        ConsumerData data = createConsumerData("production-started-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            orderConsumerService.handler(data);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve processar evento PRODUCTION_COMPLETED com sucesso")
    void testHandlerProductionCompleted_Success() {
        // Arrange
        order.setStatus(OrderStatusEnum.EM_PRPARACAO);
        order.updateStatusEvent(OrderStatusEventEnum.PREPARATION_IN_PROGRESS, "Em preparação");
        ConsumerData data = createConsumerData("production-completed-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderConsumerService.handler(data);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatusEventEnum.FOR_DELIVERY, savedOrder.getStatusEvent());
        assertEquals(OrderStatusEnum.PRONTO, savedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao processar PRODUCTION_COMPLETED com status inválido")
    void testHandlerProductionCompleted_InvalidStatus() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PAYMENT, "Status inválido");
        ConsumerData data = createConsumerData("production-completed-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            orderConsumerService.handler(data);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve processar evento PRODUCTION_DELIVERED com sucesso")
    void testHandlerProductionDelivered_Success() {
        // Arrange
        order.setStatus(OrderStatusEnum.PRONTO);
        order.updateStatusEvent(OrderStatusEventEnum.FOR_DELIVERY, "Pronto para entrega");
        ConsumerData data = createConsumerData("production-delivered-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderConsumerService.handler(data);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatusEventEnum.DELIVERED, savedOrder.getStatusEvent());
        assertEquals(OrderStatusEnum.ENTREGUE, savedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não for encontrado")
    void testHandler_OrderNotFound() {
        // Arrange
        ConsumerData data = createConsumerData("payment-created-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderConsumerService.handler(data);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve validar transição de status corretamente")
    void testValidateStatusTransition_Success() {
        // Arrange
        order.updateStatusEvent(OrderStatusEventEnum.AWAITING_PAYMENT, "Aguardando");
        ConsumerData data = createConsumerData("payment-completed-event");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        assertDoesNotThrow(() -> orderConsumerService.handler(data));

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve processar múltiplos eventos em sequência correta")
    void testHandler_SequentialEvents() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act - Simular fluxo completo
        ConsumerData paymentCreated = createConsumerData("payment-created-event");
        orderConsumerService.handler(paymentCreated);
        assertEquals(OrderStatusEventEnum.AWAITING_PAYMENT, order.getStatusEvent());

        ConsumerData paymentCompleted = createConsumerData("payment-completed-event");
        orderConsumerService.handler(paymentCompleted);
        assertEquals(OrderStatusEventEnum.AWAITING_PREPARATION, order.getStatusEvent());

        ConsumerData productionStarted = createConsumerData("production-started-event");
        orderConsumerService.handler(productionStarted);
        assertEquals(OrderStatusEventEnum.PREPARATION_IN_PROGRESS, order.getStatusEvent());
        assertEquals(OrderStatusEnum.EM_PRPARACAO, order.getStatus());

        ConsumerData productionCompleted = createConsumerData("production-completed-event");
        orderConsumerService.handler(productionCompleted);
        assertEquals(OrderStatusEventEnum.FOR_DELIVERY, order.getStatusEvent());
        assertEquals(OrderStatusEnum.PRONTO, order.getStatus());

        ConsumerData productionDelivered = createConsumerData("production-delivered-event");
        orderConsumerService.handler(productionDelivered);
        assertEquals(OrderStatusEventEnum.DELIVERED, order.getStatusEvent());
        assertEquals(OrderStatusEnum.ENTREGUE, order.getStatus());

        // Assert
        verify(orderRepository, times(5)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve processar fluxo de falha no pagamento")
    void testHandler_PaymentFailureFlow() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        ConsumerData paymentCreated = createConsumerData("payment-created-event");
        orderConsumerService.handler(paymentCreated);
        assertEquals(OrderStatusEventEnum.AWAITING_PAYMENT, order.getStatusEvent());

        ConsumerData paymentFailed = createConsumerData("payment-failed-event");
        orderConsumerService.handler(paymentFailed);

        // Assert
        assertEquals(OrderStatusEventEnum.CANCELLED, order.getStatusEvent());
        verify(orderRepository, times(2)).save(any(Order.class));
    }
}
