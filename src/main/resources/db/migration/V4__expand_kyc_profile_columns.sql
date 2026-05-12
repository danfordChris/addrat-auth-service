-- Align kyc_profiles table with the fields used by auth-service entity mapping.
ALTER TABLE kyc_profiles
    ADD COLUMN IF NOT EXISTS employment_status VARCHAR(30),
    ADD COLUMN IF NOT EXISTS employer_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS employer_address TEXT,
    ADD COLUMN IF NOT EXISTS tin_number VARCHAR(50),
    ADD COLUMN IF NOT EXISTS business_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS business_tin_number VARCHAR(50),
    ADD COLUMN IF NOT EXISTS business_registration_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS income_range VARCHAR(50),
    ADD COLUMN IF NOT EXISTS income_source VARCHAR(30),
    ADD COLUMN IF NOT EXISTS loan_amount_requested NUMERIC(19,2),
    ADD COLUMN IF NOT EXISTS loan_purpose TEXT,
    ADD COLUMN IF NOT EXISTS repayment_period_months INTEGER;
