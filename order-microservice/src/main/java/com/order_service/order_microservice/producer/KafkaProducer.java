package com.order_service.order_microservice.producer;

import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class KafkaProducer {

    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    public  void sendOrderEvent(String message){

        kafkaTemplate.send("order-topic",message);
        System.out.println("order event sent "+message);
    }


}
