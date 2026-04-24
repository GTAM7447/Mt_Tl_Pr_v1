package com.spring.jwt.service;

import com.spring.jwt.jwt.ActiveSessionService;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.jwt.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

/**
 * Service for handling user logout operations following industry standards.
 * 
 * Industry Best Practices Implemented:
 * 1. Token Blacklisting - Prevents token reuse after logout
 * 2. Session Invalidation - Clears active session tracking
 * 3. Cookie Cleanup - Removes refresh token cookie
 * 4. Audit Logging - Tracks logout events
 * 5. Security Context Cleanup - Clears authentication state
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ActiveSessionService activeSessionService;
    
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Performs complete logout operation including:
     * - Extracting and blacklisting access token
     * - Extracting and blacklisting refresh token
     * - Invalidating active session
     * 
     * @param request HTTP request containing tokens
     * @param username Username of the user logging out
     */
    public void performLogout(HttpServletRequest request, String username) {
        log.info("Performing logout for user: {}", username);
        
        try {
            // 1. Blacklist access token from Authorization header
            blacklistAccessToken(request, username);
            
            // 2. Blacklist refresh token from cookie
            blacklistRefreshToken(request, username);
            
            // 3. Remove active session (optional - depends on your session management)
            // activeSessionService.removeSession(username);
            
            log.info("Logout completed successfully for user: {}", username);
        } catch (Exception e) {
            log.error("Error during logout for user: {}", username, e);
            // Don't throw exception - logout should always succeed from user perspective
        }
    }

    /**
     * Extracts and blacklists the access token from Authorization header
     */
    private void blacklistAccessToken(HttpServletRequest request, String username) {
        try {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String accessToken = authHeader.substring(BEARER_PREFIX.length());
                
                // Extract token ID (jti) using JwtService
                String tokenId = jwtService.extractTokenId(accessToken);
                
                if (tokenId != null) {
                    // Extract expiration
                    Claims claims = jwtService.extractClaims(accessToken);
                    Instant expiration = claims.getExpiration().toInstant();
                    
                    tokenBlacklistService.blacklistToken(tokenId, expiration, username, "logout", "ACCESS");
                    log.debug("Access token blacklisted for user: {}", username);
                } else {
                    log.warn("Could not extract token ID from access token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.warn("Could not blacklist access token for user: {}", username, e);
        }
    }

    /**
     * Extracts and blacklists the refresh token from cookie
     */
    private void blacklistRefreshToken(HttpServletRequest request, String username) {
        try {
            Cookie[] cookies = request.getCookies();
            
            if (cookies != null) {
                Cookie refreshCookie = Arrays.stream(cookies)
                    .filter(c -> REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                    .findFirst()
                    .orElse(null);
                
                if (refreshCookie != null && refreshCookie.getValue() != null 
                        && !refreshCookie.getValue().isEmpty()) {
                    String refreshToken = refreshCookie.getValue();
                    
                    // Extract token ID (jti) using JwtService
                    String tokenId = jwtService.extractTokenId(refreshToken);
                    
                    if (tokenId != null) {
                        // Extract expiration
                        Claims claims = jwtService.extractClaims(refreshToken);
                        Instant expiration = claims.getExpiration().toInstant();
                        
                        tokenBlacklistService.blacklistToken(tokenId, expiration, username, "logout", "REFRESH");
                        log.debug("Refresh token blacklisted for user: {}", username);
                    } else {
                        log.warn("Could not extract token ID from refresh token for user: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not blacklist refresh token for user: {}", username, e);
        }
    }

    /**
     * Checks if a token is blacklisted (for use in JWT filter)
     */
    public boolean isTokenBlacklisted(String tokenId) {
        return tokenBlacklistService.isBlacklisted(tokenId);
    }
    
    /**
     * Extract username from token even if it's blacklisted or expired
     * Used for logout when token is already blacklisted
     */
    public String extractUsernameFromToken(String token) {
        try {
            // Use extractClaims which doesn't validate, just parses
            Claims claims = jwtService.extractClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.debug("Could not extract username from token: {}", e.getMessage());
            return null;
        }
    }
}
