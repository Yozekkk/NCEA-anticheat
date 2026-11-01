# NCEA AntiCheat 1.0.0 Stable

**NCEA Studio** · **Paper 1.20 / 1.20.1** · **Java 17**

This is the first stable-channel release of the functionality currently implemented. Stable is the owner's release decision; it does not imply perfect detection or completed gameplay benchmarking.

## Included

- Five active heuristic checks: **Speed**, **Fly**, **Reach**, **AutoClicker**, and **FastPlace**.
- Independent violation levels and decay, contextual exemptions, staff alerts, administrative commands, validated YAML reload.
- Default **OBSERVE** mode. Alerts are available; automatic bans and setbacks are not implemented.
- Two separately compiled Paper artifacts and a configurable NCEA AC console banner derived from the owner's original pixel artwork.
- New NCEA Studio README artwork: animated hero, check illustrations, architecture, status, console, configuration, testing and documentation panels, plus reduced-motion static alternatives.

## Verification

- Clean Gradle build: successful.
- JUnit 5: **77 passed, 0 failed** (60 core, 17 Paper).
- JaCoCo line coverage: **81.4% core**, **49.2% Paper adapter**. Coverage is not evidence of gameplay detection accuracy.
- Separate Paper 1.20 and 1.20.1 smoke starts: cold and repeat starts passed on both versions with Java 17; commands and valid/invalid reload passed on cold starts. See the [test report](https://github.com/Yozekkk/NCEA-anticheat/blob/main/docs/TEST_RESULTS.md).
- A connected Minecraft client and real PvP load were **not** tested. Detection and false-positive rates were **not** measured.

## Upgrade from 0.1.0 Development

1. Back up `plugins/NCEAAntiCheat/` and your server.
2. Stop the server. Replace the old JAR with **one** matching your Paper version.
3. Start on Java 17 and check the `Status ENABLED` banner, `/nceaac version`, `/nceaac checks` and `/nceaac config validate`.
4. Review existing YAML values and alerts; the five checks remain in OBSERVE by default.

## Known limits

No packet checks, predictive physics, automatic punishments or setbacks. Reach has no packet-history compensation; AutoClicker observes successful attacks rather than all mouse clicks. Hosting web consoles and full client gameplay have not been validated. See [known limitations](https://github.com/Yozekkk/NCEA-anticheat/blob/main/docs/KNOWN_LIMITATIONS.md).

## Downloads

- [Paper 1.20 JAR](https://github.com/Yozekkk/NCEA-anticheat/releases/download/v1.0.0/NCEA-AntiCheat-1.20.jar)
- [Paper 1.20.1 JAR](https://github.com/Yozekkk/NCEA-anticheat/releases/download/v1.0.0/NCEA-AntiCheat-1.20.1.jar)

The previous `v0.1.0-dev.0` prerelease remains available as an archive.
