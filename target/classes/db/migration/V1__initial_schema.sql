-- Enable pgcrypto for gen_random_uuid() if not already enabled
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password     VARCHAR(255),
    full_name    VARCHAR(255),
    email        VARCHAR(255),
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    last_login   TIMESTAMP,
    pin          VARCHAR(255),
    device_token VARCHAR(255)
);

CREATE TABLE kyc_profiles (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                     UUID NOT NULL UNIQUE,
    full_name                   VARCHAR(255),
    date_of_birth               DATE,
    gender                      VARCHAR(20),
    id_type                     VARCHAR(30),
    id_number                   VARCHAR(100),
    residence_address           TEXT,
    business_details            TEXT,
    employment_status           VARCHAR(30),
    employer_name               VARCHAR(255),
    employer_address            TEXT,
    tin_number                  VARCHAR(50),
    business_name               VARCHAR(255),
    business_tin_number         VARCHAR(50),
    business_registration_number VARCHAR(100),
    income_range                VARCHAR(50),
    income_source               VARCHAR(30),
    loan_amount_requested       NUMERIC(19,2),
    loan_purpose                TEXT,
    repayment_period_months     INTEGER,
    marital_status              VARCHAR(20),
    number_of_dependents        INTEGER,
    status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason            TEXT,
    completion_step             VARCHAR(30) NOT NULL DEFAULT 'PERSONAL_INFO',
    created_at                  TIMESTAMP,
    updated_at                  TIMESTAMP,
    approved_at                 TIMESTAMP
);

CREATE INDEX idx_user_id ON kyc_profiles(user_id);
CREATE INDEX idx_status  ON kyc_profiles(status);

CREATE TABLE kyc_documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kyc_profile_id  UUID NOT NULL,
    document_type   VARCHAR(30) NOT NULL,
    file_name       VARCHAR(500),
    file_url        TEXT,
    file_size_bytes BIGINT,
    mime_type       VARCHAR(100),
    uploaded_at     TIMESTAMP
);

CREATE INDEX idx_kyc_profile_id ON kyc_documents(kyc_profile_id);
CREATE INDEX idx_document_type  ON kyc_documents(document_type);

CREATE TABLE admin_users (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email            VARCHAR(255) UNIQUE NOT NULL,
    password         VARCHAR(255) NOT NULL,
    full_name        VARCHAR(255),
    role             VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    password_changed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    last_login       TIMESTAMP
);

CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id   UUID,
    old_values  TEXT,
    new_values  TEXT,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP
);

CREATE TABLE credit_board_scores (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL UNIQUE,
    score        NUMERIC(10,4),
    grade        VARCHAR(10),
    loan_limit   NUMERIC(19,2),
    eligible     BOOLEAN NOT NULL DEFAULT FALSE,
    evaluated_at TIMESTAMP
);
