-- Migration script to update complete_profile table with enterprise features
-- Version: V7
-- Description: Add comprehensive metrics, audit trail, soft delete, and optimistic locking

-- First, check if the old table exists and rename it
-- Rename table if needed (handle both possible names)
SET @tableName = (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
                  WHERE TABLE_SCHEMA = DATABASE() 
                  AND TABLE_NAME IN ('completeprofile', 'complete_profile', 'CompleteProfile')
                  LIMIT 1);

-- Rename table to standard name if it's not already
SET @renameSQL = IF(@tableName = 'completeprofile' OR @tableName = 'CompleteProfile',
                    CONCAT('ALTER TABLE ', @tableName, ' RENAME TO complete_profile'),
                    'SELECT 1');
PREPARE stmt FROM @renameSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update column names to snake_case (handle existing columns)
SET @changeCompleteProfileIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'complete_profile' 
                                     AND COLUMN_NAME = 'completeProfileId') > 0,
                                    'ALTER TABLE complete_profile CHANGE COLUMN completeProfileId complete_profile_id INT AUTO_INCREMENT',
                                    'SELECT 1');
PREPARE stmt FROM @changeCompleteProfileIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update foreign key column names
SET @changeUserProfileIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                 WHERE TABLE_SCHEMA = DATABASE() 
                                 AND TABLE_NAME = 'complete_profile' 
                                 AND COLUMN_NAME = 'userProfileId') > 0,
                                'ALTER TABLE complete_profile CHANGE COLUMN userProfileId user_profile_id INT',
                                'SELECT 1');
PREPARE stmt FROM @changeUserProfileIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeHoroscopeDetailsIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                      WHERE TABLE_SCHEMA = DATABASE() 
                                      AND TABLE_NAME = 'complete_profile' 
                                      AND COLUMN_NAME = 'horoscopeDetailsId') > 0,
                                     'ALTER TABLE complete_profile CHANGE COLUMN horoscopeDetailsId horoscope_details_id INT',
                                     'SELECT 1');
PREPARE stmt FROM @changeHoroscopeDetailsIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeEducationIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'complete_profile' 
                               AND COLUMN_NAME = 'educationId') > 0,
                              'ALTER TABLE complete_profile CHANGE COLUMN educationId education_id INT',
                              'SELECT 1');
PREPARE stmt FROM @changeEducationIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeFamilyBackgroundIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                      WHERE TABLE_SCHEMA = DATABASE() 
                                      AND TABLE_NAME = 'complete_profile' 
                                      AND COLUMN_NAME = 'familyBackgroundId') > 0,
                                     'ALTER TABLE complete_profile CHANGE COLUMN familyBackgroundId family_background_id INT',
                                     'SELECT 1');
PREPARE stmt FROM @changeFamilyBackgroundIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeExpectationsIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                  WHERE TABLE_SCHEMA = DATABASE() 
                                  AND TABLE_NAME = 'complete_profile' 
                                  AND COLUMN_NAME = 'expectationsId') > 0,
                                 'ALTER TABLE complete_profile CHANGE COLUMN expectationsId partner_preference_id INT',
                                 'SELECT 1');
PREPARE stmt FROM @changeExpectationsIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeContactIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                             WHERE TABLE_SCHEMA = DATABASE() 
                             AND TABLE_NAME = 'complete_profile' 
                             AND COLUMN_NAME = 'contactId') > 0,
                            'ALTER TABLE complete_profile CHANGE COLUMN contactId contact_details_id INT',
                            'SELECT 1');
PREPARE stmt FROM @changeContactIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Remove old documentId column if it exists (we use documents list now)
SET @dropDocumentIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                            WHERE TABLE_SCHEMA = DATABASE() 
                            AND TABLE_NAME = 'complete_profile' 
                            AND COLUMN_NAME = 'documentId') > 0,
                           'ALTER TABLE complete_profile DROP COLUMN documentId',
                           'SELECT 1');
PREPARE stmt FROM @dropDocumentIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add new completeness metrics columns
SET @addCompletionPercentageSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'complete_profile' 
                                     AND COLUMN_NAME = 'completion_percentage') = 0,
                                    'ALTER TABLE complete_profile ADD COLUMN completion_percentage INT NOT NULL DEFAULT 0',
                                    'SELECT 1');
PREPARE stmt FROM @addCompletionPercentageSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addCompletenessScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                  WHERE TABLE_SCHEMA = DATABASE() 
                                  AND TABLE_NAME = 'complete_profile' 
                                  AND COLUMN_NAME = 'completeness_score') = 0,
                                 'ALTER TABLE complete_profile ADD COLUMN completeness_score INT NOT NULL DEFAULT 0',
                                 'SELECT 1');
PREPARE stmt FROM @addCompletenessScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addProfileQualitySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'complete_profile' 
                               AND COLUMN_NAME = 'profile_quality') = 0,
                              'ALTER TABLE complete_profile ADD COLUMN profile_quality VARCHAR(20) NOT NULL DEFAULT \'POOR\'',
                              'SELECT 1');
PREPARE stmt FROM @addProfileQualitySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addMissingSectionsCountSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'complete_profile' 
                                     AND COLUMN_NAME = 'missing_sections_count') = 0,
                                    'ALTER TABLE complete_profile ADD COLUMN missing_sections_count INT NOT NULL DEFAULT 7',
                                    'SELECT 1');
PREPARE stmt FROM @addMissingSectionsCountSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add profile status columns
SET @addProfileVisibilitySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                  WHERE TABLE_SCHEMA = DATABASE() 
                                  AND TABLE_NAME = 'complete_profile' 
                                  AND COLUMN_NAME = 'profile_visibility') = 0,
                                 'ALTER TABLE complete_profile ADD COLUMN profile_visibility VARCHAR(20) NOT NULL DEFAULT \'PRIVATE\'',
                                 'SELECT 1');
PREPARE stmt FROM @addProfileVisibilitySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addVerificationStatusSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                   WHERE TABLE_SCHEMA = DATABASE() 
                                   AND TABLE_NAME = 'complete_profile' 
                                   AND COLUMN_NAME = 'verification_status') = 0,
                                  'ALTER TABLE complete_profile ADD COLUMN verification_status VARCHAR(20) NOT NULL DEFAULT \'UNVERIFIED\'',
                                  'SELECT 1');
PREPARE stmt FROM @addVerificationStatusSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add strength metrics columns
SET @addBasicInfoScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'complete_profile' 
                               AND COLUMN_NAME = 'basic_info_score') = 0,
                              'ALTER TABLE complete_profile ADD COLUMN basic_info_score INT DEFAULT 0',
                              'SELECT 1');
PREPARE stmt FROM @addBasicInfoScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addContactInfoScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                 WHERE TABLE_SCHEMA = DATABASE() 
                                 AND TABLE_NAME = 'complete_profile' 
                                 AND COLUMN_NAME = 'contact_info_score') = 0,
                                'ALTER TABLE complete_profile ADD COLUMN contact_info_score INT DEFAULT 0',
                                'SELECT 1');
PREPARE stmt FROM @addContactInfoScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addPersonalDetailsScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'complete_profile' 
                                     AND COLUMN_NAME = 'personal_details_score') = 0,
                                    'ALTER TABLE complete_profile ADD COLUMN personal_details_score INT DEFAULT 0',
                                    'SELECT 1');
PREPARE stmt FROM @addPersonalDetailsScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addFamilyInfoScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                WHERE TABLE_SCHEMA = DATABASE() 
                                AND TABLE_NAME = 'complete_profile' 
                                AND COLUMN_NAME = 'family_info_score') = 0,
                               'ALTER TABLE complete_profile ADD COLUMN family_info_score INT DEFAULT 0',
                               'SELECT 1');
PREPARE stmt FROM @addFamilyInfoScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addProfessionalInfoScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                      WHERE TABLE_SCHEMA = DATABASE() 
                                      AND TABLE_NAME = 'complete_profile' 
                                      AND COLUMN_NAME = 'professional_info_score') = 0,
                                     'ALTER TABLE complete_profile ADD COLUMN professional_info_score INT DEFAULT 0',
                                     'SELECT 1');
PREPARE stmt FROM @addProfessionalInfoScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addPreferencesScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                 WHERE TABLE_SCHEMA = DATABASE() 
                                 AND TABLE_NAME = 'complete_profile' 
                                 AND COLUMN_NAME = 'preferences_score') = 0,
                                'ALTER TABLE complete_profile ADD COLUMN preferences_score INT DEFAULT 0',
                                'SELECT 1');
PREPARE stmt FROM @addPreferencesScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDocumentScoreSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'complete_profile' 
                              AND COLUMN_NAME = 'document_score') = 0,
                             'ALTER TABLE complete_profile ADD COLUMN document_score INT DEFAULT 0',
                             'SELECT 1');
PREPARE stmt FROM @addDocumentScoreSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add verification flags
SET @addHasProfilePhotoSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                WHERE TABLE_SCHEMA = DATABASE() 
                                AND TABLE_NAME = 'complete_profile' 
                                AND COLUMN_NAME = 'has_profile_photo') = 0,
                               'ALTER TABLE complete_profile ADD COLUMN has_profile_photo BOOLEAN NOT NULL DEFAULT FALSE',
                               'SELECT 1');
PREPARE stmt FROM @addHasProfilePhotoSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addMobileVerifiedSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'complete_profile' 
                               AND COLUMN_NAME = 'mobile_verified') = 0,
                              'ALTER TABLE complete_profile ADD COLUMN mobile_verified BOOLEAN NOT NULL DEFAULT FALSE',
                              'SELECT 1');
PREPARE stmt FROM @addMobileVerifiedSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addEmailVerifiedSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'complete_profile' 
                              AND COLUMN_NAME = 'email_verified') = 0,
                             'ALTER TABLE complete_profile ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE',
                             'SELECT 1');
PREPARE stmt FROM @addEmailVerifiedSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addIdentityVerifiedSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                 WHERE TABLE_SCHEMA = DATABASE() 
                                 AND TABLE_NAME = 'complete_profile' 
                                 AND COLUMN_NAME = 'identity_verified') = 0,
                                'ALTER TABLE complete_profile ADD COLUMN identity_verified BOOLEAN NOT NULL DEFAULT FALSE',
                                'SELECT 1');
PREPARE stmt FROM @addIdentityVerifiedSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add audit trail columns
SET @addCreatedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'complete_profile' 
                          AND COLUMN_NAME = 'created_at') = 0,
                         'ALTER TABLE complete_profile ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
                         'SELECT 1');
PREPARE stmt FROM @addCreatedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addUpdatedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'complete_profile' 
                          AND COLUMN_NAME = 'updated_at') = 0,
                         'ALTER TABLE complete_profile ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
                         'SELECT 1');
PREPARE stmt FROM @addUpdatedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addCreatedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'complete_profile' 
                          AND COLUMN_NAME = 'created_by') = 0,
                         'ALTER TABLE complete_profile ADD COLUMN created_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addCreatedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addUpdatedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'complete_profile' 
                          AND COLUMN_NAME = 'updated_by') = 0,
                         'ALTER TABLE complete_profile ADD COLUMN updated_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addUpdatedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add soft delete columns
SET @addDeletedSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'complete_profile' 
                        AND COLUMN_NAME = 'deleted') = 0,
                       'ALTER TABLE complete_profile ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE',
                       'SELECT 1');
PREPARE stmt FROM @addDeletedSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDeletedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'complete_profile' 
                          AND COLUMN_NAME = 'deleted_at') = 0,
                         'ALTER TABLE complete_profile ADD COLUMN deleted_at TIMESTAMP NULL',
                         'SELECT 1');
PREPARE stmt FROM @addDeletedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDeletedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'complete_profile' 
                          AND COLUMN_NAME = 'deleted_by') = 0,
                         'ALTER TABLE complete_profile ADD COLUMN deleted_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addDeletedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add optimistic locking
SET @addVersionSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'complete_profile' 
                        AND COLUMN_NAME = 'version') = 0,
                       'ALTER TABLE complete_profile ADD COLUMN version INT NOT NULL DEFAULT 0',
                       'SELECT 1');
PREPARE stmt FROM @addVersionSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Create indexes for performance
DROP INDEX IF EXISTS idx_complete_profile_user_id ON complete_profile;
DROP INDEX IF EXISTS idx_complete_profile_completed ON complete_profile;
DROP INDEX IF EXISTS idx_complete_profile_percentage ON complete_profile;
DROP INDEX IF EXISTS idx_complete_profile_quality ON complete_profile;
DROP INDEX IF EXISTS idx_complete_profile_verification ON complete_profile;
DROP INDEX IF EXISTS idx_complete_profile_deleted ON complete_profile;
DROP INDEX IF EXISTS idx_complete_profile_created_at ON complete_profile;

CREATE INDEX idx_complete_profile_user_id ON complete_profile(user_id);
CREATE INDEX idx_complete_profile_completed ON complete_profile(profile_completed);
CREATE INDEX idx_complete_profile_percentage ON complete_profile(completion_percentage);
CREATE INDEX idx_complete_profile_quality ON complete_profile(profile_quality);
CREATE INDEX idx_complete_profile_verification ON complete_profile(verification_status);
CREATE INDEX idx_complete_profile_deleted ON complete_profile(deleted);
CREATE INDEX idx_complete_profile_created_at ON complete_profile(created_at);

-- Add constraints
ALTER TABLE complete_profile 
    DROP CONSTRAINT IF EXISTS uk_complete_profile_user_id;
    
ALTER TABLE complete_profile 
    ADD CONSTRAINT uk_complete_profile_user_id UNIQUE (user_id);

-- Add foreign key constraints (drop if exists first)
ALTER TABLE complete_profile 
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_user_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_user_profile_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_horoscope_details_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_education_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_family_background_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_partner_preference_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_contact_details_id,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_created_by,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_updated_by,
    DROP FOREIGN KEY IF EXISTS fk_complete_profile_deleted_by;

-- Add foreign key constraints (only if referenced tables exist)
SET @addUserFkSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES 
                       WHERE TABLE_SCHEMA = DATABASE() 
                       AND TABLE_NAME = 'user') > 0,
                      'ALTER TABLE complete_profile ADD CONSTRAINT fk_complete_profile_user_id FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE',
                      'SELECT 1');
PREPARE stmt FROM @addUserFkSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing records to set default values
UPDATE complete_profile 
SET 
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP),
    deleted = COALESCE(deleted, FALSE),
    version = COALESCE(version, 0),
    completion_percentage = COALESCE(completion_percentage, 0),
    completeness_score = COALESCE(completeness_score, 0),
    profile_quality = COALESCE(profile_quality, 'POOR'),
    missing_sections_count = COALESCE(missing_sections_count, 7),
    profile_visibility = COALESCE(profile_visibility, 'PRIVATE'),
    verification_status = COALESCE(verification_status, 'UNVERIFIED'),
    has_profile_photo = COALESCE(has_profile_photo, FALSE),
    mobile_verified = COALESCE(mobile_verified, FALSE),
    email_verified = COALESCE(email_verified, FALSE),
    identity_verified = COALESCE(identity_verified, FALSE)
WHERE created_at IS NULL OR updated_at IS NULL OR deleted IS NULL OR version IS NULL
   OR completion_percentage IS NULL OR completeness_score IS NULL OR profile_quality IS NULL
   OR missing_sections_count IS NULL OR profile_visibility IS NULL OR verification_status IS NULL
   OR has_profile_photo IS NULL OR mobile_verified IS NULL OR email_verified IS NULL OR identity_verified IS NULL;