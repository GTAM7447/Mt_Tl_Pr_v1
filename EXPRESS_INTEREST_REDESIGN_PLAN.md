# Express Interest Module - Enterprise Redesign Plan

## Overview
Complete redesign of the ExpressInterest module following enterprise-level architecture patterns established in ContactDetails, PartnerPreference, and other modules.

## Architecture Components

### 1. **Entity Layer** (`entity/ExpressInterest.java`)
- Soft delete support
- Audit fields (createdBy, updatedBy, createdAt, updatedAt)
- Optimistic locking with @Version
- Proper relationships and constraints
- Status enum with validation

### 2. **DTOs** (`ExpressInterest/dto/`)
- `ExpressInterestCreateRequest` - Create new interest
- `ExpressInterestUpdateRequest` - Update status/message
- `ExpressInterestResponse` - Standard response
- `ExpressInterestDetailResponse` - With profile details
- `ExpressInterestStatsResponse` - Statistics

### 3. **Repository** (`ExpressInterest/ExpressInterestRepository.java`)
- Custom queries with @Query
- Pagination support
- Soft delete filtering
- Statistics queries

### 4. **Service Layer**
- `ExpressInterestService` - Interface with comprehensive operations
- `ExpressInterestServiceImpl` - Implementation with business logic
- `ExpressInterestValidationService` - Business rule validation
- `ExpressInterestMatchingService` - Compatibility checking

### 5. **Mapper** (`ExpressInterest/ExpressInterestMapper.java`)
- MapStruct-style manual mapping
- Entity ↔ DTO conversions
- Null-safe operations

### 6. **Controller** (`ExpressInterest/ExpressInterestController.java`)
- RESTful endpoints
- Swagger documentation
- Proper HTTP status codes
- Security annotations

### 7. **Exception Handler** (`ExpressInterest/ExpressInterestExceptionHandler.java`)
- Module-specific exception handling
- Consistent error responses

### 8. **Admin Controller** (`admin/AdminExpressInterestController.java`)
- Admin-only operations
- Bulk operations
- Analytics endpoints

## Key Features

### User Operations
1. **Send Interest** - Express interest in another user
2. **View Sent Interests** - See interests sent by current user
3. **View Received Interests** - See interests received by current user
4. **Accept Interest** - Accept a received interest
5. **Decline Interest** - Decline a received interest
6. **Withdraw Interest** - Withdraw a sent interest
7. **Get Interest Details** - View specific interest with profile
8. **Get Statistics** - Personal interest statistics

### Admin Operations
1. **View All Interests** - Paginated list with filters
2. **Get Interest by ID** - View any interest
3. **Get User Interests** - All interests for specific user
4. **Get Statistics** - System-wide statistics
5. **Bulk Operations** - Mass status updates
6. **Analytics** - Interest patterns and trends

### Business Rules
1. **No Duplicate Interests** - Can't send interest to same user twice
2. **No Self Interest** - Can't send interest to yourself
3. **Subscription Check** - Verify user has active subscription
4. **Profile Completeness** - Minimum profile completion required
5. **Daily Limit** - Maximum interests per day
6. **Compatibility Check** - Basic compatibility validation
7. **Status Transitions** - Valid status change rules
8. **Notification Triggers** - Auto-notify on status changes

### Security Features
1. **IDOR Protection** - Users can only access their own interests
2. **Role-Based Access** - Admin vs User permissions
3. **Audit Trail** - Track all changes
4. **Soft Delete** - Preserve data integrity
5. **Optimistic Locking** - Prevent concurrent updates

### Performance Features
1. **Caching** - Cache frequently accessed data
2. **Pagination** - All list operations paginated
3. **Lazy Loading** - Efficient data fetching
4. **Query Optimization** - Indexed queries
5. **Async Operations** - Non-blocking notifications

## Database Schema

```sql
CREATE TABLE express_interest (
    interest_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_user_id INT NOT NULL,
    to_user_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message TEXT,
    response_message TEXT,
    compatibility_score INT,
    
    -- Audit fields
    created_by INT,
    updated_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    deleted_by INT NULL,
    
    -- Optimistic locking
    version INT DEFAULT 0,
    
    -- Constraints
    UNIQUE KEY uk_from_to_user (from_user_id, to_user_id, is_deleted),
    FOREIGN KEY (from_user_id) REFERENCES user(id),
    FOREIGN KEY (to_user_id) REFERENCES user(id),
    CHECK (from_user_id != to_user_id),
    CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'WITHDRAWN', 'EXPIRED'))
);

CREATE INDEX idx_from_user_status ON express_interest(from_user_id, status, is_deleted);
CREATE INDEX idx_to_user_status ON express_interest(to_user_id, status, is_deleted);
CREATE INDEX idx_created_at ON express_interest(created_at);
```

## API Endpoints

### User Endpoints (`/api/v1/interests`)
- `POST /` - Send interest
- `GET /sent` - Get sent interests (paginated)
- `GET /received` - Get received interests (paginated)
- `GET /{interestId}` - Get interest details
- `PATCH /{interestId}/accept` - Accept interest
- `PATCH /{interestId}/decline` - Decline interest
- `PATCH /{interestId}/withdraw` - Withdraw interest
- `GET /statistics` - Get personal statistics
- `GET /compatibility/{userId}` - Check compatibility

### Admin Endpoints (`/api/v1/admin/interests`)
- `GET /` - Get all interests (paginated, filtered)
- `GET /{interestId}` - Get any interest
- `GET /user/{userId}` - Get user's interests
- `GET /statistics` - System statistics
- `GET /analytics` - Interest analytics
- `POST /bulk/expire` - Bulk expire old interests
- `DELETE /{interestId}` - Hard delete interest

## Implementation Order

1. ✅ Create migration script
2. ✅ Update entity with enterprise features
3. ✅ Create comprehensive DTOs
4. ✅ Implement repository with custom queries
5. ✅ Create validation service
6. ✅ Create matching service
7. ✅ Implement mapper
8. ✅ Implement service layer
9. ✅ Create exception handler
10. ✅ Implement user controller
11. ✅ Implement admin controller
12. ✅ Create Postman collection
13. ✅ Write documentation

## Testing Strategy

1. **Unit Tests** - Service layer logic
2. **Integration Tests** - Repository queries
3. **API Tests** - Controller endpoints
4. **Security Tests** - Authorization checks
5. **Performance Tests** - Load testing

## Success Criteria

✅ All CRUD operations working
✅ Proper security and authorization
✅ Comprehensive validation
✅ Efficient queries with pagination
✅ Complete Swagger documentation
✅ Working Postman collection
✅ Proper error handling
✅ Audit trail functionality
✅ Caching implemented
✅ Admin operations functional
