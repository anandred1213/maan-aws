package com.order_service.order_microservice.consumer;


import com.order_service.order_microservice.dto.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(UserEvent event) {
        System.out.println("Order Service received user event: " + event);
        // You can now use this event to create an order, etc.
    }
}
