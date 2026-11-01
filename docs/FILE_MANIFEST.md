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
| `docs/assets/readme/` | Оригинальные SVG, GIF-шапка с fallback для reduced motion, GIF логотипа и визуальные схемы для GitHub README |
| `scripts/generate_readme_assets.py` | Воспроизводимая генерация графики из локального NCEA AC PNG; нужен Pillow только для этого скрипта |
| `.github/workflows/ci.yml` | Java 17 CI: JUnit, обе Paper-сборки, проверка JAR и artifact upload |
| `scripts/integration_smoke.py` | Локальный Paper smoke QA, требует официальные JAR |
| `scripts/list-project-paths.sh` | Проверяемый список абсолютных путей |
| `dist/` | Два итоговых артефакта, создаются Gradle |
| `.integration/` | Локальные серверы и результаты, исключены из Git |
| `.local/project-paths.txt` | Локальный машинно-зависимый список путей, исключён из Git |
| `.local/changed-paths.txt` | Локальный список 43 созданных или изменённых файлов этой доработки, исключён из Git |

Для полной актуальной карты запустите `scripts/list-project-paths.sh`. [Архитектура](ARCHITECTURE.md) · [Разработка](DEVELOPMENT.md).
