package com.practise.revision.controller;

import com.practise.revision.dto.AuthRequest;
import com.practise.revision.dto.OrderDto;
import com.practise.revision.dto.UserDto;
import com.practise.revision.dto.UserEvent;
import com.practise.revision.entity.UserProfileImage;
import com.practise.revision.producer.UserEventProducer;
import com.practise.revision.security.JwtUtil;
import com.practise.revision.service.OrderService;
import com.practise.revision.service.UserManagementService;

import com.practise.revision.service.serviceImpl.UserProfileImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class UserController {
    
    private final UserManagementService userManagementService;
    private final OrderService orderService;
    private final UserEventProducer userEventProducer;

    private final UserProfileImageService userProfileImageService;

    public UserController(UserManagementService userManagementService,
                          OrderService orderService,
                          UserEventProducer userEventProducer, UserProfileImageService userProfileImageService) {
        this.userManagementService = userManagementService;
        this.orderService = orderService;
        this.userEventProducer = userEventProducer;

        this.userProfileImageService = userProfileImageService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userManagementService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    /*
    * endpoint to login the user
    *
    */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody AuthRequest authRequest) {
        UserDto loggedInUser = userManagementService.getUser(authRequest);
        return ResponseEntity.ok(loggedInUser);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable int id){
        UserDto user = userManagementService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/publish")
    public ResponseEntity<Map<String, String>> publishUserEvent(@Valid @RequestBody UserEvent event) {
        userEventProducer.sendUserCreatedEvent(event);
        return ResponseEntity.ok(Map.of("message", "Event published successfully", "user", event.getName()));
    }

    @PutMapping("/updateUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto){
        UserDto updatedUser = userManagementService.updateUser( userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id){
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Integer id) {
        List<OrderDto> orders = orderService.getUserOrders(id);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/users/v1/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Void> uploadProfileImage(@RequestParam("file") MultipartFile file){
        userProfileImageService.uploadProfileImage(file);
        return ResponseEntity.ok().build();
    }

}
