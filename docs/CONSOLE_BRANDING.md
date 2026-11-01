# Консольное оформление NCEA AntiCheat

Официальное оформление NCEA Studio для выпуска **1.0.0 Stable**. Исходник пиксельной геометрии — [`Ascii/NCEA_AC_original_banner.sh`](../Ascii/NCEA_AC_original_banner.sh); Java не выполняет Bash. В каталоге `Ascii/` также есть текстовое описание. Оригинальное изображение `Ascii/Ascii final result.png` найдено при подготовке GitHub-публикации и использовано как визуальный эталон README; HTML и Python-референсы не обнаружены.

## Источник и вывод

Семь видимых строк исходного логотипа хранятся как UTF-8 сегменты в [`ncea-ac-original.segments`](../ncea-core/src/main/resources/branding/ncea-ac-original.segments). Цвет и геометрия разделены. Оригинальная палитра: основной `#5555FF`, тень `#0000AA`, разделитель `#AAAAAA`, фон референса `#000000`. Фон терминала плагин не меняет. Для исходного режима ANSI 16 используются SGR `0;94`, `0;34`, `0;94;44`, `0;34;44`, `0;37` и обязательный сброс `0`. Поддерживаются ANSI 256 и TrueColor; при `colors: auto` управляющие последовательности разрешаются только для интерактивной консоли с подходящим `TERM` без `NO_COLOR`. В веб-консоли хостинга цвета следует включать только после проверки панели.

Логгер Paper пишет каждую строку отдельно, поэтому временные метки панели могут визуально увеличить отступы. Ширина логотипа — 77 терминальных ячеек. Настройка `width` задаёт область выравнивания; если она уже логотипа, действует `fallback`.

## Режимы

| Стиль | Назначение |
|---|---|
| `original-3d` | Исходная пиксельная геометрия, семь строк |
| `monochrome` | Та же геометрия без ANSI |
| `ascii` | Упрощённый ASCII-логотип |
| `compact` | Короткая строка для узкой консоли |
| `custom` | UTF-8 файл `custom-banner.txt` |
| `disabled` | Нет заставки; короткое сообщение выключения настраивается отдельно |

`charset: ascii` переводит объёмный стиль в безопасный fallback. `branding.enabled: false` отключает всё оформление, включая сообщение выключения. `branding.banner.enabled: false` отключает только заставку. Полный справочник: [CONFIG_REFERENCE](CONFIG_REFERENCE.md).

## Предпросмотр без сервера

```bash
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-paper:previewBanner
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-paper:previewBanner -PbannerColors=on -PbannerMode=ansi16
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-paper:previewBanner -PbannerStyle=monochrome
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-paper:previewBanner -PbannerStyle=compact
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-paper:previewBanner -PbannerStyle=custom -PbannerCustom=/absolute/path/banner.txt
```

Задача использует тот же `BannerRenderer`, что Paper. Файлы появляются в `ncea-paper/build/previews/`: `banner.txt` без ANSI, `banner.ansi.txt` с ANSI и `banner.html` для браузера. Превью подставляет демонстрационные данные и не доказывает загрузку плагина.

[Собственный шаблон](BANNER_CUSTOMIZATION.md) · [Параметры](CONFIG_REFERENCE.md) · [Тесты](TEST_RESULTS.md)
