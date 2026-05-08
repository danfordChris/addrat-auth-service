# Auth Service - Migration & Upgrade Notes

## Database Schema Migrations

### 2026-05-08: Allow NULL Password for OTP-Based Authentication

**Reason**: Mobile app uses OTP-based authentication without passwords. Password column should allow NULL values.

**Change**:
```sql
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;
```

**Entity Definition**:
```java
@Column(nullable = true)
private String password;  // Allow NULL for OTP-based auth
```

**For Fresh Installations**:
- Hibernate will automatically create the `password` column as nullable
- No manual migration needed

**For Existing Databases**:
- Run the SQL command above to remove the NOT NULL constraint
- Or drop and recreate the users table with the new schema

**Verification**:
```sql
SELECT column_name, is_nullable 
FROM information_schema.columns 
WHERE table_name='users' AND column_name='password';
-- Should show: password | YES
```

---

### New Columns Added (2026-05-08)

The User entity now includes:

```java
@Column(nullable = true)
private String pin;  // Transaction PIN for mobile app

@Column(nullable = true)
private String deviceToken;  // FCM token for push notifications
```

These columns are automatically created by Hibernate on fresh installations.

---

## Docker Container Considerations

When restarting Docker containers:

1. **Data Persistence**: 
   - Database volume `postgres_data` persists data across container restarts
   - No data loss on container restart

2. **Schema Changes**:
   - Hibernate `ddl-auto: update` mode applies new columns automatically
   - For constraint changes, manual SQL may be required if table exists

3. **Fresh Start**:
   ```bash
   # Clean start (WARNING: deletes database)
   docker-compose down -v
   docker-compose up -d
   # Database will be recreated with correct schema
   ```

---

## Deployment Steps

### Development/Staging
1. Update code with new entity definitions
2. Rebuild Docker image: `docker-compose build auth-service`
3. Start services: `docker-compose up -d`
4. If schema issues occur, apply manual SQL migrations

### Production
1. Create database backup
2. Apply SQL migrations
3. Deploy new version
4. Verify OTP flow end-to-end

---

## Troubleshooting

### "password column NOT NULL constraint" Error
**Symptom**: OTP verification fails with NOT NULL constraint error

**Solution**:
```bash
# Connect to database
docker exec pesa-postgres psql -U postgres -d pesa_lending

# Apply constraint fix
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

# Verify
SELECT is_nullable FROM information_schema.columns 
WHERE table_name='users' AND column_name='password';
```

### Hibernate Not Creating New Columns
**Symptom**: New columns (pin, deviceToken) not appearing in database

**Solution**: Ensure `spring.jpa.hibernate.ddl-auto=update` is set in application.yml

**Verification**:
```bash
# Check Hibernate DDL mode
docker logs pesa-auth-service | grep "ddl-auto"

# Should show: ddl-auto=update
```

---

## Backward Compatibility

- ✅ Existing OTP endpoints work without changes
- ✅ New endpoints use `/users/me/*` pattern
- ✅ Legacy endpoints (`/kyc/*`) still supported
- ⚠️ Password field is now optional (breaking change if password-based auth was used)

---

## Future Migrations

When adding new features:

1. **Database Schema Changes**:
   - Update entity definitions
   - Test with Hibernate auto-update
   - Document SQL fallback for manual migration

2. **API Changes**:
   - Add new endpoints alongside old ones
   - Maintain backward compatibility where possible
   - Version API if breaking changes needed

3. **Testing**:
   - Test fresh install (empty database)
   - Test upgrade (existing database)
   - Test rollback (revert changes)

---

**Last Updated**: 2026-05-08
**Status**: Deployed to development
**Next Review**: Before production deployment
