#!/usr/bin/env python3
"""Local Paper startup smoke test; requires previously downloaded official Paper JARs."""
import json
import pathlib
import re
import shutil
import subprocess
import threading
import time

ROOT = pathlib.Path(__file__).resolve().parents[1]
JAVA = pathlib.Path('/usr/lib/jvm/java-17-openjdk/bin/java')
JDK = pathlib.Path('/usr/lib/jvm/java-25-openjdk/bin')
OUT = ROOT / '.integration' / ('qa-' + time.strftime('%Y%m%d-%H%M%S'))
OUT.mkdir(parents=True, exist_ok=False)
results = {}


def run(version, directory, attempt, modify):
    log = []
    ready = threading.Event()
    started = time.monotonic()
    process = subprocess.Popen([str(JAVA), '-Xms256M', '-Xmx1G', '-jar', 'paper.jar', 'nogui'],
                               cwd=directory, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                               stderr=subprocess.STDOUT, text=True, bufsize=1)
    def reader():
        for line in process.stdout:
            log.append(line)
            if 'Done (' in line:
                ready.set()
    threading.Thread(target=reader, daemon=True).start()
    ok = ready.wait(55)
    metrics = {'ready': ok, 'startup_wall_s': round(time.monotonic() - started, 3)}
    if ok:
        status = (pathlib.Path('/proc') / str(process.pid) / 'status').read_text()
        for key in ('VmRSS', 'Threads'):
            match = re.search(r'^' + key + r':\s*(.+)$', status, re.MULTILINE)
            metrics[key] = match.group(1) if match else 'unavailable'
        for name, command in [('jstat', [str(JDK / 'jstat'), '-gcutil', str(process.pid)]),
                              ('jcmd_threads', [str(JDK / 'jcmd'), str(process.pid), 'Thread.print'])]:
            try:
                output = subprocess.run(command, capture_output=True, text=True, timeout=12)
                metrics[name] = output.stdout.splitlines()[:2] if name == 'jstat' else {
                    'exit': output.returncode,
                    'thread_states': output.stdout.count('java.lang.Thread.State'),
                    'deadlock_reported': 'Found one Java-level deadlock' in output.stdout,
                }
            except Exception as exc:
                metrics[name] = str(exc)
        for command in ('nceaac status', 'nceaac checks', 'nceaac config validate', 'nceaac help'):
            process.stdin.write(command + '\n')
        process.stdin.flush()
        time.sleep(0.5)
        if modify:
            checks = directory / 'plugins' / 'NCEAAntiCheat' / 'checks.yml'
            original = checks.read_text()
            try:
                checks.write_text(original.replace('enabled: true', 'enabled: false', 1))
                process.stdin.write('nceaac reload\nnceaac checks\n')
                process.stdin.flush()
                time.sleep(0.5)
                checks.write_text(original.replace('weight: 1.0', 'weight: -1.0', 1))
                process.stdin.write('nceaac reload\nnceaac config validate\nnceaac checks\n')
                process.stdin.flush()
                time.sleep(0.5)
            finally:
                checks.write_text(original)
    process.stdin.write('stop\n')
    process.stdin.flush()
    try:
        process.wait(timeout=25)
    except subprocess.TimeoutExpired:
        process.kill()
        process.wait()
    text = ''.join(log)
    (directory / f'{attempt}.log').write_text(text)
    metrics.update({'exit': process.returncode,
                    'banner_count': text.count('Developed by NCEA Studio'),
                    'enabled_count': len(re.findall(r'\bStatus\s+ENABLED\b', text)),
                    'plugin_loaded': f'enabled for Paper {version}' in text or bool(re.search(r'\bStatus\s+ENABLED\b', text)),
                    'commands': text.count('[NCEA]'),
                    'unexpected_exception': 'Exception' in text,
                    'valid_reload': 'Configuration reloaded' in text,
                    'invalid_reload': 'Invalid configuration' in text})
    return metrics


for version in ('1.20', '1.20.1'):
    directory = OUT / version
    (directory / 'plugins').mkdir(parents=True)
    shutil.copy2(ROOT / '.integration' / version / 'paper.jar', directory / 'paper.jar')
    shutil.copy2(ROOT / 'dist' / f'NCEA-AntiCheat-{version}.jar', directory / 'plugins' / 'NCEA-AntiCheat.jar')
    (directory / 'eula.txt').write_text('eula=true\n')
    (directory / 'server.properties').write_text('online-mode=false\nserver-ip=127.0.0.1\nserver-port=0\n')
    cold = run(version, directory, 'cold-start', True)
    restart = run(version, directory, 'restart', False)
    results[version] = {'cold': cold, 'restart': restart}
    assert all(x['ready'] and x['exit'] == 0 and x['banner_count'] == 1 and x['enabled_count'] == 1 and x['plugin_loaded'] for x in (cold, restart))
    assert cold['valid_reload'] and cold['invalid_reload']
    assert not restart['unexpected_exception']
(OUT / 'results.json').write_text(json.dumps(results, indent=2))
print(OUT)
print(json.dumps(results, indent=2))
