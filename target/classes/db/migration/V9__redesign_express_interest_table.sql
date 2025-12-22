-- Migration V9: Redesign Express Interest Table with Enterprise Features
-- This migration drops and recreates the express_interest table with enterprise-level features

-- Drop existing table and recreate with proper structure
DROP TABLE IF EXISTS express_interest;

CREATE TABLE express_interest (
    interest_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Core relationship fields
    from_user_id INT NOT NULL,
    to_user_id INT NOT NULL,
    
    -- Status and messaging
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message TEXT COMMENT 'Optional message from sender',
    response_message TEXT COMMENT 'Optional response message from receiver',
    
    -- Compatibility and matching
    compatibility_score INT COMMENT 'Calculated compatibility score (0-100)',
    auto_matched BOOLEAN DEFAULT FALSE COMMENT 'Whether this was auto-suggested',
    
    -- Expiry and limits
    expires_at TIMESTAMP NULL COMMENT 'When this interest expires',
    daily_limit_count INT DEFAULT 1 COMMENT 'Count towards daily limit',
    
    -- Audit fields
    created_by INT COMMENT 'User who created this record',
    updated_by INT COMMENT 'User who last updated this record',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Soft delete fields
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    deleted_by INT NULL COMMENT 'User who deleted this record',
    
    -- Optimistic locking
    version INT DEFAULT 0,
    
    -- Additional metadata
    source_platform VARCHAR(50) DEFAULT 'WEB' COMMENT 'Platform where interest was sent (WEB, MOBILE, API)',
    ip_address VARCHAR(45) COMMENT 'IP address of sender',
    user_agent TEXT COMMENT 'User agent of sender',
    
    -- Constraints
    UNIQUE KEY uk_from_to_user_active (from_user_id, to_user_id, is_deleted),
    FOREIGN KEY (from_user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (deleted_by) REFERENCES user(id) ON DELETE SET NULL,
    
    -- Business rule constraints
    CHECK (from_user_id != to_user_id),
    CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'WITHDRAWN', 'EXPIRED')),
    CHECK (compatibility_score IS NULL OR (compatibility_score >= 0 AND compatibility_score <= 100)),
    CHECK (daily_limit_count > 0),
    CHECK (is_deleted IN (0, 1)),
    CHECK (version >= 0)
);

-- Performance indexes
CREATE INDEX idx_express_interest_from_user_status ON express_interest(from_user_id, status, is_deleted, created_at DESC);
CREATE INDEX idx_express_interest_to_user_status ON express_interest(to_user_id, status, is_deleted, created_at DESC);
CREATE INDEX idx_express_interest_created_at ON express_interest(created_at DESC);
CREATE INDEX idx_express_interest_expires_at ON express_interest(expires_at) WHERE expires_at IS NOT NULL;
CREATE INDEX idx_express_interest_compatibility ON express_interest(compatibility_score DESC) WHERE compatibility_score IS NOT NULL;
CREATE INDEX idx_express_interest_status_created ON express_interest(status, created_at DESC);

-- Composite indexes for common queries
CREATE INDEX idx_express_interest_user_date_range ON express_interest(from_user_id, created_at, is_deleted);
CREATE INDEX idx_express_interest_daily_limit ON express_interest(from_user_id, DATE(created_at), is_deleted);

-- Full-text search index for messages (if needed)
-- CREATE FULLTEXT INDEX idx_express_interest_message_search ON express_interest(message, response_message);

-- Add comments for documentation
ALTER TABLE express_interest COMMENT = 'Express Interest system for matrimonial matching - Enterprise version with audit trail, soft delete, and business rules';

-- Insert sample data for testing (optional - remove in production)
-- This helps verify the migration worked correctly
INSERT INTO express_interest (
    from_user_id, to_user_id, status, message, compatibility_score, 
    created_by, expires_at, source_platform
) VALUES 
(1, 2, 'PENDING', 'Hi, I found your profile interesting. Would love to connect!', 85, 1, DATE_ADD(NOW(), INTERVAL 30 DAY), 'WEB'),
(2, 3, 'ACCEPTED', 'Hello! Your profile looks great.', 92, 2, DATE_ADD(NOW(), INTERVAL 30 DAY), 'MOBILE'),
(3, 1, 'DECLINED', 'Thank you for your interest.', 78, 3, DATE_ADD(NOW(), INTERVAL 30 DAY), 'WEB')
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Verify the migration
SELECT 
    COUNT(*) as total_interests,
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_count,
    COUNT(CASE WHEN status = 'ACCEPTED' THEN 1 END) as accepted_count,
    COUNT(CASE WHEN status = 'DECLINED' THEN 1 END) as declined_count,
    COUNT(CASE WHEN is_deleted = 1 THEN 1 END) as deleted_count
FROM express_interest;