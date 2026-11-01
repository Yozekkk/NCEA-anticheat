#!/usr/bin/env python3
"""Generate original NCEA README artwork from the local NCEA AC reference image."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import html

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs/assets/readme'
OUT.mkdir(parents=True, exist_ok=True)
SOURCE = ROOT / 'Ascii/Ascii final result.png'
BLUE = '#5555FF'
DEEP = '#0000AA'
PURPLE = '#8358FF'
CYAN = '#35D5FF'
WHITE = '#F1F5FF'
BG = '#070914'


def frame(width, height, body, extra_css=''):
    css = f'''*{{box-sizing:border-box}} .glow{{filter:url(#glow)}} .pulse{{animation:pulse 6s ease-in-out infinite}}
    .scan{{animation:scan 8s linear infinite}} .rain{{animation:rain 9s linear infinite}}
    .reveal{{animation:reveal 1.8s ease-out both}}
    @keyframes pulse{{50%{{opacity:.48}}}} @keyframes scan{{to{{transform:translateY({height}px)}}}}
    @keyframes rain{{to{{transform:translateY({height}px)}}}}
    @keyframes reveal{{from{{opacity:.45;transform:translateY(7px)}}to{{opacity:1;transform:translateY(0)}}}}
    @media (prefers-reduced-motion:reduce){{.pulse,.scan,.rain,.reveal{{animation:none!important}}}}
    {extra_css}'''
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" role="img" aria-label="NCEA AntiCheat artwork">
<defs><linearGradient id="panel" x2="1" y2="1"><stop stop-color="{BG}"/><stop offset="1" stop-color="#101638"/></linearGradient>
<linearGradient id="stroke"><stop stop-color="{BLUE}"/><stop offset=".55" stop-color="{PURPLE}"/><stop offset="1" stop-color="{CYAN}"/></linearGradient>
<filter id="glow" x="-30%" y="-100%" width="160%" height="300%"><feGaussianBlur stdDeviation="5" result="b"/><feMerge><feMergeNode in="b"/><feMergeNode in="SourceGraphic"/></feMerge></filter></defs>
<style>{css}</style>
<rect width="{width}" height="{height}" rx="20" fill="url(#panel)"/>
{body}</svg>'''


def grid(w, h, step=30):
    return ''.join(f'<path d="M{x} 0V{h}" stroke="{BLUE}" stroke-opacity=".09"/>' for x in range(0,w,step)) + ''.join(f'<path d="M0 {y}H{w}" stroke="{CYAN}" stroke-opacity=".06"/>' for y in range(0,h,step))


def label(x,y,text,size=16,color=WHITE,weight=600,extra=''):
    return f'<text x="{x}" y="{y}" fill="{color}" font-family="DejaVu Sans,Arial,sans-serif" font-size="{size}" font-weight="{weight}" {extra}>{html.escape(text)}</text>'


def pixel_logo(x,y,scale=1):
    image=Image.open(SOURCE).convert('RGB')
    step=3
    parts=[]
    for py in range(0,image.height,step):
        run_color=None; run_start=0
        for px in range(0,image.width+step,step):
            color=None
            if px<image.width:
                r,g,b=image.getpixel((min(px+1,image.width-1),min(py+1,image.height-1)))
                if b>120 and r>80: color=BLUE
                elif b>55: color=DEEP
            if color!=run_color:
                if run_color:
                    parts.append(f'<rect x="{run_start}" y="{py}" width="{px-run_start}" height="{step}" fill="{run_color}"/>')
                run_color=color;run_start=px
    return f'<g transform="translate({x} {y}) scale({scale})" shape-rendering="crispEdges">'+''.join(parts)+'</g>'

rain=''.join(label(x,18+(i%3)*18,'01 10 00 11',11,CYAN,400,'opacity=".16"') for i,x in enumerate(range(55,1160,130)))
hero = grid(1200,430)+f'<g class="rain">{rain}</g>'
hero += f'<rect x="24" y="23" width="1152" height="384" rx="14" fill="none" stroke="url(#stroke)" stroke-opacity=".5"/>'
hero += '<path d="M25 78H1175M25 366H1175" stroke="url(#stroke)" stroke-opacity=".45"/>'
hero += label(56,59,'NCEA / SECURITY SYSTEMS',17,CYAN,700,'letter-spacing="3"')
hero += label(1130,59,'v0.1.0 DEV',15,WHITE,600,'text-anchor="end"')
hero += label(600,135,'NCEA AntiCheat',56,WHITE,800,'text-anchor="middle" class="reveal" letter-spacing="2"')
hero += f'<path d="M285 151H915" stroke="url(#stroke)" stroke-width="2" class="pulse"/>'
hero += pixel_logo(244,165)
hero += label(600,394,'PAPER 1.20 / 1.20.1     •     JAVA 17     •     OBSERVE MODE',17,CYAN,600,'text-anchor="middle" letter-spacing="1"')
hero += f'<rect class="scan" x="25" y="0" width="1150" height="2" fill="{CYAN}" opacity=".22"/>'
(OUT/'hero.svg').write_text(frame(1200,430,hero),encoding='utf-8')

divider = '<path d="M0 23H1200" stroke="url(#stroke)" stroke-width="2" opacity=".55"/>'
divider += '<path d="M445 23h310" stroke="'+CYAN+'" stroke-width="3" class="pulse glow"/>'
divider += label(600,17,'N C E A   /   A C',13,WHITE,700,'text-anchor="middle" letter-spacing="3"')
divider += '<g class="rain">'+''.join(label(x,40,'0101',9,CYAN,400,'opacity=".45"') for x in range(40,1200,150))+'</g>'
(OUT/'matrix-divider.svg').write_text(frame(1200,46,divider),encoding='utf-8')

cards=[('PAPER ADAPTER','Events • commands • lifecycle',58,105),('CORE / CHECK ENGINE','Observations • 5 checks',430,105),('VIOLATION ENGINE','VL • decay • alerts',802,105),('CONFIGURATION','Validated YAML • reload',244,235),('CONSOLE BRANDING','Logo • themes • preview',616,235)]
arch=grid(1200,360)
for title,sub,x,y in cards:
    arch+=f'<rect x="{x}" y="{y}" width="340" height="92" rx="10" fill="#101638" stroke="{BLUE}" stroke-width="1.5"/>'
    arch+=label(x+18,y+35,title,18,WHITE,700)+label(x+18,y+65,sub,14,CYAN,400)
arch+='<path d="M398 151H430M770 151H802M600 197V235M430 281H584M956 281h-32" stroke="url(#stroke)" stroke-width="3" fill="none"/>'
arch+=label(600,54,'SYSTEM ARCHITECTURE',29,WHITE,800,'text-anchor="middle" letter-spacing="2"')
arch+=label(600,338,'No packet inspection or physical simulation in 0.1.0 Development',14,CYAN,400,'text-anchor="middle"')
(OUT/'architecture.svg').write_text(frame(1200,360,arch),encoding='utf-8')

checks=grid(1200,290)+label(600,47,'ACTIVE CHECKS / OBSERVE',28,WHITE,800,'text-anchor="middle" letter-spacing="2"')
for i,(name,sub) in enumerate([('SPEED','horizontal displacement'),('FLY','vertical movement'),('REACH','attack distance'),('AUTOCLICKER','click intervals'),('FASTPLACE','placement rate')]):
    x=29+i*238
    checks+=f'<rect x="{x}" y="80" width="220" height="160" rx="10" fill="#101638" stroke="{[BLUE,PURPLE,CYAN,BLUE,PURPLE][i]}"/>'
    checks+=label(x+18,127,f'0{i+1}',32,CYAN,800)+label(x+18,164,name,20,WHITE,800)+label(x+18,192,sub,12,'#AAB8DB',400)
    checks+=f'<path d="M{x+18} 211H{x+202}" stroke="{BLUE}" opacity=".5"/>'
checks+=label(600,273,'Heuristics with exemptions and violation accumulation • no automatic bans',13,CYAN,400,'text-anchor="middle"')
(OUT/'checks.svg').write_text(frame(1200,290,checks),encoding='utf-8')

rows=(ROOT/'ncea-paper/build/previews/banner.txt').read_text(encoding='utf-8').splitlines()
terminal=grid(1200,395)
terminal+='<rect x="30" y="27" width="1140" height="340" rx="11" fill="#090D1D" stroke="'+BLUE+'"/>'
terminal+='<path d="M30 69H1170" stroke="'+BLUE+'" opacity=".5"/>'
terminal+='<circle cx="55" cy="48" r="5" fill="'+PURPLE+'"/><circle cx="73" cy="48" r="5" fill="'+BLUE+'"/><circle cx="91" cy="48" r="5" fill="'+CYAN+'"/>'
terminal+=label(600,53,'NCEA AntiCheat • console branding preview',15,WHITE,600,'text-anchor="middle"')
for i,line in enumerate(rows[:7]):
    terminal+=f'<text x="96" y="{96+i*29}" fill="{BLUE if i<4 else DEEP}" font-family="DejaVu Sans Mono,monospace" font-size="18" xml:space="preserve">{html.escape(line.rstrip())}</text>'
terminal+=label(96,324,'Platform Paper 1.20.1   |   Checks 5   |   Mode OBSERVE   |   Status ENABLED',15,CYAN,400)
terminal+=label(600,388,'Rendered logo preview • not a gameplay screenshot',13,'#AAB8DB',400,'text-anchor="middle"')
(OUT/'terminal.svg').write_text(frame(1200,395,terminal),encoding='utf-8')

status=grid(1200,155)+f'<rect x="34" y="28" width="1132" height="99" rx="11" fill="#12122C" stroke="{PURPLE}"/>'
status+=label(60,69,'DEVELOPMENT / 0.1.0',24,WHITE,800)
status+=label(60,99,'Observe-only heuristics • gameplay validation pending',16,CYAN,400)
status+=f'<circle cx="1093" cy="77" r="13" fill="{PURPLE}" class="pulse"/>'
status+=label(1068,83,'DEV',14,WHITE,700,'text-anchor="end"')
(OUT/'status.svg').write_text(frame(1200,155,status),encoding='utf-8')

footer=grid(1200,135)+label(600,62,'NCEA STUDIO',31,WHITE,800,'text-anchor="middle" letter-spacing="5"')
footer+=label(600,94,'ncea-studio.com  /  NCEA AntiCheat  /  0.1.0 Development',15,CYAN,400,'text-anchor="middle"')
footer+='<path d="M175 111H1025" stroke="url(#stroke)" stroke-width="2"/>'
(OUT/'footer.svg').write_text(frame(1200,135,footer),encoding='utf-8')

# Animated visual preview based on the supplied logo image. This does not depict gameplay.
source=Image.open(SOURCE).convert('RGB')
frames=[]
for n in range(12):
    canvas=Image.new('RGB',(900,300),(7,9,20))
    canvas.paste(source,(94,26))
    draw=ImageDraw.Draw(canvas,'RGBA')
    y=30+n*17
    draw.rectangle((94,y,805,y+6),fill=(53,213,255,26))
    draw.line((94,y,805,y),fill=(53,213,255,80),width=1)
    draw.text((94,250),'NCEA AC  /  CONSOLE BRANDING PREVIEW',fill=(180,207,255,255))
    frames.append(canvas.quantize(colors=64))
frames[0].save(OUT/'logo-preview.gif',save_all=True,append_images=frames[1:],duration=120,loop=0,optimize=True,disposal=2)
print('generated',*[f'{p.name}:{p.stat().st_size}' for p in sorted(OUT.iterdir())])
