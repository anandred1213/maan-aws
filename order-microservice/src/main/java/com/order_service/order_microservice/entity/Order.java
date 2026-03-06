package com.order_service.order_microservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    @Column(name = "order_details")
    private String orderDetails;
    
    @Column(name = "user_id")
    private Integer userId;

    public Order() {}

    public Order(String orderDetails, Integer userId) {
        this.orderDetails = orderDetails;
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}