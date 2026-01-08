# JWT Error Codes - Quick Reference

## 🚨 Error Codes

| Code | Status | Meaning | User Action |
|------|--------|---------|-------------|
| `DEVICE_FINGERPRINT_MISMATCH` | 401 | Token from different device/browser | **Login again from this device** |
| `INVALID_JWT_TOKEN` | 401 | Token expired or invalid | Login again |
| `MISSING_AUTHORIZATION_HEADER` | 401 | No Authorization header | Add header |
| `INVALID_AUTHORIZATION_FORMAT` | 401 | Wrong header format | Use "Bearer <token>" |
| `ENDPOINT_NOT_FOUND` | 404 | URL doesn't exist | Check API docs |
| `ACCESS_DENIED` | 403 | Insufficient permissions | Contact admin |

---

## 🔍 DEVICE_FINGERPRINT_MISMATCH

### What It Means
Your JWT token was created on a different device or browser and cannot be used here.

### Why It Happens
- Copied token from Chrome to Firefox
- Copied token from desktop to mobile
- Changed browser User-Agent
- Using VPN or proxy that changes IP

### How to Fix
1. **Login again** from the current device/browser
2. **Don't copy tokens** between devices
3. **Clear cache** and cookies, then login
4. **Contact support** if you need multi-device access

### Example Error Response
```json
{
  "status": 401,
  "errorCode": "DEVICE_FINGERPRINT_MISMATCH",
  "message": "Token was generated on a different device or browser",
  "suggestedActions": [
    "Login again from this device/browser to get a new token",
    "If you need to use the same account on multiple devices, contact support"
  ]
}
```

---

## 🔧 Configuration

### Enable/Disable Device Fingerprinting

**Development (Disabled):**
```properties
jwt.device-fingerprinting-enabled=false
```

**Production (Enabled):**
```properties
jwt.device-fingerprinting-enabled=true
```

---

## 📱 Frontend Handling

### JavaScript Example
```javascript
async function handleApiError(error) {
  switch(error.errorCode) {
    case 'DEVICE_FINGERPRINT_MISMATCH':
      alert('Please login again from this device');
      redirectToLogin();
      break;
      
    case 'INVALID_JWT_TOKEN':
      alert('Session expired. Please login again');
      redirectToLogin();
      break;
      
    default:
      alert('An error occurred: ' + error.message);
  }
}
```

### React Example
```jsx
function ErrorHandler({ error }) {
  if (error.errorCode === 'DEVICE_FINGERPRINT_MISMATCH') {
    return (
      <Alert severity="warning">
        <AlertTitle>Device Mismatch</AlertTitle>
        Your session was created on a different device.
        <Button onClick={handleLogin}>Login Again</Button>
      </Alert>
    );
  }
  // ... handle other errors
}
```

---

## 🧪 Testing

### Test Device Fingerprint Mismatch

**Step 1:** Enable device fingerprinting
```properties
jwt.device-fingerprinting-enabled=true
```

**Step 2:** Login from Chrome
```bash
POST /jwt/login
{
  "username": "test@example.com",
  "password": "password"
}
```

**Step 3:** Copy token to Firefox and make API call
```bash
GET /api/v1/profiles
Authorization: Bearer <token-from-chrome>
```

**Expected Result:**
```json
{
  "errorCode": "DEVICE_FINGERPRINT_MISMATCH"
}
```

---

## 📊 Monitoring

### Log Patterns to Watch

**Device Mismatch:**
```
WARN: Device fingerprint mismatch for request: /api/v1/profiles
```

**Token Validation:**
```
DEBUG: === TOKEN VALIDATION START ===
DEBUG: Device fingerprinting enabled: true
WARN: ❌ Token validation failed: Device fingerprint mismatch
```

### Metrics to Track
- Device fingerprint mismatch rate
- Token validation failure rate
- User login frequency
- Multi-device usage patterns

---

## 🎯 Quick Troubleshooting

### User Can't Access API After Login

**Check 1:** Is device fingerprinting enabled?
```bash
grep "jwt.device-fingerprinting-enabled" application.properties
```

**Check 2:** Are they using the same browser?
- Check error code in response
- If `DEVICE_FINGERPRINT_MISMATCH`, they need to login again

**Check 3:** Check logs
```bash
grep "Device fingerprint mismatch" logs/application.log
```

### Token Works in Postman But Not in Browser

**Likely Cause:** Device fingerprint mismatch

**Solution:** 
1. Disable device fingerprinting for testing
2. Or login directly from browser

---

## 💡 Best Practices

### For Users
✅ Login from each device separately  
✅ Don't copy tokens between devices  
✅ Clear cache if having issues  
✅ Contact support for multi-device needs  

### For Developers
✅ Handle `DEVICE_FINGERPRINT_MISMATCH` specifically  
✅ Show clear error messages to users  
✅ Provide "Login Again" button  
✅ Log device mismatch events  

### For DevOps
✅ Monitor device mismatch rate  
✅ Enable device fingerprinting in production  
✅ Disable in development for easier testing  
✅ Track token validation failures  

---

## 📞 Support

### Common Questions

**Q: Why can't I use my token on mobile after logging in on desktop?**  
A: Device fingerprinting is enabled. Login separately on mobile.

**Q: Can I disable device fingerprinting?**  
A: Yes, set `jwt.device-fingerprinting-enabled=false` (not recommended for production)

**Q: How do I support multiple devices?**  
A: Each device needs its own login session. Tokens are device-specific for security.

**Q: What if I'm using a VPN?**  
A: VPN changes your IP, which may trigger device mismatch. Login again after connecting to VPN.

---

**Quick Reference Version:** 1.0  
**Last Updated:** January 8, 2026  
**Print This:** Keep handy for quick troubleshooting!
