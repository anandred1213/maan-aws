package com.practise.revision.service;

import com.practise.revision.dto.UserDto;
import com.practise.revision.entity.User;
import com.practise.revision.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUser_Success() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John Doe", result.getName());
        verify(userRepository).findById(1);
    }

    @Test
    void getUser_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUser(1));
        verify(userRepository).findById(1);
    }

    @Test
    void updateUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("Old Name");

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("Updated Name");

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1, userDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(userRepository).findById(1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userRepository).findById(1);
        verify(userRepository).delete(user);
    }
}