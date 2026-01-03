package com.spring.jwt.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
@Slf4j
public class HttpRequestContextExtractor {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public String extractClientIpAddress() {
        return getHttpServletRequest()
                .map(this::getClientIpFromRequest)
                .orElse(UNKNOWN);
    }

    public String extractUserAgent() {
        return getHttpServletRequest()
                .map(request -> request.getHeader("User-Agent"))
                .filter(ua -> ua != null && !ua.trim().isEmpty())
                .orElse(UNKNOWN);
    }

    public String extractReferer() {
        return getHttpServletRequest()
                .map(request -> request.getHeader("Referer"))
                .filter(ref -> ref != null && !ref.trim().isEmpty())
                .orElse(UNKNOWN);
    }

    public String extractRequestMethod() {
        return getHttpServletRequest()
                .map(HttpServletRequest::getMethod)
                .orElse(UNKNOWN);
    }

    public String extractRequestUri() {
        return getHttpServletRequest()
                .map(HttpServletRequest::getRequestURI)
                .orElse(UNKNOWN);
    }

    public String extractSessionId() {
        return getHttpServletRequest()
                .map(request -> request.getSession(false))
                .map(session -> session.getId())
                .orElse(UNKNOWN);
    }

    private String getClientIpFromRequest(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return extractFirstIp(ip);
            }
        }

        String remoteAddr = request.getRemoteAddr();
        return isValidIp(remoteAddr) ? remoteAddr : UNKNOWN;
    }

    private boolean isValidIp(String ip) {
        return ip != null 
                && !ip.trim().isEmpty() 
                && !"unknown".equalsIgnoreCase(ip);
    }

    private String extractFirstIp(String ip) {
        if (ip.contains(",")) {
            return ip.split(",")[0].trim();
        }
        return ip.trim();
    }

    private Optional<HttpServletRequest> getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                return Optional.of(attributes.getRequest());
            }
        } catch (Exception e) {
            log.debug("Unable to get HTTP request from context: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public boolean isRequestContextAvailable() {
        return getHttpServletRequest().isPresent();
    }

    public RequestMetadata extractRequestMetadata() {
        return RequestMetadata.builder()
                .ipAddress(extractClientIpAddress())
                .userAgent(extractUserAgent())
                .referer(extractReferer())
                .requestMethod(extractRequestMethod())
                .requestUri(extractRequestUri())
                .sessionId(extractSessionId())
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class RequestMetadata {
        private String ipAddress;
        private String userAgent;
        private String referer;
        private String requestMethod;
        private String requestUri;
        private String sessionId;
    }
}
