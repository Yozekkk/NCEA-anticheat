# Getting started

NCEA AntiCheat 0.1.0 Development observes events on Paper 1.20/1.20.1. Read the [known limitations](KNOWN_LIMITATIONS.md) first.

1. Prepare the matching Paper version and Java 17.
2. Copy `dist/NCEA-AntiCheat-1.20.jar` or `dist/NCEA-AntiCheat-1.20.1.jar` into `plugins/`.
3. Start the server and look for `Status: ENABLED` in the ASCII banner.
4. From console or an admin account, run `nceaac status`, `nceaac checks`, and `nceaac config validate`.
5. Staff with `nceaac.alerts` opt into notifications with `/nceaac alerts`.

Edit `plugins/NCEAAntiCheat/config.yml` and `checks.yml`, then run `/nceaac reload`. Invalid values keep the previous live settings. See [installation](INSTALLATION.en.md) and [configuration](CONFIGURATION.en.md).
