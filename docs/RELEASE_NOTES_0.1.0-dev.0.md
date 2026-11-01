# NCEA AntiCheat 0.1.0 Development

**Pre-release** for Paper **1.20** and **1.20.1**, Java **17**. Developed by **NCEA Studio**.

## Available in this build

- Five observe-mode heuristic checks: Speed, Fly, Reach, AutoClicker, FastPlace.
- Independent violation levels with decay, context exemptions, staff alerts, and administrative commands.
- Validated YAML settings and reload; original NCEA AC Unicode console branding with themes, ANSI fallbacks, and a standalone preview.
- Two separately compiled artifacts against the matching Paper API versions. The binaries are identical because the shared source produces the same bytecode; they were tested on both server versions.

## Verification

- 77 JUnit 5 tests passed: 60 core and 17 Paper-layer tests.
- Local Paper 1.20 and 1.20.1 cold and repeat server starts completed on Java 17. Commands, reload, startup banner and shutdown were checked.
- 16 additional local server starts covered eight branding modes on each Paper version.
- Both JARs have SHA-256 `c98a0dd1c0cc845bda58e4a29305572fa42dfa03600430c8d7635f260cb58eda`.

## Limits

This release remains **Development**. It has no packet checks, physical prediction, automatic punishment, or setbacks. A connected Minecraft client, real PvP gameplay, false-positive rates, and TPS impact under load have not been tested. Use it as an observable development build and review alerts manually.

Documentation: [README](../README.md) · [checks](CHECKS.md) · [test results](TEST_RESULTS.md) · [known limitations](KNOWN_LIMITATIONS.md) · [third-party audit](THIRD_PARTY_AUDIT.md).
