package com.ncea.anticheat.core.branding;

/** Applies terminal colors to logo cells and resets every colored run. */
public final class AnsiColorFormatter {
    public static final String RESET = "\u001B[0m";
    private AnsiColorFormatter() {}
    public static String codeForStyle(BannerTheme theme, BrandingSettings.ColorMode mode, String style) {
        if (mode == BrandingSettings.ColorMode.ANSI16 && theme.name().equals("ncea-original")) {
            return switch (style) {
                case "P" -> "\u001B[0;94m";
                case "D" -> "\u001B[0;34m";
                case "N" -> "\u001B[0;37m";
                case "PB" -> "\u001B[0;94;44m";
                case "DB" -> "\u001B[0;34;44m";
                default -> throw new IllegalArgumentException("Unknown logo style");
            };
        }
        String foreground = switch (style) {
            case "P", "PB" -> theme.primary();
            case "D", "DB" -> theme.shadow();
            case "N" -> theme.accent();
            default -> throw new IllegalArgumentException("Unknown logo style");
        };
        String fg = codeForHex(theme, mode, foreground);
        if (!style.endsWith("B")) return mode == BrandingSettings.ColorMode.ANSI16 ? fg : RESET + fg;
        String bg = codeForHex(theme, mode, theme.shadow()).replace("[38;", "[48;");
        if (mode == BrandingSettings.ColorMode.ANSI16) bg = "\u001B[44m";
        return mode == BrandingSettings.ColorMode.ANSI16 ? fg + bg : RESET + fg + bg;
    }
    private static String codeForHex(BannerTheme theme, BrandingSettings.ColorMode mode, String hex) {
        if (mode == BrandingSettings.ColorMode.ANSI16) return "\u001B[0;" + closest16(hex) + "m";
        int rgb=theme.rgb(hex), r=rgb>>16&255, g=rgb>>8&255, b=rgb&255;
        if (mode == BrandingSettings.ColorMode.TRUECOLOR) return "\u001B[38;2;"+r+";"+g+";"+b+"m";
        int index=16+36*Math.round(r/51f)+6*Math.round(g/51f)+Math.round(b/51f);
        return "\u001B[38;5;"+index+"m";
    }

    private static String closest16(String hex) {
        int rgb = Integer.parseInt(hex.substring(1),16), r=rgb>>16&255,g=rgb>>8&255,b=rgb&255;
        if (r>g*1.2 && r>b*1.2) return "95";
        if (g>r*1.2 && g>b*1.2) return "96";
        if (b>r*1.2 || b>g*1.2) return b>150 ? "94" : "34";
        return r+g+b>400 ? "37" : "90";
    }
}
