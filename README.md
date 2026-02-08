# MassecDev Inventory App (WIP)

This project is an Android inventory-management prototype built with Kotlin + Jetpack Compose.

It currently includes a working app shell (authentication, tab navigation, feature screens, reusable UI components), but many business flows are still mock/in-memory and not fully integrated with backend services.

## What the app does today

### 1. Authentication flow
- Launches into `LoginScreen` when not authenticated.
- Supports:
  - login by username/password
  - login by fixed device ID/MAC
- Calls backend endpoints via simple HTTP POST.
- Persists:
  - `is_logged_in` in `SharedPreferences`
  - optional saved credentials in Room
  - login attempt logs in Room

### 2. Main app shell
After login, the app opens a tabbed shell with:
- Home
- Inventory
- Warehouse
- Settings

The shell includes:
- top app bar
- bottom navigation
- navigation drawer (license/account placeholder content)
- per-screen floating action button (single action or action menu)

### 3. Inventory module
- Add inventory entries through a bottom sheet (multiple form modes).
- Two-tab view: unsynchronized / synchronized.
- Long-press multi-select behavior.
- Bulk actions: sync, delete.
- Confirm-delete dialog.

Data is currently held in memory (ViewModel state), not persisted to a business database.

### 4. Warehouse module
Contains multiple subflows with similar list/sync/delete behavior:
- Receipt of goods
- Issuing goods
- Transfer of goods
- Return of goods
- Write-off of goods
- Ordering goods
- Virtual warehouse
- Templates

Most flows are local-state prototypes. `Ordering goods` has the most custom logic (internal vs external orders + status tabs).

### 5. Settings module
Contains master-data style screens:
- Products
- Suppliers
- Warehouses
- Cost centers
- Locations
- Inventory lists
- Inventory groups
- Diagnostics

Most screens support search, selection, and delete, but use sample/local lists.
Actions such as download/upload/export are mostly stubbed placeholders.

### 6. Diagnostics module
- Terminal-like screen for sending commands.
- Built-in default commands + editable custom commands.
- Custom commands are persisted in DataStore.
- Simulates incoming scale messages and command responses.
- Real hardware command transport is still TODO.

### 7. Bluetooth (experimental)
- BLE scanning/connection utilities exist (`core/bluetooth`).
- Includes permission helpers for modern Android BLE permissions.
- A test screen exists but is not part of the normal app navigation flow.

## How it is built

### Tech stack
- Kotlin
- Jetpack Compose (Material 3)
- AndroidX Navigation Compose
- Room
- Kotlin Serialization
- DataStore Preferences
- Coroutines
- Accompanist (system UI + swipe refresh)

### Project architecture (high-level)
- Feature-first package structure:
  - `features/auth`
  - `features/inventory`
  - `features/warehouse`
  - `features/settings`
  - `features/diagnostics`
  - `core/bluetooth`
- Navigation split into:
  - root auth/main host
  - per-tab nested nav graphs for warehouse/settings
- State is primarily ViewModel + `StateFlow`.
- Reusable list behavior is centralized in `BaseListViewModel<T>`:
  - item collection
  - selection state
  - pending delete
  - bottom-sheet visibility
  - helper methods for bulk sync/delete
- Shared reusable Compose components in `ui/components`:
  - `UnifiedItemCard`
  - `SelectionToolbar`
  - `SearchBar`
  - `ConfirmDeleteDialog`
  - form bottom sheets

### Data and integration details
- Login API endpoints are hardcoded in `LoginRepository`.
- App allows cleartext traffic to `185.203.18.87` via `network_security_config.xml`.
- Auth-related local storage:
  - Room DB: credentials + login logs
  - SharedPreferences: simple auth session flag
- Diagnostics custom commands use DataStore JSON serialization.

## Run the app

### Requirements
- Android Studio with Android SDK
- JDK 11
- Android SDK matching:
  - `compileSdk = 35`
  - `minSdk = 21`
  - `targetSdk = 35`

### Steps
1. Open the project in Android Studio.
2. Ensure `local.properties` points to your SDK path.
3. Sync Gradle.
4. Run `app` on emulator/device.

Optional CLI commands:
```bash
./gradlew :app:assembleDebug
./gradlew test
```

## Current status and known gaps

This codebase is clearly mid-development. Main gaps:
- Home tab is placeholder (`MenuScreen`) with TODO to replace.
- Many settings/warehouse actions are UI prototypes without backend sync.
- Several screens use static/mock data only.
- Diagnostics currently simulates command transport.
- BLE integration is partial and not wired into production flow.
- Tests are scaffold/default tests only.

Security caveats for production hardening:
- Login endpoints currently use HTTP (not HTTPS).
- Credentials and login logs are stored locally in plain form.

## Suggested next implementation milestones
1. Replace mock lists with repository + API-backed paging/caching.
2. Unify persistent storage strategy (Room/DataStore) for business entities.
3. Complete diagnostics-to-BLE command pipeline.
4. Harden auth + secure credential handling.
5. Add real unit/UI tests for core user flows.
