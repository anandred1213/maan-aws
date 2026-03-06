package com.practise.revision.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Utility Class
 * 
 * Handles JWT token generation, validation, and extraction of claims.
 * Uses HMAC-SHA algorithm for signing tokens.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    CustomUserDetails customUserDetails;

    /**
     * Generate signing key from secret
     * Converts secret string to SecretKey for HMAC-SHA signing
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT Token
     * 
     * Flow:
     * 1. Set subject (user email)
     * 2. Set issued date (current time)
     * 3. Set expiration date (current time + expiration period)
     * 4. Sign with secret key
     * 5. Return compact JWT string
     * 
     * @param userDetails User's email to embed in token
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles",
                customUserDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        log.info("Generating JWT token for user: {}", userDetails.getUsername());
        return Jwts.builder()
                .subject(userDetails.getUsername())  // Step 1: Set user identifier
                .issuedAt(new Date())  // Step 2: Set issue time
                .expiration(new Date(System.currentTimeMillis() + expiration))  // Step 3: Set expiration
                .signWith(getSigningKey())  // Step 4: Sign token
                .compact();  // Step 5: Build compact string
    }

    /**
     * Extract email from JWT token
     * @param token JWT token
     * @return User's email from token subject
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validate JWT Token
     * 
     * Flow:
     * 1. Parse and extract claims from token
     * 2. Check if token is expired
     * 3. Return true if valid and not expired
     * 4. Return false if parsing fails or token expired
     * 
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);  // Step 1: Parse token
            boolean isValid = !isTokenExpired(token);  // Step 2 & 3: Check expiration
            log.debug("Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.warn("Token validation failed", e);
            return false;  // Step 4: Invalid token
        }
    }

    /**
     * Extract all claims from JWT token
     * Parses token and verifies signature
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Verify signature
                .build()
                .parseSignedClaims(token)  // Parse token
                .getPayload();  // Extract claims
    }

    /**
     * Check if token is expired
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
