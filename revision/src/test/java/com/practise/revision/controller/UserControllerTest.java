package com.practise.revision.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practise.revision.dto.OrderDto;
import com.practise.revision.dto.UserDto;
import com.practise.revision.dto.UserEvent;
import com.practise.revision.producer.UserEventProducer;
import com.practise.revision.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserEventProducer userEventProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        UserDto createdUser = new UserDto();
        createdUser.setId(1);
        createdUser.setName("John Doe");
        createdUser.setEmail("john@example.com");

        when(userService.createUser(any(UserDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void getUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("John Doe");

        when(userService.getUser(1)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService).getUser(1);
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");

        UserDto updatedUser = new UserDto();
        updatedUser.setId(1);
        updatedUser.setName("Updated Name");

        when(userService.updateUser(anyInt(), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/updateUser?id=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(userService).updateUser(1, userDto);
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/users/deleteUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.userId").value("1"));

        verify(userService).deleteUser(1);
    }

    @Test
    void publishUserEvent_Success() throws Exception {
        UserEvent event = new UserEvent();
        event.setName("John Doe");

        doNothing().when(userEventProducer).sendUserCreatedEvent(any(UserEvent.class));

        mockMvc.perform(post("/users/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Event published successfully"))
                .andExpect(jsonPath("$.user").value("John Doe"));

        verify(userEventProducer).sendUserCreatedEvent(any(UserEvent.class));
    }

    @Test
    void getUserOrders_Success() throws Exception {
        OrderDto order1 = new OrderDto();
        order1.setOrderId(1L);
        OrderDto order2 = new OrderDto();
        order2.setOrderId(2L);
        List<OrderDto> orders = Arrays.asList(order1, order2);

        when(userService.getUserOrders(1)).thenReturn(orders);

        mockMvc.perform(get("/users/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[1].orderId").value(2));

        verify(userService).getUserOrders(1);
    }
}