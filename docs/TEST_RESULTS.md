# Результаты тестирования — NCEA AntiCheat 1.0.0 Stable

## Выпуск 1.0.0 — повторная проверка 2026-11-01

| Проверка | Фактический результат |
|---|---|
| `./gradlew clean build --offline` | Успешно, 19 задач выполнено |
| JUnit 5 | 77 пройдено, 0 ошибок: 60 core и 17 Paper |
| JaCoCo, строки | Core 81,4%; Paper 49,2% |
| Paper 1.20, Java 17 | Холодный старт и повторный старт: успешно; баннер ровно один раз на запуск, статус ENABLED, команды, валидный/невалидный reload, штатная остановка |
| Paper 1.20.1, Java 17 | Те же сценарии: успешно |
| Внутренние JAR metadata | Оба `plugin.yml` содержат `version: 1.0.0`; одинаковый SHA-256 `bf44dd57d7f1a8050d4f79a87fe9549817f8a24efdaa28bc04d03637e2a66503` |
| Графика README | 25 SVG прошли XML parsing; 14 GIF имеют 8–10 кадров; визуально осмотрены hero, архитектура и Speed |
| Игровой клиент / PvP | Не проводились |

Артефакты smoke-теста: локальная исключённая из Git папка `.integration/qa-20261101-115544/` с журналами и `results.json`. Холодный старт занял 20,696 с на Paper 1.20 и 19,764 с на 1.20.1; повторный — 6,586 с и 6,581 с соответственно. Это время полного сервера, не плагина. RSS процесса после холодного старта: 828816 KiB и 905080 KiB; также относится ко всему Paper-процессу. В логах серверных smoke-тестов нет исключений. Веб-консоль хостинга, реальная игра, detection rate, false-positive rate и TPS под PvP-нагрузкой не измерены.

Оба JAR компилируются отдельно против соответствующего Paper API, но имеют одинаковый SHA-256, поскольку используют один исходный набор и одинаковые совместимые сигнатуры API.

## Исторические результаты 0.1.0 Development

# Фактические результаты тестирования

## Доработка консольного оформления — 2026-11-01

Исходник: `Ascii/NCEA_AC_original_banner.sh`; оригинальное изображение `Ascii/Ascii final result.png` обнаружено при подготовке GitHub-публикации; HTML и Python-файлы в каталоге отсутствуют. Snapshot-тест сравнил семь видимых строк и SHA-256 геометрии `01ab6d86593082be202a69370731c9c7cee7d2298c6a062ae57bf6229f4e1b26`. Bash не исполнялся.

`JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build --offline` прошёл; код компилируется с `--release 17`. JUnit 5: **60 тестов ядра + 17 тестов Paper-слоя = 77**, ошибок и пропусков нет. JaCoCo: core **342/420 = 81,4% строк**, в том числе старые core-классы **131/132 = 99,2%**, новый пакет оформления **211/288 = 73,3%**; Paper **153/311 = 49,2%**. Уменьшение общего процента core относительно прежнего прогона связано с добавлением нового пакета. Это покрытие unit-тестами, не игровая точность античита.

Локальный Paper QA: `scripts/integration_smoke.py` запустил Java 17, Paper 1.20 build 17 и 1.20.1 build 196, по холодному и повторному старту. Оба сервера достигли `Done`, показали исходный Unicode-баннер ровно один раз, ответили на административные команды, приняли валидный reload, отклонили невалидный reload и завершились с кодом 0 без непредвиденных исключений. Последняя проверка финальных JAR и измерения сохранены в `.integration/qa-20261101-104730/`; отдельная матрица режимов — в `.integration/qa-20261101-103833/`.

`scripts/branding_matrix.py` отдельно выполнил **16 серверных запусков**: на каждой версии `auto`, `colors-off`, `colors-on`, `ascii`, `unicode`, `compact`, `disabled`, `custom`. Все достигли `Done`, завершились с кодом 0, без исключений. В `auto/off` ANSI в логотипе отсутствовал; в `on` ANSI присутствовал; `disabled` не показывал `ENABLED`; остальные режимы дали ожидаемую геометрию. Подробные JSON и логи: `.integration/qa-20261101-103833/branding-matrix.json`. Панель хостинга, подключённый игровой клиент, фактические false positives и TPS под PvP-нагрузкой не проверялись.

После последней чистой сборки оба JAR прошли ZIP CRC; их SHA-256 совпадает: `c98a0dd1c0cc845bda58e4a29305572fa42dfa03600430c8d7635f260cb58eda`. Изолированные компиляции против Paper API 1.20 и 1.20.1 дают одинаковый байткод. Архивы содержат `plugin.yml`, `branding.yml`, `custom-banner.txt`, ресурс сегментов и классы Java 17.

Отрицательный запуск финального JAR на Paper 1.20.1 с повреждённым `branding.yml` (`branding: [`) подтвердил: сервер достиг `Done` и штатно завершился с кодом 0, плагин сообщил `Invalid branding configuration`, не вывел логотип и не сообщил `ENABLED`. Исходный файл восстановлен; лог: `.integration/qa-20261101-104420/1.20.1/invalid-branding.log`.

Исходные результаты 50 тестов ниже сохранены для истории; они относятся к предыдущему состоянию кода.

## Финальный QA-прогон — 2026-11-01

Команда `JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build :ncea-core:javadoc` завершилась успешно. Java 17 bytecode (major 61) подтверждён в обоих JAR. Javadoc ядра собран без предупреждений. Наличие `plugin.yml`, `config.yml`, `checks.yml`, основных классов и целостность ZIP проверены.

| Набор | Запущено | Ошибок | JaCoCo, покрыто строк |
|---|---:|---:|---:|
| `ncea-core` JUnit 5 | 41 | 0 | 142/143 = 99,3% |
| `ncea-paper` JUnit 5 | 9 | 0 | 66/198 = 33,3% |
| **Итого** | **50** | **0** | Разные слои; единый процент не вычисляется |

JaCoCo для Paper-адаптера низок: unit-тесты проверяют прежде всего `Settings`, а игровой event path требует клиента. Покрытие не используется как подмена реального испытания.

| Сценарий | Paper 1.20 build 17 | Paper 1.20.1 build 196 |
|---|---|---|
| Холодный старт Java 17 | Успешен | Успешен |
| Баннер `Status: ENABLED` | 1 раз | 1 раз |
| `status`, `checks`, `config validate`, `help` | Ответы получены | Ответы получены |
| Валидный reload с выключением Speed | Успешен, `speed=off` | Успешен, `speed=off` |
| Невалидный reload (`weight: -1`) | Отклонён, прежнее состояние сохранилось | Отклонён, прежнее состояние сохранилось |
| Штатная остановка и повторный старт | Exit 0, баннер 1 раз | Exit 0, баннер 1 раз |
| Неожиданные исключения в здоровом запуске | Не найдены | Не найдены |

Отдельный отрицательный запуск Paper 1.20.1 с `max-ping: 1` подтвердил отключение плагина без баннера и без `Status: ENABLED`. YAML-тест проверил также повреждённый синтаксис. Исходные значения в тестовом окружении восстановлены.

Финальные JAR имеют одинаковый SHA-256 `c59036a1444b89472a1e8d82fd56603c94811f8ade50aeff6c8bfbea35b43368`; исходники скомпилированы независимо против API 1.20 и 1.20.1, байткод совпал. Логи и измерения изолированного прогона: `.integration/qa-20261101-094824/` (не входит в Git), скрипт: `scripts/integration_smoke.py`. Отрицательный старт: `.integration/qa-20261101-094506/1.20.1/failed-start.log`.

**Не выполнено:** подключение клиента, PvP/движение в игровом мире, проверка входа/выхода игрока, реальные доли обнаружения и false positives, TPS impact под нагрузкой. Отсутствие дублирования обработчиков подтверждено аудитом кода reload (он не регистрирует listener повторно), но не игровыми событиями.

## Предыдущий базовый прогон

2026-11-01: `JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build` succeeded. JDK 25 compiled both Paper adapters with `--release 17`; both JARs have class-file major version 61 (Java 17), valid `plugin.yml`, `checks.yml` and core classes. JUnit `CoreTest` has five passing tests: VL decay/evidence, Speed/Fly, Reach, AutoClicker/FastPlace, and duplicate registration. Final artifacts were started on both server versions after rebuild. The two JAR SHA-256 hashes are identical (`18d001ce90cc3c68712b18171ec4410547b80a7747470968a325056d2a1d1697`), expected from shared source and identical emitted bytecode.

Integration: official Paper 1.20 build 17 and 1.20.1 build 196 server JARs were downloaded and SHA-256 checked. With Java 17, both servers reached `Done`, loaded NCEA, answered `/nceaac status`, `checks`, `config validate`, and stopped cleanly (exit 0). Test logs are in ignored `.integration/1.20/startup.log` and `.integration/1.20.1/startup.log`.

On Paper 1.20.1, reloading after changing `speed.enabled` to false showed `speed=off`; a later invalid `weight: -1.0` was rejected and the prior live state stayed active. The test restored the original file. `nceaac config validate` reported the invalid value.

Both release archives passed CRC inspection and contained the main class, plugin.yml, config.yml, checks.yml and core classes. All three preset YAML files parsed successfully. No client was connected. Join/quit, PvP, sprint, jump, water, teleport, knockback, check detection rates, false positive rates, TPS impact, throughput and memory consumption remain untested. Event timing metrics exist but no meaningful measurements have been collected.

## Проверка перед GitHub-публикацией

2026-11-01: `JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build --offline` прошла, 60 core и 17 Paper JUnit прошли без ошибок. Локальный `scripts/integration_smoke.py` повторно запустил финальные JAR на Java 17 с Paper 1.20 и 1.20.1: холодный и повторный старт обеих версий достигли `Done`, логотип и `ENABLED` выведены по одному разу, команды и валидный/невалидный reload проверены, exit 0, непредвиденных исключений нет. Локальный журнал: `.integration/qa-20261101-111300/` (не публикуется).

Все семь SVG `docs/assets/readme/` разобраны XML-парсером и отрендерены `rsvg-convert`; GIF содержит 12 кадров 900×300. Локальные ссылки README и документации проверены, битых ссылок нет. Движение CSS в SVG на GitHub может не поддерживаться; статический кадр остаётся читаемым, GIF даёт анимированный предпросмотр. Это визуальные тесты документации, не игровые тесты античита.
