#!/usr/bin/env python3
"""Run local banner variants on both existing isolated Paper smoke servers."""
import json
import pathlib
import shutil
import subprocess
import threading
import time

root = pathlib.Path(__file__).resolve().parents[1]
base = sorted((root / '.integration').glob('qa-*'))[-1]
java = '/usr/lib/jvm/java-17-openjdk/bin/java'
variants = {
    'auto': {},
    'colors-off': {'colors: auto': 'colors: off'},
    'colors-on': {'colors: auto': 'colors: on'},
    'ascii': {'style: original-3d': 'style: ascii'},
    'unicode': {'charset: unicode': 'charset: unicode'},
    'compact': {'style: original-3d': 'style: compact'},
    'disabled': {'style: original-3d': 'style: disabled'},
    'custom': {'style: original-3d': 'style: custom'},
}
results = {}
for version in ('1.20', '1.20.1'):
    directory = base / version
    shutil.copy2(root / 'dist' / f'NCEA-AntiCheat-{version}.jar', directory / 'plugins/NCEA-AntiCheat.jar')
    branding = directory / 'plugins/NCEAAntiCheat/branding.yml'
    original = branding.read_text(encoding='utf-8')
    results[version] = {}
    try:
        for name, replacements in variants.items():
            text = original
            for source, target in replacements.items():
                if source not in text:
                    raise AssertionError(f'{source} missing in {branding}')
                text = text.replace(source, target, 1)
            branding.write_text(text, encoding='utf-8')
            ready = threading.Event()
            lines = []
            started = time.monotonic()
            process = subprocess.Popen([java, '-Xms256M', '-Xmx1G', '-jar', 'paper.jar', 'nogui'],
                                       cwd=directory, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                       stderr=subprocess.STDOUT, text=True, bufsize=1)
            def reader():
                for line in process.stdout:
                    lines.append(line)
                    if 'Done (' in line:
                        ready.set()
            thread = threading.Thread(target=reader, daemon=True)
            thread.start()
            success = ready.wait(50)
            if success:
                process.stdin.write('stop\n')
                process.stdin.flush()
            else:
                process.kill()
            try:
                process.wait(timeout=20)
            except subprocess.TimeoutExpired:
                process.kill(); process.wait()
            thread.join(timeout=2)
            log = ''.join(lines)
            (directory / f'branding-{name}.log').write_text(log, encoding='utf-8')
            logo = '▀█▄████▄' in log
            ansi_logo = any('\x1b[' in line and '▀█▄████▄' in line for line in lines)
            status = 'Status         ENABLED' in log
            result = {'ready': success, 'exit': process.returncode, 'logo': logo,
                      'logo_ansi': ansi_logo, 'enabled': status,
                      'exception': 'Exception' in log,
                      'wall_s': round(time.monotonic() - started, 3)}
            results[version][name] = result
            assert success and process.returncode == 0 and not result['exception'], (version, name, result)
            assert logo == (name in ('auto', 'colors-off', 'colors-on', 'unicode')), (version, name, result)
            assert ansi_logo == (name == 'colors-on'), (version, name, result)
            assert status == (name != 'disabled'), (version, name, result)
    finally:
        branding.write_text(original, encoding='utf-8')
(base / 'branding-matrix.json').write_text(json.dumps(results, indent=2), encoding='utf-8')
print(base / 'branding-matrix.json')
print(json.dumps(results, indent=2))
