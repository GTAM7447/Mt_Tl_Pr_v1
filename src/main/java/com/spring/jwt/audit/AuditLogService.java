package com.spring.jwt.audit;

import com.spring.jwt.entity.AuditLog;
import com.spring.jwt.utils.HttpRequestContextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final HttpRequestContextExtractor httpRequestContextExtractor;

    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, String entityType, Object entityId, String details, String status) {
        try {
            String userId = getCurrentUserId();
            String ipAddress = httpRequestContextExtractor.extractClientIpAddress();
            
            AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(String.format("%s_%s", action, entityType))
                .details(String.format("Entity: %s, ID: %s, Details: %s", entityType, entityId, details))
                .ipAddress(ipAddress)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
            
            entityManager.persist(auditLog);
            entityManager.flush();
            
            log.debug("Audit log created: {} {} by user {} from IP {}", 
                action, entityType, userId, ipAddress);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreate(String entityType, Object entityId, String details) {
        logAction("CREATE", entityType, entityId, details, "SUCCESS");
    }

    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdate(String entityType, Object entityId, String details) {
        logAction("UPDATE", entityType, entityId, details, "SUCCESS");
    }

    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDelete(String entityType, Object entityId, String details) {
        logAction("DELETE", entityType, entityId, details, "SUCCESS");
    }

    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String action, String entityType, Object entityId, String errorMessage) {
        logAction(action, entityType, entityId, "Error: " + errorMessage, "FAILURE");
    }

    private String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return "SYSTEM";
            }

            Object principal = authentication.getPrincipal();
            
            if (principal instanceof Integer) {
                return String.valueOf(principal);
            }
            
            if (principal instanceof String) {
                return (String) principal;
            }
            
            return "UNKNOWN";
        } catch (Exception e) {
            return "SYSTEM";
        }
    }
}
