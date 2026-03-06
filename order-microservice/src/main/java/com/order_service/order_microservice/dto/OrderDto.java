package com.order_service.order_microservice.dto;

public class OrderDto {
    private Long orderId;
    private String orderDetails;
    private Integer userId;

    public OrderDto() {}

    public OrderDto(Long orderId, String orderDetails, Integer userId) {
        this.orderId = orderId;
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

    @Override
    public String toString() {
        return "OrderDto{" +
                "orderId=" + orderId +
                ", orderDetails='" + orderDetails + '\'' +
                ", userId=" + userId +
                '}';
    }
}