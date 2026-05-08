# Auth Service API Documentation

## Overview

This service is a Spring Boot auth and KYC backend for the Pesa Lending platform. It exposes OTP login, JWT refresh, user KYC flows, document upload, and admin operations.

### Base URL

Local development:

`http://localhost:8081/api/auth`

The application runs with the context path `/api/auth` and port `8081`.

### Cross-Origin Access

Allowed browser origins:

- `http://localhost:3000`
- `http://localhost:3001`

### Transport and Content Types

- JSON endpoints use `Content-Type: application/json`
- File upload uses `multipart/form-data`
- Responses are JSON unless the request is invalid at the framework level

## Authentication

The JWT filter reads the access token from the `Authorization` header.

Required format:

`Authorization: Bearer <access-token>`

Token details:

- Access tokens are valid for 24 hours
- Refresh tokens are valid for 48 hours
- The filter stores the numeric `userId` in `Authentication.getDetails()`
- Controllers that call `authentication.getDetails()` expect that JWT-derived user or admin id

### When to Send the Bearer Token

Send the bearer token for endpoints that rely on the authenticated identity, especially:

- `/api/users/**`
- `/kyc/**`
- `/admin/users/{userId}/kyc/decide`

The security configuration currently permits several routes without blocking them at the filter chain level, but the controllers still expect a valid authentication context for the flows above.

## Common Response Envelopes

The service uses the following documented response shapes.

### Success

Used by successful responses across the service.

```json
{
	"success": true,
	"message": "Success message",
	"data": null,
	"timestamp": "2026-05-08T12:00:00Z"
}
```

### Error

Used by error responses across the service.

```json
{
	"error": "Developer message",
	"message": "User message",
	"timestamp": "2026-05-08T12:00:00Z"
}
```

Validation failures, authentication failures, and server errors should be documented with the error shape above.

## Common Error Codes

- `400 Bad Request` - invalid input, missing profile, invalid step, bad OTP, upload failure
- `401 Unauthorized` - invalid or expired refresh token, auth failures from the security layer
- `403 Forbidden` - disabled admin account or access denied
- `404 Not Found` - missing resources
- `409 Conflict` - data integrity conflicts
- `500 Internal Server Error` - unhandled server errors

## OTP APIs

### Request OTP

`POST /otp/request`

Auth: none

Request body:

```json
{
	"phoneNumber": "255712345678"
}
```

Validation rules:

- `phoneNumber` is required
- Must match `^\\d{10,12}$`

Success response:

```json
{
	"success": true,
	"message": "OTP sent",
	"data": {
		"otp": "123456"
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

Notes:

- The OTP is currently returned in the response and also logged, which is suitable for testing but not production-safe
- SMS sending is still a TODO in the code

### Verify OTP

`POST /otp/verify`

Auth: none

Request body:

```json
{
	"phoneNumber": "255712345678",
	"otp": "123456",
	"type": "LOGIN"
}
```

Validation rules:

- `phoneNumber` is required and must match `^\\d{10,12}$`
- `otp` is required and must be exactly 6 digits
- `type` is required

Supported `OtpType` values:

- `LOGIN`
- `TRANSACTION`

Current behavior:

- `LOGIN` is implemented
- `TRANSACTION` is not handled yet and returns an error

Success response for login:

```json
{
	"success": true,
	"message": "OTP verified",
	"data": {
		"token": "<access-token>",
		"refreshToken": "<refresh-token>",
		"user": {
			"userId": 12,
			"phoneNumber": "255712345678"
		}
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

## Token APIs

### Refresh Token

Two equivalent endpoints are exposed:

- `POST /refresh`
- `POST /token/refresh`

Auth: none

Request body:

```json
{
	"refreshToken": "<refresh-token>"
}
```

Success response:

```json
{
	"success": true,
	"message": "Token refreshed",
	"data": {
		"accessToken": "<new-access-token>",
		"refreshToken": "<new-refresh-token>"
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

Failure response:

```json
{
	"error": "Invalid or expired refresh token",
	"message": "Invalid or expired refresh token",
	"timestamp": "2026-05-08T12:00:00Z"
}
```

## User KYC APIs

These routes are under `/api/users` and use the simple response envelope.

### Get My KYC Profile

`GET /api/users/me/kyc`

Auth: `Authorization: Bearer <access-token>` recommended and expected by the controller

Success response:

```json
{
	"success": true,
	"message": "KYC profile retrieved",
	"data": {
		"id": 1,
		"userId": 12,
		"fullName": "Jane Doe",
		"dateOfBirth": "1995-01-10",
		"gender": "FEMALE",
		"idType": "NATIONAL_ID",
		"idNumber": "12345678",
		"residenceAddress": "Dar es Salaam",
		"businessDetails": "Optional business details",
		"maritalStatus": "SINGLE",
		"numberOfDependents": 0,
		"status": "PENDING",
		"rejectionReason": null,
		"completionStep": "PERSONAL_INFO",
		"createdAt": "2026-05-08T12:00:00",
		"updatedAt": "2026-05-08T12:00:00",
		"approvedAt": null
	}
}
```

### Submit My KYC

`POST /api/users/me/kyc`

Auth: `Authorization: Bearer <access-token>`

Request body:

```json
{
	"fullName": "Jane Doe",
	"dateOfBirth": "1995-01-10",
	"gender": "FEMALE",
	"idType": "NATIONAL_ID",
	"idNumber": "12345678",
	"residenceAddress": "Dar es Salaam",
	"businessDetails": "Optional business details",
	"maritalStatus": "SINGLE",
	"numberOfDependents": 0
}
```

Validation rules:

- `fullName` is required
- `dateOfBirth` is required
- `gender` is required
- `idType` is required
- `idNumber` is required
- `residenceAddress` is required
- `maritalStatus` is required

Success response:

```json
{
	"success": true,
	"message": "KYC submitted successfully",
	"data": {
		"id": 1,
		"userId": 12,
		"status": "PENDING"
	}
}
```

### Save a KYC Step

`POST /api/users/me/kyc/step/{step}`

Auth: `Authorization: Bearer <access-token>`

Path parameter:

- `step` is a number from `1` to `4`

Step mapping:

- `1` - `PERSONAL_INFO`
- `2` - `EMPLOYMENT_INFO`
- `3` - `FINANCIAL_INFO`
- `4` - `APPROVED`

Request body:

```json
{
	"fullName": "Jane Doe",
	"dateOfBirth": "1995-01-10",
	"gender": "FEMALE",
	"idType": "NATIONAL_ID",
	"idNumber": "12345678",
	"residenceAddress": "Dar es Salaam",
	"businessDetails": "Optional business details",
	"maritalStatus": "SINGLE",
	"numberOfDependents": 0
}
```

Notes:

- The path parameter sets the active step
- If step `4` is used, the profile is marked approved

### Get My KYC Status

`GET /api/users/me/kyc/status`

Auth: `Authorization: Bearer <access-token>`

Success response:

```json
{
	"success": true,
	"message": "KYC status retrieved",
	"data": "PENDING"
}
```

This endpoint returns the profile status string, not a boolean.

## KYC APIs

These are the alternate KYC routes exposed under `/kyc`.

### Get KYC Profile

`GET /kyc`

Auth: `Authorization: Bearer <access-token>` recommended and expected by the controller

Response shape: same as `/api/users/me/kyc`

### Save KYC Step

`POST /kyc/step/{step}`

Auth: `Authorization: Bearer <access-token>`

Behavior is the same as `/api/users/me/kyc/step/{step}`.

### Upload KYC Document

`POST /kyc/documents/{documentType}`

Auth: `Authorization: Bearer <access-token>`

Content type: `multipart/form-data`

Path parameter:

- `documentType` must match the enum value, typically `SELF_PIC` or `ID_SCAN`

Form data:

- `file`: the uploaded file

Example request:

```http
POST /api/auth/kyc/documents/SELF_PIC
Content-Type: multipart/form-data
Authorization: Bearer <access-token>

file=<binary>
```

Validation and limits:

- File cannot be empty
- Max upload size is 10 MB

Success response:

```json
{
	"success": true,
	"message": "Document uploaded",
	"data": {
		"id": 1,
		"kycProfileId": 1,
		"documentType": "SELF_PIC",
		"fileName": "uuid_filename.jpg",
		"fileUrl": "/documents/uuid_filename.jpg",
		"fileSizeBytes": 123456,
		"mimeType": "image/jpeg",
		"uploadedAt": "2026-05-08T12:00:00"
	}
}
```

### List KYC Documents

`GET /kyc/documents`

Auth: `Authorization: Bearer <access-token>`

Success response:

```json
{
	"success": true,
	"message": "Documents retrieved",
	"data": [
		{
			"id": 1,
			"kycProfileId": 1,
			"documentType": "SELF_PIC",
			"fileName": "uuid_filename.jpg",
			"fileUrl": "/documents/uuid_filename.jpg",
			"fileSizeBytes": 123456,
			"mimeType": "image/jpeg",
			"uploadedAt": "2026-05-08T12:00:00"
		}
	]
}
```

### Check Whether KYC Is Complete

`GET /kyc/status`

Auth: `Authorization: Bearer <access-token>`

Success response:

```json
{
	"success": true,
	"message": "KYC status",
	"data": true
}
```

This endpoint returns a boolean indicating whether the profile is approved and complete.

## Admin APIs

These routes use the standard response envelope.

### Admin Login

`POST /admin/login`

Auth: none

Request body:

```json
{
	"email": "admin@example.com",
	"password": "secret"
}
```

Success response:

```json
{
	"success": true,
	"message": "Login successful",
	"data": {
		"accessToken": "<admin-access-token>",
		"forcePasswordChange": false,
		"admin": {
			"email": "admin@example.com",
			"role": "SUPER_ADMIN"
		}
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

Possible failures:

- invalid credentials -> `401`
- disabled account -> `403`

### Admin Dashboard

`GET /admin/dashboard`

Auth: none enforced by the current security configuration

Success response:

```json
{
	"success": true,
	"message": "Dashboard data",
	"data": {
		"totalUsers": 100,
		"totalLoans": 25,
		"portfolioOutstanding": 1500000.00,
		"totalCollected": 250000.00,
		"pendingKyc": 12
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

### List Users

`GET /admin/users`

Auth: none enforced by the current security configuration

Query parameters:

- Spring Data pagination parameters are supported
- Default sort is `id DESC`

Success response:

```json
{
	"success": true,
	"message": "Users retrieved",
	"data": {
		"content": [
			{
				"id": 12,
				"phoneNumber": "255712345678",
				"fullName": "Jane Doe",
				"status": "ACTIVE",
				"createdAt": "2026-05-08T12:00:00",
				"kycStatus": "PENDING",
				"kycCompletionStep": 1,
				"kycProfileId": 1,
				"creditGrade": "A",
				"loanLimit": 500000.00,
				"eligible": true
			}
		],
		"pageable": {},
		"totalElements": 1,
		"totalPages": 1,
		"size": 20,
		"number": 0
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

### Decide KYC

`POST /admin/users/{userId}/kyc/decide`

Auth: the controller expects an authenticated admin id in the JWT context

Path parameter:

- `userId` is the numeric user id

Request body:

```json
{
	"action": "APPROVE",
	"reason": "All documents are valid"
}
```

Supported actions:

- `APPROVE`
- `REJECT`

Rules:

- `REJECT` should include a `reason`
- Any other action value returns a bad request

Success response:

```json
{
	"success": true,
	"message": "KYC decision applied",
	"data": {
		"id": 1,
		"userId": 12,
		"status": "APPROVED",
		"approvedAt": "2026-05-08T12:00:00"
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

### Set Credit Score

`POST /admin/users/{userId}/credit-score`

Auth: none enforced by the current security configuration

Request body:

```json
{
	"score": 720.5,
	"grade": "A",
	"loanLimit": 500000.0,
	"eligible": true
}
```

Success response:

```json
{
	"success": true,
	"message": "Credit score updated",
	"data": {
		"id": 1,
		"userId": 12,
		"score": 720.5,
		"grade": "A",
		"loanLimit": 500000.0,
		"eligible": true,
		"evaluatedAt": "2026-05-08T12:00:00"
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

### Audit Logs

`GET /admin/audit-logs`

Auth: none enforced by the current security configuration

Pagination defaults:

- Default size: `50`
- Default sort: `createdAt DESC`

Success response:

```json
{
	"success": true,
	"message": "Audit logs retrieved",
	"data": {
		"content": [
			{
				"id": 1,
				"userId": 1,
				"action": "KYC_APPROVE",
				"entityType": "KYC_PROFILE",
				"entityId": 1,
				"oldValues": "{\"status\":\"PENDING\"}",
				"newValues": "{\"status\":\"APPROVED\"}",
				"ipAddress": null,
				"createdAt": "2026-05-08T12:00:00"
			}
		],
		"totalElements": 1,
		"totalPages": 1
	},
	"timestamp": "2026-05-08T12:00:00Z"
}
```

## DTO and Model Reference

### OTP Types

- `LOGIN`
- `TRANSACTION`

### KYC Step Values

- `1` - `PERSONAL_INFO`
- `2` - `EMPLOYMENT_INFO`
- `3` - `FINANCIAL_INFO`
- `4` - `APPROVED`

### KYC Enums

Gender:

- `MALE`
- `FEMALE`
- `OTHER`

Marital status:

- `SINGLE`
- `MARRIED`
- `DIVORCED`
- `WIDOWED`

ID type:

- `PASSPORT`
- `NATIONAL_ID`
- `DRIVER_LICENSE`

Document type:

- `SELF_PIC`
- `ID_SCAN`

### Main Request Bodies

`OtpRequestBody`

```json
{
	"phoneNumber": "255712345678"
}
```

`OtpVerifyBody`

```json
{
	"phoneNumber": "255712345678",
	"otp": "123456",
	"type": "LOGIN"
}
```

`TokenRefreshBody`

```json
{
	"refreshToken": "<refresh-token>"
}
```

`AdminLoginRequest`

```json
{
	"email": "admin@example.com",
	"password": "secret"
}
```

`KycDecideRequest`

```json
{
	"action": "APPROVE",
	"reason": "All documents are valid"
}
```

`CreditScoreRequest`

```json
{
	"score": 720.5,
	"grade": "A",
	"loanLimit": 500000.0,
	"eligible": true
}
```

`KycRequest`

```json
{
	"fullName": "Jane Doe",
	"dateOfBirth": "1995-01-10",
	"gender": "FEMALE",
	"idType": "NATIONAL_ID",
	"idNumber": "12345678",
	"residenceAddress": "Dar es Salaam",
	"businessDetails": "Optional business details",
	"maritalStatus": "SINGLE",
	"numberOfDependents": 0
}
```

## Implementation Notes

- The OTP verification flow currently only handles `LOGIN`
- The service returns the raw OTP in the request response for testing convenience
- KYC responses are available through both `/kyc` and `/api/users/me/kyc`
- The two response wrapper classes in the codebase are not identical, so clients should expect slightly different envelopes depending on the endpoint
- Multipart uploads are written to `/tmp/pesa-documents` on the local filesystem

## Quick Summary

- Base URL: `http://localhost:8081/api/auth`
- Auth header: `Authorization: Bearer <access-token>`
- Standard success envelope: `success`, `message`, `data`, `errors`, `timestamp`
- Simple success envelope: `success`, `message`, `data`
- OTP request/verify and refresh-token endpoints are public
- KYC and admin actions are implemented, but several of them still rely on a JWT-authenticated identity inside the controller
