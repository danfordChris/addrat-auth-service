# KYC (Know Your Customer) API Documentation

## Overview

The KYC API endpoints allow users to complete their Know Your Customer profile in a step-by-step process. All endpoints require JWT authentication via the `Authorization: Bearer <token>` header.

**Base URL:** `http://localhost:8080/api/v1/auth`

---

## Endpoints

### 1. GET /kyc/status

Retrieve the current KYC verification status and completed steps.

#### Request

```
GET /api/v1/auth/kyc/status
Authorization: Bearer <jwt_token>
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "KYC status",
  "data": {
    "status": "PENDING",
    "completedSteps": [1]
  },
  "timestamp": "2026-05-09T10:30:00Z"
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| status | string | KYC status: `PENDING`, `APPROVED`, or `REJECTED` |
| completedSteps | array | List of completed step numbers (1-5) |

#### Status Codes

| Code | Description |
|------|-------------|
| 200 | Status retrieved successfully |
| 400 | User has no KYC profile |
| 401 | Unauthorized (missing or invalid token) |

#### Example cURL

```bash
curl -X GET http://localhost:8080/api/v1/auth/kyc/status \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 2. POST /kyc/personal-info

Save personal information in the KYC profile.

#### Request

```
POST /api/v1/auth/kyc/personal-info
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

#### Request Body

```json
{
  "firstName": "John",
  "middleName": "Paul",
  "lastName": "Doe",
  "gender": "MALE",
  "dateOfBirth": "1990-05-15",
  "maritalStatus": "MARRIED",
  "nidaNumber": "123456789ABC",
  "physicalAddress": "123 Main Street, Dar es Salaam"
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| firstName | string | Yes | User's first name |
| middleName | string | No | User's middle name |
| lastName | string | Yes | User's last name |
| gender | string | Yes | `MALE` or `FEMALE` |
| dateOfBirth | string | Yes | Date in ISO format (YYYY-MM-DD) |
| maritalStatus | string | Yes | `SINGLE`, `MARRIED`, `DIVORCED`, or `WIDOWED` |
| nidaNumber | string | Yes | Tanzanian National ID number |
| physicalAddress | string | Yes | Residential address |

#### Response (200 OK)

```json 
{
  "success": true,
  "message": "Personal info saved",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "660e8400-e29b-41d4-a716-446655440000",
    "fullName": "John Paul Doe",
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    "maritalStatus": "MARRIED",
    "idNumber": "123456789ABC",
    "residenceAddress": "123 Main Street, Dar es Salaam",
    "status": "PENDING",
    "completionStep": "PERSONAL_INFO",
    "createdAt": "2026-05-09T10:15:00Z",
    "updatedAt": "2026-05-09T10:30:00Z"
  },
  "timestamp": "2026-05-09T10:30:00Z"
}
```

#### Status Codes

| Code | Description |
|------|-------------|
| 200 | Personal info saved successfully |
| 400 | Invalid input (validation error) |
| 401 | Unauthorized (missing or invalid token) |

#### Example cURL

```bash
curl -X POST http://localhost:8080/api/v1/auth/kyc/personal-info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "middleName": "Paul",
    "lastName": "Doe",
    "gender": "MALE",
    "dateOfBirth": "1990-05-15",
    "maritalStatus": "MARRIED",
    "nidaNumber": "123456789ABC",
    "physicalAddress": "123 Main Street, Dar es Salaam"
  }'
```

---

### 3. POST /kyc/employment-info

Save employment information in the KYC profile.

#### Request

```
POST /api/v1/auth/kyc/employment-info
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

#### Request Body

```json
{
  "employmentStatus": "EMPLOYED",
  "employerName": "ABC Corporation",
  "employerAddress": "456 Business Ave, Dar es Salaam",
  "tinNumber": "1234567890",
  "businessName": "My Side Business",
  "businessTinNumber": "0987654321",
  "businessRegistrationNumber": "BR2024001",
  "numberOfDependants": 3
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| employmentStatus | string | Yes | `EMPLOYED`, `SELF_EMPLOYED`, or `UNEMPLOYED` |
| employerName | string | Conditional | Required if `employmentStatus` is `EMPLOYED` |
| employerAddress | string | Conditional | Required if `employmentStatus` is `EMPLOYED` |
| tinNumber | string | Conditional | Tax ID for primary employment |
| businessName | string | Conditional | Required if self-employed |
| businessTinNumber | string | Conditional | Business Tax ID |
| businessRegistrationNumber | string | Conditional | Business registration number |
| numberOfDependants | integer | Yes | Number of dependents (0-20) |

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Employment info saved",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "660e8400-e29b-41d4-a716-446655440000",
    "employmentStatus": "EMPLOYED",
    "employerName": "ABC Corporation",
    "employerAddress": "456 Business Ave, Dar es Salaam",
    "tinNumber": "1234567890",
    "businessName": "My Side Business",
    "businessTinNumber": "0987654321",
    "businessRegistrationNumber": "BR2024001",
    "numberOfDependents": 3,
    "status": "PENDING",
    "completionStep": "EMPLOYMENT_INFO",
    "createdAt": "2026-05-09T10:15:00Z",
    "updatedAt": "2026-05-09T10:35:00Z"
  },
  "timestamp": "2026-05-09T10:35:00Z"
}
```

#### Status Codes

| Code | Description |
|------|-------------|
| 200 | Employment info saved successfully |
| 400 | Invalid input or missing required fields |
| 401 | Unauthorized (missing or invalid token) |

#### Example cURL

```bash
curl -X POST http://localhost:8080/api/v1/auth/kyc/employment-info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "employmentStatus": "EMPLOYED",
    "employerName": "ABC Corporation",
    "employerAddress": "456 Business Ave, Dar es Salaam",
    "tinNumber": "1234567890",
    "businessName": "My Side Business",
    "businessTinNumber": "0987654321",
    "businessRegistrationNumber": "BR2024001",
    "numberOfDependants": 3
  }'
```

---

### 4. POST /kyc/financial-details

Save financial information in the KYC profile.

#### Request

```
POST /api/v1/auth/kyc/financial-details
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

#### Request Body

```json
{
  "bankId": 1,
  "branchId": 5,
  "accountNumber": "0123456789",
  "sourceOfIncome": "SALARY",
  "incomeRange": "500000-1000000"
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| bankId | integer | Yes | Bank identifier |
| branchId | integer | Yes | Bank branch identifier |
| accountNumber | string | Yes | Bank account number |
| sourceOfIncome | string | Yes | `SALARY`, `BUSINESS`, `INVESTMENT`, or `OTHER` |
| incomeRange | string | Yes | Monthly income range (e.g., `500000-1000000` in TZS) |

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Financial details saved",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "660e8400-e29b-41d4-a716-446655440000",
    "incomeSource": "SALARY",
    "incomeRange": "500000-1000000",
    "status": "PENDING",
    "completionStep": "FINANCIAL_INFO",
    "createdAt": "2026-05-09T10:15:00Z",
    "updatedAt": "2026-05-09T10:40:00Z"
  },
  "timestamp": "2026-05-09T10:40:00Z"
}
```

#### Status Codes

| Code | Description |
|------|-------------|
| 200 | Financial details saved successfully |
| 400 | Invalid input (validation error) |
| 401 | Unauthorized (missing or invalid token) |

#### Example cURL

```bash
curl -X POST http://localhost:8080/api/v1/auth/kyc/financial-details \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "bankId": 1,
    "branchId": 5,
    "accountNumber": "0123456789",
    "sourceOfIncome": "SALARY",
    "incomeRange": "500000-1000000"
  }'
```

---

### 5. POST /kyc/submit

Submit the completed KYC profile for verification. This endpoint marks the KYC as ready for review.

#### Request

```
POST /api/v1/auth/kyc/submit
Authorization: Bearer <jwt_token>
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "KYC submitted",
  "data": {
    "status": "PENDING",
    "completedSteps": [5]
  },
  "timestamp": "2026-05-09T10:45:00Z"
}
```

#### Status Codes

| Code | Description |
|------|-------------|
| 200 | KYC submitted successfully |
| 400 | KYC not ready for submission |
| 401 | Unauthorized (missing or invalid token) |

#### Example cURL

```bash
curl -X POST http://localhost:8080/api/v1/auth/kyc/submit \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## KYC Completion Flow

The recommended order of API calls to complete KYC:

1. **Check Status** → `GET /kyc/status`
   - Verify current completion step

2. **Submit Personal Info** → `POST /kyc/personal-info`
   - Required: firstName, lastName, gender, dateOfBirth, maritalStatus, nidaNumber, physicalAddress

3. **Submit Employment Info** → `POST /kyc/employment-info`
   - Required: employmentStatus, numberOfDependants
   - Conditional fields based on employment status

4. **Submit Financial Details** → `POST /kyc/financial-details`
   - Required: bankId, branchId, accountNumber, sourceOfIncome, incomeRange

5. **Submit KYC** → `POST /kyc/submit`
   - Marks profile as ready for review

---

## Enums and Valid Values

### Gender
- `MALE`
- `FEMALE`

### Marital Status
- `SINGLE`
- `MARRIED`
- `DIVORCED`
- `WIDOWED`

### Employment Status
- `EMPLOYED`
- `SELF_EMPLOYED`
- `UNEMPLOYED`

### Income Source
- `SALARY`
- `BUSINESS`
- `INVESTMENT`
- `OTHER`

### KYC Status
- `PENDING` - Submitted, awaiting review
- `APPROVED` - Verified and approved
- `REJECTED` - Did not meet requirements

---

## Authentication

All endpoints require a valid JWT token. Obtain a token by:

1. **Request OTP**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/otp/request \
     -H "Content-Type: application/json" \
     -d '{"phoneNumber":"+255798765432"}'
   ```

2. **Verify OTP and Get Token**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/otp/verify \
     -H "Content-Type: application/json" \
     -d '{"phoneNumber":"+255798765432","otp":"271874"}'
   ```

3. **Use Token in KYC Requests**
   ```bash
   curl -X GET http://localhost:8080/api/v1/auth/kyc/status \
     -H "Authorization: Bearer <access_token_from_step_2>"
   ```

---

## Error Handling

All error responses follow this format:

```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error message",
  "timestamp": "2026-05-09T10:50:00Z"
}
```

### Common Error Messages

| Error | Status | Cause |
|-------|--------|-------|
| User has no KYC profile | 400 | User hasn't initiated KYC |
| Validation failed | 400 | Missing or invalid required fields |
| Invalid date format | 400 | dateOfBirth not in YYYY-MM-DD format |
| Unauthorized | 401 | Missing or expired JWT token |

---

## Rate Limiting

No specific rate limits are enforced on KYC endpoints. However, excessive requests may be throttled.

---

## Notes

- All timestamps are in ISO 8601 format (UTC)
- Phone numbers must be in international format (e.g., `+255798765432`)
- All string inputs are trimmed of leading/trailing whitespace
- The KYC profile is created automatically on first registration
- Each step builds on previous steps; ensure all required fields are completed before submission
