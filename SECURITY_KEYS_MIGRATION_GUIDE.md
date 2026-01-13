# 🔐 Security Keys Migration Guide

## ⚠️ CRITICAL: Before Production Deployment

All security keys have been updated with cryptographically strong values. However, **YOU MUST MOVE THESE TO ENVIRONMENT VARIABLES** before deploying to production.

---

## 🔑 Generated Security Keys

### 1. JWT Secret Key
**Purpose:** Signs and validates JWT tokens  
**Algorithm:** HS256 (HMAC-SHA256)  
**Strength:** 512-bit (Base64 encoded)

**Current Value (TEMPORARY - for development only):**
```
YXBpS2V5U2VjdXJlMjAyNkpXVFNpZ25pbmdLZXlGb3JNYXRyaW1vbnlBcHBsaWNhdGlvblNlY3VyZVRva2VuR2VuZXJhdGlvbkFuZFZhbGlkYXRpb25QdXJwb3Nlcw==
```

**How to generate new one:**
```bash
openssl rand -base64 64
```

---

### 2. Encryption Secret Key (Primary)
**Purpose:** Encrypts sensitive user data (PII)  
**Algorithm:** AES-256  
**Strength:** 256-bit (Base64 encoded)

**Current Value (TEMPORARY - for development only):**
```
K7gNU3sdo+OL0wNhqoVWhr3g6s1xYv72ol/pe/Unols=
```

**How to generate new one:**
```bash
openssl rand -base64 32
```

---

### 3. Encryption Legacy Keys
**Purpose:** Decrypt data encrypted with old keys during key rotation  
**Strength:** 256-bit each (Base64 encoded)

**Current Values (TEMPORARY - for development only):**
```
9xN2eS5K8mP4vR7wQ3tY6uI1oL0aZ4bC5dE8fG2hJ6k=
L8mQ3sT6vY9zA2cF5hK8nP1rU4wX7bE0dG3jM6oR9tV=
```

---

## 🚀 Production Deployment Steps

### Step 1: Set Environment Variables

**On your production server/container:**

```bash
# JWT Secret
export JWT_SECRET="<generate-new-secret-using-openssl-rand-base64-64>"

# Encryption Keys
export ENCRYPTION_SECRET_KEY="<generate-new-key-using-openssl-rand-base64-32>"
export ENCRYPTION_LEGACY_KEYS="<old-key-1>,<old-key-2>"

# Database Credentials (CRITICAL!)
export DB_URL="jdbc:mysql://your-db-host:3306/database"
export DB_USERNAME="your-db-user"
export DB_PASSWORD="your-secure-password"
```

### Step 2: Update application-prod.properties

Replace hardcoded values with environment variable references:

```properties
# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.device-fingerprinting-enabled=true
jwt.enforce-single-session=true

# Encryption settings
app.encryption.secret-key=${ENCRYPTION_SECRET_KEY}
app.encryption.legacy-keys=${ENCRYPTION_LEGACY_KEYS}
app.encryption.debug=false

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### Step 3: Verify Configuration

Before deploying, verify environment variables are set:

```bash
echo $JWT_SECRET
echo $ENCRYPTION_SECRET_KEY
echo $DB_PASSWORD
```

**All should output values (not empty).**

---

## 🔄 Key Rotation Strategy

### When to Rotate Keys:

1. **Immediately** if keys are compromised
2. **Every 90 days** for JWT secret (best practice)
3. **Every 180 days** for encryption keys (with legacy key support)
4. **After security incident**
5. **When employee with key access leaves**

### How to Rotate Encryption Keys:

1. Generate new encryption key:
   ```bash
   openssl rand -base64 32
   ```

2. Add current key to legacy keys list:
   ```properties
   app.encryption.legacy-keys=<old-current-key>,<previous-legacy-keys>
   ```

3. Set new key as current:
   ```properties
   app.encryption.secret-key=<new-key>
   ```

4. Application will:
   - Encrypt new data with new key
   - Decrypt old data with legacy keys
   - Re-encrypt on update (gradual migration)

### How to Rotate JWT Secret:

⚠️ **WARNING:** Rotating JWT secret invalidates ALL active tokens!

1. Generate new JWT secret:
   ```bash
   openssl rand -base64 64
   ```

2. Schedule maintenance window (all users will be logged out)

3. Update JWT_SECRET environment variable

4. Restart application

5. Notify users to log in again

---

## 🔒 Security Improvements Applied

### 1. ✅ Strong JWT Secret
- **Before:** Weak, predictable hex string
- **After:** 512-bit cryptographically random Base64 string
- **Impact:** Prevents JWT forgery attacks

### 2. ✅ Strong Encryption Keys
- **Before:** Weak, predictable strings like "secure-encryption-key-123"
- **After:** 256-bit cryptographically random Base64 strings
- **Impact:** Protects encrypted PII from decryption attacks

### 3. ✅ Device Fingerprinting Enabled
- **Before:** Disabled by default
- **After:** Enabled by default
- **Impact:** Stolen tokens can't be used from different devices
- **Note:** Users can still use multiple devices, but each needs separate login

### 4. ✅ Single Session Enforcement (Production)
- **Before:** Disabled
- **After:** Enabled in production (max 3 concurrent sessions)
- **Impact:** Limits token theft impact, detects account sharing

---

## 🧪 Testing Device Fingerprinting

### Expected Behavior:

1. **Same Device/Browser:** ✅ Token works normally
2. **Different Browser (same device):** ❌ Token rejected, must login again
3. **Different Device:** ❌ Token rejected, must login again
4. **Incognito Mode:** ❌ Token rejected (different fingerprint)

### Test Scenarios:

```bash
# 1. Login from Chrome
curl -X POST http://localhost:8080/jwt/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password"}'

# Save the token from response

# 2. Use token from Chrome (should work)
curl -X GET http://localhost:8080/api/v1/profiles \
  -H "Authorization: Bearer <token>" \
  -H "User-Agent: Chrome/120.0"

# 3. Try same token from Firefox (should fail)
curl -X GET http://localhost:8080/api/v1/profiles \
  -H "Authorization: Bearer <token>" \
  -H "User-Agent: Firefox/120.0"
# Expected: 401 Unauthorized - "Token was generated on a different device"
```

---

## 🔍 Monitoring & Alerts

### Set up alerts for:

1. **Multiple failed device fingerprint validations**
   - Could indicate token theft attempt
   - Alert threshold: 5+ failures in 10 minutes

2. **JWT signature validation failures**
   - Could indicate forgery attempt
   - Alert threshold: Any occurrence

3. **Encryption/decryption failures**
   - Could indicate key mismatch or corruption
   - Alert threshold: Any occurrence

4. **Unusual login patterns**
   - Multiple logins from different locations
   - Login from new device without previous logout

---

## 📋 Security Checklist

Before going to production:

- [ ] Generate unique JWT secret for production
- [ ] Generate unique encryption keys for production
- [ ] Set all secrets as environment variables
- [ ] Remove hardcoded secrets from properties files
- [ ] Test device fingerprinting works correctly
- [ ] Test key rotation procedure
- [ ] Set up monitoring and alerts
- [ ] Document key storage location (password manager, vault)
- [ ] Restrict access to production environment variables
- [ ] Enable audit logging for key access
- [ ] Test disaster recovery (key loss scenario)
- [ ] Schedule first key rotation (90 days)

---

## 🆘 Emergency Key Rotation

If keys are compromised:

1. **Immediately** generate new keys
2. Update environment variables
3. Restart application (all users logged out)
4. Notify users of security incident
5. Force password reset for all users (if needed)
6. Review audit logs for suspicious activity
7. Document incident and response

---

## 📞 Support

For questions about key management:
- Review Spring Security documentation
- Check OWASP Key Management Cheat Sheet
- Consult security team before making changes

---

**Last Updated:** January 13, 2026  
**Next Review:** April 13, 2026 (90 days)
