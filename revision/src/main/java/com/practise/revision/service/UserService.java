package com.practise.revision.service;

import com.practise.revision.dto.OrderDto;
import com.practise.revision.dto.UserDto;
import com.practise.revision.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    public UserDto createUser(UserDto userDto);
    public UserDto getUser(Integer id);
    public UserDto updateUser(Integer id, UserDto userDto);
    public void deleteUser(Long id);
    public List<OrderDto> getUserOrders(Integer userId);
    public boolean validateUser(String email, String password);
}
