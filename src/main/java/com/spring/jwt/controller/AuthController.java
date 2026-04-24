package com.spring.jwt.controller;

import com.spring.jwt.aspect.Loggable;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.service.LogoutService;
import com.spring.jwt.utils.BaseResponseDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtConfig jwtConfig;
    private final LogoutService logoutService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    /**
     * Logout endpoint that clears the refresh token cookie and invalidates the session.
     * Industry standard implementation:
     * 1. Extract user info BEFORE clearing context (or from token directly if context is empty)
     * 2. Blacklist tokens to prevent reuse
     * 3. Invalidate active session
     * 4. Clear refresh token cookie
     * 5. Clear security context
     */
    @PostMapping("/logout")
    @Loggable(action = "LOGOUT")
    public ResponseEntity<BaseResponseDTO> logout(HttpServletRequest request, HttpServletResponse response)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal()))
        {
            username = authentication.getName();
            log.info("Processing logout request for user: {}", username);
        } else {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer "))
            {
                try
                {
                    String token = authHeader.substring(7);
                    username = logoutService.extractUsernameFromToken(token);
                    if (username != null)
                    {
                        log.info("Processing logout request for user (from token): {}", username);
                    } else
                    {
                        log.info("Processing logout request for anonymous user");
                    }
                } catch (Exception e)
                {
                    log.warn("Could not extract username from token: {}", e.getMessage());
                    log.info("Processing logout request for anonymous user");
                }
            } else
            {
                log.info("Processing logout request for anonymous user");
            }
        }

        if (username != null)
        {
            logoutService.performLogout(request, username);
        }

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.OK.value()));
        responseDTO.setMessage("Logout successful");

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Test endpoint to check cookies in the request
     */
    @GetMapping("/check-cookies")
    public ResponseEntity<Map<String, Object>> checkCookies(HttpServletRequest request)
    {
        Map<String, Object> response = new HashMap<>();

        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            response.put("cookieCount", cookies.length);

            Map<String, String> cookieDetails = Arrays.stream(cookies)
                    .collect(Collectors.toMap(
                            Cookie::getName,
                            cookie -> {
                                String value = cookie.getValue();
                                if (value.length() > 10) {
                                    return value.substring(0, 5) + "..." + value.substring(value.length() - 5);
                                }
                                return value;
                            }));

            response.put("cookies", cookieDetails);

            boolean hasRefreshToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()));

            response.put("hasRefreshToken", hasRefreshToken);
        } else
        {
            response.put("cookieCount", 0);
            response.put("cookies", Map.of());
            response.put("hasRefreshToken", false);
        }

        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name -> headers.put(name, request.getHeader(name)));
        response.put("headers", headers);

        return ResponseEntity.ok(response);
    }
}