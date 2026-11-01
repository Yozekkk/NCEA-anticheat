# Конфигурация

Рабочие файлы находятся в `plugins/NCEAAntiCheat/`. Плагин загружает только `config.yml` и `checks.yml`; остальные файлы из первоначального плана не используются и не создаются.

## `config.yml`

| Ключ | Тип · диапазон | По умолчанию | Действие |
|---|---|---|---|
| `schema-version` | целое: `1` | `1` | Версия схемы |
| `max-ping` | целое: 50–1000 мс | `250` | При большем ping проверки пропускаются |
| `min-tps` | число: 10–20 | `18.0` | При меньшем TPS проверки пропускаются |
| `console.banner.enabled` | boolean | `true` | Показывать баннер после успешной загрузки |
| `console.banner.style` | `classic`, `3d`, `compact`, `disabled` | `3d` | Стиль баннера |
| `console.banner.colors` | boolean | `false` | ANSI только при наличии интерактивного терминала; обычный лог остаётся ASCII |
| `console.banner.show-version` | boolean | `true` | Показывать версию плагина |
| `console.banner.show-server` | boolean | `true` | Показывать версию Paper |
| `console.banner.show-checks` | boolean | `true` | Показывать число включённых проверок |
| `console.banner.show-startup-time` | boolean | `true` | Показывать время `onEnable` |

```yaml
schema-version: 1
max-ping: 250
min-tps: 18.0
console:
  banner:
    enabled: true
    style: 3d
    colors: false
    show-version: true
    show-server: true
    show-checks: true
    show-startup-time: true
```

## `checks.yml`

У каждой из **пяти реализованных** проверок одинаковые параметры. Дополнительные ключи для несуществующих проверок не активируют их.

| Суффикс `checks.<id>.` | Тип · диапазон | Смысл |
|---|---|---|
| `enabled` | boolean | Выключает вычисление и накопление состояния проверки |
| `weight` | число >0…10 | VL за одно доказательство |
| `decay` | число 0…10 | Снижение VL в секунду |
| `alert-threshold` | число 1…100 | Минимальный VL для уведомления |

Пример:

```yaml
checks:
  speed:
    enabled: true
    weight: 1.0
    decay: 0.2
    alert-threshold: 5.0
```

Базовый `checks.yml` — Balanced. Файлы `presets/checks-lenient.yml`, `checks-balanced.yml` и `checks-strict.yml` копируются вручную как `checks.yml`; Strict может увеличить число ложных сигналов. `/nceaac reload` валидирует оба файла и заменяет активные настройки только при успехе. При смене конфигурации временное состояние проверок игроков сбрасывается, VL сохраняется. При неуспешной первичной загрузке плагин отключается без баннера успеха. Для добавленных ключей баннера в старой схеме создаётся резервная копия `config.yml.bak-<timestamp>`.

Параметры `mode`, `setback` и наказаний не добавлены: текущая сборка всегда работает в режиме OBSERVE и не исполняет санкции.

## Оформление консоли

Настройки нового логотипа и безопасного вывода находятся в отдельном `branding.yml`. Он создаётся в папке плагина при первом старте; параметры `console.banner` из `config.yml` мигрируют однократно, если нового файла ещё нет. После этого изменяйте только `branding.yml`. Полный список с типами и значениями: [CONFIG_REFERENCE](CONFIG_REFERENCE.md). Пример собственного шаблона: [BANNER_CUSTOMIZATION](BANNER_CUSTOMIZATION.md).
