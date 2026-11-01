<div align="center">

[Русский](README.md) · **English**

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/hero.svg" /><img src="docs/assets/readme/hero-motion.gif" alt="NCEA AntiCheat with the original NCEA AC pixel mark" width="100%" /></picture>

**Minecraft Server AntiCheat by [NCEA Studio](https://ncea-studio.com)**

<img src="docs/assets/readme/stable-badge.svg" alt="1.0.0 Stable" height="38" /> <img src="docs/assets/readme/paper-badge.svg" alt="Paper 1.20 / 1.20.1" height="38" /> <img src="docs/assets/readme/java-badge.svg" alt="Java 17" height="38" /> <img src="docs/assets/readme/gradle-badge.svg" alt="Gradle" height="38" /> <img src="docs/assets/readme/checks-badge.svg" alt="5 checks" height="38" /> [<img src="docs/assets/readme/ci-badge.svg" alt="CI" height="38" />](https://github.com/Yozekkk/NCEA-anticheat/actions/workflows/ci.yml)

[Install](#installation) · [Checks](#detection-engine) · [Console](#ncea-ac-console) · [Docs](docs/INDEX.md) · [Русский](README.md)

</div>

<img src="docs/assets/readme/matrix-divider.svg" alt="Decorative digital divider" width="100%" />

## System

NCEA AntiCheat is NCEA Studio's modular Paper plugin. **1.0.0 Stable** collects suspicious movement, combat and placement evidence, maintains an independent violation level (VL) for each check, and alerts staff. The default **OBSERVE** mode neither cancels player actions nor issues automatic bans.

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/status.svg" /><img src="docs/assets/readme/stable-status.gif" alt="1.0.0 Stable: observe mode" width="100%" /></picture>

> [!IMPORTANT]
> This stable-channel release contains heuristic checks. No connected-client gameplay validation, detection rate, or real false-positive rate has been measured. A successful server startup is not a PvP test.

## Detection engine

<img src="docs/assets/readme/checks.svg" alt="Five implemented checks" width="100%" />

| Check | Signal | State |
|---|---|---|
| **Speed** | Horizontal distance in short `PlayerMoveEvent` windows | Working heuristic |
| **Fly** | Sustained near-zero vertical movement off ground | Working heuristic |
| **Reach** | Eye-to-server-hitbox distance on a damaging attack | Working, no packet history compensation |
| **AutoClicker** | Unique successful attacks within one second | Working; does not count all clicks |
| **FastPlace** | Intervals between successful block placements | Working heuristic |

Context exemptions, ping, TPS, and server movement grace windows reduce obvious false signals. One signal does not establish cheating. [Mechanisms and limitations](docs/CHECKS.en.md).


### Five signals · five visual models

These are algorithm illustrations, not recorded gameplay or detection measurements.

**Speed**

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/speed-demo.svg" /><img src="docs/assets/readme/speed-demo.gif" alt="Illustration of Speed" width="100%" /></picture>

**Fly**

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/fly-demo.svg" /><img src="docs/assets/readme/fly-demo.gif" alt="Illustration of Fly" width="100%" /></picture>

**Reach**

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/reach-demo.svg" /><img src="docs/assets/readme/reach-demo.gif" alt="Illustration of Reach" width="100%" /></picture>

**AutoClicker**

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/autoclicker-demo.svg" /><img src="docs/assets/readme/autoclicker-demo.gif" alt="Illustration of AutoClicker" width="100%" /></picture>

**FastPlace**

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/fastplace-demo.svg" /><img src="docs/assets/readme/fastplace-demo.gif" alt="Illustration of FastPlace" width="100%" /></picture>

<img src="docs/assets/readme/digital-snow.svg" alt="Digital snow" width="100%" />

## Architecture

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/architecture.svg" /><img src="docs/assets/readme/architecture-motion.gif" alt="NCEA AntiCheat module architecture" width="100%" /></picture>

`ncea-core` contains observations, checks, VL/decay, player data, and branding without Paper imports. `ncea-paper` handles Paper events, exemptions, YAML, commands, and lifecycle. Shared sources compile separately against Paper API **1.20** and **1.20.1**. [Architecture notes](docs/ARCHITECTURE.md).

## NCEA AC console

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/terminal.svg" /><img src="docs/assets/readme/console-boot.gif" alt="Rendered console branding preview" width="100%" /></picture>

<details><summary>Show the real monochrome logo output</summary>

```text
▀█▄████▄      ▄█▀██▄▄     ▄█▀██▄▄    ▄▀▀▀▀▄▄           ▄▀▀▀▀▄▄       ▄█▀██▄▄ 
 ███  ▀██   ▄██   ████  ▄██   ████     ▄▄▄███            ▄▄▄███    ▄██   ████
 ███   ███ ████    ▀▀  ████  ▄██▀   ▄██  ▀███         ▄██  ▀███   ████    ▀▀ 
 ███   ███ ███▀        ███▀▄█▀▀    ██▄█   ▀█▄        ██▄█   ▀█▄   ███▀       
 ▄▀    ▄▀▄ ▀▄▀▀        ▀▄▀▀▌       ▀▄▀▄   ▀▄▀        ▀▄▀▄   ▀▄▀   ▀▄▀▀       
   ▀    ▀▀     ▄   ▄▄▀     ▄   ▄▄▀  ▀██  ▄███ ▄       ▀██  ▄███ ▄     ▄   ▄▄▀
 ▀▀▀   ▀     ▀▀▀▀▀▀      ▀▀▀▀▀▀        ▀▀ ▀▀ ▀           ▀▀ ▀▀ ▀    ▀▀▀▀▀▀   
```

The geometry comes from the [provided Bash source](Ascii/NCEA_AC_original_banner.sh), alongside the [original image](Ascii/Ascii%20final%20result.png). Java never executes Bash to render it.

</details>

<img src="docs/assets/readme/logo-preview.gif" alt="Animated pixel logo branding preview, no gameplay footage" width="68%" />

*This GIF previews branding, not gameplay.* Terminal colors use conservative auto detection; ANSI 16/256/TrueColor, themes, Unicode/ASCII, and safe custom templates are available. [Branding guide](docs/CONSOLE_BRANDING.md) · [Custom banner](docs/BANNER_CUSTOMIZATION.md).

<img src="docs/assets/readme/cyber-divider.svg" alt="Neon divider" width="100%" />

## Configuration

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/config-demo.svg" /><img src="docs/assets/readme/config-demo.gif" alt="Illustrative YAML configuration" width="100%" /></picture>

First start creates `config.yml`, `checks.yml`, `branding.yml`, and `custom-banner.txt` under `plugins/NCEAAntiCheat/`.

```yaml
# branding.yml
branding:
  banner:
    style: original-3d
    charset: unicode
  console:
    colors: auto
    color-mode: ansi16
  theme:
    name: ncea-original
```

```yaml
# checks.yml excerpt
checks:
  speed:
    enabled: true
    weight: 1.0
    decay: 0.15
    alert-threshold: 5.0
```

`/nceaac reload` validates settings before applying them. OBSERVE mode does not punish players. [Check configuration](docs/CONFIGURATION.en.md) · [Branding reference](docs/CONFIG_REFERENCE.md) · [Presets](presets/).

## Installation

1. Download the JAR matching your Paper version from the [v1.0.0 stable release](https://github.com/Yozekkk/NCEA-anticheat/releases/tag/v1.0.0).
2. Put it in `plugins/` on **Paper 1.20 or 1.20.1** running **Java 17**.
3. Start the server and check for `Status ENABLED` beneath the logo.
4. Run `/nceaac status`, `/nceaac checks`, and `/nceaac config validate`.
5. Edit the YAML files and run `/nceaac reload`.

<details><summary>Build and preview locally</summary>

```bash
./gradlew clean build
./gradlew :ncea-paper:previewBanner
```

A full JDK is needed to build. Sources compile with `--release 17`; both JARs appear in `dist/`. [Installation details](docs/INSTALLATION.en.md) · [Compatibility](docs/COMPATIBILITY.md).

</details>

## Commands

| Command | Purpose |
|---|---|
| `/nceaac status` · `/nceaac version` | Version and status |
| `/nceaac checks` | Five check states |
| `/nceaac alerts` | Toggle staff alerts |
| `/nceaac violations <player>` | Online player's VL and latest evidence |
| `/nceaac reset <player>` | Clear VL and temporary counters |
| `/nceaac exempt <player>` · `/nceaac unexempt <player>` | Temporary exemption |
| `/nceaac reload` · `/nceaac config validate` | YAML reload and validation |
| `/nceaac performance` | Local counters and check timing |

`/nceaac help` lists commands; `profile` and `debug` currently share the `violations` output. [All commands](docs/COMMANDS.md) · [Permissions](docs/PERMISSIONS.md).

## Testing

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/testing-panel.svg" /><img src="docs/assets/readme/testing-panel.gif" alt="Testing panel; actual results in the table" width="100%" /></picture>

| Test | Actual result |
|---|---|
| JUnit 5 | **77 tests**, 0 failures (60 core, 17 Paper) |
| JaCoCo | Core **81.4%** line coverage; Paper **49.2%** |
| Paper 1.20 / 1.20.1 | Local cold and repeat startup on Java 17 |
| Banner variants | 16 server starts, eight variants on each version |
| Connected client | Not tested |

[Method](docs/TESTING.md) · [Results](docs/TEST_RESULTS.md) · [QA](docs/QA_REPORT.md) · [Performance](docs/PERFORMANCE.md).

## Roadmap

| Available now | Planned |
|---|---|
| Five observe-mode checks, VL/decay, exemptions, alerts, commands, branding | Packet inspection, physical simulation, more precise latency compensation, client-based PvP tests, false-positive and TPS measurements |

Automatic punishment and setbacks need separate implementation and gameplay validation. [Known limitations](docs/KNOWN_LIMITATIONS.md).

## Documentation and provenance

[Docs index](docs/INDEX.md) · [Quick start](docs/GETTING_STARTED.en.md) · [Developer guide](docs/DEVELOPMENT.md) · [File map](docs/FILE_MANIFEST.md) · [Changelog](docs/CHANGELOG.md) · [Release Notes 1.0.0](docs/RELEASE_NOTES_1.0.0.md)

The original product source was written for NCEA Studio. [LICENSE](LICENSE) currently states **All rights reserved** and grants no right to copy or modify the source. No code from the researched GPL/AGPL anticheats is bundled. Third-party sources and tool licenses are recorded in the [audit](docs/THIRD_PARTY_AUDIT.md).

<picture><source media="(prefers-reduced-motion: reduce)" srcset="docs/assets/readme/footer.svg" /><img src="docs/assets/readme/neon-footer.gif" alt="NCEA Studio — ncea-studio.com" width="100%" /></picture>
