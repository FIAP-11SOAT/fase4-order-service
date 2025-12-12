package com.fiap.soat11.order.consumer;

import org.springframework.stereotype.Service;

import io.awspring.cloud.sqs.annotation.SqsListener;

@Service
public class OrderServiceConsumer {


    @SqsListener("fase4-order-service-queue")
    public void listen(String message) {
        System.out.println(message);
    }

}
