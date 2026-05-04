# Mood Wheel MVP

Privacy-first Android mood tracker built with Kotlin, Jetpack Compose, Room, MVVM, and `StateFlow`.

## Why Kotlin + Compose

The brief asks for Room, Jetpack Compose, MVVM, and local-only Android storage. Flutter is a good cross-platform option, but this MVP keeps the native Android stack so Room and Compose stay first-class and simple.

## Project Structure

```text
app/src/main/java/com/example/moodwheel
├── data
│   ├── local
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt
│   │   ├── EntryDao.kt
│   │   └── EntryEntity.kt
│   └── repository
│       ├── MoodRepository.kt
│       └── SampleData.kt
├── domain
│   ├── export
│   │   └── EntryJsonExporter.kt
│   └── model
│       ├── MacroEmotion.kt
│       ├── MoodEntry.kt
│       └── MoodLevel.kt
├── ui
│   ├── components
│   │   ├── EmotionChips.kt
│   │   ├── EmotionWheel.kt
│   │   └── MoodSelector.kt
│   ├── screens
│   │   ├── AddMoodScreen.kt
│   │   ├── AddMoodViewModel.kt
│   │   ├── CalendarScreen.kt
│   │   ├── CalendarStatsViewModel.kt
│   │   ├── ExportScreen.kt
│   │   ├── ExportViewModel.kt
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── StatsScreen.kt
│   └── theme
│       ├── EmotionColor.kt
│       └── Theme.kt
├── MainActivity.kt
└── MoodWheelApplication.kt
```

## Data Model

Room stores one `EntryEntity` per check-in:

- `id`
- `timestamp`
- `moodLevel` from 1 to 5
- `primaryEmotion` as a stable macro emotion id
- `secondaryEmotions` as a Room-converted list
- `note`

No account, network sync, analytics, or remote storage is included.

## Emotion Wheel

`EmotionWheel` is a custom Compose `Canvas`:

- 6 large radial segments for macro emotions
- one-tap selection with immediate visual highlight
- center area intentionally ignored to reduce accidental taps
- selected macro remains visible above the micro emotion chip area

`EmotionChips` allows multi-select children such as `sereno`, `grato`, `fiducioso`.

## Screens

- Home: today prompt, last entry, weekly emotion dots, calm reminder, CTA
- Add Mood: 4 short steps for mood, emotion, date/time, note
- Calendar: month grid colored by dominant emotion per day
- Stats: simple counts, prevalent emotion, lightweight trend, recurring words
- Export: Android document picker writes local JSON

## Export JSON

`EntryJsonExporter` creates a stable `mood-wheel-v1` JSON document. The export screen uses Android's `CreateDocument` contract, so the user chooses where the file is saved.

## Build

Open `mood-wheel-compose` in Android Studio, let Gradle sync, then run the `app` configuration.

This environment had Java available, but Gradle and Android SDK tools were not on `PATH`, so the project was generated and statically reviewed rather than compiled here.
