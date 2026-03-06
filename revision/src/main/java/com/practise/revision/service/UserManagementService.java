package com.practise.revision.service;

import com.practise.revision.dto.AuthRequest;
import com.practise.revision.dto.AuthResponse;
import com.practise.revision.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserManagementService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(AuthRequest authRequest);
    UserDto getUser(Integer id);
    UserDto updateUser(UserDto userDto);
    void deleteUser(Long id);
    List<UserDto> getAllUsers();
    AuthResponse login(AuthRequest authRequest);
}
