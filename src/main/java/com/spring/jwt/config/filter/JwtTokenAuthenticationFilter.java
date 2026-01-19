package com.spring.jwt.config.filter;

import com.spring.jwt.exception.DeviceFingerprintMismatchException;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.jwt.ActiveSessionService;
import com.spring.jwt.service.security.UserDetailsCustom;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.HelperUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserDetailsServiceCustom userDetailsService;
    private final ActiveSessionService activeSessionService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    private boolean setauthreq = true;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

      
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getJwtFromRequest(request);
        
        if (token == null || token.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String[] tokenParts = token.split("\\.");
        if (tokenParts.length != 3) {
            log.error("Invalid JWT format - expected 3 parts but got {} for request: {}", 
                    tokenParts.length, request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
           
            Claims claims = jwtService.extractClaims(token);
            
            validateClaimsSchema(claims);
            
            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                log.error("Token has null or empty subject claim");
                filterChain.doFilter(request, response);
                return;
            }

            List<String> authorities = extractAndValidateAuthorities(claims);
            if (authorities == null || authorities.isEmpty()) {
                log.warn("No authorities found in token for user: {}", username);
                filterChain.doFilter(request, response);
                return;
            }

            String deviceFingerprint = jwtService.generateDeviceFingerprint(request);
            if (!jwtService.isValidToken(token, deviceFingerprint)) {
                log.warn("Token validation failed for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            Integer userId = claims.get("userId", Integer.class);
            Integer userProfileId = claims.get("userProfileId", Integer.class);
            String firstName = claims.get("firstname", String.class);
            
            UserDetailsCustom userDetails = new UserDetailsCustom(
                    username,
                    "", 
                    firstName,
                    userId,
                    userProfileId,
                    authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,  
                    null,
                    authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );
            
            auth.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                    .buildDetails(request));

            org.springframework.security.core.context.SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

        } catch (DeviceFingerprintMismatchException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // Malformed = 401
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // Invalid signature = 401
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        } catch (JwtException e) {
            // JWT error = 401
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        } catch (IllegalStateException e) {
            // Principal mismatch = 500 (config error)
            log.error("Configuration error during authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication configuration error");
            return;
        } catch (Exception e) {
            // Unexpected error = 500
            log.error("Unexpected error processing JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 6️⃣ Validate Claims Schema Explicitly
     * Fail early if claims don't match expected format
     */
    private void validateClaimsSchema(Claims claims) {
        // Validate authorities claim format
        Object authoritiesClaim = claims.get("authorities");
        if (authoritiesClaim != null && !(authoritiesClaim instanceof List<?>)) {
            throw new JwtException("Invalid authorities claim format - expected List but got " + 
                    authoritiesClaim.getClass().getSimpleName());
        }
        
        // Validate userId claim
        Object userIdClaim = claims.get("userId");
        if (userIdClaim != null && !(userIdClaim instanceof Integer)) {
            throw new JwtException("Invalid userId claim format - expected Integer but got " + 
                    userIdClaim.getClass().getSimpleName());
        }
        
        // Validate token_type claim
        Object tokenTypeClaim = claims.get("token_type");
        if (tokenTypeClaim != null && !(tokenTypeClaim instanceof String)) {
            throw new JwtException("Invalid token_type claim format - expected String but got " + 
                    tokenTypeClaim.getClass().getSimpleName());
        }
    }

    /**
     * Extract and validate authorities from claims
     * Returns null if authorities are invalid
     */
    private List<String> extractAndValidateAuthorities(Claims claims) {
        try {
            List<String> authorities = claims.get("authorities", List.class);
            if (authorities == null) {
                authorities = claims.get("roles", List.class);
            }
            
            if (authorities != null) {
                // Validate each authority is a String
                for (Object auth : authorities) {
                    if (!(auth instanceof String)) {
                        log.error("Invalid authority type in token: expected String but got {}", 
                                auth != null ? auth.getClass().getSimpleName() : "null");
                        return null;
                    }
                    // Check for corrupted authority strings
                    String authStr = (String) auth;
                    if (authStr.contains("�") || authStr.contains("\u0000")) {
                        log.error("Corrupted authority string detected: {}", authStr);
                        return null;
                    }
                }
            }
            
            return authorities;
        } catch (Exception e) {
            log.error("Error extracting authorities from claims: {}", e.getMessage());
            return null;
        }
    }

    private String getSpecificInvalidReason(String token, HttpServletRequest request) {
        try {
            if (jwtService.isBlacklisted(token)) {
                return "Token is revoked/blacklisted";
            }
            Claims claims = jwtService.extractClaims(token);
            String tokenDfp = claims.get("dfp", String.class);
            String reqDfp = jwtService.generateDeviceFingerprint(request);
            if (StringUtils.hasText(tokenDfp) && StringUtils.hasText(reqDfp) && !tokenDfp.equals(reqDfp)) {
                return "Device mismatch: please login again on this device";
            }
            String username = claims.getSubject();
            String tokenId = claims.getId();
            if (StringUtils.hasText(username) && StringUtils.hasText(tokenId) && !activeSessionService.isCurrentAccessToken(username, tokenId)) {
                return "You are logged in on another device. Please logout from the other device to continue";
            }
            return "Invalid or expired token";
        } catch (ExpiredJwtException e) {
            return "Expired token";
        } catch (JwtException e) {
            return "Malformed or invalid token";
        } catch (Exception e) {
            return "Unauthorized";
        }
    }

    /**
     * Process the JWT token and set authentication if valid
     * @return true if token is valid and authentication was set, false otherwise
     */
    private boolean processToken(HttpServletRequest request, String token) {
        String deviceFingerprint = jwtService.generateDeviceFingerprint(request);
        if (jwtService.isValidToken(token, deviceFingerprint)) {
            Claims claims = jwtService.extractClaims(token);
            String username = claims.getSubject();

            if (jwtService.isRefreshToken(token)) {
                log.warn("Refresh token used for API access - not allowed");
                return false;
            }

            if (!ObjectUtils.isEmpty(username)) {
                log.debug("Valid token found for user: {}", username);

                List<String> authorities = claims.get("authorities", List.class);
                if (authorities == null) {
                    authorities = claims.get("roles", List.class);
                }

                if (authorities != null) {
                    UserDetailsCustom userDetails = (UserDetailsCustom) userDetailsService.loadUserByUsername(username);
                    
                    if (userDetails == null) {
                        log.warn("User not found: {}", username);
                        return false;
                    }
                    
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    
                    try {
                        String tokenId = claims.getId();
                        if (!activeSessionService.isCurrentAccessToken(username, tokenId)) {
                            log.warn("Token not current for user: {}", username);
                            return false;
                        }
                    } catch (Exception ignored) {}
                    
                    log.debug("Authentication set in security context for user: {}", username);
                    return true;
                } else {
                    log.warn("No authorities found in token for user: {}", username);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Extract JWT token from request (header or cookie)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getPrefix() + " ")) {
            log.debug("Found token in Authorization header");
            return bearerToken.substring((jwtConfig.getPrefix() + " ").length());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> accessTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst();

            if (accessTokenCookie.isPresent()) {
                log.debug("Found access token in cookie");
                return accessTokenCookie.get().getValue();
            }
        }
        return null;
    }

    private void handleAccessBlocked(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        responseDTO.setMessage("d7324asdx8hg");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void handleAccessDenied(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Access denied: Authentication required");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage(message);

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Expired token");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void handleAuthenticationException(HttpServletResponse response, Exception e) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Authentication failed: " + e.getMessage());

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    public void setauthreq(boolean setauthreq)
    {
        this.setauthreq = setauthreq;
    }
}
