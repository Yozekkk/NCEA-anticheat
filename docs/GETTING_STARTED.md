# Быстрый старт

NCEA AntiCheat 0.1.0 Development наблюдает за событиями на Paper 1.20/1.20.1. До установки проверьте [ограничения](KNOWN_LIMITATIONS.md).

1. Подготовьте Paper нужной версии и Java 17.
2. Скопируйте `dist/NCEA-AntiCheat-1.20.jar` либо `dist/NCEA-AntiCheat-1.20.1.jar` в `plugins/`.
3. Запустите сервер и дождитесь `Status: ENABLED` в ASCII-баннере.
4. Проверьте команды от консоли или администратора:

```text
nceaac status
nceaac checks
nceaac config validate
```

5. Сотрудник с правом `nceaac.alerts` включает уведомления командой `/nceaac alerts`.

Настройки находятся в `plugins/NCEAAntiCheat/config.yml` и `checks.yml`. После редактирования выполните `/nceaac reload`. Плагин продолжит использовать предыдущую конфигурацию, если новые значения невалидны. Подробности — в [установке](INSTALLATION.md) и [справочнике настроек](CONFIGURATION.md).
