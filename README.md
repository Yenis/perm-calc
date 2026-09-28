<p align="center">
  <img src="assets/banner.png" alt="GemsTech gem portals" width="800">
</p>

# PermCalc

**A calculator that asks for permissions it has no business asking for — then
shows you exactly what it could do with each one. On your own device, with
nothing leaving the phone.**

The arithmetic works. Nothing about the interface hints at what the app is
capable of. Tap one of the permission demos under the keypad and PermCalc does
what any app could do the moment you tap *Allow*: takes your photo, records
audio, reads your address book, pinpoints your location, lists every photo on
your phone. Then it shows you what it got.

Every demonstration runs **entirely on your device**. The app holds no internet
permission, so it *cannot* upload anything, even if it wanted to — and you can
[verify that yourself](#privacy-model) in the APK you install.

It is built for people who have tapped *Allow* a thousand times without ever
seeing the other side of that dialog.

---

## Table of contents

- [Download](#download)
- [Why PermCalc?](#why-permcalc)
- [What it demonstrates](#what-it-demonstrates)
- [How it works](#how-it-works)
- [Privacy model](#privacy-model)
- [Using it to teach](#using-it-to-teach)
- [Architecture](#architecture)
- [Building from source](#building-from-source)
- [Releasing](#releasing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

---

## Download

Grab the latest build from the
**[releases page](https://github.com/Yenis/perm-calc/releases/latest)**:

| Platform | File | Notes |
|---|---|---|
| Android 8.0+ | `PermCalc-<version>-android.apk` | Sideload; enable "install from unknown sources" |

Building from source instead? See [Building from source](#building-from-source).

### Verifying your download

Every APK ships with a matching `.sha256` file so you can confirm it downloaded
intact and untampered. After downloading both the APK and its `.sha256`, run:

```bash
# Linux / macOS
sha256sum -c PermCalc-1.1.0-android.apk.sha256

# Windows (PowerShell) - check the two hashes match
Get-FileHash PermCalc-1.1.0-android.apk -Algorithm SHA256
Get-Content PermCalc-1.1.0-android.apk.sha256
```

A matching hash means the file is byte-for-byte what was published.

## Why PermCalc?

Android's permission dialog asks a question most people cannot actually answer.
*"Allow PermCalc to take pictures and record video?"* — allow it to do **what**,
exactly? Once? While you watch? Forever, silently, in the background?

Reading about it does not land. Most people have read that apps can misuse
permissions and have granted them anyway five minutes later, because the warning
is abstract and the *Allow* button is right there.

PermCalc closes that gap by making it concrete and personal:

- **The disguise is the lesson.** A calculator is the most unremarkable thing on
  a phone, which is exactly what makes it good cover — and exactly how a
  malicious app presents itself. Nobody audits a calculator.
- **It uses your data, not a mock-up.** Your face, your voice, your address book,
  your street. A screenshot of someone else's contacts teaches nothing.
- **It acts while you are looking away.** Grant a permission and you are returned
  to the calculator. The capture happens in the background, with a banner telling
  you it is happening — the one courtesy a real attacker would never extend.
- **It shows both sides.** Every permission is presented with its legitimate uses
  *and* its abuses. The lesson is "understand what you are granting", not
  "deny everything".
- **It cannot betray you.** No internet permission, no analytics, no accounts,
  no SDKs — and that is checkable in the APK you install rather than promised
  here. See [Privacy model](#privacy-model).

## What it demonstrates

| Permission | What PermCalc does with it | What it shows you afterwards |
|---|---|---|
| **Camera** | Silently captures one frame from the front camera and one from the rear — no shutter sound, no preview, no flash | Both photos, saved into your gallery under `Pictures/PermCalc` |
| **Microphone** | Records 8 seconds of audio the instant the permission is granted | The clip, with a play button, so you hear exactly what was captured |
| **Contacts** | Reads your entire address book — names, phone numbers, email addresses | The full count and a scrollable list of the people it found |
| **Location** | Takes a GPS/network fix and reverse-geocodes it | Street, neighbourhood, city, postal code, district, region, country, decimal and DMS coordinates, altitude, accuracy, provider and fix time |
| **Storage / Media** | Enumerates every photo and video on the device | The total count and the most recent items, with names and metadata |
| **Clipboard** | Reads whatever you have copied — text, images, file links — the moment the app returns to the foreground | The clip itself, its metadata, and the passwords, 2FA codes, card numbers, IBANs and wallet addresses a malicious app's scanner would pull straight out of it |

Each demo is bracketed by two screens: an **info sheet** before (what this
permission is for, how it gets abused) and a **reveal sheet** after (the data
itself, plus what a malicious app would have done with it instead of showing
you).

The **clipboard** demo is the exception, and deliberately so: there *is* no
clipboard permission on Android, so there is no dialog and no background phase.
It takes over the screen, asks you to leave and copy something anywhere on your
phone, and reads the clipboard the instant you return — because that is exactly
what any foreground app can do, silently, with nothing to grant and nothing to
notice. Standard Android offers no way to stop it; only
[GrapheneOS](https://grapheneos.org/features) lets you revoke clipboard access
per app.

Available in **English, Deutsch and Bosanski** — tap the language chip in the
header to switch.

## How it works

The whole app is a single flow, repeated once per permission:

```
   ┌───────────────┐
   │  Disclaimer   │  Shown on EVERY launch - acceptance is deliberately
   │               │  not persisted, so every viewer gets the first-time
   └───────┬───────┘  experience, including the next person you hand it to.
           │
           ▼
   ┌───────────────┐
   │  Calculator   │  A real, working calculator. Below the keypad:
   │  7 8 9  ÷     │  ⚠ PERMISSION DEMOS - camera, mic, contacts,
   │  4 5 6  ×     │  location, storage.
   └───────┬───────┘
           │  tap a demo
           ▼
   ┌───────────────┐
   │   Info sheet  │  What this permission is legitimately for,
   │  legit│abuse  │  and how it is abused. Grant, or skip.
   └───────┬───────┘
           │  Grant Permission
           ▼
   ┌───────────────┐
   │ Android's own │  The real system dialog. PermCalc never fakes,
   │    dialog     │  styles or pre-empts it.
   └───────┬───────┘
           │  Allow
           ▼
   ┌───────────────┐
   │  Calculator   │  You are handed back to the calculator, free to
   │ ▓ working in  │  keep using it - while an amber banner reports
   │   background  │  what is happening behind it, right now.
   └───────┬───────┘
           │  capture completes
           ▼
   ┌───────────────┐
   │ Reveal sheet  │  Your photo. Your voice. Your contacts. Your street.
   │   your data   │  Plus: what a real attacker would have done instead.
   └───────────────┘
```

The background step is the part that changes minds. The data is not collected
while you watch a progress bar in a dedicated screen — it is collected while you
are doing arithmetic and have already forgotten you granted anything.

## Privacy model

PermCalc is an app that photographs you and reads your contacts. It only
deserves to be installed if that is verifiable rather than promised, so:

- **No `INTERNET` permission.** This is the load-bearing guarantee. Android will
  not let the process open a socket, so no captured data can leave the device by
  any code path, intentional or accidental. Everything else below follows from
  it. Check the shipped APK yourself:

  ```bash
  # aapt2 ships in your Android SDK under build-tools/<version>/
  aapt2 dump permissions PermCalc-1.1.0-android.apk
  ```

  `INTERNET` is absent from the list. Every permission it *does* request maps to
  a demo you have to start yourself.

- **No analytics, no crash reporting, no accounts, no ads, no SDKs** of any kind.
- **Captured data stays where you can reach it.** Photos go to your gallery
  (`Pictures/PermCalc`) so you can inspect and delete them with any photo app.
  The audio clip is written to the app's private storage as
  `permcalc_demo.m4a` and is overwritten on each run; uninstalling removes it.
  Contacts, location and media listings are read into memory to be displayed and
  are never written to disk.
- **Nothing is retained between launches.** Even the disclaimer acceptance is
  intentionally not persisted. The only thing PermCalc remembers is your chosen
  language.
- **Permissions are only ever requested when you ask for that demo**, through
  Android's own dialog, and skipping a demo is always offered alongside granting
  it.

The source is here to be read — the demos are a handful of short files in
[`app/src/main/java/com/gemstech/permcalc/demos/`](app/src/main/java/com/gemstech/permcalc/demos/),
using nothing but plain platform APIs.

## Using it to teach

PermCalc was built to be handed to someone, not just installed. A few things
that make that work:

- **Hand over the phone before saying what the app is.** Let them use the
  calculator first. The disclaimer resets on every launch, so the next person
  gets the same cold open.
- **Start with the camera demo.** Seeing their own face appear in a photo they
  did not know was taken is the moment the abstraction breaks.
- **Then do the microphone.** Hearing the room played back is the one people
  describe as unsettling afterwards.
- **Finish with location.** The reveal breaks the fix down to the street, which
  makes "an app knows where you are" stop sounding like a slogan.
- **Read the "How it's abused" panel out loud** and then the legitimate uses next
  to it. The goal is calibrated judgement, not blanket refusal — someone who
  denies every permission out of fear has not learned more than someone who
  grants every one out of habit.

## Architecture

A single-module native Android app. Kotlin, Jetpack Compose, Material 3. The
only runtime dependencies are AndroidX/Compose, CameraX and the Kotlin
coroutines runtime — no third-party SDKs.

```
app/src/main/java/com/gemstech/permcalc/
├── MainActivity.kt        disclaimer gate, language switching, theme host
├── Calculator.kt          the calculator engine (it really is a calculator)
├── ui/
│   ├── CalculatorScreen.kt  keypad, permission-demo row, background banner
│   ├── ClipboardScreen.kt   the clipboard demo's own full screen (no permission)
│   ├── DisclaimerScreen.kt  the first-run explanation
│   ├── InfoSheet.kt         "legitimate uses" vs "how it's abused", pre-grant
│   ├── RevealSheet.kt       the post-capture reveal, per permission
│   ├── DemoStrings.kt       maps a demo to its string resources
│   ├── Localization.kt      EN/DE/BS switching at runtime
│   ├── Common.kt            shared buttons and bottom-sheet scaffolding
│   └── theme/Theme.kt       the Tanzanite palette (see Branding, below)
└── demos/
    ├── CameraDemo.kt        CameraX - front + rear capture, MediaStore save
    ├── MicrophoneDemo.kt    MediaRecorder - 8s AAC clip
    ├── ContactsDemo.kt      ContactsContract - names, numbers, emails
    ├── LocationDemo.kt      LocationManager + Geocoder - fix and address
    ├── StorageDemo.kt       MediaStore - image and video enumeration
    ├── Permissions.kt       per-demo permission sets, API-level aware
    └── Models.kt            result types
```

All user-facing text lives in `app/src/main/res/values{,-de,-bs}/strings.xml`.
Adding a language is a matter of adding a `values-xx/strings.xml` and extending
`SUPPORTED_LANGS`.

### Branding

PermCalc ships under the **GemsTech** brand alongside
[PortalGems](https://github.com/Yenis/portal-gems). Its gem is **Tanzanite —
Insight**: the app exists to change what you believe about your phone, not
merely to present facts legibly. Theme color lives in `ui/theme/Theme.kt`;
`res/values/colors.xml` mirrors the handful of values the platform needs before
Compose starts.

Semantic colors stay independent of the gem — green for the legitimate framing,
red for the abusive one, and Citrine amber for the live background-activity
banner, which reports something happening *now* rather than delivering a
verdict.

## Building from source

### Prerequisites

- JDK 17
- Android SDK (compileSdk 35, build-tools 35), via Android Studio or
  `sdkmanager`

No Node, no Rust, no `node_modules` — this app was migrated off React
Native/Expo and is now plain Kotlin.

### Debug build

```bash
./gradlew :app:assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Release build

```bash
./gradlew :app:assembleRelease
# → app/build/outputs/apk/release/app-release.apk
```

Release builds are signed with your own keystore via a `keystore.properties` at
the repository root (never committed):

```properties
PERMCALC_UPLOAD_STORE_FILE=permcalc-release.keystore
PERMCALC_UPLOAD_KEY_ALIAS=permcalc
PERMCALC_UPLOAD_STORE_PASSWORD=...
PERMCALC_UPLOAD_KEY_PASSWORD=...
```

with the keystore itself at `app/permcalc-release.keystore`. Without that file,
release builds fall back to the debug key so a clean checkout still builds.

## Releasing

Pushing a version tag builds and publishes everything:

```bash
# 1. bump versionCode / versionName in app/build.gradle.kts
# 2. add the matching ## [x.y.z] section to CHANGELOG.md
# 3. tag and push
git tag v1.0.1 && git push origin v1.0.1
```

[`.github/workflows/release.yml`](.github/workflows/release.yml) then creates the
GitHub release (using that CHANGELOG section as its notes), builds a signed APK,
and attaches it with a `.sha256`. The workflow needs four repository secrets:

| Secret | Value |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | `base64 -w0 app/permcalc-release.keystore` |
| `ANDROID_KEYSTORE_PASSWORD` | store password |
| `ANDROID_KEY_ALIAS` | key alias |
| `ANDROID_KEY_PASSWORD` | key password |

These are the same secret names PortalGems uses, so both repositories configure
identically.

## License

PermCalc is free software: you may redistribute and modify it under the terms of
the **[GNU General Public License v3.0 or later](LICENSE)**.

Copyright © 2026 Yenis.

Dependencies (AndroidX, Jetpack Compose, CameraX, Kotlin coroutines) are all
Apache-2.0, which is compatible with GPLv3.

### Why copyleft

The choice of a copyleft license is deliberate, and specific to what this
repository contains.

PermCalc is, in effect, a complete surveillance payload with the network access
removed. Silent dual-camera capture, audio recording, a full address-book dump
and location resolution down to the street are all implemented here and working.
The only thing standing between this repository and functional spyware is the
absent `INTERNET` permission and an upload call — a diff of maybe fifteen lines.

Under a permissive license, someone could fork it, add those fifteen lines,
close the source, and ship the result. Under the GPL, any modified version that
gets distributed has to carry its source with it — which directly defeats the
point of covert spyware, because the covertness is the product.

This does not stop someone who ignores licenses outright. Nothing does. What it
does is remove the legitimate path entirely and leave the author standing to act
on the illegitimate one.

There is also a consistency argument. This README asks you to trust the app by
reading it — to check for yourself that it holds no `INTERNET` permission and
cannot phone home. A license that lets that source disappear from every
downstream copy would undercut the one guarantee the app is built on. Keeping
the source visible downstream is the same promise, extended to everyone who
receives a fork.

## Acknowledgments

- The **Android Open Source Project**, for a permission model that is genuinely
  good — runtime prompts, granular media access, and the privacy indicators
  that, on Android 12 and newer, make this app's point visible in the status
  bar while it runs.
- Everyone who has been handed this phone, tapped *Allow*, and then said
  "wait, it already did it?" — that reaction is the entire specification.
