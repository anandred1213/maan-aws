package com.order_service.order_microservice.KafkaListners;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEvetnConsumer {

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
    public void listen (String message){
        System.out.println("received message  in order-service "+message);
    }
}
