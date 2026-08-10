# Aitake (MyChat) — Android

Minimal Android chat app (Kotlin + Jetpack Compose + Firebase). Built with Gradle 8.5, AGP 8.2.2, compileSdk 34.

## Status

This project is a **UI skeleton** with stub screens. All auth flows, real-time chat, profile management, FCM, and presence features are placeholders. See the screens under `app/src/main/java/com/mychat/app/ui/` — every screen currently shows a placeholder text only.

## Build

```bash
# Requires JDK 17 and Android SDK with platform 34 + build-tools 34.0.0
gradle assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

## Firebase

`app/google-services.json` is a sample/placeholder config. For a real working build, replace it with your own Firebase project's config and add `google-services` plugin credentials.

## Structure

- `app/src/main/java/com/mychat/app/`
  - `MainActivity.kt`, `MyApplication.kt`
  - `navigation/` — NavGraph + Screen routes
  - `ui/auth/` — Login, Register, ForgotPassword (stubs)
  - `ui/chat/`, `ui/home/`, `ui/search/`, `ui/profile/`, `ui/settings/`, `ui/splash/`, `ui/theme/` — all stubs
