# Changelog

All notable changes to PermCalc are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-09-28

One more demo — the clipboard, the kind of access that needs no permission at all.

### Added

- **The clipboard demo.** A sixth demo, and the sneakiest: reading the clipboard
  needs no Android permission at all, so there is no dialog and no background
  phase. It takes over its own screen, asks you to leave and copy anything
  anywhere on your phone, and reads what you copied the instant you return —
  text, images or file links. The reveal shows the clip, its metadata (label,
  copy time, whether the source app flagged it sensitive), and runs the same
  cheap scan a malicious app would: it singles out passwords, 2FA codes, card
  numbers (Luhn-checked), IBANs (mod-97-checked), crypto addresses, emails and
  phone numbers. Everything stays in memory and is dropped when the screen
  closes. Blunt by design, because there is no setting to hide behind — standard
  Android cannot stop this, and only recent versions of GrapheneOS can revoke it per app.
- **All six languages.** The demo — its info sheet, the leave-and-copy
  instructions, the reveal and the scanner labels — is translated into English,
  Deutsch, Bosanski, Español, Français and Русский, like every other screen.

### Notes

- The demo is honest about Android's own protections: on Android 12 and newer the
  system flashes a brief "pasted from your clipboard" toast after a cross-app
  read, so the copy folds that in rather than claiming the read is invisible —
  it appears only after the data is already taken, and older versions show
  nothing at all.

## [1.1.0] - 2026-09-06

Three more languages, and a language switcher.

### Added

- **Español, Français and Русский.** PermCalc now speaks six languages, matching
  PortalGems. Every screen is translated, including the reveals and the
  "how it's abused" panels.
- **The language switcher on the disclaimer screen.** Previously it lived only in
  the calculator header, which meant the first thing you saw — the screen asking
  you to accept — was stuck in a language you might not read. It is now on both
  screens.

### Changed

- **The language button opens a menu instead of cycling.** With six languages,
  cycling meant up to five taps to reach yours. The menu lists all of them at
  once, each named in its own language — English, Deutsch, Bosanski, Español,
  Français, Русский — because someone looking for their language needs to
  recognise it, not read its name in a language they do not speak.

### Fixed

- **Bosnian wording that read as Croatian.** `fotografirati` → `fotografisati`,
  `sučelje` → `interfejs`, `virtualni` → `virtuelni`, `sinkronizacija` →
  `sinhronizacija`, `stražnja kamera` → `zadnja kamera`, `pohrana` → `memorija`,
  and `dohvaćanje` → `određivanje`.
- **Several Bosnian strings that were simply wrong**, rather than regional: an
  ungrammatical negation in the disclaimer, a `bez da` Germanism, a verb typo
  (`preuzimeš`), an untranslated English plural (`stalkersima` → `uhodama`), and
  a location label that collided with the word for weather.

[1.1.0]: https://github.com/Yenis/perm-calc/releases/tag/v1.1.0

## [1.0.0] - 2026-09-05

First release. A calculator that asks for permissions it has no business
asking for, then shows you exactly what it could do with each one - on your
own device, with nothing leaving the phone.

### Added

- **Five permission demos** behind a working calculator: camera (a silent
  photo), microphone (a short recording, played back), contacts (your address
  book, read out), location (where you are, broken down), and media storage
  (the photos on your device).
- **A reveal for every demo** explaining what an app can learn from that
  permission, and how the same access looks when it is used legitimately
  versus abused.
- **Three languages** - English, German and Bosnian.
- **Nothing is uploaded.** Every demo runs on-device, and closing the app
  resets it to a first-time state.

[1.0.0]: https://github.com/Yenis/perm-calc/releases/tag/v1.0.0
