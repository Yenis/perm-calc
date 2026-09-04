# PermCalc — native Android app

This is a **native Kotlin + Jetpack Compose** app (Material 3). It was migrated
off React Native / Expo; there is no JS, Metro, or `node_modules` anymore.

- Build: `./gradlew :app:assembleDebug` (output: `app/build/outputs/apk/debug/app-debug.apk`).
- Package `com.gemstech.permcalc`, minSdk 26, targetSdk 34, compileSdk 35.
- UI is 100% Compose under `app/src/main/java/com/gemstech/permcalc/`.
- Educational strings live in `app/src/main/res/values{,-de,-bs}/strings.xml` (EN/DE/BS).
- Permission demos are in `.../demos/` (CameraX, MediaRecorder, ContactsContract,
  LocationManager, MediaStore).

It's an educational privacy-awareness demo: it shows how granted Android
permissions can be abused, running each demo entirely on-device (nothing is
uploaded).

## Branding

PermCalc ships under the **GemsTech** brand alongside PortalGems
(`com.gemstech.portalgems`). Its gem is **Tanzanite — Insight**: the app's
purpose is a shift in what the user believes about their phone, not merely a
legible presentation of facts. All theme color lives in
`ui/theme/Theme.kt::Palette`; `res/values/colors.xml` mirrors the three values
the platform needs before Compose starts (window background, launcher
background, accent), so change both together.

Semantic colors stay independent of the gem: green for the legitimate framing,
red for the abusive one, and Citrine amber (`caution*`) for the live
background-activity banner — that banner reports something happening *now*,
not a verdict, so it must not read as the malicious framing.

## Releases

Pushing a `v*` tag builds a signed APK and attaches it to a GitHub release —
see `.github/workflows/release.yml`. The release notes come from the matching
`## [x.y.z]` section of `CHANGELOG.md`, so write that section before tagging,
and bump `versionCode`/`versionName` in `app/build.gradle.kts` to match the tag
(CI warns on a mismatch but does not fail).

Release signing reads an untracked `keystore.properties` at the repo root
(`PERMCALC_UPLOAD_STORE_FILE`, `_KEY_ALIAS`, `_STORE_PASSWORD`, `_KEY_PASSWORD`).
CI writes it from the `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`,
`ANDROID_KEY_ALIAS` and `ANDROID_KEY_PASSWORD` secrets — the same secret names
PortalGems uses, so both repos configure identically. When the file is absent,
`assembleRelease` falls back to the debug key so a clean checkout still builds.
