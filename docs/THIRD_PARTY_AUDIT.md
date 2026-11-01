# Third-party audit

Revisions inspected on 2026-11-01. No source fragments copied into NCEA. All algorithms here are original and use general concepts only.

| Project | Commit SHA | License file | Concepts studied | Direct code | Redistribution obligations if copied |
|---|---|---|---|---|---|
| [GrimAC](https://github.com/GrimAnticheat/Grim) | `3dc139b7474777326a8925b9d6f3ed15efcaac19` | `LICENSE`, GPL-3.0 | prediction pipeline, packet-to-check separation, setback concept | none | GPL source and notices for derivatives; excluded |
| [Updated NoCheatPlus](https://github.com/Updated-NoCheatPlus/NoCheatPlus) | `9ce8772fcf0cfc2fc71c0f8c83b3647f63e43223` | `LICENSE.txt`, GPL-3.0 | per-check violations, action policy, movement exceptions | none | GPL source and notices for derivatives; excluded |
| [LightAntiCheat](https://github.com/tiredvekster/LightAntiCheat) | `ec801a0eca80aa8d53587411e643bead7c8bdb1a` | `LICENSE`, AGPL-3.0 | context exemptions, check organization, alerts | none | AGPL source/network obligations for derivatives; excluded |
| [IceLater](https://github.com/nuekkis/Minecraft-Anti-Cheat) | `b180e1855dabcd812f76321f052dda3cb25f1910` | `LICENSE`, MIT, copyright 2025 nuekkis | small Paper check registry and configuration layout | none | retain copyright and license if code copied; no copied code |

The source samples inspected include Grim `PredictionRunner`, NCP `ViolationData`, LAC `SpeedA` and IceLater `CheckManager`. No algorithms or expressions were transcribed. GPL/AGPL and MIT permit modification subject to their respective conditions; NCEA does not bundle those projects.

The five installed skills came from [github/awesome-copilot](https://github.com/github/awesome-copilot), whose `LICENSE` is MIT. They were already present under `/home/tima/.agents/skills`; no duplicate installation occurred. [TiQIMENG/paper-plugin-dev](https://github.com/TiQIMENG/paper-plugin-dev) at `26ddaf0ad41d285748d8095ae1583f94e423ea9d` has no repository license endpoint, so it was inspected but not installed or copied. Its README describes a Claude skill, not a Paper API guarantee.

## Навыки и runtime-зависимости

Новые локальные инструкции `java-docs` и `create-readme` скопированы из закреплённого commit `5269f7ea2d6c3a9bfda5ed878b6726cf509121fd` репозитория `github/awesome-copilot` под MIT; текст лицензии сохранён в `.agents/skills/_upstream/awesome-copilot-LICENSE`. `java-testcontainers` изучен, но не устанавливался и не применяется из-за отсутствия Docker. Подробности — в [SKILLS.md](SKILLS.md).

Paper API подключён как compile-only для JAR. JUnit 5 и JaCoCo используются только при тестировании/сборке; они не входят в итоговый плагин. Сторонний код античитов в JAR не включён.

## GitHub README artwork

## AngelOS README artwork — current documentation adaptation

The current README uses **34 adapted SVG source files** from [MixaDoDs/AngelOS-Dotfiles](https://github.com/MixaDoDs/AngelOS-Dotfiles) pinned at `e5f771cfee9dedf81b6e4a648dd53c763d904e09`. Its own artwork is MIT © 2026 MixaDoDs; the [full MIT notice](licenses/ANGELOS-MIT.txt) is included. Embedded Cozette, Pixeloid Sans and Caveat subsets retain their separate [MIT](licenses/COZETTE-MIT.txt) and [SIL OFL 1.1](licenses/ANGELOS-FONTS-OFL-1.1.txt) notices. The asset-by-asset mapping is in [ASSET_CREDITS.md](ASSET_CREDITS.md). The NCEA source-code `LICENSE` remains unchanged and does not relicense these graphics.

No AngelOS demo GIFs, desktop screenshots, wallpapers, icons, SDDM theme, plugin code or other third-party media were copied. Three new NCEA technical GIFs are labelled as schematics; a fourth replays an actual local Paper startup log excerpt. The older section below records the previous README artwork state before this adaptation; it is retained as history.


[AngelOS-Dotfiles](https://github.com/MixaDoDs/AngelOS-Dotfiles) изучен как пример визуального ритма, анимаций и раскрывающихся секций. Его SVG, GIF, изображения, разметка и текст в NCEA не копировались. Новые файлы `docs/assets/readme/` сгенерированы для NCEA из предоставленного `Ascii/Ascii final result.png` и оригинального кода генератора. Python Pillow и локальный `rsvg-convert` используются только при генерации графики; в Gradle-сборку и Paper-плагин не входят.
