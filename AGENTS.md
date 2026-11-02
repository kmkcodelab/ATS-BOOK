# AGENTS.md — Development Guidelines for ats book

This document specifies the architectural patterns, coding standards, and agent rules for **ats book**, adopting the standards defined in the Everything-Claude-Code (ECC) repository adapted for Google Antigravity and Android Jetpack Compose development.

---

## 1. Project Overview & Core Constraints

- **Project Name:** `ats book` (Offline Story Reader App)
- **Language & Platform:** Kotlin, Android (Min SDK 28, Target SDK 36)
- **UI Framework:** Pure Jetpack Compose with Material 3 (No XML layouts)
- **Architecture:** Clean Architecture + MVVM (Unidirectional Data Flow)
- **Offline First:** All stories load from `assets/stories.json`. No network API calls for stories.
- **Preferences:** Jetpack DataStore / SharedPreferences for bookmarking, reading progress, and settings.

---

## 2. Clean Architecture & Layering Rules

Follow the Clean Architecture guidelines (reference: `.agents/skills/android-clean-architecture`):

1. **Domain Layer (Pure Kotlin):**
   - Contains domain models (`Story`, `Category`), repository interfaces, and use cases.
   - Must NEVER depend on Android framework classes or the UI layer.
   - Use Cases encapsulate a single business action using `operator fun invoke`.

2. **Data Layer:**
   - Contains repository implementations (`StoryRepository`, `PreferencesRepository`) and data sources (local asset reader, DataStore).
   - Maps data transfer / JSON objects to clean domain models before emitting to domain or UI.
   - Exposes reactive streams via `Flow` and suspend functions returning `Result<T>`.

3. **Presentation / UI Layer:**
   - Pure Jetpack Compose Composables organized by feature (`home`, `reader`, `bookmarks`).
   - ViewModels manage UI state using `StateFlow<UiState>` and handle user actions/events.
   - Composables must be stateless whenever possible, with state hoisted to caller or ViewModel.

---

## 3. Kotlin Coding Standards (from ECC)

Reference: `.agents/rules/kotlin-coding-style.md` and `.agents/rules/kotlin-patterns.md`

### Immutability & State Updates
- Prefer `val` over `var`. Default to immutable collections (`List`, `Set`, `Map`).
- Use Kotlin `data class` for models and UI state.
- Update state immutably using `.copy()`:
  ```kotlin
  _uiState.update { it.copy(stories = newStories, isLoading = false) }
  ```

### Null Safety
- **NEVER** use the non-null assertion operator `!!`.
- Use safe-call (`?.`), Elvis operator (`?:`), or `requireNotNull()` with descriptive messages.
- Use `?.let { }` for scoped operations on nullable values.

### Sealed State Hierarchies
- Model screen state as sealed interfaces/classes with exhaustive `when` handling (no wildcard `else` branches):
  ```kotlin
  sealed interface ReaderUiState {
      data object Loading : ReaderUiState
      data class Success(val story: Story, val isBookmarked: Boolean, val fontSize: Float) : ReaderUiState
      data class Error(val message: String) : ReaderUiState
  }
  ```

---

## 4. Coroutines & Concurrency Rules (from ECC)

Reference: `.agents/skills/kotlin-coroutines-flows`

1. **Structured Concurrency:**
   - Always use `viewModelScope` in ViewModels or `rememberCoroutineScope()` / `LaunchedEffect` in Composables.
   - **NEVER** use `GlobalScope`.
2. **Cold Flows to StateFlow:**
   - Convert cold flows to `StateFlow` in ViewModels using `stateIn`:
     ```kotlin
     val uiState: StateFlow<UiState> = repository.observeStories()
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5_000),
             initialValue = UiState.Loading
         )
     ```
3. **Cancellation & Error Handling:**
   - **NEVER** catch `CancellationException` — always let it propagate.
   - Handle I/O and JSON parsing safely using `runCatching` or specific try-catch blocks.
   - Dispatchers: Use `Dispatchers.IO` for asset reading/disk I/O and `Dispatchers.Default` for heavy JSON parsing or text processing.

---

## 5. Jetpack Compose & UI Guidelines

1. **Jetpack Compose Only:** No XML layouts.
2. **Performance & Smooth Scrolling:**
   - Use `LazyColumn` for long story text and story lists with unique `key` parameters.
   - Avoid creating new lambdas or allocating objects inside recomposition loops; use `remember`.
   - Provide `@Preview` annotations for key screens and components (Light and Dark theme variants).
3. **Typography & Reading Experience:**
   - Support adjustable font sizes (`PreferencesRepository`).
   - Line height multiplier of 1.5x for effortless long-session reading.
   - Support Dark Mode and Light Mode with soft eye-friendly colors.

---

## 6. Monitored Build Guidelines (PocketDev Environment)

When building Android debug APKs:
- Do NOT run plain `gradle build` or start Gradle daemons.
- Build only the debug APK using the designated PocketDev flags:
  - Cached fast path:
    ```bash
    timeout 5m gradle -Dorg.gradle.jvmargs= --no-daemon --max-workers=2 --offline --init-script /root/.gradle/init.d/pocketdev-android.gradle -Pandroid.aapt2FromMavenOverride=/root/android-sdk/build-tools/35.0.0/aapt2 assembleDebug --console=plain --stacktrace
    ```
  - First build / dependency resolution: remove `--offline` and allow up to 15m timeout.
- Poll long-running build tasks every 10 seconds without waiting silently.

---

## 7. Mounted Skills and Rule Files

The following ECC skills and rules are mounted directly in this project:
- `.agents/rules/kotlin-coding-style.md`
- `.agents/rules/kotlin-patterns.md`
- `.agents/rules/kotlin-security.md`
- `.agents/rules/kotlin-testing.md`
- `.agents/rules/common-coding-style.md`
- `.agents/rules/common-patterns.md`
- `.agents/rules/common-performance.md`
- `.agents/skills/android-clean-architecture/`
- `.agents/skills/kotlin-coroutines-flows/`
- `.agents/skills/kotlin-patterns/`
- `.agents/skills/kotlin-testing/`
