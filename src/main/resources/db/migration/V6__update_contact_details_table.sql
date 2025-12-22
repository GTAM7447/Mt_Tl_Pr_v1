-- Migration script to update contact_details table with enterprise features
-- Version: V6
-- Description: Add soft delete, optimistic locking, audit trail, and improved schema

-- First, check if the old table exists and rename it
-- Rename table if needed (handle both possible names)
SET @tableName = (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
                  WHERE TABLE_SCHEMA = DATABASE() 
                  AND TABLE_NAME IN ('contactDetails', 'contact_details', 'ContactDetails')
                  LIMIT 1);

-- Rename table to standard name if it's not already
SET @renameSQL = IF(@tableName = 'contactDetails' OR @tableName = 'ContactDetails',
                    CONCAT('ALTER TABLE ', @tableName, ' RENAME TO contact_details'),
                    'SELECT 1');
PREPARE stmt FROM @renameSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Handle the contactId column migration properly
-- First check if old 'contactId' column exists and rename it
SET @renameContactIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                             WHERE TABLE_SCHEMA = DATABASE() 
                             AND TABLE_NAME = 'contact_details' 
                             AND COLUMN_NAME = 'contactId') > 0,
                            'ALTER TABLE contact_details CHANGE COLUMN contactId contact_details_id INT AUTO_INCREMENT',
                            'SELECT 1');
PREPARE stmt FROM @renameContactIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Handle the userId column migration properly
-- First check if old 'userId' column exists and rename it
SET @renameUserIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'userId') > 0,
                         'ALTER TABLE contact_details CHANGE COLUMN userId user_id INT NOT NULL',
                         'SELECT 1');
PREPARE stmt FROM @renameUserIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update column names and constraints (handle existing columns)
SET @renameFullAddressSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'contact_details' 
                               AND COLUMN_NAME = 'fullAddress') > 0,
                              'ALTER TABLE contact_details CHANGE COLUMN fullAddress full_address VARCHAR(500)',
                              'SELECT 1');
PREPARE stmt FROM @renameFullAddressSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @renameMobileNumberSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                WHERE TABLE_SCHEMA = DATABASE() 
                                AND TABLE_NAME = 'contact_details' 
                                AND COLUMN_NAME = 'mobileNumber') > 0,
                               'ALTER TABLE contact_details CHANGE COLUMN mobileNumber mobile_number VARCHAR(20) NOT NULL',
                               'SELECT 1');
PREPARE stmt FROM @renameMobileNumberSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @renameAlternateNumberSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                   WHERE TABLE_SCHEMA = DATABASE() 
                                   AND TABLE_NAME = 'contact_details' 
                                   AND COLUMN_NAME = 'alternateNumber') > 0,
                                  'ALTER TABLE contact_details CHANGE COLUMN alternateNumber alternate_number VARCHAR(20)',
                                  'SELECT 1');
PREPARE stmt FROM @renameAlternateNumberSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @renamePinCodeSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                           WHERE TABLE_SCHEMA = DATABASE() 
                           AND TABLE_NAME = 'contact_details' 
                           AND COLUMN_NAME = 'pinCode') > 0,
                          'ALTER TABLE contact_details CHANGE COLUMN pinCode pin_code VARCHAR(20) NOT NULL',
                          'SELECT 1');
PREPARE stmt FROM @renamePinCodeSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Modify existing columns to ensure correct data types
SET @modifyCitySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'contact_details' 
                        AND COLUMN_NAME = 'city') > 0,
                       'ALTER TABLE contact_details MODIFY COLUMN city VARCHAR(100) NOT NULL',
                       'SELECT 1');
PREPARE stmt FROM @modifyCitySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add new columns for enhanced functionality (only if they don't exist)
SET @addStreetAddressSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'contact_details' 
                              AND COLUMN_NAME = 'street_address') = 0,
                             'ALTER TABLE contact_details ADD COLUMN street_address VARCHAR(200)',
                             'SELECT 1');
PREPARE stmt FROM @addStreetAddressSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addStateSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                      WHERE TABLE_SCHEMA = DATABASE() 
                      AND TABLE_NAME = 'contact_details' 
                      AND COLUMN_NAME = 'state') = 0,
                     'ALTER TABLE contact_details ADD COLUMN state VARCHAR(100)',
                     'SELECT 1');
PREPARE stmt FROM @addStateSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addCountrySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'contact_details' 
                        AND COLUMN_NAME = 'country') = 0,
                       'ALTER TABLE contact_details ADD COLUMN country VARCHAR(100) NOT NULL DEFAULT \'India\'',
                       'SELECT 1');
PREPARE stmt FROM @addCountrySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addWhatsappNumberSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'contact_details' 
                               AND COLUMN_NAME = 'whatsapp_number') = 0,
                              'ALTER TABLE contact_details ADD COLUMN whatsapp_number VARCHAR(20)',
                              'SELECT 1');
PREPARE stmt FROM @addWhatsappNumberSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addEmailAddressSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                             WHERE TABLE_SCHEMA = DATABASE() 
                             AND TABLE_NAME = 'contact_details' 
                             AND COLUMN_NAME = 'email_address') = 0,
                            'ALTER TABLE contact_details ADD COLUMN email_address VARCHAR(100)',
                            'SELECT 1');
PREPARE stmt FROM @addEmailAddressSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addEmergencyContactNameSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'contact_details' 
                                     AND COLUMN_NAME = 'emergency_contact_name') = 0,
                                    'ALTER TABLE contact_details ADD COLUMN emergency_contact_name VARCHAR(100)',
                                    'SELECT 1');
PREPARE stmt FROM @addEmergencyContactNameSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addEmergencyContactNumberSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                       WHERE TABLE_SCHEMA = DATABASE() 
                                       AND TABLE_NAME = 'contact_details' 
                                       AND COLUMN_NAME = 'emergency_contact_number') = 0,
                                      'ALTER TABLE contact_details ADD COLUMN emergency_contact_number VARCHAR(20)',
                                      'SELECT 1');
PREPARE stmt FROM @addEmergencyContactNumberSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addEmergencyContactRelationSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                         WHERE TABLE_SCHEMA = DATABASE() 
                                         AND TABLE_NAME = 'contact_details' 
                                         AND COLUMN_NAME = 'emergency_contact_relation') = 0,
                                        'ALTER TABLE contact_details ADD COLUMN emergency_contact_relation VARCHAR(50)',
                                        'SELECT 1');
PREPARE stmt FROM @addEmergencyContactRelationSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addPreferredContactMethodSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                       WHERE TABLE_SCHEMA = DATABASE() 
                                       AND TABLE_NAME = 'contact_details' 
                                       AND COLUMN_NAME = 'preferred_contact_method') = 0,
                                      'ALTER TABLE contact_details ADD COLUMN preferred_contact_method VARCHAR(50)',
                                      'SELECT 1');
PREPARE stmt FROM @addPreferredContactMethodSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addContactVisibilitySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                  WHERE TABLE_SCHEMA = DATABASE() 
                                  AND TABLE_NAME = 'contact_details' 
                                  AND COLUMN_NAME = 'contact_visibility') = 0,
                                 'ALTER TABLE contact_details ADD COLUMN contact_visibility VARCHAR(50) NOT NULL DEFAULT \'PRIVATE\'',
                                 'SELECT 1');
PREPARE stmt FROM @addContactVisibilitySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addIsVerifiedMobileSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                 WHERE TABLE_SCHEMA = DATABASE() 
                                 AND TABLE_NAME = 'contact_details' 
                                 AND COLUMN_NAME = 'is_verified_mobile') = 0,
                                'ALTER TABLE contact_details ADD COLUMN is_verified_mobile BOOLEAN NOT NULL DEFAULT FALSE',
                                'SELECT 1');
PREPARE stmt FROM @addIsVerifiedMobileSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addIsVerifiedEmailSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                WHERE TABLE_SCHEMA = DATABASE() 
                                AND TABLE_NAME = 'contact_details' 
                                AND COLUMN_NAME = 'is_verified_email') = 0,
                               'ALTER TABLE contact_details ADD COLUMN is_verified_email BOOLEAN NOT NULL DEFAULT FALSE',
                               'SELECT 1');
PREPARE stmt FROM @addIsVerifiedEmailSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addVerificationAttemptsSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'contact_details' 
                                     AND COLUMN_NAME = 'verification_attempts') = 0,
                                    'ALTER TABLE contact_details ADD COLUMN verification_attempts INT NOT NULL DEFAULT 0',
                                    'SELECT 1');
PREPARE stmt FROM @addVerificationAttemptsSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addLastVerificationAttemptSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                        WHERE TABLE_SCHEMA = DATABASE() 
                                        AND TABLE_NAME = 'contact_details' 
                                        AND COLUMN_NAME = 'last_verification_attempt') = 0,
                                       'ALTER TABLE contact_details ADD COLUMN last_verification_attempt TIMESTAMP NULL',
                                       'SELECT 1');
PREPARE stmt FROM @addLastVerificationAttemptSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add audit trail columns (only if they don't exist)
SET @addCreatedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'created_at') = 0,
                         'ALTER TABLE contact_details ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
                         'SELECT 1');
PREPARE stmt FROM @addCreatedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addUpdatedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'updated_at') = 0,
                         'ALTER TABLE contact_details ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
                         'SELECT 1');
PREPARE stmt FROM @addUpdatedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addCreatedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'created_by') = 0,
                         'ALTER TABLE contact_details ADD COLUMN created_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addCreatedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addUpdatedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'updated_by') = 0,
                         'ALTER TABLE contact_details ADD COLUMN updated_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addUpdatedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add soft delete columns (only if they don't exist)
SET @addDeletedSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'contact_details' 
                        AND COLUMN_NAME = 'deleted') = 0,
                       'ALTER TABLE contact_details ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE',
                       'SELECT 1');
PREPARE stmt FROM @addDeletedSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDeletedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'deleted_at') = 0,
                         'ALTER TABLE contact_details ADD COLUMN deleted_at TIMESTAMP NULL',
                         'SELECT 1');
PREPARE stmt FROM @addDeletedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDeletedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'contact_details' 
                          AND COLUMN_NAME = 'deleted_by') = 0,
                         'ALTER TABLE contact_details ADD COLUMN deleted_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addDeletedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add optimistic locking (only if it doesn't exist)
SET @addVersionSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'contact_details' 
                        AND COLUMN_NAME = 'version') = 0,
                       'ALTER TABLE contact_details ADD COLUMN version INT NOT NULL DEFAULT 0',
                       'SELECT 1');
PREPARE stmt FROM @addVersionSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add constraints (drop if exists first to avoid errors)
ALTER TABLE contact_details 
    DROP CONSTRAINT IF EXISTS uk_contact_details_user_id;
    
ALTER TABLE contact_details 
    ADD CONSTRAINT uk_contact_details_user_id UNIQUE (user_id);

ALTER TABLE contact_details 
    DROP CONSTRAINT IF EXISTS uk_contact_details_mobile;
    
ALTER TABLE contact_details 
    ADD CONSTRAINT uk_contact_details_mobile UNIQUE (mobile_number);

-- Create indexes for performance (drop if exists first)
DROP INDEX IF EXISTS idx_contact_details_user_id ON contact_details;
DROP INDEX IF EXISTS idx_contact_details_mobile ON contact_details;
DROP INDEX IF EXISTS idx_contact_details_city ON contact_details;
DROP INDEX IF EXISTS idx_contact_details_state ON contact_details;
DROP INDEX IF EXISTS idx_contact_details_country ON contact_details;
DROP INDEX IF EXISTS idx_contact_details_deleted ON contact_details;
DROP INDEX IF EXISTS idx_contact_details_pin_code ON contact_details;

CREATE INDEX idx_contact_details_user_id ON contact_details(user_id);
CREATE INDEX idx_contact_details_mobile ON contact_details(mobile_number);
CREATE INDEX idx_contact_details_city ON contact_details(city);
CREATE INDEX idx_contact_details_state ON contact_details(state);
CREATE INDEX idx_contact_details_country ON contact_details(country);
CREATE INDEX idx_contact_details_deleted ON contact_details(deleted);
CREATE INDEX idx_contact_details_pin_code ON contact_details(pin_code);

-- Add foreign key constraints (drop if exists first)
ALTER TABLE contact_details 
    DROP FOREIGN KEY IF EXISTS fk_contact_details_user_id,
    DROP FOREIGN KEY IF EXISTS fk_contact_details_created_by,
    DROP FOREIGN KEY IF EXISTS fk_contact_details_updated_by,
    DROP FOREIGN KEY IF EXISTS fk_contact_details_deleted_by;

ALTER TABLE contact_details 
    ADD CONSTRAINT fk_contact_details_user_id 
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE contact_details 
    ADD CONSTRAINT fk_contact_details_created_by 
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL;

ALTER TABLE contact_details 
    ADD CONSTRAINT fk_contact_details_updated_by 
    FOREIGN KEY (updated_by) REFERENCES user(id) ON DELETE SET NULL;

ALTER TABLE contact_details 
    ADD CONSTRAINT fk_contact_details_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES user(id) ON DELETE SET NULL;

-- Update existing records to set default values
UPDATE contact_details 
SET 
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP),
    deleted = COALESCE(deleted, FALSE),
    version = COALESCE(version, 0),
    contact_visibility = COALESCE(contact_visibility, 'PRIVATE'),
    is_verified_mobile = COALESCE(is_verified_mobile, FALSE),
    is_verified_email = COALESCE(is_verified_email, FALSE),
    verification_attempts = COALESCE(verification_attempts, 0),
    country = COALESCE(country, 'India')
WHERE created_at IS NULL OR updated_at IS NULL OR deleted IS NULL OR version IS NULL 
   OR contact_visibility IS NULL OR is_verified_mobile IS NULL OR is_verified_email IS NULL 
   OR verification_attempts IS NULL OR country IS NULL;

-- Remove default values from country after migration
SET @removeCountryDefaultSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                  WHERE TABLE_SCHEMA = DATABASE() 
                                  AND TABLE_NAME = 'contact_details' 
                                  AND COLUMN_NAME = 'country') > 0,
                                 'ALTER TABLE contact_details ALTER COLUMN country DROP DEFAULT',
                                 'SELECT 1');
PREPARE stmt FROM @removeCountryDefaultSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Remove default values from contact_visibility after migration
SET @removeContactVisibilityDefaultSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                            WHERE TABLE_SCHEMA = DATABASE() 
                                            AND TABLE_NAME = 'contact_details' 
                                            AND COLUMN_NAME = 'contact_visibility') > 0,
                                           'ALTER TABLE contact_details ALTER COLUMN contact_visibility DROP DEFAULT',
                                           'SELECT 1');
PREPARE stmt FROM @removeContactVisibilityDefaultSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;