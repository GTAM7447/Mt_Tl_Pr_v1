-- Migration script to add soft delete functionality to horoscope_details table
-- This script adds the necessary columns for soft delete functionality

-- Add soft delete columns
ALTER TABLE horoscope_details 
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN deleted_at TIMESTAMP NULL,
ADD COLUMN deleted_by INTEGER NULL;

-- Add audit columns if they don't exist
ALTER TABLE horoscope_details 
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add index for performance on soft delete queries
CREATE INDEX idx_horoscope_details_deleted ON horoscope_details(deleted);
CREATE INDEX idx_horoscope_details_user_deleted ON horoscope_details(user_id, deleted);

-- Add foreign key constraint for deleted_by (references users table)
ALTER TABLE horoscope_details 
ADD CONSTRAINT fk_horoscope_details_deleted_by 
FOREIGN KEY (deleted_by) REFERENCES users(id);

-- Update existing records to have proper timestamps if they don't exist
UPDATE horoscope_details 
SET created_at = CURRENT_TIMESTAMP, 
    updated_at = CURRENT_TIMESTAMP 
WHERE created_at IS NULL OR updated_at IS NULL;