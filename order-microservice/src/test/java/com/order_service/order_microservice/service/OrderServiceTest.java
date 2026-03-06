package com.order_service.order_microservice.service;

import com.order_service.order_microservice.config.AsyncService;
import com.order_service.order_microservice.dto.OrderDto;
import com.order_service.order_microservice.entity.Order;
import com.order_service.order_microservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AsyncService asyncService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_Success() throws InterruptedException {
        doNothing().when(asyncService).processOrder(1L);

        String result = orderService.createOrder(1L);

        assertEquals("order received, process started asynchronously 1", result);
        verify(asyncService).processOrder(1L);
    }

    @Test
    void getUserOrders_Success() {
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setUserId(1);
        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setUserId(1);
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findByUserId(1)).thenReturn(orders);

        List<OrderDto> result = orderService.getUserOrders(1);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(2L, result.get(1).getOrderId());
        verify(orderRepository).findByUserId(1);
    }

    @Test
    void editOrder_Success() {
        Order existingOrder = new Order();
        existingOrder.setOrderId(1L);
        existingOrder.setOrderDetails("Old details");

        Order updatedOrder = new Order();
        updatedOrder.setOrderId(1L);
        updatedOrder.setOrderDetails("New details");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderDto result = orderService.editOrder(1L, "New details");

        assertNotNull(result);
        assertEquals("New details", result.getOrderDetails());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void editOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.editOrder(1L, "New details"));
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderDetails_Success() {
        String result = orderService.getOrderDetails(1L);
        assertEquals("order details for the orderId 1", result);
    }
}