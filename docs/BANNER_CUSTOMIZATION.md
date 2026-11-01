# Собственный баннер

Откройте `plugins/NCEAAntiCheat/branding.yml` и установите:

```yaml
branding:
  banner:
    style: custom
  custom:
    file: custom-banner.txt
```

Отредактируйте `plugins/NCEAAntiCheat/custom-banner.txt` как UTF-8. Пример поставляется в JAR и копируется при первом запуске:

```text
NCEA AntiCheat ${version}
Paper ${platform} | Checks ${checks} | ${mode}
```

Доступны `${version}`, `${platform}`, `${checks}`, `${java}`, `${mode}`, `${status}`, `${startup_ms}`, `${studio}`. Значения подставляются как текст: команды и выражения не выполняются. Разрешены обычные Unicode, ASCII и блочные символы `█ ▀ ▄ ▓ ▒ ░`. Файл — не более 8192 байт, 20 строк и 120 терминальных ячеек на строку. Путь ограничен безопасным именем `.txt` в папке плагина. ESC, управляющие символы, неизвестные переменные и слишком длинные строки отклоняются; при проблеме выводится компактный fallback и предупреждение в журнале.

Цвета задаются через `branding.theme`, а не встроенными ANSI-кодами в файле. Проверьте вид локально:

```bash
./gradlew :ncea-paper:previewBanner -PbannerStyle=custom -PbannerCustom=/absolute/path/custom-banner.txt
```

После правки работающего сервера используйте `/nceaac reload`. [Общий справочник](CONSOLE_BRANDING.md).
