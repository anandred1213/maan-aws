package com.order_service.order_microservice.service;

import com.order_service.order_microservice.config.AsyncService;
import com.order_service.order_microservice.dto.OrderDto;
import com.order_service.order_microservice.entity.Order;
import com.order_service.order_microservice.repository.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private OrderRepository orderRepository;

    public  String createOrder(Long orderId) throws InterruptedException {
        asyncService.processOrder(orderId);
        return "order received, process started asynchronously "+orderId;

    }
    // method to print all order details based on order id
    public String getOrderDetails(Long orderId){
        return "order details for the orderId "+orderId;
    }

    // method to get all orders for a specific user
    public List<OrderDto> getUserOrders(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> {
                    OrderDto dto = new OrderDto();
                    BeanUtils.copyProperties(order, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteOrder() {
        System.out.println("deletion of orders is not allowed");
    }
    
    // method to edit the order details - allows modification of non-sensitive fields only
    public OrderDto editOrder(Long orderId, String newOrderDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Only allow modification of non-sensitive fields
        if (newOrderDetails != null && !newOrderDetails.trim().isEmpty()) {
            order.setOrderDetails(newOrderDetails);
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        OrderDto dto = new OrderDto();
        BeanUtils.copyProperties(updatedOrder, dto);
        return dto;
    }
      /*  @Autowired
        RestTemplate restTemplate;
*/
// communicating with the user service with the help of rest template
/*
    public UserResponse getUserResponse(int id) {
        String url = "http://revision/users/" + id;

        System.out.println("Calling URL: " + url);

        UserResponse response = restTemplate.getForObject(url, UserResponse.class);

        System.out.println("User response received: " + response);

        return response;
    }


    */


/*

// webclient synchronous communcation
    @Autowired
    WebClient webClient;

    public UserResponse getUserFromUserService(int id){
        String url="htpp://localhost:8080/users/"+id;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block(); // makes the communication synchronous
    }


  */
/*    @Autowired
    UserClient userClient;

    public UserResponse getUserResponse(int id){
        return userClient.getUserResponse(id);
    }*/

    // asynchronous communication  via @Async annotation






}
