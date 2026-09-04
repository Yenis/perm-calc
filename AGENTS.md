# PermCalc — native Android app

This is a **native Kotlin + Jetpack Compose** app (Material 3). It was migrated
off React Native / Expo; there is no JS, Metro, or `node_modules` anymore.

- Build: `./gradlew :app:assembleDebug` (output: `app/build/outputs/apk/debug/app-debug.apk`).
- Package `com.permcalc.app`, minSdk 26, targetSdk 34, compileSdk 35.
- UI is 100% Compose under `app/src/main/java/com/permcalc/app/`.
- Educational strings live in `app/src/main/res/values{,-de,-bs}/strings.xml` (EN/DE/BS).
- Permission demos are in `.../demos/` (CameraX, MediaRecorder, ContactsContract,
  LocationManager, MediaStore).

It's an educational privacy-awareness demo: it shows how granted Android
permissions can be abused, running each demo entirely on-device (nothing is
uploaded).
