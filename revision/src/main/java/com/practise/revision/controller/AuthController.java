package com.practise.revision.controller;

import com.practise.revision.dto.*;
import com.practise.revision.entity.RefreshToken;
import com.practise.revision.security.JwtUtil;
import com.practise.revision.service.RefreshTokenService;
import com.practise.revision.service.UserAuthenticationService;
import com.practise.revision.service.UserManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Handles user authentication, JWT token generation, and token refresh.
 * These endpoints are public (configured in SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final JwtUtil JwtUtil;
    private final UserAuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private  final UserManagementService userManagementService;

    public AuthController(JwtUtil JwtUtil,
                          UserAuthenticationService authenticationService,
                          RefreshTokenService refreshTokenService, UserManagementService userManagementService) {
        this.JwtUtil = JwtUtil;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.userManagementService = userManagementService;
    }

    /**
     * Login Endpoint
     * 
     * Flow:
     * 1. Receive email and password from request body
     * 2. Validate credentials against database
     * 3. If valid, generate JWT access token (15 min) and refresh token (7 days)
     * 4. Return both tokens in response
     * 5. If invalid, return 401 Unauthorized
     * 
     * @param request Contains email and password
     * @return Access token and refresh token if credentials valid, 401 otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
      /*  log.info("Login attempt for user: {}", request.getEmail());
        
        if (authenticationService.validateUser(request.getEmail(), request.getPassword())) {
            String accessToken = JwtUtil.generateToken(request);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());
            
            log.info("Login successful for user: {}", request.getEmail());
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
        }
        
        log.warn("Login failed for user: {}", request.getEmail());
        return ResponseEntity.status(401).build();*/

      return   ResponseEntity.ok(userManagementService.login(request));
    }

    /**
     * Refresh Token Endpoint
     * 
     * Flow:
     * 1. Receive refresh token from request
     * 2. Validate refresh token exists and not expired
     * 3. Generate new access token
     * 4. Return new access token with same refresh token
     * 5. If invalid/expired, return 403 Forbidden
     * 
     * @param request Contains refresh token
     * @return New access token and refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

    /**
     * Logout Endpoint
     * 
     * Deletes refresh token from database
     * 
     * @param request Contains refresh token to invalidate
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        log.info("Logout request received");
        
        refreshTokenService.findByToken(refreshToken).ifPresent(token -> {
            refreshTokenService.deleteByUserEmail(token.getUserEmail());
            log.info("User logged out successfully: {}", token.getUserEmail());
        });
        
        return ResponseEntity.ok("Logged out successfully");
    }
}
