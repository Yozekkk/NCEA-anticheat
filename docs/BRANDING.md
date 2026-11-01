# Брендинг NCEA AntiCheat

**Продукт:** NCEA AntiCheat · **Студия:** [NCEA Studio](https://ncea-studio.com) · **Выпуск:** 0.1.0 Development

## Подлинный знак NCEA AC

В проекте доступны [оригинальное изображение](../Ascii/Ascii%20final%20result.png) и [Bash-представление](../Ascii/NCEA_AC_original_banner.sh). Объёмная пиксельная надпись `NCEA AC` и палитра `#5555FF`, `#0000AA`, `#AAAAAA`, `#000000` легли в основу консольного рендерера. Java использует UTF-8 сегменты и не выполняет Bash. Статус и числовые поля формируются после успешной инициализации из реальных данных сервера.

Для GitHub README созданы оригинальные SVG и GIF в `docs/assets/readme/`. Графика сочетает исходный пиксельный знак с тёмной цифровой сеткой, синим, фиолетовым и бирюзовым акцентами. `hero-motion.gif` показывает мягкое движение сетки в GitHub, `hero.svg` служит статическим fallback при `prefers-reduced-motion`; второй GIF показывает только логотип. Файлы генерируются локальным `scripts/generate_readme_assets.py` из PNG, без чужих графических ассетов.

## Консоль

`branding.yml` управляет стилями `original-3d`, `monochrome`, `ascii`, `compact`, `custom`, `disabled`; темами `ncea-original`, `ncea-purple`, `ncea-cyan`, `monochrome`, `custom`; ANSI 16/256/TrueColor и безопасным автоматическим режимом. По умолчанию исходная Unicode-геометрия выводится один раз при успешной загрузке, а ANSI разрешается только при консервативном определении совместимого терминала. Панель хостинга может добавлять префикс к каждой строке; её отображение следует проверять отдельно.

[Руководство по консоли](CONSOLE_BRANDING.md) · [Настройки](CONFIG_REFERENCE.md) · [Аудит ресурсов](THIRD_PARTY_AUDIT.md)
