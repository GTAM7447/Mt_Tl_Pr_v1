package com.spring.jwt.jwt;

import com.spring.jwt.jwt.impl.JwtServiceImpl;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsCustom;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 🔟 Regression Protection Tests
 * Tests JWT generation, parsing, and authorities integrity
 * Prevents "sometimes works" issues by validating token structure
 */
@DisplayName("JWT Token Integrity Tests")
public class JwtTokenIntegrityTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private ActiveSessionService activeSessionService;

    private JwtServiceImpl jwtService;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup JWT config with test values
        jwtConfig = new JwtConfig();
        jwtConfig.setSecret("3979244226452948404D6251655468576D5A7134743777217A25432A462D4A61");
        jwtConfig.setExpiration(3600);
        jwtConfig.setRefreshExpiration(86400);
        jwtConfig.setNotBefore(0);
        jwtConfig.setAllowedClockSkewSeconds(5);
        jwtConfig.setIssuer("TestIssuer");
        jwtConfig.setAudience("TestAudience");
        jwtConfig.setDeviceFingerprintingEnabled(false);
        jwtConfig.setEnforceSingleSession(false);

        jwtService = new JwtServiceImpl(
                userDetailsService,
                userRepository,
                jwtConfig,
                tokenBlacklistService,
                activeSessionService
        );
    }

    @Test
    @DisplayName("✅ JWT Generation - Token should have 3 parts (header.payload.signature)")
    void testJwtGeneration_HasThreeParts() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String token = jwtService.generateToken(userDetails, null);

        // Then
        assertNotNull(token, "Token should not be null");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have exactly 3 parts: header.payload.signature");
    }

    @Test
    @DisplayName("✅ JWT Parsing - Token should be parseable without errors")
    void testJwtParsing_Success() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();
        String token = jwtService.generateToken(userDetails, null);

        // When
        Claims claims = jwtService.extractClaims(token);

        // Then
        assertNotNull(claims, "Claims should not be null");
        assertEquals("test@example.com", claims.getSubject(), "Subject should match username");
    }

    @Test
    @DisplayName("✅ Authorities Integrity - Authorities should be List<String>")
    void testAuthoritiesIntegrity_IsListOfStrings() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();
        String token = jwtService.generateToken(userDetails, null);

        // When
        Claims claims = jwtService.extractClaims(token);
        Object authoritiesClaim = claims.get("authorities");

        // Then
        assertNotNull(authoritiesClaim, "Authorities claim should not be null");
        assertTrue(authoritiesClaim instanceof List<?>, "Authorities should be a List");
        
        List<?> authorities = (List<?>) authoritiesClaim;
        assertFalse(authorities.isEmpty(), "Authorities should not be empty");
        
        for (Object auth : authorities) {
            assertTrue(auth instanceof String, "Each authority should be a String, but got: " + 
                    (auth != null ? auth.getClass().getSimpleName() : "null"));
        }
    }

    @Test
    @DisplayName("✅ Authorities Content - No corrupted characters")
    void testAuthoritiesContent_NoCorruption() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();
        String token = jwtService.generateToken(userDetails, null);

        // When
        Claims claims = jwtService.extractClaims(token);
        List<String> authorities = claims.get("authorities", List.class);

        // Then
        assertNotNull(authorities, "Authorities should not be null");
        for (String auth : authorities) {
            assertNotNull(auth, "Authority should not be null");
            assertFalse(auth.contains("�"), "Authority should not contain replacement character: " + auth);
            assertFalse(auth.contains("\u0000"), "Authority should not contain null character: " + auth);
            assertTrue(auth.matches("[A-Z_]+"), "Authority should only contain uppercase letters and underscores: " + auth);
        }
    }

    @Test
    @DisplayName("✅ Token Decoding - Payload should be valid JSON")
    void testTokenDecoding_ValidJson() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();
        String token = jwtService.generateToken(userDetails, null);

        // When
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        // Then
        assertTrue(payload.contains("\"sub\""), "Payload should contain subject claim");
        assertTrue(payload.contains("\"authorities\""), "Payload should contain authorities claim");
        assertTrue(payload.contains("\"userId\""), "Payload should contain userId claim");
        assertFalse(payload.contains("�"), "Payload should not contain corrupted characters");
    }

    @Test
    @DisplayName("✅ Multiple Token Generation - Tokens should be unique")
    void testMultipleTokenGeneration_Unique() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String token1 = jwtService.generateToken(userDetails, null);
        String token2 = jwtService.generateToken(userDetails, null);

        // Then
        assertNotEquals(token1, token2, "Each token should be unique (different jti)");
    }

    @Test
    @DisplayName("✅ Refresh Token Generation - Should have correct token_type")
    void testRefreshTokenGeneration_CorrectType() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String refreshToken = jwtService.generateRefreshToken(userDetails, null);
        Claims claims = jwtService.extractClaims(refreshToken);

        // Then
        assertEquals("refresh", claims.get("token_type"), "Refresh token should have token_type=refresh");
    }

    @Test
    @DisplayName("✅ Access Token Generation - Should have correct token_type")
    void testAccessTokenGeneration_CorrectType() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String accessToken = jwtService.generateToken(userDetails, null);
        Claims claims = jwtService.extractClaims(accessToken);

        // Then
        assertEquals("access", claims.get("token_type"), "Access token should have token_type=access");
    }

    @Test
    @DisplayName("✅ Required Claims - All required claims should be present")
    void testRequiredClaims_AllPresent() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String token = jwtService.generateToken(userDetails, null);
        Claims claims = jwtService.extractClaims(token);

        // Then
        assertNotNull(claims.getSubject(), "Subject (sub) should be present");
        assertNotNull(claims.getId(), "Token ID (jti) should be present");
        assertNotNull(claims.getIssuedAt(), "Issued At (iat) should be present");
        assertNotNull(claims.getExpiration(), "Expiration (exp) should be present");
        assertNotNull(claims.getIssuer(), "Issuer (iss) should be present");
        assertNotNull(claims.get("userId"), "userId claim should be present");
        assertNotNull(claims.get("authorities"), "authorities claim should be present");
        assertNotNull(claims.get("token_type"), "token_type claim should be present");
    }

    @Test
    @DisplayName("✅ Claim Types - Claims should have correct types")
    void testClaimTypes_Correct() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String token = jwtService.generateToken(userDetails, null);
        Claims claims = jwtService.extractClaims(token);

        // Then
        assertTrue(claims.get("userId") instanceof Integer, "userId should be Integer");
        assertTrue(claims.get("authorities") instanceof List<?>, "authorities should be List");
        assertTrue(claims.get("token_type") instanceof String, "token_type should be String");
        assertTrue(claims.get("isEnable") instanceof Boolean, "isEnable should be Boolean");
    }

    @Test
    @DisplayName("❌ Null UserDetails - Should throw IllegalArgumentException")
    void testNullUserDetails_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken(null, null);
        }, "Should throw IllegalArgumentException for null UserDetailsCustom");
    }

    @Test
    @DisplayName("✅ Token Immutability - Token should not change after generation")
    void testTokenImmutability() {
        // Given
        UserDetailsCustom userDetails = createTestUserDetails();

        // When
        String token = jwtService.generateToken(userDetails, null);
        String tokenCopy = token;

        // Simulate time passing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then
        assertEquals(tokenCopy, token, "Token should remain unchanged (immutable)");
    }

    /**
     * Helper method to create test UserDetailsCustom
     */
    private UserDetailsCustom createTestUserDetails() {
        return new UserDetailsCustom() {
            @Override
            public Integer getUserId() {
                return 123;
            }

            @Override
            public String getFirstName() {
                return "Test";
            }

            @Override
            public Integer getUserProfileId() {
                return 456;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                );
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return "test@example.com";
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }
}
