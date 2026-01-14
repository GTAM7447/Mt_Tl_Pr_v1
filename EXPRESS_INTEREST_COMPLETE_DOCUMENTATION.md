# Express Interest Module - Complete Documentation

## Overview

The Express Interest module has been completely redesigned with enterprise-level architecture, following the same patterns established in ContactDetails, PartnerPreference, and other modules. This documentation provides a comprehensive guide on how the system works, its features, and how to use it.

## 🏗️ Architecture Overview

### Enterprise Design Patterns Used

1. **Layered Architecture**: Clear separation between presentation, business, and data layers
2. **Repository Pattern**: Data access abstraction with custom queries
3. **Service Layer Pattern**: Business logic encapsulation with validation
4. **DTO Pattern**: Data transfer objects for API communication
5. **Mapper Pattern**: Entity-DTO conversion with null safety
6. **Exception Handling**: Centralized error handling with specific exceptions
7. **Audit Trail**: Complete tracking of who did what and when
8. **Soft Delete**: Data preservation with logical deletion
9. **Optimistic Locking**: Concurrent modification prevention
10. **Caching**: Performance optimization with strategic caching

### Module Structure

```
ExpressInterest/
├── dto/             