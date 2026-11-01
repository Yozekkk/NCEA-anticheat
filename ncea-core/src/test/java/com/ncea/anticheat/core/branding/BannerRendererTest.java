package com.ncea.anticheat.core.branding;

import com.ncea.anticheat.core.StartupBanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class BannerRendererTest {
    private final StartupBanner.Info info=new StartupBanner.Info("1.0.0","1.20.1",17,5,"OBSERVE",57,StartupBanner.Status.ENABLED);
    private final BrandingSettings defaults=BrandingSettings.defaults();
    private BrandingSettings settings(BrandingSettings.Style style,BrandingSettings.CharsetMode charset,
            BrandingSettings.Colors colors,BrandingSettings.ColorMode mode,BannerTheme theme,int width) {
        return new BrandingSettings(true,true,style,charset,BrandingSettings.Alignment.LEFT,width,
                BrandingSettings.Fallback.COMPACT,colors,mode,theme,defaults.information(),true,
                BrandingSettings.ShutdownStyle.COMPACT,"custom-banner.txt");
    }
    private List<String> render(BrandingSettings s,ConsoleCapabilities caps,String custom) {return BannerRenderer.render(s,info,caps,custom);}
    @Test void originalGeometrySnapshot() throws Exception {
        String plain=String.join("\n",LogoSegments.plainRows())+"\n";
        String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(plain.getBytes(StandardCharsets.UTF_8)));
        assertEquals("01ab6d86593082be202a69370731c9c7cee7d2298c6a062ae57bf6229f4e1b26",hash);
        assertEquals(7,LogoSegments.original().size());
        assertTrue(LogoSegments.plainRows().stream().allMatch(row->VisualWidth.of(row)==77));
    }
    @Test void unicodeMonoPreservesOriginalRows() {
        var s=settings(BrandingSettings.Style.MONOCHROME,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.ON,BrandingSettings.ColorMode.ANSI16,defaults.theme(),0);
        List<String> lines=render(s,new ConsoleCapabilities(true,true),null);
        assertEquals(LogoSegments.plainRows(),lines.subList(0,7));
        assertTrue(lines.stream().noneMatch(line->line.contains("\u001B")));
    }
    @Test void asciiFallbackHasOnlyAscii() {
        var s=settings(BrandingSettings.Style.ASCII,BrandingSettings.CharsetMode.ASCII,
                BrandingSettings.Colors.ON,BrandingSettings.ColorMode.ANSI16,defaults.theme(),0);
        List<String> lines=render(s,new ConsoleCapabilities(true,false),null);
        assertEquals(LogoSegments.plainRows().stream().map(BannerRenderer::ascii).toList(),lines.subList(0,7));
        assertTrue(lines.stream().allMatch(line->line.chars().allMatch(cp->cp>=32&&cp<127)));
    }
    @Test void originalAnsi16MatchesSourceCodesAndResetsEachRow() {
        var s=settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.ON,BrandingSettings.ColorMode.ANSI16,defaults.theme(),0);
        List<String> lines=render(s,new ConsoleCapabilities(false,true),null);
        assertTrue(lines.subList(0,7).stream().allMatch(line->line.endsWith(AnsiColorFormatter.RESET)));
        String all=String.join("\n",lines);
        for(String code:List.of("\u001B[0;94m","\u001B[0;34m","\u001B[0;94;44m","\u001B[0;34;44m","\u001B[0m")) assertTrue(all.contains(code),code);
        assertEquals(77,VisualWidth.of(lines.get(0)));
    }
    @ParameterizedTest @EnumSource(value=BrandingSettings.ColorMode.class,names={"ANSI256","TRUECOLOR"})
    void extendedColorsResetBackground(BrandingSettings.ColorMode mode) {
        var s=settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.ON,mode,defaults.theme(),0);
        String all=String.join("\n",render(s,new ConsoleCapabilities(true,true),null));
        assertTrue(all.contains(mode==BrandingSettings.ColorMode.TRUECOLOR ? "\u001B[38;2;85;85;255m" : "\u001B[38;5;"));
        assertTrue(all.contains(mode==BrandingSettings.ColorMode.TRUECOLOR ? "\u001B[48;2;0;0;170m" : "\u001B[48;5;"));
        assertFalse(all.contains("\u001B[44m"));
        for(String line:render(s,new ConsoleCapabilities(true,true),null).subList(0,7)) assertTrue(line.endsWith(AnsiColorFormatter.RESET));
    }
    @Test void colorAutoFallsBackWithoutTerminal() {
        var s=settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.AUTO,BrandingSettings.ColorMode.ANSI16,defaults.theme(),0);
        assertTrue(render(s,new ConsoleCapabilities(false,true),null).stream().noneMatch(x->x.contains("\u001B")));
    }
    @Test void narrowWidthUsesCompactFallback() {
        var s=settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.OFF,BrandingSettings.ColorMode.ANSI16,defaults.theme(),20);
        List<String> lines=render(s,new ConsoleCapabilities(false,true),null);
        assertEquals("[ NCEA AC ]",lines.get(0));
        assertTrue(lines.stream().allMatch(line->VisualWidth.of(line)<=20));
    }
    @Test void centerAlignmentUsesVisibleWidth() {
        var s=new BrandingSettings(true,true,BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Alignment.CENTER,80,BrandingSettings.Fallback.COMPACT,BrandingSettings.Colors.ON,
                BrandingSettings.ColorMode.ANSI16,defaults.theme(),defaults.information(),true,
                BrandingSettings.ShutdownStyle.COMPACT,"custom-banner.txt");
        String line=render(s,new ConsoleCapabilities(true,true),null).get(0);
        assertTrue(line.startsWith(" "));
        assertEquals(VisualWidth.of(line),80-((80-77+1)/2));
        List<String> lines=render(s,new ConsoleCapabilities(true,true),null);
        int versionIndent=lines.get(9).indexOf('V');
        int platformIndent=lines.get(10).indexOf('P');
        int statusIndent=lines.get(lines.size()-1).indexOf('S');
        assertEquals(versionIndent,platformIndent);
        assertEquals(versionIndent,statusIndent);
    }
    @Test void customBannerExpandsVariablesWithoutExecutingText() {
        var s=settings(BrandingSettings.Style.CUSTOM,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.OFF,BrandingSettings.ColorMode.ANSI16,defaults.theme(),0);
        List<String> lines=render(s,new ConsoleCapabilities(false,true),"█ NCEA ${version} ${checks} ${status}\n");
        assertEquals("█ NCEA 1.0.0 5 ENABLED",lines.get(0));
    }
    @Test void customRejectsControlUnknownAndOversize() {
        assertThrows(IllegalArgumentException.class,()->CustomBannerTemplate.expand("\u001B[31mBAD",info));
        assertThrows(IllegalArgumentException.class,()->CustomBannerTemplate.expand("${execute}",info));
        assertThrows(IllegalArgumentException.class,()->CustomBannerTemplate.expand("a".repeat(8193),info));
    }
    @Test void invalidHexAndUnknownThemeAreRejected() {
        assertThrows(IllegalArgumentException.class,()->new BannerTheme("custom","#GGGGGG","#0000AA","#AAAAAA","#000000"));
        assertThrows(IllegalArgumentException.class,()->BannerTheme.named("unknown",defaults.theme()));
    }
    @Test void themeChangesActualColor() {
        String original=String.join("",render(settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.ON,BrandingSettings.ColorMode.TRUECOLOR,defaults.theme(),0),new ConsoleCapabilities(true,true),null));
        String purple=String.join("",render(settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.ON,BrandingSettings.ColorMode.TRUECOLOR,BannerTheme.named("ncea-purple",null),0),new ConsoleCapabilities(true,true),null));
        assertTrue(original.contains("38;2;85;85;255"));
        assertTrue(purple.contains("38;2;170;85;255"));
    }
    @Test void failedStatusNeverShowsEnabled() {
        var failed=new StartupBanner.Info("1.0.0","1.20.1",17,5,"OBSERVE",57,StartupBanner.Status.FAILED);
        String all=String.join("\n",BannerRenderer.render(defaults,failed,new ConsoleCapabilities(false,true),null));
        assertTrue(all.contains("Status         FAILED"));
        assertFalse(all.contains("Status         ENABLED"));
    }
    @Test void disabledBannerIsEmpty() {
        var s=new BrandingSettings(true,false,defaults.style(),defaults.charset(),defaults.alignment(),0,
                defaults.fallback(),defaults.colors(),defaults.colorMode(),defaults.theme(),defaults.information(),
                true,defaults.shutdownStyle(),defaults.customFile());
        assertTrue(render(s,new ConsoleCapabilities(true,true),null).isEmpty());
    }
    @Test void visualWidthHandlesAnsiCjkAndCombining() {
        assertEquals(4,VisualWidth.of("\u001B[0;94m█中e\u0301\u001B[0m"));
        assertEquals(77,VisualWidth.of(LogoSegments.plainRows().get(0)));
    }
    @Test void autoTerminalDetectionIsConservative() {
        assertFalse(ConsoleCapabilities.detect(false,Map.of("TERM","xterm-256color"),StandardCharsets.UTF_8).ansi());
        assertFalse(ConsoleCapabilities.detect(true,Map.of("TERM","dumb"),StandardCharsets.UTF_8).ansi());
        assertFalse(ConsoleCapabilities.detect(true,Map.of("TERM","xterm","NO_COLOR","1"),StandardCharsets.UTF_8).ansi());
        assertTrue(ConsoleCapabilities.detect(true,Map.of("TERM","xterm"),StandardCharsets.UTF_8).ansi());
    }
    @Test void htmlPreviewEscapesCustomText() {
        var s=settings(BrandingSettings.Style.CUSTOM,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.OFF,BrandingSettings.ColorMode.ANSI16,defaults.theme(),0);
        String html=BannerPreview.htmlPreview(s,info,"<script>alert(1)</script>");
        assertFalse(html.contains("<script>"));assertTrue(html.contains("&lt;script&gt;"));
    }
    @Test void htmlPreviewRespectsNarrowWidthFallback() {
        var s=settings(BrandingSettings.Style.ORIGINAL_3D,BrandingSettings.CharsetMode.UNICODE,
                BrandingSettings.Colors.OFF,BrandingSettings.ColorMode.ANSI16,defaults.theme(),20);
        String html=BannerPreview.htmlPreview(s,info,null);
        assertTrue(html.contains("[ NCEA AC ]"));
        assertFalse(html.contains("▀█▄████▄"));
    }
}
