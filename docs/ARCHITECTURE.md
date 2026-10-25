# SafeOps — Architecture Specification

## 1. Overview & Architectural Principles

SafeOps is built using **Feature-First + Clean Architecture** tailored for high-reliability enterprise Android applications. The architecture strictly enforces single-direction dependency flow, separation of concerns, testability, and an offline-first foundation.

```
Presentation (UI + ViewModels + State)
      ↓
Domain (Entities + UseCases + Repositories Contracts)
      ↓
Data (Repository Implementations + Mappers)
      ↓
Remote / Local / Database (APIs, DataStore, EncryptedSharedPreferences, Room)
```

---

## 2. Dependency Direction & Strict Boundaries

1. **Presentation Layer** `[IMPLEMENTED / FOUNDATION READY]`:
   - Contains Jetpack Compose UI composables, ViewModels, UI State models, and UI Events.
   - Observes `StateFlow` from ViewModels.
   - **Strict Rule**: Presentation never directly accesses Retrofit, Room, DataStore, or API clients.

2. **Domain Layer** `[IMPLEMENTED / FOUNDATION READY]`:
   - Pure Kotlin module layer with no Android framework dependencies.
   - Defines core business entities (`User`, `Role`, `Permit`, `Hazard`, `Incident`, `Inspection`, `AuthenticatedSession`).
   - Contains single-purpose UseCases (`LoginUseCase`, `GetSessionUseCase`, `ResolveRoleNavigationUseCase`, etc.).
   - Declares Repository contracts (`AuthRepository`, `SessionRepository`, `PermitRepository`).

3. **Data Layer** `[IMPLEMENTED / FOUNDATION READY]`:
   - Implements repository interfaces declared in Domain.
   - Coordinates between `RemoteDataSource` and `LocalDataSource`.
   - Maps DTO / API responses to domain entities using explicit mappers.

4. **Remote / Local Storage Layer**:
   - `core/network` `[FOUNDATION ONLY]`: Centralized network client abstraction, environment configs (Dev, Staging, Prod), interceptors, and error mapping.
   - `core/security` `[IMPLEMENTED]`: `EncryptedPreferencesStorage` for secure session token storage (AES256-GCM / AES256-SIV) with Keystore corruption recovery.
   - `core/datastore` `[IMPLEMENTED]`: `UserPreferencesDataSource` for lightweight non-sensitive preferences (role selection, remember me, theme).
   - `core/database` `[FOUNDATION ONLY / CONTRACTS DEFINED]`: Offline synchronization contracts (`SyncStatus`, `OfflineSyncable`, `LocalSyncQueue`). Full Room tables `[PLANNED]`.

---

## 3. Role-Based Access & Centralized Routing `[IMPLEMENTED]`

SafeOps supports 4 primary roles:
- `ADMIN`: Full operational governance, worker management, project controls.
- `SAFETY_OFFICER`: Hazard inspections, PTW permit review/approval/suspension/closure.
- `ENGINEER`: Site execution, engineering permit submission, hazard reporting.
- `WORKER`: Field worker workspace, daily safety briefs, assigned permits, field hazard reporting.

**Authorization Rule**:
Navigation decisions and permission resolution are centralized inside `RoleNavigationConfig` and `ResolveRoleNavigationUseCase`. UI composables never contain scattered role strings.

---

## 4. Error Handling Hierarchy `[IMPLEMENTED]`

All technical exceptions (e.g., `SocketTimeoutException`, `UnknownHostException`, `IOException`) are caught in repository/data layers and mapped via `ErrorMapper` into `SafeOpsError`:
- `SafeOpsError.NetworkUnavailable`
- `SafeOpsError.Timeout`
- `SafeOpsError.Unauthorized`
- `SafeOpsError.Forbidden`
- `SafeOpsError.NotFound`
- `SafeOpsError.ValidationError`
- `SafeOpsError.ServerError`
- `SafeOpsError.UnknownError`

---

## 5. Security & Token Storage `[IMPLEMENTED]`

- Access and refresh tokens are stored using `androidx.security.crypto.EncryptedSharedPreferences`.
- Logging via `SafeOpsLogger` automatically redacts passwords, tokens, and authorization headers in both debug and release configurations.

---

## 6. Module Roadmap & Implementation Status

| Module / Feature Area | Status | Notes |
|---|---|---|
| Toolchain Baseline (Gradle 9.2.1, AGP 9.0.1, Kotlin 2.0.21, SDK 36) | **IMPLEMENTED** | 100% clean, no suppressions |
| Design System (Colors, Typography, Spacing, Shapes, 18 Components) | **IMPLEMENTED** | Material 3 + Custom Tokens |
| Authentication Shell & Login Screen UI | **IMPLEMENTED** | Matches visual reference |
| Role-Based Navigation & Dynamic App Shell | **IMPLEMENTED** | Type-safe Compose navigation |
| Core Infrastructure (Security, DataStore, Network, Logger, Dispatchers) | **IMPLEMENTED** | Tested & resilient |
| Unit Test Suite (19 passing unit tests) | **IMPLEMENTED** | JUnit4 + Turbine + Coroutines Test |
| Full Digital Work Permits (PTW Workflow & Forms) | **PLANNED** | Next Phase |
| Hazard & Incident Reporting Workflow | **PLANNED** | Next Phase |
| Safety Inspections & Checklists | **PLANNED** | Next Phase |
| Room Local Database & Offline Sync Engine | **PLANNED** | Contracts defined in Foundation |
| Push Notifications & Background Workers | **PLANNED** | Next Phase |
