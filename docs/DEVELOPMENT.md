# Руководство разработчика

## Сборка

Требуется полный JDK 17+ и Gradle Wrapper. На текущем хосте полный JDK находится в `/usr/lib/jvm/java-25-openjdk`; Gradle использует `--release 17` для обоих API.

```bash
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew clean build
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-core:test :ncea-paper:test
JAVA_HOME=/usr/lib/jvm/java-25-openjdk ./gradlew :ncea-core:jacocoTestReport :ncea-paper:jacocoTestReport
```

`ncea-paper` содержит source sets `paper120` и `paper1201`; задачи `jar120` и `jar1201` создают два артефакта в `dist/`. Исходный код общий, поэтому совпадение SHA-256 допустимо и проверяется. Paper API остаётся compile-only в итоговом JAR.

## Добавление проверки

1. Описать источник измерения, математическое условие, доказательство и исключения в `docs/CHECKS.md`.
2. Реализовать `Check` и зарегистрировать его один раз через `CheckManager`.
3. Добавить ID в валидатор `Settings`, настройки в `checks.yml` и три пресета.
4. Написать JUnit 5 тесты для нормальных действий, границ, небезопасного контекста и повторных событий.
5. Проверить запуск на обеих версиях Paper и игровой сценарий с клиентом, прежде чем считать детектор готовым.

Не обращайтесь к Bukkit/Paper из Netty или асинхронных потоков. Не вводите наказания из одного эвристического сигнала. Следуйте [AGENTS.md](../AGENTS.md) и локальному [скиллу](../.agents/skills/ncea-anticheat-development/SKILL.md). Для багов фиксируйте воспроизводимый сценарий, затем тест и исправление. Консольный баннер и настройки должны оставаться согласованными.
