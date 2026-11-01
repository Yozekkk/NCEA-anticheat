#!/usr/bin/env bash
set -euo pipefail
project_root="$(realpath -e "$(dirname "${BASH_SOURCE[0]}")/..")"
printf 'Project root: %s\n' "$project_root"
for relative in ncea-core/src/main/java ncea-paper/src/main/java ncea-core/src/main/resources/branding/ncea-ac-original.segments ncea-paper/src/main/resources/branding.yml ncea-paper/src/main/resources/custom-banner.txt Ascii README.md README.en.md docs scripts ncea-core/src/test ncea-paper/src/test ncea-core/build/test-results/test ncea-paper/build/test-results/test ncea-core/build/reports/jacoco/test/jacocoTestReport.xml ncea-paper/build/reports/jacoco/test/jacocoTestReport.xml ncea-paper/build/previews/banner.txt ncea-paper/build/previews/banner.ansi.txt ncea-paper/build/previews/banner.html dist/NCEA-AntiCheat-1.20.jar dist/NCEA-AntiCheat-1.20.1.jar docs/FINAL_REPORT.md; do
  path="$project_root/$relative"
  if [[ -e "$path" ]]; then realpath -e "$path"; else printf 'MISSING %s\n' "$path"; fi
done
for directory in "$project_root/ncea-core/src/main/java/com/ncea/anticheat/core/branding" "$project_root/ncea-paper/src/main/java/com/ncea/anticheat/paper" "$project_root/docs" "$project_root/ncea-paper/src/main/resources" "$project_root/Ascii" "$project_root/scripts"; do
  if [[ -d "$directory" ]]; then
    find "$directory" -maxdepth 1 -type f \( -name '*.java' -o -name '*.md' -o -name '*.yml' -o -name '*.txt' -o -name '*.sh' -o -name '*.py' \) -print0 | sort -z | while IFS= read -r -d '' path; do realpath -e "$path"; done
  fi
done
for directory in "$project_root/ncea-core/src/test" "$project_root/ncea-paper/src/test"; do
  if [[ -d "$directory" ]]; then
    find "$directory" -type f \( -name '*.java' -o -name '*.txt' \) -print0 | sort -z | while IFS= read -r -d '' path; do realpath -e "$path"; done
  fi
done
