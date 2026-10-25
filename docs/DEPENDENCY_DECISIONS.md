# SafeOps — Architectural & Dependency Decisions

## 1. Dependency Rationale Table

| Dependency | Category | Rationale |
|---|---|---|
| `androidx.compose.material3` | UI / Design System | Modern Material 3 design baseline customized with SafeOps tokens |
| `androidx.compose.material:material-icons-extended` | Icons | Vector Material Symbols providing consistent iconography without emojis |
| `androidx.navigation:navigation-compose` | Navigation | Centralized type-safe Compose routing supporting role-based navigation |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | Architecture | Lifecycle-aware StateFlow emission and ViewModel bindings |
| `androidx.datastore:datastore-preferences` | Local Storage | Asynchronous, type-safe persistence for user preferences, role selection, and remember-me |
| `androidx.security:security-crypto` | Security | Hardware-backed MasterKey encryption for access/refresh tokens (AES256-GCM) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | Concurrency | Structured concurrency with custom `DispatcherProvider` for testability |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | Serialization | High-performance compiler-assisted JSON serialization |
| `app.cash.turbine:turbine` | Unit Testing | Flow testing library for ViewModel StateFlow verification |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | Unit Testing | Test coroutine dispatchers and timing control |

---

## 2. Dependency Management Strategy

1. **Centralized Version Catalog**:
   - Managed via `gradle/libs.versions.toml`.
   - Single source of truth for version definitions.
   - Zero duplicated dependency declarations.

2. **No Redundant / Heavy Third-Party Libraries**:
   - Avoided multiple conflicting DI or networking frameworks during the foundation phase.
   - Clean `AppContainer` IoC container ensures complete test isolation and zero global state coupling.
