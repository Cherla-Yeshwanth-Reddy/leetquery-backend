-- Add Spring Security required fields to users table
-- This migration adds the missing security fields to the User entity

ALTER TABLE users
ADD COLUMN IF NOT EXISTS account_not_expired BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS credentials_not_expired BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing users to have security enabled
UPDATE users 
SET account_not_expired = TRUE,
    credentials_not_expired = TRUE
WHERE account_not_expired IS NULL OR credentials_not_expired IS NULL;

-- Add indexes for common security checks
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(is_enabled);
CREATE INDEX IF NOT EXISTS idx_users_account_locked ON users(is_account_locked);
