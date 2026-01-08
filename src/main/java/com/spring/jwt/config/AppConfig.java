package com.spring.jwt.config;

import com.spring.jwt.config.filter.*;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Arrays;
import java.util.List;
import com.spring.jwt.exception.SecurityExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@Slf4j
public class AppConfig {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        @Lazy
        private JwtService jwtService;

        @Autowired
        private JwtConfig jwtConfig;

        @Autowired
        private CustomAuthenticationProvider customAuthenticationProvider;

        @Autowired
        private SecurityHeadersFilter securityHeadersFilter;

        @Autowired
        private XssFilter xssFilter;

        @Autowired
        private SqlInjectionFilter sqlInjectionFilter;

        @Autowired
        private RateLimitingFilter rateLimitingFilter;

        @Autowired
        private com.spring.jwt.jwt.ActiveSessionService activeSessionService;

        @Autowired
        private ObjectMapper objectMapper;

        @Value("${app.url.frontend:http://localhost:5173}")
        private String frontendUrl;

        @Value("#{'${app.cors.allowed-origins:http://localhost:5173/,https://matrimony-v1.netlify.app,https://matrimon1.netlify.app}'.split(',')}")
        private List<String> allowedOrigins;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsServiceCustom userDetailsService() {
                return new UserDetailsServiceCustom(userRepository);
        }

        @Bean
        public JwtRefreshTokenFilter jwtRefreshTokenFilter(
                        AuthenticationManager authenticationManager,
                        JwtConfig jwtConfig,
                        JwtService jwtService,
                        UserDetailsServiceCustom userDetailsService,
                        com.spring.jwt.jwt.ActiveSessionService activeSessionService) {
                return new JwtRefreshTokenFilter(authenticationManager, jwtConfig, jwtService, userDetailsService,
                                activeSessionService);
        }

        @Bean
        public ForwardedHeaderFilter forwardedHeaderFilter() {
                return new ForwardedHeaderFilter();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        @org.springframework.beans.factory.annotation.Qualifier("requestMappingHandlerMapping") 
                        org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping handlerMapping)
                        throws Exception {
                log.debug("Configuring security filter chain");

                http.csrf(csrf -> csrf
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .ignoringRequestMatchers(
                                                "/api/**",
                                                "/user/**",
                                                "/api/users/**",

                                                jwtConfig.getUrl(),
                                                jwtConfig.getRefreshUrl()));

                http.cors(Customizer.withDefaults());

                http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                http.headers(headers -> headers
                                .xssProtection(xss -> xss
                                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                                .contentSecurityPolicy(csp -> csp
                                                .policyDirectives(
                                                                "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'"))
                                .frameOptions(frame -> frame.deny())
                                .referrerPolicy(referrer -> referrer
                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                .permissionsPolicy(permissions -> permissions
                                                .policy("camera=(), microphone=(), geolocation=()")));

                log.debug("Configuring URL-based security rules");
                http.authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(jwtConfig.getUrl()).permitAll()
                                .requestMatchers(jwtConfig.getRefreshUrl()).permitAll()

                                .requestMatchers("/api/v1/users/register").permitAll()
                                .requestMatchers("/api/v1/users/password/**").permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/v1/profiles/public/**").permitAll()

                                .requestMatchers("/api/v1/exam/**").permitAll()

                                .requestMatchers("/api/completeProfile/getProfile/**").permitAll()
                                .requestMatchers("/api/v1/complete-profile/public/**").permitAll()
                                .requestMatchers("/api/v1/interests/**").authenticated()

                                .requestMatchers(
                                                "/v2/api-docs",
                                                "/v3/api-docs",
                                                "/v*/a*-docs/**",
                                                "/swagger-resources",
                                                "/swagger-resources/**",
                                                "/configuration/ui",
                                                "/configuration/security",
                                                "/swagger-ui/**",
                                                "/webjars/**",
                                                "/swagger-ui.html")
                                .permitAll()

                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/user/**").permitAll()

                                .requestMatchers("/api/v1/documents/**").authenticated()

                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                                .requestMatchers("/api/v1/**").authenticated()

                                .anyRequest().authenticated());

                log.debug("Configuring security filters");
                
                // Create SecurityExceptionHandler for proper error responses
                SecurityExceptionHandler securityExceptionHandler = securityExceptionHandler(handlerMapping);
                
                JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter = new JwtUsernamePasswordAuthenticationFilter(
                                authenticationManager(http), jwtConfig, jwtService, userRepository,
                                activeSessionService);
                JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter = new JwtTokenAuthenticationFilter(jwtConfig,
                                jwtService, userDetailsService(), activeSessionService, securityExceptionHandler);
                JwtRefreshTokenFilter jwtRefreshTokenFilter = new JwtRefreshTokenFilter(authenticationManager(http),
                                jwtConfig, jwtService, userDetailsService(), activeSessionService);

                // CRITICAL: Filter order matters!
                // 1. JWT filters MUST run FIRST to extract token BEFORE any sanitization
                // 2. Security filters (XSS, SQL Injection) run AFTER JWT authentication
                // 3. Rate limiting runs early to block abuse
                
                // JWT Authentication filters - run FIRST (highest priority)
                http.addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(jwtRefreshTokenFilter, UsernamePasswordAuthenticationFilter.class);
                
                // Security filters - run AFTER JWT authentication
                // These filters now exclude Authorization header from sanitization
                http.addFilterAfter(securityHeadersFilter, JwtTokenAuthenticationFilter.class)
                                .addFilterAfter(xssFilter, SecurityHeadersFilter.class)
                                .addFilterAfter(sqlInjectionFilter, XssFilter.class)
                                .addFilterAfter(rateLimitingFilter, SqlInjectionFilter.class);

                http.authenticationProvider(customAuthenticationProvider);

                SecurityExceptionHandler exceptionHandler = securityExceptionHandler(handlerMapping);

                http.exceptionHandling(exceptions -> exceptions
                                .authenticationEntryPoint(exceptionHandler)
                                .accessDeniedHandler(exceptionHandler));

                log.debug("Security configuration completed");
                return http.build();
        }

        @Bean
        public SecurityExceptionHandler securityExceptionHandler(
                        @org.springframework.beans.factory.annotation.Qualifier("requestMappingHandlerMapping")
                        org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping handlerMapping) {
                return new SecurityExceptionHandler(objectMapper, handlerMapping);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                return new CorsConfigurationSource() {
                        @Override
                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(allowedOrigins);
                                config.setAllowedMethods(Arrays.asList("GET", "PATCH", "POST", "PUT", "DELETE",
                                                "OPTIONS", "HEAD"));
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Arrays.asList(
                                                "Authorization",
                                                "Content-Type",
                                                "X-Requested-With",
                                                "Accept",
                                                "Origin",
                                                "Access-Control-Request-Method",
                                                "Access-Control-Request-Headers",
                                                "Cache-Control",
                                                "Pragma"));
                                config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
                                config.setMaxAge(3600L);

                                log.debug("CORS Configuration - Allowed Origins: {}", allowedOrigins);
                                log.debug("CORS Configuration - Request Origin: {}", request.getHeader("Origin"));

                                return config;
                        }
                };
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
                builder.userDetailsService(userDetailsService())
                                .passwordEncoder(passwordEncoder());
                return builder.build();
        }

}
