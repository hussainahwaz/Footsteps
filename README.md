Footsteps (might change name later)

~ in the code the app is written as workoutcalender cause that's initially how this started as. Will change that later. Just needed an intial commit

This is a quiet, GitHub-contribution-style habit tracker for Android. Log workouts, reading, meditation, or any recurring habit as tiles on a calendar grid, and see your consistency build up over time.

Built with Kotlin, Jetpack Compose and Material 3.

Features

- Multiple trackers — create as many habits as you want, each with its own name, icon, color, and completion method.
- Three completion methods per tracker
  - Quick Tap — tap a tile to mark a day done.
  - Detailed — tap opens a form to log specifics (sets/reps, notes, etc.).
  - Both — tap to mark done, long-press to open the detailed entry.
- Goal frequency — trackers aren't limited to "did it today." Set a target of 1–7 days a week; streaks and stats adapt automatically (daily trackers streak on consecutive days, weekly-goal trackers streak on consecutive weeks that hit target).
- Contribution-style calendar grid with three display modes:
  - Normal — full weekday-aligned grid with day numbers.
  - Minimal — compact, left-packed grid with day numbers, no weekday header.
  - Super Minimal — tiles only, for a quick glance.
- Monthly and yearly views per tracker, plus an overall "how consistent was I" home grid across all trackers.
- Statistics — this month, total completed, current streak, best streak (and "this week" progress for weekly-goal trackers).
- Create, edit, and delete trackers — trackers can be renamed and have their icon, color, completion method, or goal frequency changed at any time after creation.
- Five color themes (Sage, Ocean, Blossom, Cream, Electric Indigo) plus independent dark mode, both persisted across restarts.
- Local persistence via Jetpack DataStore — trackers, entries, and preferences survive app restarts with no account or backend required.

Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Jetpack DataStore (Preferences) for local persistence
- kotlinx.serialization for storing tracker data as JSON
- AndroidX Lifecycle (`lifecycle-viewmodel-compose`) for theme state
- Min SDK 26, target/compile SDK 37

 Project structure

```
com.example.workoutcalender
├── data/
│   ├── TrackerRepository.kt      # DataStore-backed persistence for trackers/entries
│   └── ThemePreferences.kt       # DataStore-backed persistence for theme + dark mode
├── model/
│   ├── Tracker.kt                # Core domain model, streak logic, presets
│   └── TrackerIcons.kt           # Icon key -> ImageVector registry
├── ui/
│   ├── components/               # Reusable UI: contribution grid, tiles, stat block, etc.
│   ├── screens/                  # Home, Trackers list, Tracker detail, Create/Edit form, Settings
│   └── theme/                    # Color themes, typography, ConsistencyTheme
└── MainActivity.kt                # App entry point, navigation stack, top-level state
```

Building

1. Clone the repo and open it in Android Studio.
2. Let Gradle sync (uses a version catalog — `gradle/libs.versions.toml`).
3. Run on a device or emulator with API 26+.

No API keys, backend, or additional setup required — everything runs and persists locally on-device.

Status

v1 — Initial release. Core tracking, theming, and per-tracker goal customization are in place. Planned next: drag-to-reorder trackers on Home/Trackers screens.

License

This project is licensed under the MIT License.
