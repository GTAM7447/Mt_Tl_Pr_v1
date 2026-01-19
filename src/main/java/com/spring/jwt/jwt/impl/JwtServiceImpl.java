package com.spring.jwt.jwt.impl;

import com.spring.jwt.exception.BaseException;
import com.spring.jwt.exception.DeviceFingerprintMismatchException;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.jwt.TokenBlacklistService;
import com.spring.jwt.jwt.ActiveSessionService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsCustom;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private static final String CLAIM_KEY_DEVICE_FINGERPRINT = "dfp";
    private static final String CLAIM_KEY_TOKEN_TYPE = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ActiveSessionService activeSessionService;

    @Autowired
    public JwtServiceImpl(@Lazy UserDetailsService userDetailsService, 
                          UserRepository userRepository, 
                          @Lazy JwtConfig jwtConfig,
                           TokenBlacklistService tokenBlacklistService,
                           ActiveSessionService activeSessionService) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtConfig = jwtConfig;
        this.tokenBlacklistService = tokenBlacklistService;
        this.activeSessionService = activeSessionService;
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setAllowedClockSkewSeconds(jwtConfig.getAllowedClockSkewSeconds())
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Key  getKey() {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(key);
    }

    @Override
    public String generateToken(UserDetailsCustom userDetailsCustom) {
        return generateToken(userDetailsCustom, null);
    }

    /**
     * 2️⃣ CANONICAL JWT GENERATION METHOD - Access Token
     * This is the ONLY method that should generate access tokens
     * DO NOT create duplicate token builders elsewhere
     * 7️⃣ JWT is IMMUTABLE after creation - never modify after signing
     */
    @Override
    public String generateToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint) {
        if (userDetailsCustom == null) {
            throw new IllegalArgumentException("UserDetailsCustom cannot be null");
        }
        
        Instant now = Instant.now();
        Instant notBefore = now.plusSeconds(Math.max(0, jwtConfig.getNotBefore()));

        List<String> roles = userDetailsCustom.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        for (String role : roles) {
            if (role == null || role.contains("�") || role.contains("\u0000")) {
                throw new IllegalStateException("Corrupted authority detected: " + role);
            }
        }

        Integer userId = userDetailsCustom.getUserId();
        String firstName = userDetailsCustom.getFirstName();

        JwtBuilder jwtBuilder = Jwts.builder()
            .setSubject(userDetailsCustom.getUsername())
            .setIssuer(jwtConfig.getIssuer())
            .setAudience(jwtConfig.getAudience())
            .setId(UUID.randomUUID().toString())
            .claim("firstname", firstName != null ? firstName : "")
            .claim("userId", userId)
            .claim("authorities", roles)
            .claim("isEnable", userDetailsCustom.isEnabled());

        if (userDetailsCustom.getUserProfileId() != null) {
            jwtBuilder.claim("userProfileId", userDetailsCustom.getUserProfileId());
        }
        
        jwtBuilder.claim(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(notBefore))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
                .signWith(getKey(), SignatureAlgorithm.HS256);

        if (jwtConfig.isDeviceFingerprintingEnabled() && StringUtils.hasText(deviceFingerprint)) {
            jwtBuilder.claim(CLAIM_KEY_DEVICE_FINGERPRINT, deviceFingerprint);
        }

        String token = jwtBuilder.compact();
        
        if (token == null || token.split("\\.").length != 3) {
            throw new IllegalStateException("Generated token is malformed");
        }
        
        return token;
    }
    
    /**
     * 2️⃣ CANONICAL JWT GENERATION METHOD - Refresh Token
     * This is the ONLY method that should generate refresh tokens
     * DO NOT create duplicate token builders elsewhere
     * 7️⃣ JWT is IMMUTABLE after creation - never modify after signing
     */
    @Override
    public String generateRefreshToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint) {
        if (userDetailsCustom == null) {
            throw new IllegalArgumentException("UserDetailsCustom cannot be null");
        }
        
        Instant now = Instant.now();
        Instant notBefore = now.plusSeconds(Math.max(0, jwtConfig.getNotBefore()));

        List<String> roles = userDetailsCustom.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        for (String role : roles) {
            if (role == null || role.contains("�") || role.contains("\u0000")) {
                throw new IllegalStateException("Corrupted authority detected: " + role);
            }
        }

        JwtBuilder jwtBuilder = Jwts.builder()
            .setSubject(userDetailsCustom.getUsername())
            .setIssuer(jwtConfig.getIssuer())
            .setId(UUID.randomUUID().toString())
            .claim("userId", userDetailsCustom.getUserId())
            .claim("authorities", roles);

        if (userDetailsCustom.getUserProfileId() != null) {
            jwtBuilder.claim("userProfileId", userDetailsCustom.getUserProfileId());
        }
        
        jwtBuilder.claim(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(notBefore))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getRefreshExpiration())))
                .signWith(getKey(), SignatureAlgorithm.HS256);

        if (jwtConfig.isDeviceFingerprintingEnabled() && StringUtils.hasText(deviceFingerprint)) {
            jwtBuilder.claim(CLAIM_KEY_DEVICE_FINGERPRINT, deviceFingerprint);
        }

        String token = jwtBuilder.compact();
        
        if (token == null || token.split("\\.").length != 3) {
            throw new IllegalStateException("Generated refresh token is malformed");
        }
        
        return token;
    }
    
    @Override
    public String extractDeviceFingerprint(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get(CLAIM_KEY_DEVICE_FINGERPRINT, String.class);
        } catch (Exception e) {
            log.warn("Error extracting device fingerprint from token", e);
            return null;
        }
    }
    
    @Override
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get(CLAIM_KEY_TOKEN_TYPE, String.class);
            log.debug("Token type: {}", tokenType);
            return TOKEN_TYPE_REFRESH.equals(tokenType);
        } catch (Exception e) {
            log.warn("Error checking if token is refresh token: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String generateDeviceFingerprint(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        try {
            // IMPORTANT: Do NOT include IP address in fingerprint
            // IP addresses change frequently (mobile networks, WiFi, VPN)
            // This would cause legitimate users to be logged out constantly
            
            String ua = request.getHeader("User-Agent");
            String lang = request.getHeader("Accept-Language");
            String enc = request.getHeader("Accept-Encoding");

            // Build fingerprint from stable browser characteristics only
            StringBuilder deviceInfo = new StringBuilder();
            deviceInfo.append(ua != null ? ua : "unknown").append("|");
            deviceInfo.append(lang != null ? lang : "unknown").append("|");
            deviceInfo.append(enc != null ? enc : "unknown");

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(deviceInfo.toString().getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating device fingerprint (SHA-256 not available): {}", e.getMessage());
            return null;
        } catch (Exception e) {
            // PRODUCTION SAFETY: If fingerprint generation fails for any reason, return null
            // This ensures the application continues to work even if something unexpected happens
            log.error("Unexpected error generating device fingerprint - returning null for safety: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public Map<String, Object> extractAllCustomClaims(String token) {
        Claims claims = extractClaims(token);

        Map<String, Object> customClaims = new HashMap<>(claims);
        customClaims.remove("sub");
        customClaims.remove("iat");
        customClaims.remove("exp");
        customClaims.remove("jti");
        customClaims.remove("iss");
        customClaims.remove("aud");
        customClaims.remove("nbf");
        
        return customClaims;
    }

    @Override
    public boolean isValidToken(String token) {
        return isValidToken(token, null);
    }
    
    @Override
    public boolean isValidToken(String token, String deviceFingerprint) {
        try {
            if (isBlacklisted(token)) {
                return false;
            }
            
            final String username = extractUsername(token);
            
            if (StringUtils.isEmpty(username)) {
                return false;
            }
    
            // No DB call - username validation only
            Claims claims = extractAllClaims(token);
            
            // Check token type
            String tokenType = claims.get(CLAIM_KEY_TOKEN_TYPE, String.class);

            Date nbf = claims.getNotBefore();
            if (nbf != null && nbf.after(new Date())) {
                return false;
            }
            
            // Device fingerprint validation
            if (jwtConfig.isDeviceFingerprintingEnabled()) {
                try {
                    String tokenDeviceFingerprint = claims.get(CLAIM_KEY_DEVICE_FINGERPRINT, String.class);
                    
                    if (StringUtils.hasText(tokenDeviceFingerprint)) {
                        if (!StringUtils.hasText(deviceFingerprint)) {
                            // Cannot generate fingerprint - allow request
                            log.warn("Cannot generate device fingerprint - allowing request");
                        } else if (!tokenDeviceFingerprint.equals(deviceFingerprint)) {
                            throw new DeviceFingerprintMismatchException(
                                "Token was generated on a different device or browser. Please login again from this device.");
                        }
                    }
                } catch (DeviceFingerprintMismatchException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Error during device fingerprint validation: {}", e.getMessage());
                }
            }

            if (jwtConfig.isEnforceSingleSession()) {
                try {
                    String tokenId = claims.getId();
                    if (StringUtils.hasText(tokenId) && !activeSessionService.isCurrentAccessToken(username, tokenId)) {
                        return false;
                    }
                } catch (Exception e) {
                    log.warn("Could not verify active session: {}", e.getMessage());
                }
            }

            return true;
        } catch (DeviceFingerprintMismatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token){
        Claims claims;

        try {
            claims = Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(jwtConfig.getAllowedClockSkewSeconds())
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token expiration");
        }catch (UnsupportedJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token's not supported");
        }catch (MalformedJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid format 3 part of token");
        }catch (SignatureException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid format token");
        }catch (Exception e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getLocalizedMessage());
        }

        return claims;
    }

    @Override
    public void blacklistToken(String token) {
        try {
            Claims claims = extractClaims(token);
            String tokenId = claims.getId();
            Date expiration = claims.getExpiration();
            
            if (tokenId != null && expiration != null) {
                tokenBlacklistService.blacklistToken(tokenId, expiration.toInstant());
                log.debug("Token blacklisted: {}", tokenId);
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
        }
    }
    
    @Override
    public String extractTokenId(String token) {
        try {
            Claims claims = extractClaims(token);
            String tokenId = claims.getId();
            if (tokenId == null) {
                log.warn("Token ID (jti) is null in token");
            }
            return tokenId;
        } catch (Exception e) {
            log.error("Error extracting token ID: {} - Token preview: {}", 
                    e.getMessage(), 
                    token != null && token.length() > 30 ? token.substring(0, 30) + "..." : "null or too short");
            return null;
        }
    }
    
    @Override
    public boolean isBlacklisted(String token) {
        try {
            String tokenId = extractTokenId(token);
            return tokenId != null && tokenBlacklistService.isBlacklisted(tokenId);
        } catch (Exception e) {
            log.error("Error checking blacklist: {}", e.getMessage());
            return false;
        }
    }
}


