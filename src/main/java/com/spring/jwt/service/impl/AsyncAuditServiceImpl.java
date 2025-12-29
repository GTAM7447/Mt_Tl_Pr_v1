package com.spring.jwt.service.impl;

import com.spring.jwt.entity.AuditLog;
import com.spring.jwt.repository.AuditLogRepository;
import com.spring.jwt.service.AsyncAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncAuditServiceImpl implements AsyncAuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public CompletableFuture<Void> logActivity(String userId, String action, String details, String ipAddress,
            String status) {
        try {
            AuditLog logEntry = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .details(details)
                    .ipAddress(ipAddress)
                    .status(status)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(logEntry);
            log.debug("Audit log saved: {} - {}", action, userId);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
