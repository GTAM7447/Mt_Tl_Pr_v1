-- Migration script to add soft delete functionality to family_background table
-- This script adds the necessary columns for soft delete functionality

-- Add soft delete columns
ALTER TABLE family_background 
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN deleted_at TIMESTAMP NULL,
ADD COLUMN deleted_by INTEGER NULL;

-- Add audit columns if they don't exist
ALTER TABLE family_background 
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN created_by INTEGER NULL,
ADD COLUMN updated_by INTEGER NULL;

-- Add version column for optimistic locking
ALTER TABLE family_background 
ADD COLUMN version INTEGER NOT NULL DEFAULT 0;

-- Add indexes for performance on soft delete queries
CREATE INDEX idx_family_background_deleted ON family_background(deleted);
CREATE INDEX idx_family_background_user_deleted ON family_background(user_id, deleted);

-- Add foreign key constraints
ALTER TABLE family_background 
ADD CONSTRAINT fk_family_background_deleted_by 
FOREIGN KEY (deleted_by) REFERENCES users(id);

ALTER TABLE family_background 
ADD CONSTRAINT fk_family_background_created_by 
FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE family_background 
ADD CONSTRAINT fk_family_background_updated_by 
FOREIGN KEY (updated_by) REFERENCES users(id);

-- Update existing records to have proper timestamps and version if they don't exist
UPDATE family_background 
SET created_at = CURRENT_TIMESTAMP, 
    updated_at = CURRENT_TIMESTAMP,
    version = 0
WHERE created_at IS NULL OR updated_at IS NULL OR version IS NULL;