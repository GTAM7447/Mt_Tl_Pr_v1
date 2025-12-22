-- Migration script to update partner_preference table with enterprise features
-- Version: V5
-- Description: Add soft delete, optimistic locking, audit trail, and improved schema

-- First, check if the old table exists and rename it
-- Rename table if needed (handle both possible names)
SET @tableName = (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
                  WHERE TABLE_SCHEMA = DATABASE() 
                  AND TABLE_NAME IN ('partnerpreference', 'partner_preference', 'PartnerPreference')
                  LIMIT 1);

-- Rename table to standard name if it's not already
SET @renameSQL = IF(@tableName = 'partnerpreference' OR @tableName = 'PartnerPreference',
                    CONCAT('ALTER TABLE ', @tableName, ' RENAME TO partner_preference'),
                    'SELECT 1');
PREPARE stmt FROM @renameSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Handle the age column migration properly
-- First check if old 'age' column exists and drop it
SET @dropAgeSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                      WHERE TABLE_SCHEMA = DATABASE() 
                      AND TABLE_NAME = 'partner_preference' 
                      AND COLUMN_NAME = 'age') > 0,
                     'ALTER TABLE partner_preference DROP COLUMN age',
                     'SELECT 1');
PREPARE stmt FROM @dropAgeSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add age_range column if it doesn't exist
SET @addAgeRangeSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'age_range') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN age_range VARCHAR(50) NOT NULL DEFAULT \'18-80\'',
                         'SELECT 1');
PREPARE stmt FROM @addAgeRangeSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Handle the height column migration properly
-- First check if old 'height' column exists and drop it
SET @dropHeightSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'partner_preference' 
                        AND COLUMN_NAME = 'height') > 0,
                       'ALTER TABLE partner_preference DROP COLUMN height',
                       'SELECT 1');
PREPARE stmt FROM @dropHeightSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add height_range column if it doesn't exist
SET @addHeightRangeSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                            WHERE TABLE_SCHEMA = DATABASE() 
                            AND TABLE_NAME = 'partner_preference' 
                            AND COLUMN_NAME = 'height_range') = 0,
                           'ALTER TABLE partner_preference ADD COLUMN height_range VARCHAR(50) NOT NULL DEFAULT \'4\\\'0" - 7\\\'0"\'',
                           'SELECT 1');
PREPARE stmt FROM @addHeightRangeSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update column names and constraints (handle existing columns)
SET @changePartnerPreferenceIdSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                        WHERE TABLE_SCHEMA = DATABASE() 
                                        AND TABLE_NAME = 'partner_preference' 
                                        AND COLUMN_NAME = 'partnerPreferenceId') > 0,
                                       'ALTER TABLE partner_preference CHANGE COLUMN partnerPreferenceId partner_preference_id INT AUTO_INCREMENT',
                                       'SELECT 1');
PREPARE stmt FROM @changePartnerPreferenceIdSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeLookingForSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'partner_preference' 
                              AND COLUMN_NAME = 'lookingFor') > 0,
                             'ALTER TABLE partner_preference CHANGE COLUMN lookingFor looking_for VARCHAR(100) NOT NULL',
                             'SELECT 1');
PREPARE stmt FROM @changeLookingForSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeEatingHabitsSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                WHERE TABLE_SCHEMA = DATABASE() 
                                AND TABLE_NAME = 'partner_preference' 
                                AND COLUMN_NAME = 'eatingHabits') > 0,
                               'ALTER TABLE partner_preference CHANGE COLUMN eatingHabits eating_habits VARCHAR(50)',
                               'SELECT 1');
PREPARE stmt FROM @changeEatingHabitsSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeCountryLivingInSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                   WHERE TABLE_SCHEMA = DATABASE() 
                                   AND TABLE_NAME = 'partner_preference' 
                                   AND COLUMN_NAME = 'countryLivingIn') > 0,
                                  'ALTER TABLE partner_preference CHANGE COLUMN countryLivingIn country_living_in VARCHAR(100)',
                                  'SELECT 1');
PREPARE stmt FROM @changeCountryLivingInSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeCityLivingInSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                WHERE TABLE_SCHEMA = DATABASE() 
                                AND TABLE_NAME = 'partner_preference' 
                                AND COLUMN_NAME = 'cityLivingIn') > 0,
                               'ALTER TABLE partner_preference CHANGE COLUMN cityLivingIn city_living_in VARCHAR(100)',
                               'SELECT 1');
PREPARE stmt FROM @changeCityLivingInSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changeResidentStatusSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                  WHERE TABLE_SCHEMA = DATABASE() 
                                  AND TABLE_NAME = 'partner_preference' 
                                  AND COLUMN_NAME = 'residentStatus') > 0,
                                 'ALTER TABLE partner_preference CHANGE COLUMN residentStatus resident_status VARCHAR(50)',
                                 'SELECT 1');
PREPARE stmt FROM @changeResidentStatusSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changePartnerOccupationSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                     WHERE TABLE_SCHEMA = DATABASE() 
                                     AND TABLE_NAME = 'partner_preference' 
                                     AND COLUMN_NAME = 'partnerOccupation') > 0,
                                    'ALTER TABLE partner_preference CHANGE COLUMN partnerOccupation partner_occupation VARCHAR(100)',
                                    'SELECT 1');
PREPARE stmt FROM @changePartnerOccupationSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @changePartnerIncomeSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                 WHERE TABLE_SCHEMA = DATABASE() 
                                 AND TABLE_NAME = 'partner_preference' 
                                 AND COLUMN_NAME = 'partnerIncome') > 0,
                                'ALTER TABLE partner_preference CHANGE COLUMN partnerIncome partner_income INT',
                                'SELECT 1');
PREPARE stmt FROM @changePartnerIncomeSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Modify existing columns to ensure correct data types
SET @modifyComplexionSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'partner_preference' 
                              AND COLUMN_NAME = 'complexion') > 0,
                             'ALTER TABLE partner_preference MODIFY COLUMN complexion VARCHAR(50)',
                             'SELECT 1');
PREPARE stmt FROM @modifyComplexionSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @modifyReligionSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                            WHERE TABLE_SCHEMA = DATABASE() 
                            AND TABLE_NAME = 'partner_preference' 
                            AND COLUMN_NAME = 'religion') > 0,
                           'ALTER TABLE partner_preference MODIFY COLUMN religion VARCHAR(50)',
                           'SELECT 1');
PREPARE stmt FROM @modifyReligionSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @modifyCasteSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                         WHERE TABLE_SCHEMA = DATABASE() 
                         AND TABLE_NAME = 'partner_preference' 
                         AND COLUMN_NAME = 'caste') > 0,
                        'ALTER TABLE partner_preference MODIFY COLUMN caste VARCHAR(50)',
                        'SELECT 1');
PREPARE stmt FROM @modifyCasteSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @modifyEducationSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                             WHERE TABLE_SCHEMA = DATABASE() 
                             AND TABLE_NAME = 'partner_preference' 
                             AND COLUMN_NAME = 'education') > 0,
                            'ALTER TABLE partner_preference MODIFY COLUMN education VARCHAR(100)',
                            'SELECT 1');
PREPARE stmt FROM @modifyEducationSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add new columns for enhanced functionality (only if they don't exist)
SET @addDrinkingHabitsSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                               WHERE TABLE_SCHEMA = DATABASE() 
                               AND TABLE_NAME = 'partner_preference' 
                               AND COLUMN_NAME = 'drinking_habits') = 0,
                              'ALTER TABLE partner_preference ADD COLUMN drinking_habits VARCHAR(50)',
                              'SELECT 1');
PREPARE stmt FROM @addDrinkingHabitsSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addSmokingHabitsSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'partner_preference' 
                              AND COLUMN_NAME = 'smoking_habits') = 0,
                             'ALTER TABLE partner_preference ADD COLUMN smoking_habits VARCHAR(50)',
                             'SELECT 1');
PREPARE stmt FROM @addSmokingHabitsSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addStateLivingInSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'partner_preference' 
                              AND COLUMN_NAME = 'state_living_in') = 0,
                             'ALTER TABLE partner_preference ADD COLUMN state_living_in VARCHAR(100)',
                             'SELECT 1');
PREPARE stmt FROM @addStateLivingInSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addSubCasteSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                         WHERE TABLE_SCHEMA = DATABASE() 
                         AND TABLE_NAME = 'partner_preference' 
                         AND COLUMN_NAME = 'sub_caste') = 0,
                        'ALTER TABLE partner_preference ADD COLUMN sub_caste VARCHAR(100)',
                        'SELECT 1');
PREPARE stmt FROM @addSubCasteSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addMaritalStatusSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                              WHERE TABLE_SCHEMA = DATABASE() 
                              AND TABLE_NAME = 'partner_preference' 
                              AND COLUMN_NAME = 'marital_status') = 0,
                             'ALTER TABLE partner_preference ADD COLUMN marital_status VARCHAR(50)',
                             'SELECT 1');
PREPARE stmt FROM @addMaritalStatusSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addMotherTongueSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                             WHERE TABLE_SCHEMA = DATABASE() 
                             AND TABLE_NAME = 'partner_preference' 
                             AND COLUMN_NAME = 'mother_tongue') = 0,
                            'ALTER TABLE partner_preference ADD COLUMN mother_tongue VARCHAR(50)',
                            'SELECT 1');
PREPARE stmt FROM @addMotherTongueSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addAdditionalPreferencesSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                      WHERE TABLE_SCHEMA = DATABASE() 
                                      AND TABLE_NAME = 'partner_preference' 
                                      AND COLUMN_NAME = 'additional_preferences') = 0,
                                     'ALTER TABLE partner_preference ADD COLUMN additional_preferences VARCHAR(1000)',
                                     'SELECT 1');
PREPARE stmt FROM @addAdditionalPreferencesSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add audit trail columns (only if they don't exist)
SET @addCreatedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'created_at') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
                         'SELECT 1');
PREPARE stmt FROM @addCreatedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addUpdatedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'updated_at') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
                         'SELECT 1');
PREPARE stmt FROM @addUpdatedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addCreatedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'created_by') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN created_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addCreatedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addUpdatedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'updated_by') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN updated_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addUpdatedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add soft delete columns (only if they don't exist)
SET @addDeletedSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'partner_preference' 
                        AND COLUMN_NAME = 'deleted') = 0,
                       'ALTER TABLE partner_preference ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE',
                       'SELECT 1');
PREPARE stmt FROM @addDeletedSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDeletedAtSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'deleted_at') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN deleted_at TIMESTAMP NULL',
                         'SELECT 1');
PREPARE stmt FROM @addDeletedAtSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @addDeletedBySQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                          WHERE TABLE_SCHEMA = DATABASE() 
                          AND TABLE_NAME = 'partner_preference' 
                          AND COLUMN_NAME = 'deleted_by') = 0,
                         'ALTER TABLE partner_preference ADD COLUMN deleted_by INT',
                         'SELECT 1');
PREPARE stmt FROM @addDeletedBySQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add optimistic locking (only if it doesn't exist)
SET @addVersionSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'partner_preference' 
                        AND COLUMN_NAME = 'version') = 0,
                       'ALTER TABLE partner_preference ADD COLUMN version INT NOT NULL DEFAULT 0',
                       'SELECT 1');
PREPARE stmt FROM @addVersionSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add constraints (drop if exists first to avoid errors)
ALTER TABLE partner_preference 
    DROP CONSTRAINT IF EXISTS uk_partner_preference_user_id;
    
ALTER TABLE partner_preference 
    ADD CONSTRAINT uk_partner_preference_user_id UNIQUE (user_id);

-- Create indexes for performance (drop if exists first)
DROP INDEX IF EXISTS idx_partner_preference_user_id ON partner_preference;
DROP INDEX IF EXISTS idx_partner_preference_religion ON partner_preference;
DROP INDEX IF EXISTS idx_partner_preference_caste ON partner_preference;
DROP INDEX IF EXISTS idx_partner_preference_education ON partner_preference;
DROP INDEX IF EXISTS idx_partner_preference_deleted ON partner_preference;
DROP INDEX IF EXISTS idx_partner_preference_income ON partner_preference;

CREATE INDEX idx_partner_preference_user_id ON partner_preference(user_id);
CREATE INDEX idx_partner_preference_religion ON partner_preference(religion);
CREATE INDEX idx_partner_preference_caste ON partner_preference(caste);
CREATE INDEX idx_partner_preference_education ON partner_preference(education);
CREATE INDEX idx_partner_preference_deleted ON partner_preference(deleted);
CREATE INDEX idx_partner_preference_income ON partner_preference(partner_income);

-- Add foreign key constraints (drop if exists first)
ALTER TABLE partner_preference 
    DROP FOREIGN KEY IF EXISTS fk_partner_preference_user_id,
    DROP FOREIGN KEY IF EXISTS fk_partner_preference_created_by,
    DROP FOREIGN KEY IF EXISTS fk_partner_preference_updated_by,
    DROP FOREIGN KEY IF EXISTS fk_partner_preference_deleted_by;

ALTER TABLE partner_preference 
    ADD CONSTRAINT fk_partner_preference_user_id 
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE partner_preference 
    ADD CONSTRAINT fk_partner_preference_created_by 
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL;

ALTER TABLE partner_preference 
    ADD CONSTRAINT fk_partner_preference_updated_by 
    FOREIGN KEY (updated_by) REFERENCES user(id) ON DELETE SET NULL;

ALTER TABLE partner_preference 
    ADD CONSTRAINT fk_partner_preference_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES user(id) ON DELETE SET NULL;

-- Add check constraints for business rules (drop if exists first)
ALTER TABLE partner_preference 
    DROP CONSTRAINT IF EXISTS chk_partner_preference_income_positive;
    
ALTER TABLE partner_preference 
    ADD CONSTRAINT chk_partner_preference_income_positive 
    CHECK (partner_income IS NULL OR partner_income > 0);

-- Update existing records to set default values
UPDATE partner_preference 
SET 
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP),
    deleted = COALESCE(deleted, FALSE),
    version = COALESCE(version, 0)
WHERE created_at IS NULL OR updated_at IS NULL OR deleted IS NULL OR version IS NULL;

-- Remove default values from age_range and height_range after migration
SET @removeAgeRangeDefaultSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                   WHERE TABLE_SCHEMA = DATABASE() 
                                   AND TABLE_NAME = 'partner_preference' 
                                   AND COLUMN_NAME = 'age_range') > 0,
                                  'ALTER TABLE partner_preference ALTER COLUMN age_range DROP DEFAULT',
                                  'SELECT 1');
PREPARE stmt FROM @removeAgeRangeDefaultSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @removeHeightRangeDefaultSQL = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                                      WHERE TABLE_SCHEMA = DATABASE() 
                                      AND TABLE_NAME = 'partner_preference' 
                                      AND COLUMN_NAME = 'height_range') > 0,
                                     'ALTER TABLE partner_preference ALTER COLUMN height_range DROP DEFAULT',
                                     'SELECT 1');
PREPARE stmt FROM @removeHeightRangeDefaultSQL;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;