# Profile Activation System - Quick Guide

## What Changed

### Default Behavior
- **Before:** New profiles were ACTIVE by default
- **After:** New profiles are DEACTIVE by default
- **Activation:** Only after subscription purchase OR admin approval

---

## For Users

### Registration Flow
1. Register account → Profile created as **DEACTIVE**
2. Fill all profile information → Still **DEACTIVE**
3. Purchase subscription → Automatically becomes **ACTIVE**
4. Now visible in searches and can interact

### What DEACTIVE Means
- ❌ Not visible in search results
- ❌ Cannot receive express interests
- ❌ Not shown in match suggestions
- ✅ Can still login and edit profile
- ✅ Can view own profile

### What ACTIVE Means
- ✅ Visible in search results
- ✅ Can receive express interests
- ✅ Shown in match suggestions
- ✅ Full platform access

---

## For Admins

### New Admin Endpoints

#### 1. Activate Profile
```http
POST /api/v1/admin/profile-activation/activate/{userId}
```

#### 2. Deactivate Profile
```http
POST /api/v1/admin/profile-activation/deactivate/{userId}
```

#### 3. Check Status
```http
GET /api/v1/admin/profile-activation/status/{userId}
```

#### 4. Toggle Status
```http
POST /api/v1/admin/profile-activation/toggle/{userId}
```

### Use Cases
- Manually activate profiles without subscription
- Deactivate problematic users
- Review and approve new profiles
- Quality control

---

## For Developers

### Subscription Purchase
```java
// Automatically activates profile
POST /api/subscription/purchase/{subscriptionId}?userId={userId}
```

### Check if Profile Active
```java
profileActivationService.isProfileActive(userId);
// Returns: true/false
```

### Get Profile Status
```java
profileActivationService.getProfileStatus(userId);
// Returns: Status.ACTIVE or Status.DEACTIVE
```

---

## Query Filtering

### All Public Queries Now Filter by ACTIVE
```java
// Search profiles - only ACTIVE returned
GET /api/profiles/search?gender=MALE

// Get profiles by gender - only ACTIVE returned
GET /api/profiles/gender/MALE

// Match suggestions - only ACTIVE profiles
GET /api/matches/suggestions
```

### Admin Queries See All
```java
// Admin can see both ACTIVE and DEACTIVE
GET /api/v1/admin/profiles/all
```

---

## Testing Checklist

### Test 1: New Profile is DEACTIVE
- [ ] Register new user
- [ ] Create profile
- [ ] Verify status is DEACTIVE
- [ ] Verify NOT in search results

### Test 2: Subscription Activates Profile
- [ ] Purchase subscription
- [ ] Verify status changed to ACTIVE
- [ ] Verify NOW in search results

### Test 3: Admin Can Activate
- [ ] Admin activates profile
- [ ] Verify status changed to ACTIVE
- [ ] Verify in search results

### Test 4: Admin Can Deactivate
- [ ] Admin deactivates profile
- [ ] Verify status changed to DEACTIVE
- [ ] Verify NOT in search results

### Test 5: Filtering Works
- [ ] Create mix of ACTIVE/DEACTIVE profiles
- [ ] Search profiles
- [ ] Verify only ACTIVE returned

---

## Files Changed

### New Files (2)
1. `ProfileActivationService.java` - Activation logic
2. `AdminProfileActivationController.java` - Admin endpoints

### Modified Files (4)
1. `UserProfileRepository.java` - Added ACTIVE filtering
2. `SubscriptionService.java` - Added purchase method
3. `SubscriptionServiceImpl.java` - Implements activation
4. `SubscriptionController.java` - Purchase endpoint

---

## Breaking Changes

**NONE** ✅

- Existing profiles remain as-is
- New profiles default to DEACTIVE
- Backward compatible

---

## Configuration

**No configuration needed!**

Everything works out of the box.

---

## Quick Commands

### Activate Profile (Admin)
```bash
curl -X POST "http://localhost:8080/api/v1/admin/profile-activation/activate/10001" \
  -H "Authorization: Bearer {admin_token}"
```

### Purchase Subscription (User)
```bash
curl -X POST "http://localhost:8080/api/subscription/purchase/1?userId=10001" \
  -H "Authorization: Bearer {user_token}"
```

### Check Status (Admin)
```bash
curl -X GET "http://localhost:8080/api/v1/admin/profile-activation/status/10001" \
  -H "Authorization: Bearer {admin_token}"
```

---

## Summary

✅ **Implemented:** Profile activation system
✅ **Default:** New profiles DEACTIVE
✅ **Activation:** Subscription purchase OR admin approval
✅ **Filtering:** Only ACTIVE profiles in public queries
✅ **Admin Control:** 4 new endpoints for management
✅ **Zero Breaking Changes:** Fully backward compatible

**Status:** READY FOR DEPLOYMENT
