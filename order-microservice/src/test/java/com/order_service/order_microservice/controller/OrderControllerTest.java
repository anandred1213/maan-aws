package com.order_service.order_microservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order_service.order_microservice.dto.OrderDto;
import com.order_service.order_microservice.producer.KafkaProducer;
import com.order_service.order_microservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private KafkaProducer kafkaProducer;

    @Test
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(1L)).thenReturn("order received, process started asynchronously 1");

        mockMvc.perform(get("/orders/create/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("order received, process started asynchronously 1"));

        verify(orderService).createOrder(1L);
    }

    @Test
    void getUserOrders_Success() throws Exception {
        OrderDto order1 = new OrderDto();
        order1.setOrderId(1L);
        order1.setUserId(1);
        OrderDto order2 = new OrderDto();
        order2.setOrderId(2L);
        order2.setUserId(1);
        List<OrderDto> orders = Arrays.asList(order1, order2);

        when(orderService.getUserOrders(1)).thenReturn(orders);

        mockMvc.perform(get("/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[1].orderId").value(2));

        verify(orderService).getUserOrders(1);
    }

    @Test
    void deleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder();

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string("order cannot be deleted as we give respect to privacy"));

        verify(orderService).deleteOrder();
    }
}