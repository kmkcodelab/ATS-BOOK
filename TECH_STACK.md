# Tech Stack Specifications for ats book

## Core Android Specifications
- **Language:** Kotlin
- **Minimum SDK:** 28 (Android 9.0)
- **Target SDK:** 36 (Android 15+)
- **UI Toolkit:** Jetpack Compose with Material 3

## Architecture & Data Management
- **Pattern:** MVVM (Model-View-ViewModel)
- **State Management:** StateFlow / State in Compose
- **Data Source:** Assets folder containing `stories.json` (Offline-first approach)
- **Preferences:** Jetpack DataStore (for storing bookmarks, theme preference, font size)

## Third-party & Monetization
- **Ads Integration:** Google Mobile Ads SDK (AdMob)
  - Banner Ad at the bottom of the reading screen.
  - Interstitial Ad between story transitions.
  - Rewarded Ad for unlocking premium/locked stories.