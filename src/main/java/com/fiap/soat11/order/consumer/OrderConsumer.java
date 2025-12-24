package com.fiap.soat11.order.consumer;

import org.springframework.stereotype.Service;

import com.fiap.soat11.order.dto.ConsumerData;

import io.awspring.cloud.sqs.annotation.SqsListener;

@Service
public class OrderConsumer {

    private OrderConsumerService orderConsumerService;

    public OrderConsumer(OrderConsumerService orderConsumerService) {
        this.orderConsumerService = orderConsumerService;
    }

    @SqsListener("fase4-order-service-queue")
    public void listen(ConsumerData data) {
        orderConsumerService.handler(data);
    }

}
