package com.spring.jwt.service;

import java.util.concurrent.CompletableFuture;

public interface AsyncAuditService {
    CompletableFuture<Void> logActivity(String userId, String action, String details, String ipAddress, String status);
}
