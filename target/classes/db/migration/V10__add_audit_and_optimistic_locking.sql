-- Add optimistic locking and audit trail to all tables

-- Users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Subscription table
ALTER TABLE subscription 
ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS audit_created_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS audit_updated_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by INT,
ADD COLUMN IF NOT EXISTS updated_by INT;

-- Document table
ALTER TABLE document 
ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by INT,
ADD COLUMN IF NOT EXISTS updated_by INT,
ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS deleted_by INT;

CREATE INDEX IF NOT EXISTS idx_document_deleted ON document(deleted);

-- Update existing records to have version = 0
UPDATE users SET version = 0 WHERE version IS NULL;
UPDATE subscription SET version = 0 WHERE version IS NULL;
UPDATE document SET version = 0 WHERE version IS NULL;

-- Add indexes for audit queries
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_mobile ON users(mobile_number);
CREATE INDEX IF NOT EXISTS idx_user_gender ON users(gender);
CREATE INDEX IF NOT EXISTS idx_user_status ON users(completeProfile);

CREATE INDEX IF NOT EXISTS idx_subscription_status ON subscription(status);
CREATE INDEX IF NOT EXISTS idx_subscription_created ON subscription(created_at);
