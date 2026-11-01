# NCEA AntiCheat project rules

- Target Java 17 bytecode and Paper 1.20 / 1.20.1 only. Build both source sets against their exact API versions.
- Keep core independent of Bukkit. Paper API calls and player state mutation stay on the primary server thread.
- Register only checks with implemented evidence logic and tests. Never advertise planned checks as active.
- Treat ambiguous movement, latency, low TPS, teleports, velocity, liquids, special blocks and plugin motion conservatively.
- Default to observe only. Do not add automatic bans or setbacks without gameplay validation.
- No copied GPL/AGPL code. Keep third-party revision, license and attribution records current.
- Configuration changes must validate before replacing live settings. Document every setting that affects runtime.
- Run `./gradlew build` and inspect both JARs after changes. Do not call compilation an integration test.

- StartupBanner renders ASCII only after successful initialization; never report ENABLED after config or platform failure.
- Keep config migration additive, validate before applying, and back up existing files before writes.
- Run JUnit and JaCoCo for core and Paper settings, then cold-start both final JARs under Java 17.
