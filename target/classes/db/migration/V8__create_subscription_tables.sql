-- Migration V8: Create enhanced subscription tables
-- This migration creates the subscription_plans and user_subscriptions tables
-- with comprehensive features for subscription management

-- Create subscription_plans table
CREATE TABLE subscription_plans (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Basic Plan Information
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    plan_code VARCHAR(20) NOT NULL UNIQUE,
    
    -- Credit and Usage Configuration
    total_credits INT NOT NULL,
    daily_credit_limit INT,
    monthly_credit_limit INT,
    
    -- Duration and Validity
    duration_months INT NOT NULL,
    validity_days INT,
    
    -- Pricing Information
    price DECIMAL(10,2),
    discount_price DECIMAL(10,2),
    currency VARCHAR(10) DEFAULT 'INR',
    
    -- Plan Features and Limits
    max_profile_views INT,
    max_contact_reveals INT,
    priority_support BOOLEAN DEFAULT FALSE,
    advanced_search BOOLEAN DEFAULT FALSE,
    profile_boost BOOLEAN DEFAULT FALSE,
    
    -- Plan Status and Availability
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    is_popular BOOLEAN DEFAULT FALSE,
    is_recommended BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    
    -- Soft Delete Implementation
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    deleted_by INT,
    
    -- Audit Trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    -- Optimistic Locking
    version BIGINT DEFAULT 0,
    
    -- Indexes
    INDEX idx_plan_code (plan_code),
    INDEX idx_status (status),
    INDEX idx_display_order (display_order),
    INDEX idx_deleted (deleted),
    INDEX idx_created_at (created_at)
);

-- Create user_subscriptions table
CREATE TABLE user_subscriptions (
    user_subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Relationships
    user_id INT NOT NULL,
    subscription_plan_id INT NOT NULL,
    
    -- Subscription Lifecycle
    subscription_status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED', 'SUSPENDED', 'PENDING_PAYMENT', 'PENDING_ACTIVATION') NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    auto_renewal BOOLEAN DEFAULT FALSE,
    cancelled_at TIMESTAMP NULL,
    cancelled_by INT,
    cancellation_reason VARCHAR(500),
    
    -- Credit Management
    allocated_credits INT NOT NULL,
    used_credits INT NOT NULL DEFAULT 0,
    remaining_credits INT NOT NULL,
    daily_credits_used INT DEFAULT 0,
    monthly_credits_used INT DEFAULT 0,
    last_credit_reset TIMESTAMP,
    
    -- Payment Information
    amount_paid DECIMAL(10,2),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    payment_status VARCHAR(20),
    
    -- Usage Statistics
    profile_views_used INT DEFAULT 0,
    contact_reveals_used INT DEFAULT 0,
    last_activity TIMESTAMP,
    
    -- Soft Delete Implementation
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    deleted_by INT,
    
    -- Audit Trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    -- Optimistic Locking
    version BIGINT DEFAULT 0,
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(subscription_id) ON DELETE RESTRICT,
    
    -- Unique Constraints
    UNIQUE KEY uk_user_active_subscription (user_id, subscription_plan_id, subscription_status),
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_subscription_plan_id (subscription_plan_id),
    INDEX idx_subscription_status (subscription_status),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_deleted (deleted),
    INDEX idx_created_at (created_at),
    INDEX idx_last_activity (last_activity)
);

-- Insert default subscription plans
INSERT INTO subscription_plans (
    name, description, plan_code, total_credits, daily_credit_limit, monthly_credit_limit,
    duration_months, price, currency, max_profile_views, max_contact_reveals,
    priority_support, advanced_search, profile_boost, is_popular, is_recommended, display_order
) VALUES 
-- Basic Plan
('Basic Plan', 'Essential features for getting started', 'BASIC', 100, 5, 100, 1, 499.00, 'INR', 50, 10, FALSE, FALSE, FALSE, FALSE, FALSE, 1),

-- Premium Plan
('Premium Plan', 'Enhanced features with priority support', 'PREMIUM', 500, 25, 500, 3, 1499.00, 'INR', 250, 50, TRUE, TRUE, FALSE, TRUE, TRUE, 2),

-- Gold Plan
('Gold Plan', 'Complete access with all premium features', 'GOLD', 1000, 50, 1000, 6, 2999.00, 'INR', 500, 100, TRUE, TRUE, TRUE, FALSE, FALSE, 3),

-- Annual Premium
('Annual Premium', 'Best value premium plan for a full year', 'ANNUAL_PREMIUM', 6000, 25, 500, 12, 14999.00, 'INR', 250, 50, TRUE, TRUE, TRUE, FALSE, TRUE, 4);

-- Add triggers for version increment (optimistic locking)
DELIMITER $$

CREATE TRIGGER subscription_plans_version_trigger
    BEFORE UPDATE ON subscription_plans
    FOR EACH ROW
BEGIN
    SET NEW.version = OLD.version + 1;
END$$

CREATE TRIGGER user_subscriptions_version_trigger
    BEFORE UPDATE ON user_subscriptions
    FOR EACH ROW
BEGIN
    SET NEW.version = OLD.version + 1;
END$$

DELIMITER ;