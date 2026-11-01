#!/usr/bin/env python3
"""Rebuild NCEA Stable README artwork from the supplied local pixel logo.

Requires Pillow and rsvg-convert for the optional GIF raster pass. SVGs are
self-contained, use no JavaScript, and remain legible with motion disabled.
"""
from __future__ import annotations
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import html
import io
import math
import shutil
import subprocess

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs/assets/readme'
OUT.mkdir(parents=True, exist_ok=True)
SOURCE = ROOT / 'Ascii/Ascii final result.png'
C = {'bg':'#070914','navy':'#090C25','blue':'#5555FF','deep':'#0000AA',
     'purple':'#8358FF','cyan':'#35D5FF','white':'#F1F5FF','muted':'#9DA3BF','green':'#39E6A2'}
HEAD='Oxanium,DejaVu Sans,Arial,sans-serif'
MONO='JetBrains Mono,DejaVu Sans Mono,monospace'


def esc(s): return html.escape(str(s), quote=True)
def txt(x,y,s,size=20,color='white',weight=600,anchor=None,spacing=None,mono=False,extra=''):
    at=f' text-anchor="{anchor}"' if anchor else ''
    sp=f' letter-spacing="{spacing}"' if spacing else ''
    return f'<text x="{x}" y="{y}" fill="{C.get(color,color)}" font-family="{MONO if mono else HEAD}" font-size="{size}" font-weight="{weight}"{at}{sp} {extra}>{esc(s)}</text>'
def rect(x,y,w,h,fill='navy',stroke='blue',rx=8,opacity=1,extra=''):
    return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}" fill="{C.get(fill,fill)}" stroke="{C.get(stroke,stroke)}" opacity="{opacity}" {extra}/>'
def path(d,color='cyan',width=1,opacity=1,extra=''):
    return f'<path d="{d}" fill="none" stroke="{C.get(color,color)}" stroke-width="{width}" opacity="{opacity}" {extra}/>'
def dot(x,y,r=3,color='cyan',extra=''):
    return f'<circle cx="{x}" cy="{y}" r="{r}" fill="{C.get(color,color)}" {extra}/>'
def grid(w,h,step=36):
    return '<g opacity=".22">'+''.join(path(f'M{x} 0V{h}','blue',1,.3) for x in range(0,w,step))+''.join(path(f'M0 {y}H{w}','cyan',1,.2) for y in range(0,h,step))+'</g>'
def corner(x,y,side=1):
    return path(f'M{x+side*24} {y}H{x}V{y+side*24}','cyan',2,.7)
def shell(w,h,body,title='',aria='NCEA AntiCheat graphic'):
    css=f'''@keyframes fall{{to{{transform:translateY({h}px)}}}}
@keyframes drift{{50%{{transform:translate(10px,-8px);opacity:.3}}}}
@keyframes scan{{to{{transform:translateY({h}px)}}}}
@keyframes pulse{{50%{{opacity:.42}}}}
@keyframes flow{{to{{stroke-dashoffset:-40}}}}
@keyframes enter{{0%{{opacity:.22;transform:translateY(8px)}}100%{{opacity:1;transform:translateY(0)}}}}
@keyframes glitch{{2%,96%{{transform:translateX(0)}}97%{{transform:translateX(3px)}}98%{{transform:translateX(-2px)}}99%{{transform:translateX(0)}}}}
.fall{{animation:fall 12s linear infinite}} .drift{{animation:drift 7s ease-in-out infinite}}
.scan{{animation:scan 7s linear infinite}} .pulse{{animation:pulse 3s ease-in-out infinite}}
.flow{{animation:flow 5s linear infinite}} .enter{{animation:enter 2s ease-out both}}
.glitch{{animation:glitch 8s linear infinite}}
@media (prefers-reduced-motion:reduce){{.fall,.drift,.scan,.pulse,.flow,.enter,.glitch{{animation:none!important}}}}'''
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {w} {h}" role="img" aria-label="{esc(aria)}">
<title>{esc(title or aria)}</title><defs>
<linearGradient id="bg" x2="1" y2="1"><stop stop-color="{C['bg']}"/><stop offset=".5" stop-color="{C['navy']}"/><stop offset="1" stop-color="#111A40"/></linearGradient>
<linearGradient id="line"><stop stop-color="{C['blue']}"/><stop offset=".5" stop-color="{C['purple']}"/><stop offset="1" stop-color="{C['cyan']}"/></linearGradient>
<filter id="glow" x="-40%" y="-90%" width="180%" height="280%"><feGaussianBlur stdDeviation="5" result="blur"/><feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge></filter>
<clipPath id="clip"><rect x="2" y="2" width="{w-4}" height="{h-4}" rx="18"/></clipPath>
</defs><style>{css}</style><rect width="{w}" height="{h}" rx="18" fill="url(#bg)"/>
<g clip-path="url(#clip)">{grid(w,h)}{body}</g>
<rect x="1" y="1" width="{w-2}" height="{h-2}" rx="18" fill="none" stroke="url(#line)" opacity=".4"/>
</svg>'''
def save(name,w,h,body,title='',aria='NCEA AntiCheat graphic'):
    (OUT/name).write_text(shell(w,h,body,title,aria),encoding='utf-8')
def chip_label(x,y,s):return txt(x,y,s,12,'cyan',700,spacing=2,mono=True)

# The mark is converted into run-length pixel rectangles. No font imitation.
logo=Image.open(SOURCE).convert('RGB')
def pixel_logo(x,y,scale=1):
    out=[]; step=3
    for py in range(0,logo.height,step):
        last=None; start=0
        for px in range(0,logo.width+step,step):
            color=None
            if px<logo.width:
                r,g,b=logo.getpixel((min(px+1,logo.width-1),min(py+1,logo.height-1)))
                if b>130 and r>80:color=C['blue']
                elif b>55:color=C['deep']
            if color!=last:
                if last:out.append(f'<rect x="{start}" y="{py}" width="{px-start}" height="{step}" fill="{last}"/>')
                last=color;start=px
    return f'<g transform="translate({x} {y}) scale({scale})" shape-rendering="crispEdges">'+''.join(out)+'</g>'

def rain(w,h):
    pieces=[]
    for i,x in enumerate(range(30,w,92)):
        chars='01011001' if i%2 else '10100110'
        pieces.append(txt(x,21+i%3*29,chars,12,'cyan',400,mono=True,extra='opacity=".22"'))
    return '<g class="fall">'+''.join(pieces)+'</g>'
def snow(w,h):
    return '<g class="drift">'+''.join(dot((i*127+31)%w,(i*71+19)%h,1+(i%3==0),'cyan',extra='opacity=".35"') for i in range(46))+'</g>'

hero=rain(1200,470)+snow(1200,470)
hero+=path('M36 65H1164M36 404H1164','blue',1,.7)+path('M120 227H1080','cyan',2,.25)
hero+=corner(36,32)+corner(1164,32,-1)+corner(36,438)+corner(1164,438,-1)
hero+=txt(64,55,'NCEA / SECURITY SYSTEMS',15,'cyan',700,spacing=3,mono=True)
hero+=txt(1137,55,'BUILD 1.0.0  /  STABLE',14,'green',700,anchor='end',mono=True)
hero+=txt(600,155,'NCEA ANTICHEAT',65,'white',800,anchor='middle',spacing=3,extra='class="enter glitch"')
hero+=txt(600,194,'VERSION 1.0.0  —  STABLE RELEASE',18,'green',700,anchor='middle',spacing=3,mono=True,extra='class="enter"')
hero+=pixel_logo(244,210)
hero+=path('M150 397H1050','cyan',2,.55,extra='stroke-dasharray="8 10" class="flow"')
hero+=txt(600,437,'PAPER 1.20 / 1.20.1    •    JAVA 17    •    OBSERVE MODE',15,'muted',600,anchor='middle',spacing=1,mono=True)
hero+=f'<rect x="35" y="10" width="1130" height="2" fill="{C["cyan"]}" opacity=".28" class="scan"/>'
save('hero.svg',1200,470,hero,'NCEA AntiCheat 1.0.0 Stable hero','NCEA AntiCheat Stable with original pixel logo')

# Standalone strips: distinct motion motifs, no repeated generic divider.
strip=rain(1200,88)+path('M0 53H1200','blue',1,.5)+path('M185 53H1015','cyan',3,.8,extra='stroke-dasharray="12 8" class="flow"')+txt(600,39,'NCEA / SIGNAL ACTIVE',13,'white',700,anchor='middle',spacing=4,mono=True)
save('matrix-divider.svg',1200,88,strip,'Matrix rain signal divider')
strip2=snow(1200,75)+path('M0 37H1200','purple',1,.7)+path('M435 37H765','cyan',3,.9)+txt(600,30,'DIGITAL SNOW',12,'white',700,anchor='middle',spacing=4,mono=True)
save('digital-snow.svg',1200,75,strip2,'Digital snow divider')
strip3=path('M0 34H1200','blue',2,.55)+path('M84 34H1116','cyan',2,.75,extra='stroke-dasharray="24 14" class="flow"')+txt(600,25,'SECURE TELEMETRY / NCEA',12,'white',700,anchor='middle',spacing=3,mono=True)
save('cyber-divider.svg',1200,68,strip3,'Cyber data-flow divider')

# Chamfered, layered mini panels; label represents facts, not live telemetry.
badges=[('stable-badge.svg','01','1.0.0  STABLE','green',210),('paper-badge.svg','P','PAPER 1.20 / 1.20.1','cyan',250),
('java-badge.svg','J','JAVA 17','blue',166),('gradle-badge.svg','G','GRADLE','purple',165),
('checks-badge.svg','05','5 ACTIVE CHECKS','cyan',205),('ci-badge.svg','✓','JAVA 17 CI','green',185),
('license-badge.svg','©','ALL RIGHTS RESERVED','muted',255),('studio-badge.svg','N','NCEA STUDIO','purple',200)]
for name,icon,label,color,w in badges:
    poly=f'<path d="M12 1H{w-24}L{w-1} 23V55H24L1 32V12Z" fill="{C["navy"]}" stroke="{C[color]}" stroke-width="2"/>'
    inner=path(f'M18 10H{w-32}M{w-38} 46H50',color,1,.45)
    b=poly+inner+path('M46 12V45',color,2,.65)+txt(24,37,icon,18,color,800,anchor='middle',mono=True)+txt(58,36,label,13,'white',700,spacing=1,mono=True)
    b+=dot(w-27,28,3,color,extra='class="pulse"')
    save(name,w,56,b,label,label)

status=rain(1200,166)+snow(1200,166)
status+=path('M39 31H1160L1179 50V125L1160 145H39L20 125V50Z','green',2,.7)
status+=txt(59,79,'STABLE / 1.0.0',32,'white',800,spacing=2)+txt(61,111,'FIVE OBSERVE-MODE CHECKS  •  PAPER 1.20 / 1.20.1',15,'muted',600,mono=True)
status+=dot(1102,87,17,'green',extra='class="pulse" filter="url(#glow)"')+txt(1102,94,'✓',22,'bg',800,anchor='middle')
status+=path('M38 136H1171','cyan',2,.5,extra='stroke-dasharray="20 16" class="flow"')
save('status.svg',1200,166,status,'NCEA 1.0.0 Stable status')

# One overall map, then five distinct check diagrams.
checks=rain(1200,290)+txt(600,52,'DETECTION GRID',31,'white',800,anchor='middle',spacing=3)
for i,(name,tag) in enumerate([('SPEED','MOVE'),('FLY','VERTICAL'),('REACH','COMBAT'),('AUTOCLICKER','TIMING'),('FASTPLACE','WORLD')]):
    x=25+i*237
    checks+=f'<path d="M{x+10} 81H{x+207}L{x+220} 94V221L{x+207} 235H{x+10}L{x} 222V94Z" fill="{C["navy"]}" stroke="{C["cyan" if i%2==0 else "purple"]}" opacity=".95"/>'
    checks+=chip_label(x+17,109,f'0{i+1} / {tag}')+txt(x+17,159,name,20,'white',800)+path(f'M{x+18} 183H{x+198}','blue',2,.6)
    checks+=dot(x+31,206,4,'green',extra='class="pulse"')+txt(x+46,211,'OBSERVE',11,'muted',600,mono=True)
checks+=txt(600,272,'SCHEMATIC CHECK OVERVIEW • NO LIVE PLAYER DATA',12,'muted',500,anchor='middle',mono=True)
save('checks.svg',1200,290,checks,'Five implemented checks')

names=['speed','fly','reach','autoclicker','fastplace']
for kind in names:
    b=snow(1200,258)+chip_label(50,46,f'CHECK / {kind.upper()}')
    b+=txt(1150,45,'SCHEMATIC • NOT LIVE DATA',12,'muted',500,anchor='end',mono=True)
    b+=path('M48 205H1150','blue',1,.5)
    if kind=='speed':
        b+=txt(52,96,'HORIZONTAL DISPLACEMENT',23,'white',800)+path('M110 158H984','cyan',3,.7,extra='stroke-dasharray="8 12" class="flow"')
        b+=path('M785 127V189','purple',2,.9)+txt(787,116,'THRESHOLD',12,'purple',700,mono=True)
        b+=dot(423,158,17,'cyan',extra='filter="url(#glow)" class="drift"')+path('M423 158L576 158','cyan',3,.8)
        b+=txt(52,238,'Movement window / evidence accumulation',13,'muted',500,mono=True)
    elif kind=='fly':
        b+=txt(52,96,'VERTICAL PERSISTENCE',23,'white',800)+path('M456 190V75M618 190V75','blue',1,.6)
        b+=path('M535 190V82','cyan',3,.9,extra='stroke-dasharray="7 8" class="flow"')+dot(535,119,18,'purple',extra='class="drift" filter="url(#glow)"')
        b+=txt(680,121,'OFF GROUND',17,'cyan',700,mono=True)+txt(680,154,'NEAR-ZERO ΔY',16,'white',600,mono=True)
        b+=txt(52,238,'Repeated observations / server-context exemptions',13,'muted',500,mono=True)
    elif kind=='reach':
        b+=txt(52,96,'EYE → SERVER HITBOX',23,'white',800)+dot(318,162,24,'cyan')+dot(870,162,24,'purple')
        b+=path('M342 162H846','green',4,.7,extra='stroke-dasharray="12 10" class="flow"')
        b+=path('M730 115V203','purple',2,.8)+txt(530,133,'DISTANCE SAMPLE',15,'white',700,anchor='middle',mono=True)
        b+=txt(52,238,'Illustrative measurement / no packet-history compensation',13,'muted',500,mono=True)
    elif kind=='autoclicker':
        b+=txt(52,96,'ATTACK TIMING WINDOW',23,'white',800)+path('M132 185H1030M132 116V185','blue',2,.7)
        for x,y in [(220,141),(328,158),(390,135),(515,154),(600,144),(685,136),(752,155),(835,138),(945,149)]:
            b+=path(f'M{x} 183V{y}','cyan',5,.9)+dot(x,y,4,'purple')
        b+=txt(52,238,'Successful attack events only / not raw click telemetry',13,'muted',500,mono=True)
    else:
        b+=txt(52,96,'PLACEMENT INTERVALS',23,'white',800)
        for i in range(5):
            x=225+i*146
            b+=rect(x,136,72,65,'navy','cyan' if i<3 else 'purple',3,.95)+txt(x+36,173,str(i+1),18,'white',700,anchor='middle',mono=True)
            if i<4:b+=path(f'M{x+75} 167H{x+138}','cyan',2,.7,extra='stroke-dasharray="5 6" class="flow"')
        b+=txt(52,238,'Successful BlockPlaceEvent intervals / illustrative sequence',13,'muted',500,mono=True)
    save(f'{kind}-demo.svg',1200,258,b,f'{kind.title()} check schematic',f'{kind.title()} check schematic animation')

arch=rain(1200,410)+txt(600,50,'TELEMETRY ARCHITECTURE',32,'white',800,anchor='middle',spacing=2)
boxes=[('PAPER ADAPTER',70,104,'Event context'),('NCEA CORE',410,104,'Immutable observations'),('DETECTION ENGINE',795,104,'Five checks'),
('CONFIGURATION',70,266,'Validated YAML'),('VIOLATION ENGINE',410,266,'VL + decay'),('ALERTS',795,266,'Staff delivery')]
for title,x,y,sub in boxes:
    arch+=f'<path d="M{x+14} {y}H{x+276}L{x+295} {y+18}V{y+88}H{x}V{y+14}Z" fill="{C["navy"]}" stroke="{C["blue"]}"/>'
    arch+=txt(x+22,y+36,title,18,'white',800)+txt(x+22,y+68,sub,14,'cyan',500,mono=True)
arch+=path('M365 150H410M705 150H795M365 311H410M705 311H795M555 192V266','cyan',3,.8,extra='stroke-dasharray="12 10" class="flow"')
arch+=txt(1100,240,'CONSOLE',14,'purple',700,anchor='end',mono=True)+txt(1100,259,'RENDERER',14,'purple',700,anchor='end',mono=True)
arch+=path('M1040 192V270','purple',2,.6)+txt(600,393,'PAPER 1.20 / 1.20.1  •  MAIN SERVER THREAD  •  NO PACKET LAYER',12,'muted',500,anchor='middle',mono=True)
save('architecture.svg',1200,410,arch,'NCEA architecture data flow')

snapshot=(ROOT/'ncea-core/src/test/resources/branding/original-seven-rows.snapshot.txt').read_text(encoding='utf-8').splitlines()
term=rect(45,32,1110,388,'#050917','blue',12)+path('M45 76H1155','blue',1,.7)
term+=dot(73,54,5,'purple')+dot(91,54,5,'blue')+dot(109,54,5,'cyan')
term+=txt(600,60,'NCEA AC  /  SERVER CONSOLE PREVIEW',15,'white',700,anchor='middle',mono=True)
for i,row in enumerate(snapshot):
    term+=txt(124,111+i*35,row.rstrip(),18,'blue' if i<4 else 'deep',500,mono=True,extra='xml:space="preserve"')
term+=txt(110,379,'NCEA AntiCheat 1.0.0  •  Paper 1.20.1  •  Checks 5  •  OBSERVE  •  ENABLED',14,'green',600,mono=True)
term+=txt(600,446,'Illustrative boot sequence based on real banner output; no gameplay footage',13,'muted',500,anchor='middle')
save('terminal.svg',1200,470,term,'NCEA console banner preview')

config=rain(1200,330)+txt(55,54,'CONFIGURATION / VALIDATED SNAPSHOT',27,'white',800)
config+=rect(54,80,680,216,'#080D23','blue',9)+rect(780,80,350,216,'navy','purple',9)
for i,(k,v) in enumerate([('branding.banner.style','original-3d'),('branding.console.colors','auto'),('checks.speed.enabled','true'),('checks.speed.weight','1.0')]):
    y=120+i*43
    config+=txt(85,y,k,16,'cyan',500,mono=True)+txt(690,y,v,16,'green',600,anchor='end',mono=True)
config+=txt(955,133,'VALIDATE',22,'white',800,anchor='middle')+path('M834 163H1076','green',3,.7)
config+=txt(955,210,'✓  SCHEMA 1',19,'green',700,anchor='middle',mono=True)+txt(955,247,'ATOMIC RELOAD',14,'muted',500,anchor='middle',mono=True)
config+=txt(600,318,'Values shown are configuration examples, not gameplay evidence',12,'muted',500,anchor='middle')
save('config-demo.svg',1200,330,config,'Validated YAML configuration')

testing=rain(1200,326)+txt(55,51,'VERIFIED BUILD / JAVA 17',27,'white',800)
for i,(title,value) in enumerate([('JUnit','77 PASS'),('CORE LINES','81.4%'),('PAPER LINES','49.2%'),('PAPER STARTS','1.20 / 1.20.1')]):
    x=40+i*293
    testing+=rect(x,91,270,168,'navy','green' if i==0 else 'blue',9)+chip_label(x+18,123,title)
    testing+=txt(x+18,187,value,26,'green' if i==0 else 'white',800,mono=True)+path(f'M{x+19} 219H{x+246}','cyan',2,.7)
testing+=txt(600,307,'Historical 0.1.0 tests • final 1.0.0 verification appears in docs/TEST_RESULTS.md',12,'muted',500,anchor='middle')
save('testing-panel.svg',1200,326,testing,'NCEA test results dashboard')

document=snow(1200,316)+txt(54,50,'DOCUMENTATION / SOURCE OF TRUTH',27,'white',800)
for i,(title,sub) in enumerate([('INSTALL','Paper setup'),('CHECKS','Real algorithms'),('CONFIG','YAML reference'),('QA','Measured results')]):
    x=46+i*294
    document+=f'<path d="M{x+14} 91H{x+238}L{x+258} 111V244H{x}V105Z" fill="{C["navy"]}" stroke="{C["cyan" if i==2 else "purple"]}"/>'
    document+=txt(x+22,149,title,22,'white',800)+txt(x+22,188,sub,14,'cyan',500,mono=True)+path(f'M{x+22} 215H{x+210}','blue',2,.7)
document+=txt(600,298,'Readable Markdown remains available next to every visual',13,'muted',500,anchor='middle')
save('documentation-panel.svg',1200,316,document,'NCEA documentation navigation')

foot=snow(1200,187)+rain(1200,187)+path('M120 38H1080','cyan',2,.75,extra='stroke-dasharray="24 12" class="flow"')
foot+=txt(600,104,'NCEA STUDIO',40,'white',800,anchor='middle',spacing=5)+txt(600,143,'ncea-studio.com   /   NCEA AntiCheat 1.0.0 Stable',16,'cyan',600,anchor='middle',mono=True)
save('footer.svg',1200,187,foot,'NCEA Studio footer')

# GIF pass: per-section motion has a different focus. These are conceptual
# visualizations; they do not claim a measured detector event or gameplay run.
if shutil.which('rsvg-convert'):
    def raster(name,width=760):
        data=subprocess.check_output(['rsvg-convert',str(OUT/name)])
        image=Image.open(io.BytesIO(data)).convert('RGB')
        h=round(image.height*width/image.width)
        return image.resize((width,h),Image.Resampling.LANCZOS)
    def gif(svg_name,gif_name,kind,frames=9):
        base=raster(svg_name)
        width,height=base.size
        pal=base.quantize(colors=80,dither=Image.Dither.NONE)
        series=[]
        for n in range(frames):
            image=base.copy();d=ImageDraw.Draw(image,'RGBA')
            if kind=='hero':
                # Power-on, logo reveal, brief glitch, then calm rain/scan.
                if n<2:d.rectangle((0,45,width,height-35),fill=(7,9,20,180-n*60))
                elif n<4:d.rectangle((150,118,width-130,height-47),fill=(7,9,20,80-n*24))
                if n==5:
                    d.line((210,146,555,146),fill=(53,213,255,120),width=3)
                for i,x in enumerate(range(55,width-40,102)):
                    y=(n*18+i*31)%(height-34)+12
                    d.text((x,y),'01',fill=(53,213,255,32))
                y=32+n*20;d.line((33,y,width-34,y),fill=(53,213,255,40),width=1)
            elif kind=='status':
                x=round(width*.91);y=round(height*.52);r=6+n%5
                d.ellipse((x-r,y-r,x+r,y+r),outline=(57,230,162,70),width=2)
                d.line((20+n*11,height-16,180+n*11,height-16),fill=(57,230,162,90),width=2)
            elif kind=='speed':
                x=round(width*(.15+.65*n/(frames-1)));y=round(height*.61)
                d.ellipse((x-9,y-9,x+9,y+9),fill=(53,213,255,175))
                d.line((x-78,y,x-12,y),fill=(131,88,255,130),width=4)
            elif kind=='fly':
                x=round(width*.446);y=round(height*(.72-.4*n/(frames-1)))
                d.ellipse((x-8,y-8,x+8,y+8),fill=(131,88,255,190))
                d.line((x,y+12,x,y+42),fill=(53,213,255,95),width=2)
            elif kind=='reach':
                x=round(width*(.27+.4*n/(frames-1)));y=round(height*.62)
                d.ellipse((x-5,y-5,x+5,y+5),fill=(57,230,162,200))
                d.line((x,105,x,180),fill=(131,88,255,45),width=1)
            elif kind=='autoclicker':
                x=round(width*(.16+.67*n/(frames-1)))
                d.line((x,92,x,180),fill=(53,213,255,100),width=3)
            elif kind=='fastplace':
                x=round(width*(.19+.123*(n%5)))
                d.rectangle((x,115,x+50,165),outline=(57,230,162,185),width=4)
            elif kind=='architecture':
                x=round(width*(.30+.36*n/(frames-1)));y=round(height*.36)
                d.ellipse((x-5,y-5,x+5,y+5),fill=(53,213,255,180))
                d.line((x,150,x,183),fill=(131,88,255,85),width=2)
            elif kind=='console':
                # Reveal the actual banner rows and then the final ENABLED line.
                top=round(height*.20);step=round(height*.075)
                visible=min(7,max(0,n-1))
                if visible<7:d.rectangle((55,top+visible*step,width-38,top+7*step+5),fill=(5,9,23,245))
                if n<frames-2:d.rectangle((45,height-85,width-44,height-36),fill=(5,9,23,245))
                d.rectangle((43,33,45+n*55,35),fill=(53,213,255,90))
            elif kind=='config':
                y=round(height*(.29+.13*(n%4)))
                d.rectangle((42,y,465,y+24),outline=(53,213,255,100),width=2)
            elif kind=='testing':
                x=round(width*(.03+.24*(n%4)))
                d.ellipse((x,70,x+12,82),fill=(57,230,162,180))
            elif kind=='docs':
                x=round(width*(.04+.245*(n%4)))
                d.rectangle((x,70,x+155,160),outline=(53,213,255,110),width=3)
            elif kind=='footer':
                for i,x in enumerate(range(70,width-40,120)):
                    y=(n*13+i*23)%(height-15)
                    d.ellipse((x,y,x+3,y+3),fill=(53,213,255,90))
            elif kind=='logo':
                y=32+n*18;d.line((45,y,width-45,y),fill=(53,213,255,55),width=2)
            series.append(image.quantize(palette=pal,dither=Image.Dither.NONE))
        series[0].save(OUT/gif_name,save_all=True,append_images=series[1:],duration=130,
                       loop=0,optimize=True,disposal=1)
    for svg,gif_name,kind in [('hero.svg','hero-motion.gif','hero'),('status.svg','stable-status.gif','status'),
        ('speed-demo.svg','speed-demo.gif','speed'),('fly-demo.svg','fly-demo.gif','fly'),
        ('reach-demo.svg','reach-demo.gif','reach'),('autoclicker-demo.svg','autoclicker-demo.gif','autoclicker'),
        ('fastplace-demo.svg','fastplace-demo.gif','fastplace'),('architecture.svg','architecture-motion.gif','architecture'),
        ('terminal.svg','console-boot.gif','console'),('config-demo.svg','config-demo.gif','config'),
        ('testing-panel.svg','testing-panel.gif','testing'),('documentation-panel.svg','documentation-panel.gif','docs'),
        ('footer.svg','neon-footer.gif','footer'),('terminal.svg','logo-preview.gif','logo')]:
        gif(svg,gif_name,kind,10 if kind in ('hero','console') else 8)
print('generated',len(list(OUT.iterdir())),'assets; total bytes',sum(p.stat().st_size for p in OUT.iterdir()))
