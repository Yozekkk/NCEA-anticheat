# Установка

| Сервер | Артефакт | Java | Проверка загрузки |
|---|---|---|---|
| Paper 1.20 | `dist/NCEA-AntiCheat-1.20.jar` | 17+ | `Platform: Paper 1.20` |
| Paper 1.20.1 | `dist/NCEA-AntiCheat-1.20.1.jar` | 17+ | `Platform: Paper 1.20.1` |

Остановите сервер, поместите **один** подходящий JAR в `plugins/` и запустите Paper. `plugin.yml` использует `api-version: '1.20'`; дополнительные плагины для работы NCEA не нужны. На первом старте создаются `plugins/NCEAAntiCheat/config.yml` и `checks.yml`. Старый `config.yml` с той же схемой получает недостающие параметры баннера; перед записью создаётся `config.yml.bak-<timestamp>`.

При загрузке сервер иной версии плагин выводит ошибку и отключается. Баннер `Status: ENABLED` появляется только после успешной инициализации. После установки выполните `/nceaac status`, `/nceaac checks`, `/nceaac config validate`. Для обновления остановите сервер, замените JAR и запустите его снова; сохраняйте резервную копию конфигураций.

Для сборки из исходников: `./gradlew clean build` с полным JDK 17+. На данном рабочем месте: `JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build`.

## Консольный логотип

Первый запуск создаёт `branding.yml` и `custom-banner.txt` в `plugins/NCEAAntiCheat/`. По умолчанию включена оригинальная семистрочная Unicode-геометрия, а ANSI-цвета выбираются консервативно. После настройки используйте `/nceaac reload`; логотип отображается только при следующем успешном включении. Для просмотра без сервера запустите `./gradlew :ncea-paper:previewBanner`. См. [руководство по оформлению](CONSOLE_BRANDING.md).
