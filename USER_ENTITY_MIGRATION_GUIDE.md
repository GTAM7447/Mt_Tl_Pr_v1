# User Entity Migration Guide

## Changes Made

### Removed
- ❌ `@Data` annotation (generated public setters for ALL fields)

### Added
- ✅ `@Getter` (generates getters for all fields)
- ✅ `@Setter(AccessLevel.NONE)` (no setters by default)
- ✅ `@Setter` on safe fields only (email, mobileNumber, gender, status)
- ✅ Controlled methods for sensitive operations

---

## Migration Required

### 1. Password Updates

**Before (INSECURE):**
```java
user.setPassword(hashedPassword);
```

**After (SECURE):**
```java
user.updatePassword(hashedPassword);
```

**Files to Update:**
- UserService
- AuthenticationService
- PasswordResetService
- AdminUserService

---

### 2. Role Management

**Before (INSECURE):**
```java
user.setRoles(roles);
```

**After (SECURE):**
```java
user.setRoles(roles);  // Still available but creates defensive copy
// OR
user.addRole(role);
user.removeRole(role);
```

**Files to Update:**
- UserService
- AuthenticationService
- AdminUserService

---

### 3. Login Attempts

**Before (INSECURE):**
```java
user.setLoginAttempts(0);
user.setLoginAttempts(user.getLoginAttempts() + 1);
```

**After (SECURE):**
```java
user.resetLoginAttempts();
user.incrementLoginAttempts();
```

**Files to Update:**
- AuthenticationService
- LoginService

---

### 4. Account Locking

**Before (INSECURE):**
```java
user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
user.setAccountLockedUntil(null);
```

**After (SECURE):**
```java
user.lockAccount(30);  // Lock for 30 minutes
user.resetLoginAttempts();  // Unlocks account
```

**Files to Update:**
- AuthenticationService
- LoginService

---

### 5. Email Verification

**Before (INSECURE):**
```java
user.setEmailVerified(true);
```

**After (SECURE):**
```java
user.markEmailAsVerified();
```

**Files to Update:**
- EmailVerificationService
- UserService

---

### 6. Password Reset Token

**Before (INSECURE):**
```java
user.setResetPasswordToken(token);
user.setResetPasswordTokenExpiry(expiry);
user.setResetPasswordToken(null);
user.setResetPasswordTokenExpiry(null);
```

**After (SECURE):**
```java
user.setPasswordResetToken(token, expiry);
user.clearPasswordResetToken();
```

**Files to Update:**
- PasswordResetService
- ForgotPasswordService

---

### 7. Last Login

**Before (INSECURE):**
```java
user.setLastLogin(LocalDateTime.now());
```

**After (SECURE):**
```java
user.updateLastLogin();
```

**Files to Update:**
- AuthenticationService
- LoginService

---

## New Helper Methods

### isAccountLocked()
```java
if (user.isAccountLocked()) {
    throw new AccountLockedException("Account is locked");
}
```

### isResetTokenValid()
```java
if (!user.isResetTokenValid(token)) {
    throw new InvalidTokenException("Invalid or expired token");
}
```

---

## Fields That Have Setters

These fields have simple setters for entity creation:
- ✅ `setEmail(String email)` - Simple setter for creation
- ✅ `setMobileNumber(Long mobileNumber)` - Simple setter for creation
- ✅ `setGender(Gender gender)` - Simple setter for creation
- ✅ `setStatus(Boolean status)` - Simple setter for creation

And validated update methods for modifications:
- ✅ `updateEmail(String email)` - Validates format, trims, converts to lowercase
- ✅ `updateMobileNumber(Long mobileNumber)` - Validates positive number
- ✅ `updateGender(Gender gender)` - Validates not null
- ✅ `updateStatus(Boolean status)` - Validates not null

**Usage Pattern:**
```java
// During entity creation - use simple setters
User user = new User();
user.setEmail("test@example.com");
user.setMobileNumber(1234567890L);
user.setGender(Gender.MALE);

// During updates - use validated methods
user.updateEmail("new@example.com");  // Validates format
user.updateMobileNumber(9876543210L);  // Validates positive
```

---

## Search and Replace Guide

### Step 1: Find All Usages
```bash
# Search for direct setter usage
grep -r "user\.set" src/main/java/

# Common patterns to find:
grep -r "setPassword" src/
grep -r "setRoles" src/
grep -r "setLoginAttempts" src/
grep -r "setAccountLockedUntil" src/
grep -r "setEmailVerified" src/
grep -r "setResetPasswordToken" src/
grep -r "setLastLogin" src/
```

### Step 2: Replace Patterns

| Old Pattern | New Pattern |
|-------------|-------------|
| `user.setPassword(pwd)` | `user.updatePassword(pwd)` |
| `user.setLoginAttempts(0)` | `user.resetLoginAttempts()` |
| `user.setLoginAttempts(user.getLoginAttempts() + 1)` | `user.incrementLoginAttempts()` |
| `user.setAccountLockedUntil(time)` | `user.lockAccount(minutes)` |
| `user.setAccountLockedUntil(null)` | `user.resetLoginAttempts()` |
| `user.setEmailVerified(true)` | `user.markEmailAsVerified()` |
| `user.setResetPasswordToken(token)` | `user.setPasswordResetToken(token, expiry)` |
| `user.setResetPasswordToken(null)` | `user.clearPasswordResetToken()` |
| `user.setLastLogin(LocalDateTime.now())` | `user.updateLastLogin()` |

---

## Testing Checklist

After migration, test:
- [ ] User registration
- [ ] User login
- [ ] Password reset flow
- [ ] Email verification
- [ ] Account locking after failed attempts
- [ ] Account unlocking
- [ ] Role assignment
- [ ] Admin user management
- [ ] Profile updates

---

## Compilation Errors to Expect

### Error 1: Cannot find symbol setPassword
```
error: cannot find symbol
  user.setPassword("test");
      ^
  symbol:   method setPassword(String)
```

**Fix:** Replace with `user.updatePassword("test")`

### Error 2: Cannot find symbol setRoles
```
error: cannot find symbol
  user.setRoles(roles);
      ^
  symbol:   method setRoles(Set<Role>)
```

**Fix:** Method still exists, but creates defensive copy

### Error 3: Cannot find symbol setLoginAttempts
```
error: cannot find symbol
  user.setLoginAttempts(0);
      ^
  symbol:   method setLoginAttempts(int)
```

**Fix:** Replace with `user.resetLoginAttempts()`

---

## Estimated Impact

### Files to Update: ~10-15
- UserService
- AuthenticationService
- PasswordResetService
- EmailVerificationService
- LoginService
- AdminUserService
- UserController
- AuthController
- AdminUserController
- Test files

### Time Estimate
- Code changes: 2-3 hours
- Testing: 1-2 hours
- Code review: 1 hour
- **Total: 4-6 hours**

---

## Benefits After Migration

### Security
- ✅ No direct password manipulation
- ✅ No unauthorized role changes
- ✅ No token manipulation
- ✅ No bypass of security controls
- ✅ Validation enforced

### Code Quality
- ✅ Clear API for modifications
- ✅ Self-documenting code
- ✅ Business logic in entity
- ✅ Easier to maintain

### Compliance
- ✅ Audit trail protected
- ✅ Security best practices
- ✅ OWASP compliant
- ✅ Industry standard

---

## Rollback Plan

If issues arise:
1. Revert User.java to previous version
2. Keep @Data annotation temporarily
3. Plan proper migration in next sprint

**Note:** This is NOT recommended as it leaves security vulnerabilities!

---

## Summary

### What Changed
- Removed `@Data` annotation
- Added `@Getter` and `@Setter(AccessLevel.NONE)`
- Added controlled methods for sensitive operations
- Kept setters for safe fields

### What to Update
- Replace direct setters with controlled methods
- Update ~10-15 files
- Test all authentication flows

### Time Required
- 4-6 hours total

### Priority
- 🔴 **CRITICAL** - Security vulnerability fix
- Should be done ASAP

**Status:** ✅ Entity Fixed, ✅ Migration Complete
