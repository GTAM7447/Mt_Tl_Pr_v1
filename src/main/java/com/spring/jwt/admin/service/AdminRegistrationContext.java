package com.spring.jwt.admin.service;

/**
 * Thread-local context to track admin registration operations.
 * Used to optimize performance by skipping async CompleteProfile updates
 * during bulk registration operations.
 */
public class AdminRegistrationContext {
    
    private static final ThreadLocal<Boolean> IS_ADMIN_REGISTRATION = ThreadLocal.withInitial(() -> false);
    
    /**
     * Mark the current thread as performing admin registration.
     * This disables async CompleteProfile updates for performance.
     */
    public static void setAdminRegistrationMode(boolean enabled) {
        IS_ADMIN_REGISTRATION.set(enabled);
    }
    
    /**
     * Check if current thread is performing admin registration.
     */
    public static boolean isAdminRegistration() {
        return IS_ADMIN_REGISTRATION.get();
    }
    
    /**
     * Clear the admin registration flag.
     * MUST be called in finally block to prevent thread pool pollution.
     */
    public static void clear() {
        IS_ADMIN_REGISTRATION.remove();
    }
}
