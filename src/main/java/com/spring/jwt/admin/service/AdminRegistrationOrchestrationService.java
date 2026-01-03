package com.spring.jwt.admin.service;

import com.spring.jwt.admin.dto.AdminCompleteRegistrationRequest;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationResponse;

/**
 * Orchestration service for admin-driven complete user registration.
 * 
 * This service coordinates the creation of a user account and all associated profile sections
 * in a single transactional operation. It delegates to existing domain services while maintaining
 * clean separation of concerns and ensuring data consistency.
 * 
 * Key Responsibilities:
 * - Coordinate multi-entity creation across domain boundaries
 * - Maintain transactional integrity (all-or-nothing)
 * - Calculate and track profile completion metrics
 * - Provide comprehensive response with operation results
 * - Handle partial data submission (optional sections)
 * 
 * Design Principles:
 * - No business logic duplication (delegates to existing services)
 * - Single transactional boundary at orchestration level
 * - Backward compatible (doesn't modify existing APIs)
 * - Admin-specific validation rules via validation groups
 * - Clean rollback on any failure
 */
public interface AdminRegistrationOrchestrationService {

    /**
     * Register a complete user with all profile sections in a single operation.
     * 
     * This method orchestrates the creation of:
     * 1. User account (required)
     * 2. Profile details (optional)
     * 3. Horoscope details (optional)
     * 4. Education and profession (optional)
     * 5. Family background (optional)
     * 6. Contact details (optional)
     * 7. Partner preferences (optional)
     * 8. Complete profile aggregate (automatic)
     * 
     * All operations are wrapped in a single transaction. If any step fails,
     * the entire operation is rolled back to maintain data consistency.
     * 
     * @param request the complete registration request with user and optional profile sections
     * @return comprehensive response with created entity IDs and profile completion status
     * @throws IllegalArgumentException if required user data is missing or invalid
     * @throws DuplicateUserException if user with email/mobile already exists
     * @throws TransactionException if any database operation fails
     */
    AdminCompleteRegistrationResponse registerCompleteUser(AdminCompleteRegistrationRequest request);
}
