package com.ncea.anticheat.core.branding;

import com.ncea.anticheat.core.StartupBanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Standalone Java preview; uses BannerRenderer and never starts Paper or executes shell code. */
public final class BannerPreview {
    private BannerPreview() {}
    public static void main(String[] args) throws Exception {
        Map<String,String> options = new HashMap<>();
        for (String arg : args) {
            if (!arg.startsWith("--") || !arg.contains("=")) throw new IllegalArgumentException("Expected --key=value");
            int split=arg.indexOf('='); options.put(arg.substring(2,split),arg.substring(split+1));
        }
        BrandingSettings defaults=BrandingSettings.defaults();
        BrandingSettings.Style style=BrandingSettings.Style.valueOf(options.getOrDefault("style","original-3d").toUpperCase().replace('-','_'));
        BrandingSettings.CharsetMode charset=BrandingSettings.CharsetMode.valueOf(options.getOrDefault("charset","unicode").toUpperCase());
        BrandingSettings.Colors colors=BrandingSettings.Colors.valueOf(options.getOrDefault("colors","off").toUpperCase());
        BrandingSettings.ColorMode mode=BrandingSettings.ColorMode.valueOf(options.getOrDefault("mode","ansi16").toUpperCase());
        BannerTheme theme=BannerTheme.named(options.getOrDefault("theme","ncea-original"),defaults.theme());
        int width="auto".equals(options.getOrDefault("width","auto"))?0:Integer.parseInt(options.get("width"));
        BrandingSettings settings=new BrandingSettings(true,true,style,charset,BrandingSettings.Alignment.LEFT,width,
                BrandingSettings.Fallback.COMPACT,colors,mode,theme,defaults.information(),true,
                BrandingSettings.ShutdownStyle.COMPACT,"custom-banner.txt");
        StartupBanner.Info info=new StartupBanner.Info("0.1.0-dev","1.20.1",17,5,"OBSERVE",57,StartupBanner.Status.ENABLED);
        String custom=null;
        if (style==BrandingSettings.Style.CUSTOM) {
            String file=options.get("custom");
            if (file==null) throw new IllegalArgumentException("--custom=path required for custom style");
            custom=Files.readString(Path.of(file),StandardCharsets.UTF_8);
        }
        List<String> lines=BannerRenderer.render(settings,info,new ConsoleCapabilities(colors==BrandingSettings.Colors.ON,true),custom);
        for (String line:lines) System.out.println(line);
        String output=options.get("output");
        if (output!=null) {
            if (colors==BrandingSettings.Colors.ON && !output.endsWith(".ansi.txt"))
                throw new IllegalArgumentException("Colored output filename must end with .ansi.txt");
            Path path=Path.of(output);if(path.getParent()!=null)Files.createDirectories(path.getParent());
            Files.writeString(path,String.join("\n",lines)+"\n",StandardCharsets.UTF_8);
        }
        String html=options.get("html");
        if (html!=null) {
            Path path=Path.of(html);if(path.getParent()!=null)Files.createDirectories(path.getParent());
            Files.writeString(path,htmlPreview(settings,info,custom),StandardCharsets.UTF_8);
        }
    }
    public static String htmlPreview(BrandingSettings settings, StartupBanner.Info info, String custom) {
        BannerTheme theme=settings.theme();
        StringBuilder html=new StringBuilder("<!doctype html><html lang=\"en\"><meta charset=\"utf-8\"><title>NCEA AC preview</title><style>body{background:")
                .append(theme.background()).append(";color:#ddd;font-family:monospace}pre{line-height:1.1}</style><pre>");
        List<String> all=BannerRenderer.render(settings,info,new ConsoleCapabilities(false,true),custom);
        if (settings.style()==BrandingSettings.Style.ORIGINAL_3D && !all.isEmpty() && all.get(0).contains("▀█▄████▄")) {
            int padding=settings.alignment()==BrandingSettings.Alignment.CENTER && settings.width()>77
                    ? (settings.width()-77)/2 : 0;
            for (List<LogoSegments.Segment> row:LogoSegments.original()) {
                html.append(" ".repeat(padding));
                for (LogoSegments.Segment segment:row) {
                    String color=switch(segment.style()) { case "P","PB" -> theme.primary(); case "D","DB" -> theme.shadow(); default -> theme.accent(); };
                    html.append("<span style=\"color:").append(color);
                    if (segment.style().endsWith("B")) html.append(";background:").append(theme.shadow());
                    html.append("\">" ).append(escape(segment.text())).append("</span>");
                }
                html.append('\n');
            }
            for (String line:all.subList(Math.min(7,all.size()),all.size())) html.append(escape(line)).append('\n');
        } else {
            for (String line:all)
                html.append(escape(line)).append('\n');
        }
        return html.append("</pre></html>").toString();
    }
    private static String escape(String text) {
        return text.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}
