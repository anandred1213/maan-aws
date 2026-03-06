package com.practise.revision.service.serviceImpl;

import com.practise.revision.dto.AuthRequest;
import com.practise.revision.dto.AuthResponse;
import com.practise.revision.dto.UserDto;
import com.practise.revision.dto.UserEvent;
import com.practise.revision.entity.RefreshToken;
import com.practise.revision.entity.User;
import com.practise.revision.exceptionHandling.exceptions.UserAlreadyExistException;
import com.practise.revision.exceptionHandling.exceptions.UserNotFoundException;
import com.practise.revision.producer.UserEventProducer;
import com.practise.revision.repository.UserRepository;
import com.practise.revision.security.CustomUserDetails;
import com.practise.revision.security.JwtUtil;
import com.practise.revision.service.RefreshTokenService;
import com.practise.revision.service.UserManagementService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(UserManagementServiceImpl.class);
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    public UserManagementServiceImpl(UserRepository userRepository, 
                                     UserEventProducer userEventProducer,
                                     PasswordEncoder passwordEncoder,JwtUtil jwtUtil,
                                     AuthenticationManager authenticationManager,
                                     RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil=jwtUtil;
        this.userEventProducer = userEventProducer;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.refreshTokenService=refreshTokenService;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User byEmail = userRepository.findByEmail(userDto.getEmail());
        if(byEmail != null) {
            throw new UserAlreadyExistException("user already found with the email");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User saveToDb = userRepository.save(user);
        UserDto userDto1 = new UserDto();
        BeanUtils.copyProperties(saveToDb, userDto1);

        userEventProducer.sendUserCreatedEvent(new UserEvent(saveToDb.getId(), saveToDb.getName(), saveToDb.getEmail()));
        return userDto1;
    }

    @Override
    @CircuitBreaker(name="getUserCB", fallbackMethod = "readUserFallback")
    public UserDto getUser(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found with email: " + authRequest.getEmail());
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        UserDto userResponse = new UserDto();
        BeanUtils.copyProperties(user, userResponse);

        return userResponse;
    }

    @Override
    public UserDto getUser(Integer id) {
        return userRepository.findById(id)
                .map(this::convertToUserDto)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserDto readUserFallback(Integer id, Throwable throwable) {
        UserDto fallbackUser = new UserDto();
        fallbackUser.setId(id);
        fallbackUser.setName("user");
        fallbackUser.setEmail("user@gmail.com");
        return fallbackUser;
    }

    @Override
    @CircuitBreaker(name="updateUserCB", fallbackMethod = "updateUserFallback")
    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException("user not found with the id: " + userDto.getId()));
        
        user.setName(userDto.getName());

        User save = userRepository.save(user);
        UserDto userDto1 = new UserDto();
        BeanUtils.copyProperties(save, userDto1);
        return userDto1;
    }

    public void updateUserFallback(Integer id, UserDto userDto, Throwable throwable) {
        log.warn("Inside fallback method of update user for userId: {}", id);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
         .stream()
         .map(this::convertToUserDto)
         .toList();
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword())
        );
       CustomUserDetails userDetails=(CustomUserDetails) authenticate.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return new AuthResponse(accessToken,refreshToken.getToken());

    }



    private UserDto convertToUserDto(User user){
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }
}
