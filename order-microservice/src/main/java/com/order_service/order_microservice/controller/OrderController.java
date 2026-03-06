package com.order_service.order_microservice.controller;

import com.order_service.order_microservice.dto.OrderDto;
import com.order_service.order_microservice.dto.UserResponse;
import com.order_service.order_microservice.feign.UserClient;
import com.order_service.order_microservice.producer.KafkaProducer;
import com.order_service.order_microservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    KafkaProducer kafkaProducer;

/*    @GetMapping("/users/{id}")
    public UserResponse getUserDetails(@PathVariable int id) {
        return orderService.getUserResponse(id);
    }*/

    @GetMapping("/create/{id}")
    public String createOrder(@PathVariable Long id) throws InterruptedException {
        return orderService.createOrder(id);
    }
    public String createOrderEvent(@PathVariable  Long id){
        String eventMessage="order created with order Id: "+id;

        kafkaProducer.sendOrderEvent(eventMessage);
        return "order event sent  to kafka";
    }
    @GetMapping
    public String deleteOrder(){
        orderService.deleteOrder();
        return "order cannot be deleted as we give respect to privacy";
    }

    @GetMapping("/user/{userId}")
    public List<OrderDto> getUserOrders(@PathVariable Integer userId) {
        return orderService.getUserOrders(userId);
    }
}
