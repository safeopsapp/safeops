# SafeOps — Project Setup Guide

## 1. Prerequisites

- **IDE**: Android Studio Quail 4 / 2026.1.4+ (or compatible stable release)
- **JDK**: JDK 17 (recommended standard toolchain)
- **Android SDK**: API 36 (`compileSdk = 36`, `targetSdk = 36`, `minSdk = 26` — Native AGP 9.0.1 support without suppression)
- **Gradle Wrapper**: Gradle 9.2.1
- **Android Gradle Plugin**: AGP 9.0.1
- **Kotlin**: 2.0.21 with Compose Compiler Plugin (`org.jetbrains.kotlin.plugin.compose`)

---

## 2. Gradle Commands

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Run All Unit Tests
```bash
./gradlew test
```

### Lint Analysis
```bash
./gradlew lint
```

### Clean Build Cache
```bash
./gradlew clean
```

---

## 3. Package & Application Identifiers

- **Package**: `com.safeops.app`
- **Application ID**: `com.safeops.app`
- **Application Class**: `com.safeops.app.SafeOpsApp`
- **Main Activity**: `com.safeops.app.MainActivity`
