# Configuration

The plugin reads only `plugins/NCEAAntiCheat/config.yml` and `checks.yml`. See the [full Russian reference](CONFIGURATION.md) for the complete key table.

| Key | Type / range | Default | Effect |
|---|---|---|---|
| `schema-version` | integer `1` | `1` | Schema version |
| `max-ping` | integer 50–1000 ms | `250` | Skip checks above this ping |
| `min-tps` | number 10–20 | `18.0` | Skip checks below this TPS |
| `console.banner.enabled` | boolean | `true` | Print banner after successful startup |
| `console.banner.style` | `classic`, `3d`, `compact`, `disabled` | `3d` | Banner style |
| `console.banner.colors` | boolean | `false` | ANSI only on an interactive terminal |
| `console.banner.show-version` | boolean | `true` | Show plugin version |
| `console.banner.show-server` | boolean | `true` | Show Paper version |
| `console.banner.show-checks` | boolean | `true` | Show active check count |
| `console.banner.show-startup-time` | boolean | `true` | Show plugin initialization time |

Every implemented check has `enabled` (boolean), `weight` (>0–10 VL per flag), `decay` (0–10 VL/s), and `alert-threshold` (1–100 VL). Invalid values reject reload and preserve the previous live configuration. New banner keys are added to older schema-1 configs with a backup. `presets/` contains three manually applied check templates; there is no automatic preset switch. All checks remain observe-only.

## Console branding

The new `branding.yml` is authoritative after a one-time migration from legacy `config.yml: console.banner`. It controls the original seven-row logo, ANSI modes, themes, fallback, information fields, and custom UTF-8 template. See [console branding](CONSOLE_BRANDING.md), [configuration reference](CONFIG_REFERENCE.md), and [custom template](BANNER_CUSTOMIZATION.md). The English README summarizes the available options.
