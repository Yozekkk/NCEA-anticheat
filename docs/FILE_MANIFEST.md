# Карта файлов проекта

| Путь | Назначение |
|---|---|
| `ncea-core/src/main/java` | Ядро, данные игрока, нарушения, проверки, рендерер |
| `ncea-core/src/main/resources/branding/ncea-ac-original.segments` | Исходные семь строк логотипа с цветными сегментами |
| `ncea-paper/src/main/java` | Paper-адаптер, команды, события, загрузчик branding.yml |
| `ncea-paper/src/main/resources/branding.yml` | Встроенная схема оформления и комментарии |
| `ncea-paper/src/main/resources/custom-banner.txt` | Пример собственного шаблона |
| `ncea-core/src/test`, `ncea-paper/src/test` | JUnit 5 и снимок логотипа |
| `Ascii/` | Предоставленный Bash-референс и описание; не исполняется плагином |
| `docs/` | Пользовательская и техническая документация |
| `docs/assets/readme/` | 25 оригинальных SVG и 14 GIF для hero, пяти проверок, архитектуры, консоли, конфигурации, тестирования, документации, статуса и футера; статические SVG используются при reduced motion |
| `scripts/generate_readme_assets.py` | Воспроизводимая генерация графики из локального NCEA AC PNG; нужны Pillow и `rsvg-convert` только для этого скрипта |
| `.github/workflows/ci.yml` | Java 17 CI: JUnit, обе Paper-сборки, проверка JAR и artifact upload |
| `scripts/integration_smoke.py` | Локальный Paper smoke QA, требует официальные JAR |
| `scripts/list-project-paths.sh` | Проверяемый список абсолютных путей |
| `dist/` | Два итоговых артефакта, создаются Gradle |
| `.integration/` | Локальные серверы и результаты, исключены из Git |
| `.local/project-paths.txt` | Локальный машинно-зависимый список путей, исключён из Git |
| `.local/changed-paths.txt` | Архивный локальный список файлов предыдущей доработки, исключён из Git |

| `.local/1.0.0-changed-paths.txt` | Локальный список 62 файлов релизного коммита 1.0.0 с абсолютными путями, исключён из Git |
| `docs/RELEASE_NOTES_1.0.0.md` | Примечания к Stable-релизу, загрузки и ограничения |

Для полной актуальной карты запустите `scripts/list-project-paths.sh`. [Архитектура](ARCHITECTURE.md) · [Разработка](DEVELOPMENT.md).
