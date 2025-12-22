-- Migration script to update education_and_profession table with enterprise features
-- Version: V4
-- Description: Add soft delete, optimistic locking, audit trail, and improved schema

-- Rename table if needed (from educationAndProfession to education_and_profession)
ALTER TABLE educationAndProfession RENAME TO education_and_profession;

-- Update column names and constraints
ALTER TABLE education_and_profession 
    CHANGE COLUMN educationId education_id INT AUTO_INCREMENT,
    CHANGE COLUMN educationAndProfessionalDetailsCol additional_details VARCHAR(1000),
    MODIFY COLUMN education VARCHAR(100) NOT NULL,
    MODIFY COLUMN degree VARCHAR(100) NOT NULL,
    MODIFY COLUMN occupation VARCHAR(100) NOT NULL,
    MODIFY COLUMN occupationDetails occupation_details VARCHAR(500),
    MODIFY COLUMN incomePerYear income_per_year INT NOT NULL;

-- Add new columns for enhanced functionality
ALTER TABLE education_and_profession 
    ADD COLUMN work_location VARCHAR(100),
    ADD COLUMN company_name VARCHAR(200),
    ADD COLUMN experience_years INT;

-- Add audit trail columns
ALTER TABLE education_and_profession 
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD COLUMN created_by INT,
    ADD COLUMN updated_by INT;

-- Add soft delete columns
ALTER TABLE education_and_profession 
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at TIMESTAMP NULL,
    ADD COLUMN deleted_by INT;

-- Add optimistic locking
ALTER TABLE education_and_profession 
    ADD COLUMN version INT NOT NULL DEFAULT 0;

-- Add constraints and indexes
ALTER TABLE education_and_profession 
    ADD CONSTRAINT uk_education_profession_user_id UNIQUE (user_id);

-- Create indexes for performance
CREATE INDEX idx_education_profession_user_id ON education_and_profession(user_id);
CREATE INDEX idx_education_profession_occupation ON education_and_profession(occupation);
CREATE INDEX idx_education_profession_education ON education_and_profession(education);
CREATE INDEX idx_education_profession_deleted ON education_and_profession(deleted);
CREATE INDEX idx_education_profession_income ON education_and_profession(income_per_year);

-- Add foreign key constraints
ALTER TABLE education_and_profession 
    ADD CONSTRAINT fk_education_profession_user_id 
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE education_and_profession 
    ADD CONSTRAINT fk_education_profession_created_by 
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL;

ALTER TABLE education_and_profession 
    ADD CONSTRAINT fk_education_profession_updated_by 
    FOREIGN KEY (updated_by) REFERENCES user(id) ON DELETE SET NULL;

ALTER TABLE education_and_profession 
    ADD CONSTRAINT fk_education_profession_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES user(id) ON DELETE SET NULL;

-- Add check constraints for business rules
ALTER TABLE education_and_profession 
    ADD CONSTRAINT chk_education_profession_income_positive 
    CHECK (income_per_year > 0);

ALTER TABLE education_and_profession 
    ADD CONSTRAINT chk_education_profession_experience_positive 
    CHECK (experience_years IS NULL OR experience_years >= 0);

-- Update existing records to set default values
UPDATE education_and_profession 
SET 
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP),
    deleted = COALESCE(deleted, FALSE),
    version = COALESCE(version, 0)
WHERE created_at IS NULL OR updated_at IS NULL OR deleted IS NULL OR version IS NULL;