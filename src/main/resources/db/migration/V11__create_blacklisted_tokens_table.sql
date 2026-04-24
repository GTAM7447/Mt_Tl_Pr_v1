-- V17__create_blacklisted_tokens_table.sql
-- Create table for storing blacklisted JWT tokens
-- This ensures tokens remain invalid even after server restart

CREATE TABLE blacklisted_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_id VARCHAR(100) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL,
    token_type VARCHAR(20) NOT NULL,
    reason VARCHAR(50) NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    reuse_attempts INT NOT NULL DEFAULT 0,
    last_reuse_attempt TIMESTAMP NULL,
    
    INDEX idx_token_id (token_id),
    INDEX idx_expires_at (expires_at),
    INDEX idx_user_email (user_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add comment
ALTER TABLE blacklisted_tokens COMMENT = 'Stores blacklisted JWT tokens to prevent reuse after logout';
