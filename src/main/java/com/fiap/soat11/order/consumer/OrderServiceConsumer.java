package com.fiap.soat11.order.consumer;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.soat11.order.dto.ConsumerData;
import com.fiap.soat11.order.dto.ConsumerOrderPayload;

import io.awspring.cloud.sqs.annotation.SqsListener;

@Service
public class OrderServiceConsumer {

    private static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @SqsListener("fase4-order-service-queue")
    public void listen(ConsumerData data) {
        ConsumerOrderPayload payload = mapper.convertValue(data.payload(), ConsumerOrderPayload.class);
        System.out.println("Received order with ID: " + payload.id());
    }

}
