# README visual audit — AngelOS source adaptation

## Scope

NCEA AntiCheat 1.0.0 Stable keeps its existing Java code, JARs and Git tag. The Russian and English README now use **real adapted SVG source files** from AngelOS-Dotfiles at commit `e5f771cfee9dedf81b6e4a648dd53c763d904e09`. The project still has five active checks; the 12 tapes are presentation chapters, not check registrations.

The source files were inspected as SVG code. `header.svg` retains the 1280×560 VHS frame, REC/PLAY OSD, clipped timecode reels, scanline pattern, layered cyan/purple glitch text and original keyframe timings. `divider.svg` retains moving tracking noise. Each `tape-*.svg` retains its individually colored cassette shape, paper texture, handwritten title, on-screen clock and playback animation. The source `@media (prefers-reduced-motion:reduce)` rules are retained.

## Source-to-NCEA mapping

| AngelOS source | NCEA adaptation | Main change | Geometry and motion retained | License | Local check |
|---|---|---|---|---|---|
| `header.svg`, `header.ru.svg` | `docs/assets/angelos-adapted/header*.svg` | NCEA AntiCheat, Studio, 1.0.0 Stable, Paper | VHS composition, OSD, glitches, timecode | MIT + embedded font notices | Rendered EN/RU |
| `divider.svg` | `docs/assets/angelos-adapted/divider.svg` | NCEA accents | Tracking-noise paths and keyframes | MIT | XML/render |
| `cobweb-divider.svg`, `cobweb-tl.svg`, `cobweb-tr.svg` | Same names under `angelos-adapted/` | NCEA accents | Original network/web geometry and effects | MIT | XML/render |
| `stamp-evidence.svg`, `.ru.svg` | `stamp-evidence*.svg` | OBSERVE / NO AUTO BAN | Stamp distress, skew and layout | MIT + font notice | XML/render |
| `tape-01.svg`, `.ru.svg` | `tape-01*.svg` | Introduction / Введение | Cassette artwork, playback clock | MIT + font notices | Rendered in 12-tape sheet |
| `tape-02.svg`, `.ru.svg` | `tape-02*.svg` | Installation / Установка | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-03.svg`, `.ru.svg` | `tape-03*.svg` | Detection Engine / Движок проверок | Same | MIT + font notices | Rendered individually |
| `tape-04.svg`, `.ru.svg` | `tape-04*.svg` | Movement Checks / Движение | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-05.svg`, `.ru.svg` | `tape-05*.svg` | Combat Checks / Бой | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-06.svg`, `.ru.svg` | `tape-06*.svg` | World Checks / Проверки мира | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-07.svg`, `.ru.svg` | `tape-07*.svg` | Configuration / Конфигурация | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-08.svg`, `.ru.svg` | `tape-08*.svg` | Console Branding / Консоль NCEA | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-09.svg`, `.ru.svg` | `tape-09*.svg` | Architecture / Архитектура | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-10.svg`, `.ru.svg` | `tape-10*.svg` | Testing / Тестирование | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-11.svg`, `.ru.svg` | `tape-11*.svg` | Documentation / Документация | Same | MIT + font notices | Rendered in 12-tape sheet |
| `tape-12.svg`, `.ru.svg` | `tape-12*.svg` | NCEA Studio / Release | Damaged tape and signal-loss animation | MIT + font notices | Rendered in 12-tape sheet |
| `end.svg`, `end.ru.svg` | `end*.svg` | NCEA Studio, end of report | Blue signal-loss frame, flicker, OSD | MIT + font notice | Rendered EN/RU |

The exhaustive **34-row per-file license record** is [ASSET_CREDITS.md](ASSET_CREDITS.md). No AngelOS GIF or desktop image was transferred. `commands.gif`, `violations.gif`, and `stable-release.gif` are newly generated NCEA schematics; `paper-startup.gif` replays a real local Paper 1.20.1 startup excerpt. All four have PNG reduced-motion alternatives. Older NCEA technical GIFs are reused and remain labeled as illustrations.

## Composition and local inspection

The README follows the upstream visual sequence: large VHS header, left/right decorative motifs, evidence stamp, animated tracking divider, 12 titled tapes, technical visuals, per-tape `<details>` inventories, signal-web dividers and a final blue VHS screen. Main text remains searchable Markdown. Both languages follow the same 12-part structure.

Local rendering with `rsvg-convert` and visual inspection covered both headers, EN/RU 12-tape contact sheets, individual tape 03, corrected tape 09, and end screen. No clipping was found in the inspected views. The English tape 09 initially retained its source title because the apostrophe was XML-escaped; the generator was corrected and the output re-rendered.

GitHub rendering and screenshots are recorded below after publication. SVG files retain their built-in reduced-motion rule, but GitHub did not propagate that rule reliably to the SVG displayed through `<img>`. The README therefore uses `<picture>` to select generated PNG still frames for all adapted SVGs and existing static SVG/PNG frames for GIFs. The source uses embedded font subsets (Cozette MIT, Pixeloid Sans/Caveat OFL), with notices in `docs/licenses/`.

## GitHub rendering

Initial published commit `0663329` was checked in Chromium through GitHub: the Russian README loaded 42 images with no broken sources and showed all 12 tapes. A one-second pair of header screenshots changed by 32,477 pixels in the header region, confirming that the external SVG animation runs on GitHub. Reduced-motion emulation exposed the `<img>` media-query gap; static `<picture>` fallbacks were then added and require post-push revalidation. Full-page comparison screenshots are stored locally in `.local/angelos-reference-full.png` and `.local/ncea-readme-ru-full.png`; they are excluded from Git. At 1280 px width, the NCEA page follows the same header → tapes → demonstrations → final signal screen rhythm as the source. NCEA is shorter because it uses honest technical illustrations instead of AngelOS desktop footage.
