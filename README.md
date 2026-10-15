# Social News — Android MVP Starter

A Kotlin + Jetpack Compose starter project for the Social News app. It includes a responsive Compose UI, bottom navigation, search, category filtering, clearly labelled development sample stories, article detail view, local in-memory bookmarks, a Live empty state, and theme/language switches.

## Important status
This is an MVP starter, not the complete production application. Real news, persistent storage, official source links, video playback, weather, sports scores, quizzes, push notifications, admin backend, privacy/consent flows, and AdMob are not configured. Sample stories are labelled as demo content and must not be presented as current news.

## Requirements
- Android Studio (recent stable version)
- JDK 17
- Android SDK platform 35 (or update `compileSdk` / `targetSdk` to the SDK installed)
- Internet connection for Gradle dependency downloads

## Open and build
1. Extract the ZIP.
2. Open the `SocialNewsAndroid` folder in Android Studio.
3. Allow Gradle sync to complete.
4. Connect an Android phone with USB debugging enabled or start an emulator.
5. Run the `app` configuration.

Build a debug APK from the project root:

```bash
./gradlew assembleDebug
```

On Windows:

```bat
gradlew.bat assembleDebug
```

Expected APK path after a successful build:

`app/build/outputs/apk/debug/app-debug.apk`

If the project has no Gradle wrapper files, use Android Studio's Gradle sync/run workflow or generate a wrapper with a compatible installed Gradle version (`gradle wrapper`). Do not assume the APK exists until the build succeeds.

## Next implementation phases
1. Add repository interfaces and Retrofit/OkHttp data sources for a licensed news API or official RSS feeds.
2. Keep API secrets on a backend; never ship private provider keys in the APK.
3. Add Room for saved articles/history and DataStore for preferences.
4. Add AndroidX Media3 ExoPlayer for authorized HLS streams and verify stream availability before showing “LIVE”.
5. Add official weather/sports data providers as separate optional modules.
6. Add a real quiz data set and scoring.
7. Add Firebase Cloud Messaging only after configuring a Firebase project and notification preferences.
8. Build a backend/admin panel with authenticated roles before allowing content publishing or user-report moderation.
9. Add AdMob test ads only after account setup; keep ads disabled until consent and policy requirements are implemented.
10. Publish a privacy policy, terms, data-safety disclosures, and complete Play Console review requirements.

## Data-source policy
Use licensed APIs, official RSS feeds, official government notices, and authorized live streams. Preserve publisher attribution and original URLs. Do not scrape paid channels or imply demo content is verified current news.
