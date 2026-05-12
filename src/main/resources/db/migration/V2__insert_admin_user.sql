-- Insert default admin user
-- BCrypt hash for password 'Admin@1234'
-- To generate: bcrypt('Admin@1234')
INSERT INTO admin_users (email, password, full_name, role, is_active, password_changed, created_at, updated_at)
VALUES (
    'admin@pesa.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm',
    'System Administrator',
    'ADMIN',
    true,
    false,
    NOW(),
    NOW()
);
