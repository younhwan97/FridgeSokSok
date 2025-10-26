# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FridgeSokSok is an Android application for managing fridge inventory and generating recipes. Built with Kotlin and Jetpack Compose, it follows Clean Architecture principles with MVVM presentation layer.

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
./gradlew connectedDebugAndroidTest

# Run all checks (lint + tests)
./gradlew check
```

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
```

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
  - `SharedViewModel.kt` - Activity-scoped ViewModel (minimal use, only for receipt bitmap)
  - `HomeViewModel.kt` - Event coordinator for tab operations using SharedFlows

#### **Domain Layer** (`domain/`)
- Pure Kotlin domain models: `Food`, `Recipe`, `User`, `Fridge`, `Token`, `UserSetting`
- Repository interfaces defining data contracts
- Use cases follow operator pattern with `invoke()` method
- Example: `CreateRecipeUseCase`, `GetFoodsUseCase`, `UpdateFoodUseCase`

#### **Data Layer** (`data/`)
- Repository implementations bridge domain and data sources
- Entity models (e.g., `FoodEntity`) with bidirectional mapping: `toDomain()` and `toEntity()`
- All repository methods return `Flow<Resource<T>>` for reactive async handling
- Helper: `flowWithResource { }` wraps operations with Loading/Success/Error states

#### **Remote Layer** (`remote/`)
- Retrofit API services: `FridgeApiService`, `KakaoApiService`, `NaverApiService`
- RemoteDataSource implementations transform API responses to entities
- Interceptors:
  - `RequestHeaderInterceptor` - Adds authorization headers
  - `LoggingHeaderInterceptor` - HTTP request/response logging

#### **Local Layer** (`local/`)
- Room database with DAOs (`FoodDao`, `UserDao`)
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
  - Subscribing ViewModels collect triggers and provide feedback via callbacks
- **Navigation Data**: Pass data via `savedStateHandle` for type-safe argument passing
- **Activity-Scoped State**: `SharedViewModel` minimally used (currently only receipt bitmap)

### Recent Refactoring Pattern
Recent commits show intentional removal of shared ViewModels in favor of trigger-based communication:
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

// In subscriber ViewModel
init {
    coordinatorViewModel.actionTrigger
        .onEach { performAction() }
        .launchIn(viewModelScope)
}
```

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
- Use private mutable backing properties: `private val _state = MutableStateFlow(...)`
- Expose immutable public properties: `val state = _state.asStateFlow()`
- For cross-component events, prefer SharedFlow triggers over shared ViewModels
- Always launch coroutines in `viewModelScope`
- Wrap repository calls with Resource handling

### When Modifying Repositories
- All methods must return `Flow<Resource<T>>`
- Use `flowWithResource { }` helper to wrap data source calls
- Implement both interface (in domain) and implementation (in data)
- Add DI binding in `data/di/RepositoryModule.kt`

### When Modifying Data Models
- Add mapping extensions: `fun FooResponse.toEntity()`, `fun FooEntity.toDomain()`
- For Parcelize models (navigation args), use `@Parcelize` annotation
- Keep domain models pure Kotlin (no Android dependencies)
- Entity models can have Android/Gson dependencies

### When Modifying Navigation
- Update `Screen.kt` sealed class with new routes
- Add navigation arguments to `Screen` constructor if needed
- Use `savedStateHandle` in ViewModel to retrieve navigation arguments
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
├── common/            # Shared utilities (Resource, Logger, Constants)
├── service/           # Background services (FCM)
└── di/                # Global Hilt modules
```
