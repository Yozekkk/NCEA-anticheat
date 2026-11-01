# Installation

| Server | Artifact | Java | Expected banner |
|---|---|---|---|
| Paper 1.20 | `dist/NCEA-AntiCheat-1.20.jar` | 17+ | `Platform: Paper 1.20` |
| Paper 1.20.1 | `dist/NCEA-AntiCheat-1.20.1.jar` | 17+ | `Platform: Paper 1.20.1` |

Stop the server, place **one** matching JAR in `plugins/`, and start Paper. No companion plugin is required. The first start creates `plugins/NCEAAntiCheat/config.yml` and `checks.yml`. Existing schema-1 configs receive missing banner keys with a `config.yml.bak-<timestamp>` backup.

An unsupported server version disables the plugin with an error. `Status: ENABLED` appears only after successful initialization. Check `/nceaac status`, `/nceaac checks`, and `/nceaac config validate`. Back up configurations before replacing a JAR. Build from source with `./gradlew clean build` and a full JDK 17+.

## Console branding

On first start, the plugin creates `branding.yml` and `custom-banner.txt` in `plugins/NCEAAntiCheat/`. The default original Unicode logo appears only after successful initialization. ANSI colors use conservative auto detection. Edit `branding.yml`, run `/nceaac reload`, and restart to view the updated startup logo. A standalone preview is available with `./gradlew :ncea-paper:previewBanner`.
