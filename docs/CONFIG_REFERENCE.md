# Справочник `branding.yml`

Файл создаётся в `plugins/NCEAAntiCheat/` при первом запуске. Схема `1`; неизвестные ключи и неверные типы отклоняются. `/nceaac reload` проверяет новый снимок до применения; при ошибке остаётся прежняя конфигурация. Значения из старого `config.yml: console.banner` мигрируют один раз, если `branding.yml` ещё нет. Затем действует `branding.yml`.

| Ключ относительно `branding` | Тип; умолчание | Допустимые значения и эффект |
|---|---|---|
| `enabled` | boolean; `true` | Всё оформление при старте и остановке |
| `banner.enabled` | boolean; `true` | Заставка при успешном включении |
| `banner.style` | string; `original-3d` | `original-3d`, `monochrome`, `ascii`, `compact`, `custom`, `disabled` |
| `banner.charset` | string; `unicode` | `unicode`, `ascii`, `auto`; несовместимый набор вызывает fallback |
| `banner.alignment` | string; `center` | `left`, `center` в пределах `banner.width` |
| `banner.width` | `auto`/integer; `auto` | 20–120 ячеек; при недостатке ширины действует fallback |
| `banner.fallback` | string; `compact` | `compact`, `ascii`, `disabled` |
| `console.colors` | string; `auto` | `auto`, `on`, `off`; `auto` консервативен. YAML 1.1 читает незаключённые в кавычки `on`/`off` как boolean; загрузчик принимает их |
| `console.color-mode` | string; `ansi16` | `ansi16`, `ansi256`, `truecolor` |
| `theme.name` | string; `ncea-original` | `ncea-original`, `ncea-purple`, `ncea-cyan`, `monochrome`, `custom` |
| `theme.custom.primary` | HEX; `#5555FF` | `#RRGGBB`, основной цвет пользовательской темы |
| `theme.custom.shadow` | HEX; `#0000AA` | `#RRGGBB`, тень |
| `theme.custom.accent` | HEX; `#AAAAAA` | `#RRGGBB`, подписи/разделители |
| `theme.custom.background` | HEX; `#000000` | `#RRGGBB`, только фон HTML-предпросмотра |
| `information.show-version` | boolean; `true` | Версия из `plugin.yml` |
| `information.show-studio` | boolean; `true` | Строка NCEA Studio |
| `information.show-platform` | boolean; `true` | Обнаруженная версия Paper |
| `information.show-java` | boolean; `true` | Версия запущенной Java |
| `information.show-checks` | boolean; `true` | Число включённых проверок |
| `information.show-mode` | boolean; `true` | Режим OBSERVE |
| `information.show-startup-time` | boolean; `true` | Время инициализации плагина в мс |
| `information.show-status` | boolean; `true` | ENABLED после успешной инициализации |
| `shutdown.enabled` | boolean; `true` | Короткое сообщение при отключении |
| `shutdown.style` | string; `compact` | `compact` (2 строки), `single-line` |
| `custom.file` | filename; `custom-banner.txt` | Локальное имя `.txt` без каталогов |

Верхний `schema-version` — целое число `1`. Поставляемый файл содержит комментарии и рабочие значения. Параметры пяти проверок описаны в [CONFIGURATION](CONFIGURATION.md), а правила собственного шаблона — в [BANNER_CUSTOMIZATION](BANNER_CUSTOMIZATION.md).
