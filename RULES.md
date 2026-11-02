# AI Coding Rules for ats book

These rules adopt the **ECC (Everything-Claude-Code)** guidelines customized for **ats book**:

1. **Jetpack Compose Only:** Pure Jetpack Compose UI with Material 3. Do NOT use XML layouts.
2. **Offline First:** All story data reads directly from local asset files (`assets/stories.json`). Do NOT write network API calls for stories.
3. **Clean Architecture & MVVM:**
   - Keep UI (Composables), ViewModels, and Data Models separated logically.
   - UseCases for single-responsibility business operations.
   - Domain layer must remain pure Kotlin with zero Android framework dependencies.
4. **Kotlin Idioms & Immutability:**
   - Always prefer `val` over `var` and immutable collections.
   - Never use `!!` non-null assertion. Use `?.`, `?:`, or `requireNotNull()`.
   - Update state via `data class` `.copy()`.
5. **Coroutines & Reactive State:**
   - Always use structured concurrency with `viewModelScope`. Never use `GlobalScope`.
   - Expose UI state as `StateFlow` using `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ...)`.
   - Never catch `CancellationException`.
6. **Error Handling & Resilience:**
   - Safely handle JSON parsing and empty asset scenarios without crashing the app (`runCatching` / sealed states).
7. **Performance & Reader UX:**
   - Ensure smooth scrolling performance for long story text using `LazyColumn` with unique keys.
   - Stateless composables with state hoisted; use `remember` to avoid unnecessary reallocations.

For complete architectural details, see [AGENTS.md](file:///workspace/ats-book/AGENTS.md) and the rules in [.agents/rules/](file:///workspace/ats-book/.agents/rules/).
