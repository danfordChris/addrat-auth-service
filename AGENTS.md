# AGENTS.md

## Scope
- This guide applies to `backend/auth-service/` only.
- It describes how this service currently behaves in code, including constraints and known mismatches.

## Service Map
- Runtime: Spring Boot 3.3 on Java 17.
- Port and context path: `8081` + `/api/auth` (see `src/main/resources/application.yml`).
- Core domains in this service:
- OTP request/verification and JWT issue (`OtpController`, `OtpStoreService`, `JwtTokenProvider`).
- Token refresh (`TokenController`).
- User profile and user-owned KYC flows (`UserProfileController`, `KycController`, `KycService`).
- Admin operations for login, dashboard, KYC decisions, credit score, and audit logs (`AdminController`, `AdminService`).
- Data stores:
- PostgreSQL via Spring Data JPA.
- Redis for OTP storage and verification attempts.

## Code Structure
- Entrypoint and bootstrapping:
- `src/main/java/com/pesa/AuthServiceApplication.java`
- Security:
- `security/SecurityConfig.java`
- `security/JwtAuthFilter.java`
- `util/JwtTokenProvider.java`
- Controllers:
- `controller/OtpController.java`
- `controller/TokenController.java`
- `controller/UserProfileController.java`
- `controller/KycController.java`
- `controller/AdminController.java`
- `controller/ReferenceController.java`
- Services:
- `service/OtpStoreService.java`
- `service/KycService.java`
- `service/AdminService.java`
- Persistence:
- `entity/*`
- `repository/*`
- Infra/config:
- `common/config/RedisConfig.java`
- `common/config/RedisHealthCheck.java`
- `common/config/WebMvcConfig.java`
- API envelope and errors:
- `common/api/ApiResponse.java`
- `common/api/ApiResponses.java`
- `common/exception/*`
- DB migrations:
- `src/main/resources/db/migration/V1__initial_schema.sql`
- `V2__insert_admin_user.sql`
- `V4__expand_kyc_profile_columns.sql`

## External Contract
- Success envelope:
- `{ success, message, data, timestamp }`
- Error envelope:
- `{ error, message, timestamp }`
- Keep endpoint responses inside this envelope using `ApiResponses.success(...)` / `ApiResponses.error(...)`.

## Auth and Token Flow
- OTP request:
- `POST /api/auth/otp/request` with `phoneNumber`.
- Generates OTP and stores it in Redis key `otp:<phone>` with TTL `otp.ttl-minutes` (default 10).
- OTP verify:
- `POST /api/auth/otp/verify` with `phoneNumber`, `otp`, `purpose`.
- Validates OTP and deletes key on success.
- For `LOGIN` and `REGISTRATION`, returns access and refresh tokens plus user payload.
- Token refresh:
- `POST /api/auth/refresh` and `POST /api/auth/token/refresh` are both supported.
- JWT details:
- Access token expiration: `jwt.expiration` (default 24h).
- Refresh token expiration: `jwt.expiration * 2` (default 48h).
- JWT claims include `userId` and `phoneNumber`.

## KYC Flow
- Two overlapping surfaces exist:
- `/kyc/*` (`KycController`)
- `/users/me/kyc*` (`UserProfileController`)
- KYC persistence model is `KycProfile` with completion steps and status.
- Document uploads write file bytes to `/tmp/pesa-documents` and metadata to `kyc_documents`.

## Admin Flow
- Admin login:
- `POST /api/auth/admin/login` validates against `admin_users` and returns token.
- Dashboard:
- `GET /api/auth/admin/dashboard` uses raw SQL through `JdbcTemplate` and expects loan/payment tables in shared DB.
- KYC decision:
- `POST /api/auth/admin/users/{userId}/kyc/decide`
- Credit score:
- `POST /api/auth/admin/users/{userId}/credit-score`
- Audit logs:
- `GET /api/auth/admin/audit-logs` using custom pageable annotation.

## Pagination Convention
- Controllers using `@PaginationParam` get Pageable from `PaginationArgumentResolver`.
- Query params supported:
- `page`, `size`, `sortBy`, `sortDir`
- Validation:
- `page >= 0`
- `0 < size <= maxSize`
- `sortDir` must be `asc` or `desc`

## Build and Run
- Local run:
- `cd backend/auth-service && mvn spring-boot:run`
- Package:
- `mvn clean package -DskipTests`
- Docker build/run is defined by:
- `backend/auth-service/Dockerfile`
- service wiring in `backend/docker-compose.yml`

## Current Risks and Gotchas
- ID type mismatch risk:
- SQL migrations use `UUID` PK/FK columns.
- JPA entities/repositories use `Long` IDs with `GenerationType.IDENTITY`.
- This is a major consistency risk and should be treated as architectural debt before broad feature work.
- Security matcher risk:
- `SecurityConfig` currently permits `/admin/*`, `/kyc/*`, `/otp/*`, `/refresh`, `/token/refresh`.
- Some permitted endpoints still read `Authentication.getDetails()`, which can fail if request is actually unauthenticated.
- OTP exposure risk:
- OTP is returned in API response and logged in plaintext; acceptable only for dev/test.
- Document type mismatch risk:
- `UserProfileController` defaults `documentType=SELFIE` while enum is `SELF_PIC`/`ID_SCAN`; default path can throw enum parse errors.
- Minimal tests:
- `auth-service` currently has no `src/test` coverage.

## Editing Rules for Contributors
- Keep API envelope unchanged unless a coordinated cross-client change is planned.
- Preserve JWT claim shape (`userId`, `phoneNumber`) to avoid breaking mobile/backoffice clients.
- Avoid introducing new controller-level raw `RuntimeException`; prefer typed exceptions from `common/exception`.
- If changing auth/security behavior, update both:
- `SecurityConfig` matcher rules.
- Any controller logic relying on `Authentication.getDetails()`.
- If touching KYC fields or enums, update all of:
- DTOs
- `KycService` mapping logic
- `KycResponse` mapping in both KYC controllers
- SQL migrations
- If touching persistence ID strategy, align SQL + entities + repositories in one change set.

## Recommended Near-Term Hardening
- Standardize ID strategy (UUID everywhere or Long everywhere) and migrate safely.
- Restrict public routes in `SecurityConfig`; enforce auth where controllers require principal context.
- Remove OTP from responses/logs outside dev profile.
- Add integration tests for OTP->token->profile flow and admin KYC decision flow.
