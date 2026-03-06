package com.practise.revision.service.serviceImpl;

import com.practise.revision.dto.TokenRefreshRequest;
import com.practise.revision.dto.TokenRefreshResponse;
import com.practise.revision.entity.RefreshToken;
import com.practise.revision.entity.User;
import com.practise.revision.repository.UserRepository;
import com.practise.revision.security.CustomUserDetailsService;
import com.practise.revision.security.JwtUtil;
import com.practise.revision.service.RefreshTokenService;
import com.practise.revision.service.UserAuthenticationService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    public UserAuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        RefreshToken refreshToken= refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("invalid or expired refresh token"));
        String userEmail= refreshToken.getUserEmail();
       UserDetails userDetails =  customUserDetailsService.loadUserByUsername(userEmail);
 // generate new acceess token
        String newAccessToken = jwtUtil.generateToken(userDetails);

        // rotate refresh token
        refreshTokenService.deleteByUserEmail(userEmail);
        RefreshToken refreshToken1 = refreshTokenService.createRefreshToken(userEmail);
        return  new TokenRefreshResponse(newAccessToken, refreshToken1.getToken());
    }
}
