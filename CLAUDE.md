# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FridgeSokSok is an Android application for managing fridge inventory and generating recipes. Built with Kotlin and Jetpack Compose, it follows Clean Architecture principles with MVVM presentation layer.

- `compileSdk`/`targetSdk` 36 (Android 16), `minSdk` 26. Current release: versionCode 8 / versionName 1.7 (bump versionCode for every Play upload).
- Work history and open tasks: `docs/WORKLOG-2026-11-08.md`.

## Build Commands

### Standard Build Tasks
```bash
# Build the project
./gradlew build

# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK
./gradlew assembleRelease

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run all unit tests
./gradlew test

# Run debug unit tests only
./gradlew testDebugUnitTest

# Run instrumented tests (requires connected device/emulator)
# NOTE: uninstalls the app afterwards (login/data on the emulator is lost)
./gradlew connectedDebugAndroidTest

# Instrumented tests while keeping app data
./gradlew assembleDebug assembleDebugAndroidTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r com.yh.fridgesoksok.test/androidx.test.runner.AndroidJUnitRunner

# Run all checks (lint + debug/release unit tests)
./gradlew check
```

Test layout:
- Unit tests (`app/src/test`): one test per UseCase (`domain/usecase`), ViewModels with fakes in `data/repository/Fake*Repository.kt` (failure injection: `FakeFoodRepository.shouldFail`, `FakeUserRepository.channelLoginError`/`reissueError`), `remote/api` (TokenAuthenticator, RequestHeaderInterceptor), `remote/model` (response parsing from server examples).
- ViewModel tests use `MainDispatcherRule` (StandardTestDispatcher); call `mainDispatcherRule.advance()` to run pending coroutines. One-shot events (SharedFlow) are collected with `backgroundScope.launch(UnconfinedTestDispatcher(testScheduler))` before acting.
- Instrumented tests (`app/src/androidTest`): Room DAO, repository + in-memory Room, Compose components, E2E. `flowWithResource` emits `Loading` first, so await the final result (`first { it !is Resource.Loading }`, see `result()` helper) instead of `.first()`.

### Linting
```bash
# Run lint on default variant
./gradlew lint

# Run lint and auto-fix safe issues
./gradlew lintFix

# Update lint baseline
./gradlew updateLintBaseline
```

### Development Setup
Requires `local.properties` with API keys:
```properties
KAKAO_API_KEY="key_value"
KAKAO_API_KEY_NO_QUOTES=actual_key
NAVER_CLIENT_ID="client_id"
NAVER_CLIENT_SECRET="secret"
NAVER_CLIENT_APP_NAME="app_name"
# Optional (debug builds only): use the in-memory MockApiService instead of the real server
USE_MOCK_API=true
```

Base URL is defined as `BuildConfig.BASE_URL` in `app/build.gradle.kts`.
`MockApiService` lives in `app/src/debug` (provided via `mockFridgeApiService()`; `app/src/release` has a stub) and `USE_MOCK_API` is forced to `false` for release builds. Guest login works offline with the mock; Kakao/Naver still go through the real SDKs.
`BuildConfig.EXTENDED_CATEGORIES` (default `false`) exposes categories 18–22 (`Type.isExtended`) in filter/edit selectors; turn it on together with the server's `app.categories.extended`. `Type.fromId` always recognizes them.

### App Startup Flow
- `MainActivity.onCreate` drops saved state when restored after process death (`hasBeenCreatedInProcess`), so the app always re-enters through Onboarding before Home APIs run.
- `OnboardingScreen` calls `OnboardingViewModel.start()` / `AppInitializerViewModel.start()` once (not from `init`) and skips when the Activity is finishing (the system can create two MainActivity instances briefly).
- Onboarding: server status → token reissue (no separate validation; reissue failure = invalid session) → default fridge only if not stored → save user → API usage → FCM sync (optional; failure does not block). Login treats FCM sync as optional too.
- `AppInitializerViewModel` seeds the Room food dictionary from `assets/initial.csv`; runs under a process-wide `Mutex` and deletes existing rows before re-inserting.

### Server Behavior Notes
- Invalid access token → 401 (handled by `TokenAuthenticator`); invalid/expired refresh token on `auth/refresh` → 400.
- Guest account creation (`users/create`) is rate-limited per IP (429 for 24h). Use Kakao/Naver login or `USE_MOCK_API=true` (debug) when blocked.
- `receipts/upload` items are either matched (`id` may be negative for fresh food, `shelfLifeHours`, `dateBasis`) or unmatched `{"name","count"}`; see `ReceiptResponse` and `presentation/upload/ReceiptMapper.kt`.

### Troubleshooting
- Kakao login: `KOE008` = wrong key type (use the **Native app key**, 32 hex chars), `KOE101` = key does not exist (e.g. a key hash pasted as the app key), `Android keyHash validation failed` = register the debug/release key hash in the Kakao console (each PC's `~/.android/debug.keystore` differs; get it with `Utility.getKeyHash(context)` or `./gradlew signingReport` + Base64).
- Android Studio "Missing Android platform(s)": Studio's global SDK location must point to the same SDK as `local.properties` `sdk.dir`; do not click "Install missing platform" into a different SDK path.
- Avoid drag-and-drop moves in the Project view: moving a package folder outside `src/main/java` breaks KSP/Hilt with `error.NonExistentClass`.

## Architecture Overview

### Layered Clean Architecture

The codebase follows **Clean Architecture with MVVM** pattern across 4 main layers:

```
presentation/ → domain/ → data/ → (remote/ + local/)
```

#### **Presentation Layer** (`presentation/`)
- **Pattern**: MVVM with Jetpack Compose
- **Structure**: Each feature has dedicated folder with Screen, ViewModel, State, and Components
- **State Management**: StateFlow for observable state, SharedFlow for one-time events/triggers
- **Key Files**:
  - `Screen.kt` - Sealed class navigation routes
  - `SharedViewModel.kt` - Activity-scoped ViewModel (receipt bitmap: Camera→Upload, selected recipe: Recipe→RecipeDetail)
  - `HomeViewModel.kt` - Event coordinator for tab operations using SharedFlows

#### **Domain Layer** (`domain/`)
- Pure Kotlin domain models: `Food`, `Recipe`, `User`, `Fridge`, `Token`, `UserSetting`
- Repository interfaces defining data contracts
- Use cases follow operator pattern with `invoke()` method
- Example: `CreateRecipeUseCase`, `GetFoodsUseCase`, `UpdateFoodUseCase`

#### **Data Layer** (`data/`)
- Repository implementations bridge domain and data sources
- Entity models (e.g., `FoodEntity`) with bidirectional mapping: `toDomain()` and `toEntity()`
- Repository methods return `Flow<Resource<T>>` for reactive async handling (exception: synchronous local reads such as `UserRepository.loadUser()`, `getLocalUserFcmToken()`, `getLocalApiUsage()`)
- Helper: `flowWithResource { }` wraps operations with Loading/Success/Error states. `Resource.Error.status` is the HTTP code, `Resource.STATUS_NETWORK_ERROR` (IOException), `Resource.STATUS_CANCELLED` (`UserCancelledException`, e.g. social login cancelled), or null; `CancellationException` is rethrown

#### **Remote Layer** (`remote/`)
- Retrofit API services: `FridgeApiService`, `KakaoApiService`, `NaverApiService`
- RemoteDataSource implementations transform API responses to entities
- RemoteDataSource implementations wrap calls with `apiCall(TAG, name, input) { ... }` and `CommonResponse.requireData(name)` (`remote/impl/ApiCall.kt`). Never pass tokens as `input`.
- Networking (`remote/di/NetworkModule.kt`):
  - `RequestHeaderInterceptor` - Adds `Bearer` header from `LocalUserDataSource` (refresh token for `auth/` URLs, access token otherwise)
  - `TokenAuthenticator` - On 401, reissues tokens via `AuthApiService` (separate OkHttpClient to avoid DI cycle), saves them, and retries once. If the refresh token is rejected (400/401/403), clears tokens and emits `SessionEvents.expired`; `MainActivity` then navigates to Login
  - `AuthTokenLock` - Serializes `auth/` API calls and token refresh; rotated tokens from `auth/refresh` are saved inside the lock by `RequestHeaderInterceptor`
  - `HttpLoggingInterceptor` - BODY level in debug only, `Authorization` header redacted

#### **Local Layer** (`local/`)
- Room database with `FoodDao` (local food dictionary for search suggestions, seeded from `initial.csv`)
- User info, tokens and API usage are stored in SharedPreferences (`user_info`) via `LocalUserDataSourceImpl`
- LocalDataSource implementations for database queries
- Room entities (e.g., `FoodLocal`)

### Data Flow Pattern

All data flows follow this structure:

```kotlin
Screen (Compose UI)
  ↓ collects StateFlow
ViewModel
  ↓ launches in viewModelScope
UseCase.invoke()
  ↓ delegates to
Repository (Flow<Resource<T>>)
  ↓ calls
RemoteDataSource / LocalDataSource
  ↓ transforms
ApiResponse → Entity → Domain Model → Presentation Model
```

### Entity Mapping (3-tier model system)

```
Remote/Local Models (FoodResponse/FoodLocal)
  ↓ toEntity()
Entity Models (FoodEntity)
  ↓ toDomain()
Domain Models (Food)
  ↓ toPresentation()
Presentation Models (FoodModel)
```

### Resource Pattern

All async operations are wrapped in a sealed `Resource<T>` class:

```kotlin
sealed class Resource<T> {
    class Success<T>(data: T)
    class Error<T>(message: String, data: T?, status: Int?)
    class Loading<T>(data: T?)
}
```

Found in: `common/Resource.kt`

## Key Architectural Patterns

### State Management
- **Screen State**: Each ViewModel manages its own StateFlows (e.g., `_foods`, `_recipes`)
- **Cross-Screen Communication**: Use SharedFlow triggers instead of shared mutable state
  - Example in `HomeViewModel`: `_selectAllTrigger`, `_generateRecipeTrigger`
  - Subscribing Screens collect triggers (`LaunchedEffect`) and report results back via callbacks
- **Navigation Data**: Objects that cannot be route args (Bitmap, RecipeModel) are passed via `SharedViewModel`
- **Activity-Scoped State**: `SharedViewModel` holds receipt bitmap, selected recipe, and exposes the session-expired event

### Cross-Screen Trigger Pattern
Shared ViewModels were intentionally removed in favor of trigger-based communication:
- Recipe creation no longer relies on `SharedViewModel`
- Select/deselect operations use SharedFlow triggers
- Pattern: Pub-Sub via SharedFlow instead of shared ViewModel reference

**Example Pattern**:
```kotlin
// In coordinator ViewModel
private val _actionTrigger = MutableSharedFlow<Unit>()
val actionTrigger = _actionTrigger.asSharedFlow()

fun triggerAction() {
    viewModelScope.launch { _actionTrigger.emit(Unit) }
}

// In subscriber Screen (ViewModels stay independent; the Screen wires them)
LaunchedEffect(Unit) {
    homeViewModel.actionTrigger.collect {
        viewModel.performAction { state -> homeViewModel.updateState(state) }
    }
}
```

Found in: `presentation/fridge/FridgeScreen.kt` (subscribes to `HomeViewModel` triggers)

### Duplicate Request Prevention
Use synchronized set to prevent duplicate in-flight operations:

```kotlin
private val _pendingActions = Collections.synchronizedSet(mutableSetOf<String>())

private fun executeSingleAction(id: String, block: () -> Flow<Resource<*>>) {
    if (!_pendingActions.add(id)) return  // Skip if already pending
    block().onEach {
        if (it !is Resource.Loading) _pendingActions.remove(id)
    }.launchIn(viewModelScope)
}
```

Found in: `presentation/fridge/FridgeViewModel.kt`

### Dependency Injection (Hilt)
- **Modules**: Separate modules for repositories, remote sources, local sources
- **Binding Pattern**: Abstract modules with `@Binds` and `@Singleton`
- **Scopes**: Singleton (data layer), ViewModelScoped (ViewModels), ActivityRetainedScoped (SharedViewModel)

Example:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFoodRepository(repo: FoodRepositoryImpl): FoodRepository
}
```

## Navigation Structure

Defined in `presentation/Screen.kt` as sealed class:

```
OnboardingScreen → LoginScreen → HomeScreen
                                     ├─ FridgeTab
                                     ├─ RecipeTab
                                     └─ AccountTab

Cross-app navigation:
- CameraScreen → UploadScreen → EditFoodScreen
- RecipeTab → RecipeDetailScreen
```

**Navigation Hierarchy**:
1. Main NavHost (MainActivity) - Top-level routes
2. Home Nested NavHost (HomeNavGraph) - 3 bottom tabs
3. Main NavHost - Cross-feature navigation

## File Location Conventions

When working with features, follow these patterns:

### Adding a New Screen/Feature
1. Create folder in `presentation/` (e.g., `presentation/my_feature/`)
2. Add files:
   - `MyFeatureScreen.kt` - Composable UI
   - `MyFeatureViewModel.kt` - State management
   - `MyFeatureState.kt` - State sealed classes (if needed)
   - `components/` - Reusable composables (if needed)

### Adding Business Logic
1. Create domain model in `domain/model/MyModel.kt`
2. Create repository interface in `domain/repository/MyRepository.kt`
3. Create use case in `domain/usecase/MyUseCase.kt`
4. Implement repository in `data/impl/MyRepositoryImpl.kt`
5. Create entity in `data/model/MyEntity.kt` with mapping functions

### Adding API Integration
1. Add API service in `remote/api/MyApiService.kt` (Retrofit interface)
2. Create response models in `remote/model/MyResponse.kt`
3. Implement data source in `remote/impl/RemoteMyDataSourceImpl.kt`
4. Create data source interface in `data/remote/RemoteMyDataSource.kt`
5. Add DI binding in `remote/di/RemoteDataSourceModule.kt`

### Adding Local Storage
1. Create Room entity in `local/model/MyLocal.kt`
2. Add DAO in `local/room/MyDao.kt`
3. Update `local/room/AppDatabase.kt` to include new DAO
4. Implement data source in `local/impl/LocalMyDataSourceImpl.kt`
5. Create data source interface in `data/local/LocalMyDataSource.kt`
6. Add DI binding in `local/di/LocalDataSourceModule.kt`

## Code Modification Guidelines

### When Modifying ViewModels
- Show user-facing messages from `StringResources`, never raw `Resource.Error.message` (e.g. `loginErrorMessage(status)` in `LoginViewModel`). Do not log tokens or whole user models.
- Use private mutable backing properties: `private val _state = MutableStateFlow(...)`
- Expose immutable public properties: `val state = _state.asStateFlow()`
- For cross-component events, prefer SharedFlow triggers over shared ViewModels
- Always launch coroutines in `viewModelScope`
- Wrap repository calls with Resource handling

### When Modifying Repositories
- Methods return `Flow<Resource<T>>` (only synchronous local reads are exempt, see Data Layer)
- Use `flowWithResource { }` helper to wrap data source calls
- Implement both interface (in domain) and implementation (in data)
- Add DI binding in `data/di/RepositoryModule.kt`

### When Modifying Data Models
- Add mapping extensions: `fun FooResponse.toEntity()`, `fun FooEntity.toDomain()`
- For Parcelize models (navigation args), use `@Parcelize` annotation
- Keep domain models pure Kotlin (no Android dependencies, e.g. receipt images are passed as JPEG `ByteArray`, converted in presentation via `Bitmap.toResizedJpegBytes()`)
- Entity models can have Android/Gson dependencies

### When Modifying Navigation
- Update `Screen.kt` sealed class with new routes
- Add navigation arguments to `Screen` constructor if needed
- Use `savedStateHandle` in ViewModel to retrieve simple navigation arguments; use `SharedViewModel` only for objects that cannot be serialized into routes
- Update `MainActivity.kt` or `HomeNavGraph.kt` based on hierarchy level

## Technology Stack

- **UI**: Jetpack Compose, Material3
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt
- **Reactive**: Kotlin Flow, Coroutines
- **Networking**: Retrofit, OkHttp, Gson
- **Local Storage**: Room
- **Navigation**: Jetpack Navigation Compose
- **State Management**: StateFlow/SharedFlow
- **Authentication**: Firebase, Kakao SDK, Naver SDK
- **Image Loading**: Coil, Landscapist
- **Camera**: CameraX
- **Push Notifications**: Firebase Cloud Messaging
- **Analytics**: Firebase Analytics, Crashlytics
- **Animations**: Lottie

## Common Utilities

### Logger
```kotlin
Logger.d("TAG", "Debug message")
Logger.e("TAG", "Error message", throwable)
```
Found in: `common/Logger.kt`

### Authentication Channels
```kotlin
enum class Channel { KAKAO, NAVER, GUEST }
```
Found in: `common/Channel.kt`

## Package Structure Reference

```
com.yh.fridgesoksok/
├── presentation/       # UI layer (Compose screens, ViewModels)
├── domain/            # Business logic (models, repositories, use cases)
├── data/              # Data layer (repository implementations, entities)
├── remote/            # Remote API (Retrofit services, implementations)
├── local/             # Local storage (Room database, DAOs)
├── common/            # Shared utilities (Resource, Logger, Constants, SessionEvents, UserCancelledException)
├── service/           # Background services (FCM)
└── di/                # Global Hilt modules
```
