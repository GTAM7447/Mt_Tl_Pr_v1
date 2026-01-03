-- Add optimistic locking and audit trail to all tables
-- This migration is backward compatible and safe to run

-- Users table - Add version and audit timestamps
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS version INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Subscription table - Add version and full audit trail
ALTER TABLE subscription 
ADD COLUMN IF NOT EXISTS version INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS audit_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS audit_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by INT,
ADD COLUMN IF NOT EXISTS updated_by INT;

-- Document table - Add version, audit trail, and soft delete
ALTER TABLE document 
ADD COLUMN IF NOT EXISTS version INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by INT,
ADD COLUMN IF NOT EXISTS updated_by INT,
ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS deleted_by INT;

-- Update existing records to ensure they have proper defaults
UPDATE users SET version = 0 WHERE version IS NULL;
UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

UPDATE subscription SET version = 0 WHERE version IS NULL;
UPDATE subscription SET audit_created_at = CURRENT_TIMESTAMP WHERE audit_created_at IS NULL;
UPDATE subscription SET audit_updated_at = CURRENT_TIMESTAMP WHERE audit_updated_at IS NULL;

UPDATE document SET version = 0 WHERE version IS NULL;
UPDATE document SET deleted = FALSE WHERE deleted IS NULL;

-- Now make version columns NOT NULL after setting defaults
ALTER TABLE users MODIFY COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE subscription MODIFY COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE document MODIFY COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE document MODIFY COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_document_deleted ON document(deleted);
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_mobile ON users(mobile_number);
CREATE INDEX IF NOT EXISTS idx_user_gender ON users(gender);
CREATE INDEX IF NOT EXISTS idx_user_status ON users(completeProfile);
CREATE INDEX IF NOT EXISTS idx_subscription_status ON subscription(status);
CREATE INDEX IF NOT EXISTS idx_subscription_created ON subscription(created_at);
