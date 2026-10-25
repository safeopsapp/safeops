-- V11: Add referral codes, user status, and membership approval lifecycle

-- 1. Organizations: Add referral code
ALTER TABLE organizations
    ADD COLUMN referral_code VARCHAR(32);

-- Backfill referral_code from slug for any existing records
UPDATE organizations
    SET referral_code = UPPER(slug)
    WHERE referral_code IS NULL;

ALTER TABLE organizations
    ALTER COLUMN referral_code SET NOT NULL;

CREATE UNIQUE INDEX idx_organizations_referral_code ON organizations(referral_code);

-- 2. Users: Add lifecycle account status
ALTER TABLE users
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE';

CREATE INDEX idx_users_status ON users(status);

-- 3. Organization Memberships: Add status and onboarding metadata
ALTER TABLE organization_memberships
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN worker_role VARCHAR(64),
    ADD COLUMN work_location VARCHAR(255);

CREATE INDEX idx_org_memberships_status ON organization_memberships(status);

