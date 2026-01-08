package com.fiap.soat11.order.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.soat11.order.dto.ConsumerData;
import com.fiap.soat11.order.entity.Order;
import com.fiap.soat11.order.entity.OrderStatusEnum;
import com.fiap.soat11.order.entity.OrderStatusEventEnum;
import com.fiap.soat11.order.exception.InvalidStatusTransitionException;
import com.fiap.soat11.order.exception.OrderConsumerException;
import com.fiap.soat11.order.repository.OrderRepository;
import com.fiap.soat11.order.service.ProductionQueueService;

@Service
public class OrderConsumerService {

    private OrderRepository orderRepository;
    private ProductionQueueService productionQueueService;

    public OrderConsumerService(
            OrderRepository orderRepository,
            ProductionQueueService productionQueueService) {
        this.orderRepository = orderRepository;
        this.productionQueueService = productionQueueService;
    }

    @Transactional
    public void handler(ConsumerData data) {

        System.out.println("Received event: " + data.meta().eventName());
        System.out.println("Payload: " + data.payload().toString());

        OrderEventType eventType;
        try {
            eventType = OrderEventType.fromEventName(data.meta().eventName());
        } catch (IllegalArgumentException e) {
            throw new OrderConsumerException("Unknown event type: " + data.meta().eventName());
        }

        if (eventType == null) {
            throw new OrderConsumerException("Unknown event type: " + data.meta().eventName());
        }
        
        final UUID orderID;

        if (data.payload().production() != null) {
            orderID = data.payload().production().orderID();
        } else if (data.payload().payment() != null) {
            orderID = data.payload().payment().orderID();
        } else {
            throw new OrderConsumerException("Order ID is missing in the payload.");
        }

        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new OrderConsumerException("Order not found: " + orderID));

        switch (eventType) {
            case PAYMENT_CREATED:
                this.handlerPaymentCreated(order, data);
                break;
            case PAYMENT_COMPLETED:
                this.handlerPaymentCompleted(order, data);
                break;
            case PAYMENT_FAILED:
                this.handlerPaymentFailed(order, data);
                break;
            case PRODUCTION_STARTED:
                this.handlerProductionStarted(order);
                break;
            case PRODUCTION_COMPLETED:
                this.handlerProductionCompleted(order);
                break;
            case PRODUCTION_DELIVERED:
                this.handlerProductionDelivered(order);
                break;
        }

        orderRepository.save(order);
    }

    void handlerPaymentCreated(Order order, ConsumerData data) {
        validateStatusTransition(
                order.getStatusEvent(),
                OrderStatusEventEnum.AWAITING_PAYMENT,
                Arrays.asList(OrderStatusEventEnum.ORDER_CREATED)
        );

        // Atualiza o payment ID do pedido
        if (data.payload().payment() != null && data.payload().payment().paymentID() != null) {
            order.setPaymentId(data.payload().payment().paymentID().toString());
        }

        order.updateStatusEvent(
                OrderStatusEventEnum.AWAITING_PAYMENT,
                "Pagamento criado e aguardando confirmação.");
    }

    void handlerPaymentCompleted(Order order, ConsumerData data) {
        validateStatusTransition(
                order.getStatusEvent(),
                OrderStatusEventEnum.PAYMENT_APPROVED,
                Arrays.asList(OrderStatusEventEnum.AWAITING_PAYMENT)
        );

        // Atualiza o payment ID do pedido caso ainda não tenha sido atualizado
        if (data.payload().payment() != null && data.payload().payment().paymentID() != null) {
            order.setPaymentId(data.payload().payment().paymentID().toString());
        }

        order.updateStatusEvent(
                OrderStatusEventEnum.PAYMENT_APPROVED,
                "Pagamento aprovado com sucesso.");
        order.updateStatusEvent(
                OrderStatusEventEnum.AWAITING_PREPARATION,
                "Pedido aguardando início da preparação.");
        
        // Envia mensagem para a fila do production-service
        productionQueueService.sendOrderPaidEvent(order);
    }

    void handlerPaymentFailed(Order order, ConsumerData data) {
        validateStatusTransition(
                order.getStatusEvent(),
                OrderStatusEventEnum.PAYMENT_FAILED,
                Arrays.asList(OrderStatusEventEnum.AWAITING_PAYMENT)
        );

        if (data.payload().payment() != null && data.payload().payment().paymentID() != null) {
            order.setPaymentId(data.payload().payment().paymentID().toString());
        }

        order.updateStatusEvent(
                OrderStatusEventEnum.PAYMENT_FAILED,
                "Falha no pagamento. Pedido não pode ser processado.");
        order.updateStatusEvent(
                OrderStatusEventEnum.CANCELLED,
                "Pedido cancelado devido à falha no pagamento.");
    }

    void handlerProductionStarted(Order order) {
        validateStatusTransition(
                order.getStatusEvent(),
                OrderStatusEventEnum.PREPARATION_IN_PROGRESS,
                Arrays.asList(OrderStatusEventEnum.AWAITING_PREPARATION)
        );

        order.setStatus(OrderStatusEnum.EM_PRPARACAO);
        order.updateStatusEvent(
                OrderStatusEventEnum.PREPARATION_IN_PROGRESS,
                "Preparação do pedido iniciada.");
    }

    void handlerProductionCompleted(Order order) {
        validateStatusTransition(
                order.getStatusEvent(),
                OrderStatusEventEnum.FOR_DELIVERY,
                Arrays.asList(OrderStatusEventEnum.PREPARATION_IN_PROGRESS)
        );

        order.setStatus(OrderStatusEnum.PRONTO);
        order.updateStatusEvent(
                OrderStatusEventEnum.FOR_DELIVERY,
                "Pedido pronto para entrega.");

    }

    void handlerProductionDelivered(Order order) {
        order.setStatus(OrderStatusEnum.ENTREGUE);
        order.updateStatusEvent(
            OrderStatusEventEnum.DELIVERED,
            "Pedido entregue ao cliente.");
    }

    private void validateStatusTransition(
            OrderStatusEventEnum currentStatus,
            OrderStatusEventEnum targetStatus,
            List<OrderStatusEventEnum> allowedPreviousStatuses) {

        if (!allowedPreviousStatuses.contains(currentStatus)) {
            throw new InvalidStatusTransitionException(
                    String.format(
                            "Transição de status inválida: não é possível mudar de '%s' para '%s'. " +
                            "Status anteriores permitidos: %s",
                            currentStatus,
                            targetStatus,
                            allowedPreviousStatuses
                    )
            );
        }
    }
}
