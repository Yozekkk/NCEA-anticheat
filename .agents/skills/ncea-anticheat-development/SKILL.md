---
name: ncea-anticheat-development
description: Develop and verify NCEA AntiCheat 1.0.0 Stable for Paper 1.20 and 1.20.1.
---
# NCEA AntiCheat development

Read `AGENTS.md`, `docs/ARCHITECTURE.md`, `docs/CHECKS.md`, `docs/CONFIGURATION.md`, and `docs/THIRD_PARTY_AUDIT.md` before changing checks. Target Java 17 bytecode and compile both source sets independently against their pinned Paper API versions.

Keep Paper API access on the main server thread. Core check observations are immutable. A new check requires a concrete event source, evidence condition, safety exclusions, VL rules, settings validation, boundary tests, gameplay scenario, and documentation. Do not register names without working detectors. Disabled checks must not mutate player state. Reset transient state after teleports, velocity, respawn, reload, or exemptions. Limit player-owned queues and clean sessions on quit and disable.

For every change: run focused JUnit 5 tests, `JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build`, inspect both Java 17 JARs and resources, then cold-start Paper 1.20 and 1.20.1 under Java 17. Record actual outcomes and JaCoCo results in `docs/TEST_RESULTS.md`. Do not equate compilation with gameplay validation. Banner output must occur once after successful startup and never report `ENABLED` after a critical failure.

Console branding lives in `ncea-core/.../branding` and `ncea-paper/.../BrandingLoader.java`. Preserve the seven visible rows and segment geometry derived from `Ascii/NCEA_AC_original_banner.sh`; the snapshot test guards them. Keep geometry, palette, ANSI formatting, and Paper logging separate. Any new branding option needs YAML validation, a real renderer behavior, unit tests, a preview, and an entry in `docs/CONFIG_REFERENCE.md`. Reject unsafe custom template controls and never execute template content. `branding.yml` is authoritative after the one-time migration from legacy `config.yml` banner settings. Test `colors: on` and `colors: off` without quotes because Bukkit YAML 1.1 parses them as booleans.
