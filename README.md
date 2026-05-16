# WeatherSnap

A native Android app that lets users search live weather for a city, create a weather report using a custom camera screen, compress the captured image, save notes locally, and view saved reports.

## Tech Stack

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MVVM** architecture with **ViewModel** + **StateFlow**
- **Coroutines** for async operations
- **Hilt** for dependency injection
- **Navigation Compose** for screen navigation
- **Retrofit** + **Gson** + **OkHttp Logging** for API calls
- **Room Database** for local data persistence
- **CameraX** for custom camera implementation

## API

Uses [Open-Meteo](https://open-meteo.com/) — **no API key required**.

- Geocoding: `https://geocoding-api.open-meteo.com/v1/search`
- Forecast: `https://api.open-meteo.com/v1/forecast`

## Setup & Run

1. Clone the repository
2. Open the project in **Android Studio** (Hedgehog or newer)
3. Sync Gradle dependencies
4. Connect a physical Android device or start an emulator (min SDK 24)
5. Run the app (`Shift + F10` or click Run)

> **Note:** Camera features require a physical device or an emulator with camera support.

## App Screens

1. **Weather Screen** — Search city, view live weather (temperature, condition, humidity, wind, pressure)
2. **Create Report** — Frozen weather snapshot, capture photo via custom camera, add notes
3. **Custom Camera** — CameraX-based full-screen camera (no device camera intent)
4. **Saved Reports** — View all saved reports with captured image, weather data, image sizes, notes, and timestamp

## Developer Judgment Challenge

### Approach: SavedStateHandle for Draft Persistence

The in-progress report state (weather snapshot, image path, notes) is stored in `SavedStateHandle` inside the `ReportViewModel`. This ensures the draft survives:

- **Configuration changes** (screen rotation)
- **Process death** (app backgrounded and system kills it)

### Why this approach:

- **No duplicates**: Data is only written to Room DB on explicit "Save Report" tap. No premature DB writes means no duplicate entries from config changes.
- **Weather snapshot integrity**: The weather data is passed as navigation arguments and stored in SavedStateHandle — it's the exact data from when the user tapped "Create Report", not a re-fetched result.
- **Temp file cleanup**: If the user navigates back without saving, temp image files are cleaned up in `onCleared()`.

### Tradeoffs:

- SavedStateHandle has a ~1MB size limit, but we only store file paths (strings) and numeric values, not actual bitmaps — so this is well within limits.
- If the process is killed mid-draft and the user never returns to the app, a single temp image file may remain in storage until the next session. This is an acceptable tradeoff for simplicity.

## Project Structure

```
com.weathersnap/
├── di/                    # Hilt dependency injection
├── data/
│   ├── remote/            # Retrofit API + DTOs
│   ├── local/             # Room DB + Entity + DAO
│   └── repository/        # Repositories
├── domain/model/          # Domain models
├── ui/
│   ├── theme/             # Material 3 dark theme
│   ├── navigation/        # Navigation graph
│   ├── weather/           # Weather screen + ViewModel
│   ├── report/            # Create report screen + ViewModel
│   ├── camera/            # Custom CameraX screen
│   └── savedreports/      # Saved reports screen
└── util/                  # Weather code mapper, image compressor
```
