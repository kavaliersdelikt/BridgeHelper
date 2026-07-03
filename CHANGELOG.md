# Changelog

## [1.5.0] — 2026-07-03

### Bug Fixes
- **Avg Speed display** — The "Speed" line in the feedback overlay was showing the timing error value instead of the actual average movement speed. Added a dedicated `avgSpeed` field to `TimingResult` and wired it through the analyzer and renderer.
- **UI Scale not applied** — The `uiScale` slider in the ClickGUI had no effect on the rendered feedback. The renderer now multiplies `uiScale` into the overall scale calculation alongside `feedbackScale`.
- **Gradle wrapper line endings** — `gradlew` shipped with Windows CRLF line endings, causing `sh\r: No such file or directory` on Linux/macOS. Converted to LF and upgraded the wrapper to Gradle 4.10.3 for Java 11 compatibility.

### New Features
- **Presets system** — Three one-click presets accessible from a new PRESETS category in the ClickGUI:
  - **Default** — Standard bridging tolerances and feedback duration.
  - **Sensitive** — Tighter perfect window (8ms), reduced tolerances, sound enabled. For experienced players who want precise feedback.
  - **Casual** — Wider perfect window (25ms), generous tolerances, longer feedback display. For relaxed gameplay.
- **Reset All Defaults** — A button in the ADVANCED category instantly restores every setting to its factory default.
- **Config migration** — Added a `configVersion` field. Existing v1 config files are automatically detected and migrated (uiScale reset to 1.0) on first load, preventing stale or broken settings.

### Improvements
- **Bridge detection reliability** — Reworked `BridgeDetector` to reduce false positives and better handle real bridging scenarios:
  - Requires 2 consecutive ticks of bridging posture before triggering (eliminates flicker).
  - Allows slight upward motion (up to 0.3 blocks) to support jump-bridging onto higher blocks.
  - Increased block placement distance threshold from 4 to 5 to support diagonal bridging.
  - Relaxed upward-velocity rejection from `> 0` to `> 0.1` for smoother detection.
- **Feedback label granularity** — Added a "NEARLY PERFECT" tier and adjusted thresholds in user-friendly mode for more meaningful timing feedback (±8ms = slight, ±20ms = way too early/late).

### Build
- Bumped mod version to 1.5.0 in `build.gradle` and `@Mod` annotation.
- Changed Maven group from `com.example.bridge` to `me.bridge.helper`.
- Upgraded Gradle wrapper from 4.4.1 to 4.10.3.

---

## [1.0.0] — Initial Release

- Real-time bridge timing analysis with PERFECT / TOO_EARLY / TOO_LATE classification.
- Configurable ClickGUI with FEEDBACK, TIMING, UI, and ADVANCED categories.
- On-screen feedback overlay with animations, colors, and optional sound.
- Movement tracking, sprint detection, and sneak-timing analysis.
- JSON-based persistent configuration.
