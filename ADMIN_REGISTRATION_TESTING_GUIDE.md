# Admin Registration Testing Guide

## Quick Test

### 1. Start the Application
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. Login as Admin
```bash
POST http://localhost:8080/jwt/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "your_admin_password"
}
```

Save the JWT token from the response.

### 3. Test Complete Registration (All Sections)
```bash
POST http://localhost:8080/api/v1/admin/registration/complete
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "email": "testuser@example.com",
  "password": "SecurePass123!",
  "mobileNumber": "9876543210",
  "gender": "MALE",
  "skipEmailVerification": true,
  "autoActivate": true,
  "adminNotes": "Test registration",
  "profileDetails": {
    "firstName": "Test",
    "lastName": "User",
    "dateOfBirth": "1995-01-15",
    "age": 29,
    "height": 175,
    "weight": 70,
    "maritalStatus": "NEVER_MARRIED",
    "religion": "Hindu",
    "caste": "General",
    "motherTongue": "Hindi",
    "country": "India",
    "state": "Maharashtra",
    "district": "Mumbai",
    "aboutMe": "Test profile"
  },
  "educationDetails": {
    "highestEducation": "BACHELORS",
    "educationDetails": "B.Tech Computer Science",
    "occupation": "Software Engineer",
    "annualIncome": "800000"
  },
  "contactDetails": {
    "alternateEmail": "alternate@example.com",
    "alternatePhone": "9876543211",
    "permanentAddress": "123 Test Street",
    "currentAddress": "456 Current Street"
  },
  "familyBackground": {
    "fatherName": "Father Name",
    "fatherOccupation": "Business",
    "motherName": "Mother Name",
    "motherOccupation": "Homemaker",
    "numberOfBrothers": 1,
    "numberOfSisters": 1
  },
  "partnerPreferences": {
    "minAge": 25,
    "maxAge": 32,
    "minHeight": 160,
    "maxHeight": 180,
    "maritalStatus": "NEVER_MARRIED",
    "religion": "Hindu",
    "caste": "Any",
    "education": "BACHELORS",
    "occupation": "Any"
  },
  "horoscopeDetails": {
    "birthTime": "10:30:00",
    "birthPlace": "Mumbai",
    "rashi": "Aries",
    "nakshatra": "Ashwini",
    "gotra": "Test Gotra"
  }
}
```

### 4. Expected Response (201 Created)
```json
{
  "userId": 10001,
  "email": "testuser@example.com",
  "mobileNumber": "9876543210",
  "gender": "MALE",
  "accountActive": true,
  "emailVerified": true,
  "createdByAdmin": "admin@example.com",
  "registrationTimestamp": "2026-01-19T...",
  "adminNotes": "Test registration",
  "profileId": 5001,
  "horoscopeDetailsId": 3001,
  "educationDetailsId": 4001,
  "familyBackgroundId": 2001,
  "contactDetailsId": 6001,
  "partnerPreferencesId": 7001,
  "completeProfileId": 8001,
  "createdSections": [
    "Profile Details",
    "Horoscope Details",
    "Education & Profession",
    "Family Background",
    "Contact Details",
    "Partner Preferences"
  ],
  "missingSections": [],
  "completionPercentage": 85,
  "profileComplete": false,
  "profileQuality": "VERY_GOOD",
  "message": "User registration completed successfully with 6 profile sections"
}
```

### 5. Test Partial Registration (Only Basic Info)
```bash
POST http://localhost:8080/api/v1/admin/registration/complete
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "email": "partial@example.com",
  "password": "SecurePass123!",
  "mobileNumber": "9876543212",
  "gender": "FEMALE",
  "skipEmailVerification": true,
  "adminNotes": "Partial registration test"
}
```

### 6. Expected Response (201 Created)
```json
{
  "userId": 10002,
  "email": "partial@example.com",
  "mobileNumber": "9876543212",
  "gender": "FEMALE",
  "accountActive": true,
  "emailVerified": true,
  "createdByAdmin": "admin@example.com",
  "registrationTimestamp": "2026-01-19T...",
  "adminNotes": "Partial registration test",
  "completeProfileId": 8002,
  "createdSections": [],
  "missingSections": [
    "Profile Details",
    "Horoscope Details",
    "Education & Profession",
    "Family Background",
    "Contact Details",
    "Partner Preferences",
    "Documents"
  ],
  "completionPercentage": 0,
  "profileComplete": false,
  "profileQuality": "POOR",
  "message": "User registration completed successfully. No profile sections created."
}
```

## Validation Tests

### Test 1: Duplicate Email
```bash
POST http://localhost:8080/api/v1/admin/registration/complete
{
  "email": "testuser@example.com",  // Already exists
  "password": "SecurePass123!",
  "mobileNumber": "9999999999",
  "gender": "MALE"
}
```

**Expected**: 400 Bad Request
```json
{
  "status": 400,
  "errorCode": "APPLICATION_ERROR",
  "message": "Email is already registered: testuser@example.com"
}
```

### Test 2: Duplicate Mobile Number
```bash
POST http://localhost:8080/api/v1/admin/registration/complete
{
  "email": "newuser@example.com",
  "password": "SecurePass123!",
  "mobileNumber": "9876543210",  // Already exists
  "gender": "MALE"
}
```

**Expected**: 400 Bad Request
```json
{
  "status": 400,
  "errorCode": "APPLICATION_ERROR",
  "message": "Mobile number is already registered: 9876543210"
}
```

### Test 3: Invalid Email Format
```bash
POST http://localhost:8080/api/v1/admin/registration/complete
{
  "email": "invalid-email",
  "password": "SecurePass123!",
  "mobileNumber": "9876543213",
  "gender": "MALE"
}
```

**Expected**: 400 Bad Request
```json
{
  "status": 400,
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": "{email=Invalid email format}"
}
```

### Test 4: Invalid Mobile Number
```bash
POST http://localhost:8080/api/v1/admin/registration/complete
{
  "email": "test@example.com",
  "password": "SecurePass123!",
  "mobileNumber": "123",  // Too short
  "gender": "MALE"
}
```

**Expected**: 400 Bad Request
```json
{
  "status": 400,
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": "{mobileNumber=Mobile number must be 10 digits}"
}
```

### Test 5: Non-Admin User
Login as regular user and try to register:

**Expected**: 403 Forbidden
```json
{
  "status": 403,
  "errorCode": "ACCESS_DENIED",
  "message": "Admin role required to access administrative resources."
}
```

## Verification Steps

### 1. Check Database
```sql
-- Verify user created
SELECT * FROM users WHERE email = 'testuser@example.com';

-- Verify complete profile created
SELECT * FROM complete_profile WHERE user_id = 10001;

-- Verify profile sections
SELECT * FROM user_profile WHERE user_id = 10001;
SELECT * FROM education_and_profession WHERE user_id = 10001;
SELECT * FROM contact_details WHERE user_id = 10001;
SELECT * FROM family_background WHERE user_id = 10001;
SELECT * FROM partner_preference WHERE user_id = 10001;
SELECT * FROM horoscope_details WHERE user_id = 10001;

-- Check completion metrics
SELECT 
    user_id,
    profile_completed,
    completion_percentage,
    completeness_score,
    profile_quality,
    missing_sections_count
FROM complete_profile 
WHERE user_id = 10001;
```

### 2. Check Logs
Look for these log entries in order:
```
INFO  - Starting complete user registration for email: testuser@example.com
INFO  - Registration initiated by admin: admin@example.com
INFO  - User account created with ID: 10001
INFO  - User account created successfully with ID: 10001
INFO  - Profile Details created for user
INFO  - Horoscope Details created for user
INFO  - Education & Profession created for user
INFO  - Family Background created for user
INFO  - Contact Details created for user
INFO  - Partner Preferences created for user
INFO  - Profile sections created: [Profile Details, Horoscope Details, ...]
DEBUG - Starting profile completion calculation for user ID: 10001
DEBUG - Missing sections updated for user ID: 10001
DEBUG - Profile recalculated for user ID: 10001
DEBUG - Response metrics updated for user ID: 10001
INFO  - Profile completion calculated: 85%
INFO  - Complete user registration finished for user ID: 10001 with 85% completion
```

### 3. Test Admin User List
```bash
GET http://localhost:8080/api/v1/admin/users?page=0&size=10
Authorization: Bearer YOUR_JWT_TOKEN
```

Verify the newly created user appears in the list with correct data.

## Performance Benchmarks

Expected response times (development environment):
- Complete registration (all sections): 1-2 seconds
- Partial registration (user only): 500-700ms
- Validation errors: < 100ms

## Troubleshooting

### Issue: 500 Internal Server Error
**Check**:
1. Application logs for stack traces
2. Database connection
3. All required services are running

### Issue: Completion percentage is 0
**Check**:
1. Profile sections were actually created (check database)
2. CompleteProfile relationships are properly set
3. Logs show "Profile recalculated for user ID: X"

### Issue: Missing sections not accurate
**Check**:
1. CompleteProfile entity has all relationships
2. Calculation logic in ProfileCompletionCalculator
3. Database foreign keys are correct

### Issue: Device fingerprint mismatch
**Solution**: Already fixed in development environment
- Device fingerprinting is disabled in dev: `jwt.device-fingerprinting-enabled=false`
- If testing in production, ensure consistent User-Agent between login and API calls
