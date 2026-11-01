#!/usr/bin/env python3
"""Adapt the pinned MIT AngelOS SVG artwork; never copy its demo media.

Source: MixaDoDs/AngelOS-Dotfiles at e5f771cfee9dedf81b6e4a648dd53c763d904e09.
Usage: python3 scripts/adapt_angelos_readme.py /tmp/ncea-angelos-reference
Requires fontTools with WOFF2 support. Licenses: docs/licenses/.
"""
from __future__ import annotations
import base64
import io
import re
import sys
from pathlib import Path
from xml.sax.saxutils import escape
import xml.etree.ElementTree as ET
from fontTools.ttLib import TTFont
from fontTools.subset import Subsetter, Options

ROOT = Path(__file__).resolve().parents[1]
SOURCE = Path(sys.argv[1]).resolve() if len(sys.argv)>1 else Path('/tmp/ncea-angelos-reference')
COMMIT = 'e5f771cfee9dedf81b6e4a648dd53c763d904e09'
OUT = ROOT/'docs/assets/angelos-adapted'
OUT.mkdir(parents=True,exist_ok=True)
FOOTAGE = SOURCE/'docs/readme/footage'
if not FOOTAGE.is_dir(): raise SystemExit('AngelOS source footage is missing')

LABELS = {
 1:('Introduction','Введение'),2:('Installation','Установка'),
 3:('Detection Engine','Движок проверок'),4:('Movement Checks','Движение'),
 5:('Combat Checks','Бой'),6:('World Checks','Проверки мира'),
 7:('Configuration','Конфигурация'),8:('Console Branding','Консоль NCEA'),
 9:('Architecture','Архитектура'),10:('Testing','Тестирование'),
 11:('Documentation','Документация'),12:('NCEA Studio / Release','NCEA Studio / релиз')}
ORIGINAL_EN=['the note','two faces','the wallpaper breathes','i right-clicked',
'everything can be changed','windows on a strip','goldengate.app',
'someone in the corner',"other people&#x27;s things",'it looks after itself',
'what it runs on','save yourself']
ORIGINAL_RU=['записка','два лица','обои дышат','я нажала правой',
'всё можно поменять','окна на ленте','goldengate.app',
'кто-то в углу','чужие вещи','оно следит за собой',
'на чём оно живёт','сохрани себя']
# Keep source geometry and animation classes; recolor just the project's accents.
COLORS={'#241432':'#090c25','#140a1c':'#070914','#ff5cad':'#8358ff',
'#4fe3ff':'#35d5ff','#ff8fc8':'#5555ff','#8a7fa0':'#9da3bf',
'#f6f2ff':'#f1f5ff'}
files=['header.svg','header.ru.svg','divider.svg','cobweb-divider.svg',
'cobweb-tl.svg','cobweb-tr.svg','stamp-evidence.svg','stamp-evidence.ru.svg',
'end.svg','end.ru.svg']
files += [f'tape-{i:02d}{lang}.svg' for i in range(1,13) for lang in ('','.ru')]
# OSD, title and handwriting use the source font families. Subset per SVG so
# the NCEA text renders while keeping README downloads small.
font_paths={
 'fx-osd':SOURCE/'.local/share/fonts/pixel/CozetteVectorBold.ttf',
 'fx-title':SOURCE/'.local/share/fonts/pixel/PixeloidSans-Bold.ttf',
 'fx-hand':SOURCE/'.config/quickshell/angelos/data/fonts/Caveat-Variable.ttf'}

def font_subsets(svg_text):
 root=ET.fromstring(svg_text)
 characters=''.join(''.join(e.itertext()) for e in root.iter() if e.tag.endswith('text'))
 data={}
 for family,path in font_paths.items():
  if f'font-family:"{family}"' not in svg_text:continue
  font=TTFont(path)
  opts=Options();opts.flavor='woff2';opts.recalc_bounds=False;opts.recalc_timestamp=False
  subset=Subsetter(options=opts);subset.populate(text=characters);subset.subset(font)
  font.flavor='woff2';buf=io.BytesIO();font.save(buf)
  data[family]=base64.b64encode(buf.getvalue()).decode('ascii')
 return data

for name in files:
 s=(FOOTAGE/name).read_text()
 # Original text is replaced in place, leaving its original cassette shape,
 # tracking noise, scanline pattern, clip paths and animation timing intact.
 if name.startswith('header'):
  s=s.replace('angelOS','NCEA AntiCheat')
  s=s.replace('font-size="144"','font-size="90"')
  s=s.replace('RECOVERED MEDIA · 12 TAPES · CASE #0031-12',
              'NCEA STUDIO · 1.0.0 STABLE · PAPER 1.20 / 1.20.1')
  s=s.replace('НАЙДЕННЫЕ ЗАПИСИ · 12 КАССЕТ · ДЕЛО №0031-12',
              'NCEA STUDIO · 1.0.0 STABLE · PAPER 1.20 / 1.20.1')
  s=s.replace('31.12.2002','01.11.2026')
 elif name.startswith('tape-'):
  num=int(name[5:7]);ru='.ru.' in name
  original=(ORIGINAL_RU if ru else ORIGINAL_EN)[num-1]
  label=LABELS[num][1 if ru else 0]
  s=s.replace('>'+original+'<','>'+escape(label)+'<')
  s=re.sub(r'\d{2}\.\d{2}\.2002','01.11.2026',s)
  # Long product labels need a smaller handwritten size, but cassette geometry is untouched.
  if len(label)>19:s=s.replace('font-size="60"','font-size="43"').replace('font-size="58"','font-size="42"')
  elif len(label)>14:s=s.replace('font-size="60"','font-size="50"').replace('font-size="58"','font-size="48"')
  if num==12:
   s=s.replace('NO SIGNAL','END OF REPORT').replace('НЕТ СИГНАЛА','КОНЕЦ ОТЧЁТА')
 elif name.startswith('stamp-evidence'):
  s=s.replace('EVIDENCE','OBSERVE').replace('DO NOT ERASE','NO AUTO BAN')
  s=s.replace('ВЕЩДОК','НАБЛЮДЕНИЕ').replace('НЕ СТИРАТЬ','БЕЗ АВТОБАНА')
  if '.ru.' in name:s=s.replace('font-size="36"','font-size="26"')
 elif name.startswith('end'):
  s=s.replace('she says hi','NCEA STUDIO').replace('она передаёт привет','NCEA STUDIO')
  s=s.replace('END OF TAPE','END OF REPORT').replace('КОНЕЦ ЗАПИСИ','КОНЕЦ ОТЧЁТА')
  s=s.replace('31.12.2002','01.11.2026')
 for old,new in COLORS.items():s=s.replace(old,new)
 for family,b64 in font_subsets(s).items():
  pat=r'(@font-face\{font-family:"'+re.escape(family)+r'";src:url\(data:font/woff2;base64,)[A-Za-z0-9+/=]+'
  s=re.sub(pat,lambda m:m.group(1)+b64,s)
 # Update descriptive metadata and retain upstream provenance in the file.
 s=re.sub(r'aria-label="[^"]*"',lambda m:'aria-label="'+escape(f'NCEA AntiCheat {name} visual adapted from AngelOS')+'"',s,count=1)
 s=s.replace('><title>', '><!-- Adapted from AngelOS-Dotfiles, copyright 2026 MixaDoDs, MIT; embedded fonts retain MIT/OFL notices in docs/licenses. --><title>',1)
 if name.startswith('header'):s=re.sub(r'<title>.*?</title>','<title>NCEA AntiCheat 1.0.0 Stable - VHS header</title>',s,count=1)
 else:s=re.sub(r'<title>.*?</title>',f'<title>NCEA AntiCheat - {name}</title>',s,count=1)
 ET.fromstring(s)
 (OUT/name).write_text(s)
print('Adapted',len(files),'SVG files; source commit',COMMIT)
