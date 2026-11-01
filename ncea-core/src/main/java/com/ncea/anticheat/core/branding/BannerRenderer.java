package com.ncea.anticheat.core.branding;

import com.ncea.anticheat.core.StartupBanner;
import java.util.ArrayList;
import java.util.List;

/** Shared terminal and preview renderer for the exact source logo and information block. */
public final class BannerRenderer {
    private BannerRenderer() {}
    public static List<String> render(BrandingSettings settings, StartupBanner.Info info,
                                      ConsoleCapabilities capabilities, String customTemplate) {
        if (!settings.enabled() || !settings.bannerEnabled() || settings.style() == BrandingSettings.Style.DISABLED) return List.of();
        boolean unicode = settings.charset() == BrandingSettings.CharsetMode.UNICODE
                || settings.charset() == BrandingSettings.CharsetMode.AUTO && capabilities.unicode();
        boolean color = settings.colors() == BrandingSettings.Colors.ON
                || settings.colors() == BrandingSettings.Colors.AUTO && capabilities.ansi();
        if (settings.theme().name().equals("monochrome") || settings.style() == BrandingSettings.Style.MONOCHROME
                || settings.style() == BrandingSettings.Style.ASCII) color = false;
        BrandingSettings.Style style = settings.style();
        if (!unicode && (style == BrandingSettings.Style.ORIGINAL_3D || style == BrandingSettings.Style.MONOCHROME)) {
            style = switch (settings.fallback()) {
                case COMPACT -> BrandingSettings.Style.COMPACT;
                case ASCII -> BrandingSettings.Style.ASCII;
                case DISABLED -> BrandingSettings.Style.DISABLED;
            };
        }
        List<String> logo = switch (style) {
            case ORIGINAL_3D, MONOCHROME, ASCII -> renderLogo(style == BrandingSettings.Style.ASCII,
                    color, settings.theme(), settings.colorMode());
            case COMPACT -> List.of(unicode ? "NCEA AC" : "[ NCEA AC ]");
            case CUSTOM -> customTemplate == null ? List.of("[ NCEA AC ]") : renderCustom(customTemplate, info,
                    unicode, color, settings.theme(), settings.colorMode());
            case DISABLED -> List.of();
        };
        if (logo.isEmpty()) return List.of();
        List<String> infoLines = information(settings.information(), info);
        List<String> combined = new ArrayList<>(logo.size() + infoLines.size());
        combined.addAll(logo); combined.addAll(infoLines);
        int contentWidth = combined.stream().mapToInt(VisualWidth::of).max().orElse(0);
        int width = settings.width() == 0 ? contentWidth : settings.width();
        if (contentWidth > width) {
            if (settings.fallback() == BrandingSettings.Fallback.DISABLED) return List.of();
            logo = List.of(settings.fallback() == BrandingSettings.Fallback.ASCII ? "NCEA AC" : "[ NCEA AC ]");
            combined.clear(); combined.addAll(logo); combined.addAll(infoLines);
            if (width < 25) {
                for (int i=0;i<combined.size();i++) {
                    String line=combined.get(i);
                    if (line.equals("Developed by NCEA Studio")) line="NCEA Studio";
                    combined.set(i, fit(line,width,unicode));
                }
            }
        }
        if (settings.alignment() == BrandingSettings.Alignment.CENTER) {
            List<String> centered = new ArrayList<>(combined.size());
            int infoWidth = infoLines.stream().mapToInt(VisualWidth::of).max().orElse(0);
            int infoIndent = Math.max(0, (width - infoWidth) / 2);
            for (int i = 0; i < combined.size(); i++) {
                String line = combined.get(i);
                int indent = i < logo.size() ? Math.max(0, (width - VisualWidth.of(line)) / 2) : infoIndent;
                centered.add(" ".repeat(indent) + line);
            }
            return List.copyOf(centered);
        }
        return List.copyOf(combined);
    }
    private static List<String> renderLogo(boolean ascii, boolean color, BannerTheme theme, BrandingSettings.ColorMode mode) {
        List<String> lines = new ArrayList<>();
        for (List<LogoSegments.Segment> row : LogoSegments.original()) {
            StringBuilder line = new StringBuilder();
            for (LogoSegments.Segment segment : row) {
                String text = ascii ? ascii(segment.text()) : segment.text();
                if (color) line.append(AnsiColorFormatter.codeForStyle(theme, mode, segment.style()));
                line.append(text);
            }
            if (color) line.append(AnsiColorFormatter.RESET);
            lines.add(line.toString());
        }
        return lines;
    }
    private static List<String> renderCustom(String template, StartupBanner.Info info, boolean unicode,
            boolean color, BannerTheme theme, BrandingSettings.ColorMode mode) {
        List<String> lines = new ArrayList<>();
        for (String line : CustomBannerTemplate.expand(template, info)) {
            String safe = unicode ? line : ascii(line);
            if (!color) { lines.add(safe); continue; }
            StringBuilder styled = new StringBuilder();
            String current = "";
            for (int i = 0; i < safe.length();) {
                int cp = safe.codePointAt(i); i += Character.charCount(cp);
                String style = cp == '█' || cp == '#' ? "P" : cp == '▓' || cp == '+' ? "D" : cp == '▒' ? "N" : "";
                if (!style.equals(current)) {
                    if (!current.isEmpty()) styled.append(AnsiColorFormatter.RESET);
                    if (!style.isEmpty()) styled.append(AnsiColorFormatter.codeForStyle(theme, mode, style));
                    current = style;
                }
                styled.appendCodePoint(cp);
            }
            if (!current.isEmpty()) styled.append(AnsiColorFormatter.RESET);
            lines.add(styled.toString());
        }
        return lines;
    }
    private static List<String> information(BrandingSettings.Information options, StartupBanner.Info info) {
        List<String> lines = new ArrayList<>();
        lines.add("NCEA AntiCheat");
        if (options.studio()) lines.add("Developed by NCEA Studio");
        if (options.version()) lines.add(field("Version",info.version()));
        if (options.platform()) lines.add(field("Platform","Paper " + info.paper()));
        if (options.java()) lines.add(field("Java",Integer.toString(info.javaVersion())));
        if (options.checks()) lines.add(field("Active Checks",Integer.toString(info.activeChecks())));
        if (options.mode()) lines.add(field("Mode",info.detectionMode()));
        if (options.startupTime()) lines.add(field("Startup",info.startupMillis() + " ms"));
        if (options.status()) lines.add(field("Status",info.status().name()));
        return lines;
    }
    private static String fit(String line, int width, boolean unicode) {
        if (VisualWidth.of(line) <= width) return line;
        String ellipsis = unicode ? "…" : "...";
        StringBuilder output = new StringBuilder();
        for (int i=0;i<line.length();) {
            int cp=line.codePointAt(i), next=i+Character.charCount(cp);
            if (VisualWidth.of(output.toString()) + VisualWidth.of(new String(Character.toChars(cp))) + VisualWidth.of(ellipsis) > width) break;
            output.appendCodePoint(cp); i=next;
        }
        return output.append(ellipsis).toString();
    }
    private static String field(String label, String value) { return String.format(java.util.Locale.ROOT,"%-15s%s",label,value); }
    public static String ascii(String text) {
        StringBuilder result = new StringBuilder();
        for (int i=0;i<text.length();) {
            int cp=text.codePointAt(i);i+=Character.charCount(cp);
            result.append(switch (cp) {
                case '█','▓' -> '#'; case '▀' -> '^'; case '▄' -> '_';
                case '▌' -> '|'; case '▒','░' -> '+';
                default -> cp < 128 ? Character.toString((char)cp) : "?";
            });
        }
        return result.toString();
    }
}
