#!/usr/bin/env python3
"""Generate honest NCEA technical GIF visualizations for the AngelOS-based README.

No AngelOS demo recordings or GUI footage are copied. Optional font comes from
an inspected clone of AngelOS (Cozette, MIT); see docs/ASSET_CREDITS.md.
"""
from pathlib import Path
import sys
from PIL import Image, ImageDraw, ImageFont

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'docs/assets/angelos-adapted/demos';OUT.mkdir(parents=True,exist_ok=True)
REF=Path(sys.argv[1]).resolve() if len(sys.argv)>1 else Path('/tmp/ncea-angelos-reference')
FONT=REF/'.local/share/fonts/pixel/CozetteVectorBold.ttf'
if not FONT.is_file():FONT=Path('/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf')
FONT32=ImageFont.truetype(str(FONT),32)
FONT21=ImageFont.truetype(str(FONT),21)
FONT16=ImageFont.truetype(str(FONT),16)
W,H=960,330
COL={'bg':(7,9,20),'navy':(9,12,37),'blue':(85,85,255),'purple':(131,88,255),'cyan':(53,213,255),'white':(241,245,255),'muted':(157,163,191),'green':(57,230,162)}

def base(title,code):
 im=Image.new('RGB',(W,H),COL['bg']);d=ImageDraw.Draw(im)
 for y in range(0,H,4):d.line((0,y,W,y),fill=(14,17,37),width=1)
 for x in range(0,W,80):d.line((x,0,x,H),fill=(11,20,47),width=1)
 d.rectangle((8,8,W-9,H-9),outline=COL['blue'],width=2)
 d.line((24,55,W-25,55),fill=COL['purple'],width=2)
 d.text((35,20),title,font=FONT32,fill=COL['white'])
 d.text((W-315,27),code,font=FONT16,fill=COL['cyan'])
 d.text((36,H-35),'SCHEMATIC / NOT LIVE GAMEPLAY',font=FONT16,fill=COL['muted'])
 return im,d

def save(name,frames):
 frames[-1].save(OUT/name.replace('.gif','.png'),optimize=True)
 palette=frames[-1].quantize(colors=96)
 q=[f.quantize(palette=palette) for f in frames]
 q[0].save(OUT/name,save_all=True,append_images=q[1:],duration=180,loop=0,optimize=True,disposal=2)

frames=[]
commands=['/nceaac version','/nceaac checks','/nceaac config validate','/nceaac status']
for n in range(12):
 im,d=base('NCEA CONSOLE / COMMANDS','TAPE 02 / 12')
 for i,c in enumerate(commands):
  y=85+i*49
  d.text((54,y),'>' if i==min(n//3,3) else ' ',font=FONT21,fill=COL['green'])
  shown=c[:max(0,(n-i*2)*4)] if n<8 else c
  d.text((84,y),shown,font=FONT21,fill=COL['white'] if i<=n//3 else COL['muted'])
 d.text((615,264),'JAVA 17  ·  PAPER 1.20.x',font=FONT16,fill=COL['cyan'])
 frames.append(im)
save('commands.gif',frames)

frames=[]
for n in range(12):
 im,d=base('VIOLATION LEVEL / EVIDENCE','TAPE 03 / 12')
 d.text((57,78),'SPEED CHECK · OBSERVE MODE',font=FONT21,fill=COL['cyan'])
 d.line((85,225,880,225),fill=COL['muted'],width=2)
 values=[0,0.7,0.5,1.8,1.3,2.2,1.6,2.8,2.1,3.2,2.5,3.5]
 coords=[]
 for i,v in enumerate(values[:n+1]):coords.append((92+i*70,225-int(v*35)))
 if len(coords)>1:d.line(coords,fill=COL['green'],width=5)
 for x,y in coords:d.ellipse((x-5,y-5,x+5,y+5),fill=COL['cyan'])
 d.text((58,258),'VL ACCUMULATES AND DECAYS PER CHECK',font=FONT16,fill=COL['white'])
 frames.append(im)
save('violations.gif',frames)

frames=[]
for n in range(12):
 im,d=base('NCEA ANTICHEAT / RELEASE','TAPE 12 / 12')
 d.text((125,99),'1.0.0',font=ImageFont.truetype(str(FONT),70),fill=COL['white'])
 d.text((430,118),'STABLE',font=FONT32,fill=COL['green'] if n%4<2 else COL['cyan'])
 d.line((125,192,125+n*63,192),fill=COL['green'],width=4)
 d.text((125,225),'PAPER 1.20  /  1.20.1',font=FONT21,fill=COL['muted'])
 frames.append(im)
save('stable-release.gif',frames)
# A separate GIF replays the actual local Paper 1.20.1 startup log excerpt.
# Logger prefixes are omitted only in the visualization for readability.
excerpt=ROOT/'docs/assets/angelos-adapted/startup-excerpt.txt'
if excerpt.is_file():
 lines=[line.split('] ',1)[1] for line in excerpt.read_text().splitlines()]
 assert len(lines)==17 and any('Status         ENABLED' in line for line in lines)
 mono=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf',16)
 recorded=[]
 for n in range(len(lines)+1):
  im=Image.new('RGB',(960,500),COL['bg']);d=ImageDraw.Draw(im)
  d.rectangle((7,7,952,492),outline=COL['blue'],width=2)
  d.text((28,18),'LOCAL PAPER 1.20.1 / REAL STARTUP LOG',font=FONT21,fill=COL['cyan'])
  d.text((28,48),'01.11.2026 · PREFIXES OMITTED FOR READABILITY',font=FONT16,fill=COL['muted'])
  d.line((28,75,928,75),fill=COL['purple'],width=2)
  for i,line in enumerate(lines[:n]):
   color=COL['green'] if 'ENABLED' in line else COL['white'] if i<7 else COL['muted']
   d.text((27,86+i*23),line,font=mono,fill=color)
  recorded.append(im)
 save('paper-startup.gif',recorded)
print('Generated',len(list(OUT.glob('*.gif'))),'NCEA GIFs with static PNG fallbacks')
