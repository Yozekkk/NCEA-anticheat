# Итоговый отчёт — NCEA AntiCheat 1.0.0 Stable

Выпуск 1.0.0 Stable принят владельцем проекта. Рабочая реализация содержит пять наблюдательных проверок, VL/decay, исключения, команды, YAML и настраиваемый баннер. Статус не подразумевает измеренных detection/false-positive rates: PvP-клиент не подключался. Результаты повторного тестирования, артефакты и GitHub-публикация фиксируются в [TEST_RESULTS](TEST_RESULTS.md) и [Release Notes](RELEASE_NOTES_1.0.0.md).

Релиз опубликован: [v1.0.0](https://github.com/Yozekkk/NCEA-anticheat/releases/tag/v1.0.0), оба JAR приложены. CI релизного коммита [успешен](https://github.com/Yozekkk/NCEA-anticheat/actions/runs/41299793876). Визуальный GitHub-аудит и ограничения зафиксированы в [QA_REPORT](QA_REPORT.md).

Разделы ниже — архивные записи о выпуске 0.1.0 Development. Их значения и прежние решения сохранены для прослеживаемости.

## Архив: 0.1.0 Development

## Обновление: фирменный баннер и финальный QA

Корень проекта проверен через `realpath` и Git: `/home/tima/ncea-anticheat`. Исходники: `/home/tima/ncea-anticheat/ncea-core/src/main/java` и `/home/tima/ncea-anticheat/ncea-paper/src/main/java`. Конфигурации в JAR: `/home/tima/ncea-anticheat/ncea-paper/src/main/resources`; рабочие конфигурации Paper создаются в `plugins/NCEAAntiCheat/` каждого сервера. Документация: `/home/tima/ncea-anticheat/docs`. JUnit XML: `/home/tima/ncea-anticheat/ncea-core/build/test-results/test` и `/home/tima/ncea-anticheat/ncea-paper/build/test-results/test`. JaCoCo XML: соответствующие `build/reports/jacoco/test/jacocoTestReport.xml`. Итоговые JAR: `/home/tima/ncea-anticheat/dist/NCEA-AntiCheat-1.20.jar` и `/home/tima/ncea-anticheat/dist/NCEA-AntiCheat-1.20.1.jar`. Скрипт `scripts/list-project-paths.sh` проверяет и печатает абсолютные пути всех существенных файлов; локальный снимок — `.local/project-paths.txt`, исключённый из Git.

Оба итоговых JAR после последней чистой сборки имеют SHA-256 `c98a0dd1c0cc845bda58e4a29305572fa42dfa03600430c8d7635f260cb58eda`. Они компилируются в разных source set против соответствующих Paper API; совпадающий байткод следует из общего исходного кода и API, а не из переименования файла. Более ранние хеши в исторической части отчёта относятся к предыдущим сборкам.

Предоставленный каталог `Ascii/` содержит Bash-источник и Markdown-описание. При подготовке GitHub-публикации дополнительно найдено `Ascii/Ascii final result.png`; HTML и Python-файлы не обнаружены. Семь видимых строк Bash перенесены в сегментный UTF-8 ресурс без выполнения скрипта и защищены snapshot-тестом. Доступны `original-3d`, `monochrome`, `ascii`, `compact`, `custom`, `disabled`; темы `ncea-original`, `ncea-purple`, `ncea-cyan`, `monochrome`, `custom`; ANSI 16/256/TrueColor и консервативный авто-режим. Новый `branding.yml` и UTF-8 пользовательский шаблон проходят валидацию и reload. Устранены ошибки повреждённого YAML, небезопасного custom fallback и YAML 1.1 `colors: on/off`; информационные поля теперь выровнены общим столбцом. Повторные предупреждения `saveResource` устранены.

**Тесты:** 60 core + 17 Paper JUnit = 77, ошибок нет. JaCoCo: core 81,4% строк (из них базовое ядро 99,2%, оформление 73,3%); Paper 49,2%. На реальных локальных Paper 1.20 и 1.20.1 выполнены холодный и повторный старт, команды, валидный и невалидный reload, штатная остановка. Отдельно выполнены 16 запусков восьми режимов оформления; все успешны. Логи последней сборки: `.integration/qa-20261101-104730/`; матрица режимов: `.integration/qa-20261101-103833/`. Исторические 50 тестов и их результаты ниже сохранены без подмены.

**Ограничения:** визуальное совпадение с найденным PNG оценено по предпросмотру, но автоматического попиксельного теста нет; веб-панель хостинга и Minecraft-клиент недоступны; PvP/движение, реальные false positives и TPS под нагрузкой не измерены. Packet Checks, физическая симуляция, setback и наказания не реализованы. Версия остаётся **0.1.0 Development**, наблюдательный режим OBSERVE. Подробнее: [QA](QA_REPORT.md), [брендинг](CONSOLE_BRANDING.md), [ограничения](KNOWN_LIMITATIONS.md).

## Подтверждённые пути

| Назначение | Абсолютный путь |
|---|---|
| Корень проекта | `/home/tima/ncea-anticheat` |
| Исходники ядра | `/home/tima/ncea-anticheat/ncea-core/src/main/java` |
| Исходники Paper-адаптера | `/home/tima/ncea-anticheat/ncea-paper/src/main/java` |
| Официальная документация | `/home/tima/ncea-anticheat/docs` |
| Исходные конфигурации | `/home/tima/ncea-anticheat/ncea-paper/src/main/resources` |
| Пресеты | `/home/tima/ncea-anticheat/presets` |
| Готовые JAR | `/home/tima/ncea-anticheat/dist` |
| Результаты JUnit ядра | `/home/tima/ncea-anticheat/ncea-core/build/test-results/test` |
| Результаты JUnit Paper | `/home/tima/ncea-anticheat/ncea-paper/build/test-results/test` |
| Логи локального Paper QA | `/home/tima/ncea-anticheat/.integration/qa-20261101-094824` |

## Статусы

| Компонент | Статус | Подтверждение |
|---|---|---|
| 5 проверок, VL/decay, контекстные исключения | Реализовано; экспериментально | JUnit, аудит событий; клиент не подключался |
| Баннер `3d`, `classic`, `compact`, `disabled` | Реализовано и протестировано | JUnit + 4 серверных старта |
| YAML, валидация, резервная копия старого конфига | Реализовано и протестировано | JUnit + валидный/ошибочный reload обеих версий |
| Команды и права | Реализовано; частично протестировано | Консольные команды, JUnit AccessPolicy; игровой sender не проверен |
| Paper 1.20 / 1.20.1, Java 17 | Сборка и startup протестированы | 2 компиляции, 4 запуска, JAR major 61 |
| JaCoCo | Реализовано | 41 core + 9 Paper JUnit, 0 ошибок |
| Пакеты, физика, setbacks, санкции, SQLite | Запланировано | В этом выпуске отсутствуют |
| Игровые PvP/движение, реальные false positives | Не протестировано | Нет подключённого клиента |

## Исправленные дефекты

- Speed использовал обновлённое время окна и мог пропускать превышение; длительность измерения теперь сохраняется до проверки.
- Fly оценивал лишь последний вертикальный фрагмент окна; теперь используется суммарное смещение.
- Повторные позиции и разрывы координат от сторонних событий не увеличивают скорость.
- Выключенные проверки больше не обновляют своё состояние; reload и исключения сбрасывают временные счётчики.
- Общий cooldown уведомлений заменён независимым cooldown по проверке; очередь атак ограничена.
- YAML отвергает неверные типы и нечисловые/бесконечные значения, сохраняя прежний набор при неудачном reload.
- Старый конфиг получает новые параметры баннера с резервной копией.
- `alerts` доступна владельцу `nceaac.alerts` без полного административного права; tab completion учитывает права.
- Выключение выводит короткое сообщение; критическая ошибка загрузки не выводит успешный баннер.

## Ограничения и выпускной статус

[50 JUnit-тестов](TEST_RESULTS.md) прошли; четыре локальных запуска двух Paper-версий прошли. JaCoCo: ядро 99,3% строк, Paper-адаптер 33,3%. Отдельная проверка неуспешного старта не показала баннер успеха. Замеры памяти и потоков относятся ко всему Paper-процессу, не к плагину. Пакетного слоя, наказаний и игрового клиента нет; обнаружение и false positives не измерены. Версия остаётся **0.1.0 Development**, **готова к дальнейшей локальной разработке и тестам**, не к заявлению production/stable. Подробности: [ограничения](KNOWN_LIMITATIONS.md), [производительность](PERFORMANCE.md), [аудит лицензий](THIRD_PARTY_AUDIT.md).
