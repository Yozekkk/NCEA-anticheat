package com.ncea.anticheat.core.branding;

import java.util.Objects;

/** Validated console presentation choices. Width zero means automatic. */
public record BrandingSettings(boolean enabled, boolean bannerEnabled, Style style, CharsetMode charset, Alignment alignment,
        int width, Fallback fallback, Colors colors, ColorMode colorMode, BannerTheme theme,
        Information information, boolean shutdownEnabled, ShutdownStyle shutdownStyle, String customFile) {
    public enum Style { ORIGINAL_3D, MONOCHROME, ASCII, COMPACT, CUSTOM, DISABLED }
    public enum CharsetMode { AUTO, UNICODE, ASCII }
    public enum Alignment { LEFT, CENTER }
    public enum Fallback { COMPACT, ASCII, DISABLED }
    public enum Colors { AUTO, ON, OFF }
    public enum ColorMode { ANSI16, ANSI256, TRUECOLOR }
    public enum ShutdownStyle { COMPACT, SINGLE_LINE }
    public record Information(boolean version, boolean studio, boolean platform, boolean java,
                              boolean checks, boolean mode, boolean startupTime, boolean status) {}
    public BrandingSettings {
        Objects.requireNonNull(style); Objects.requireNonNull(charset); Objects.requireNonNull(alignment);
        Objects.requireNonNull(fallback); Objects.requireNonNull(colors); Objects.requireNonNull(colorMode);
        Objects.requireNonNull(theme); Objects.requireNonNull(information); Objects.requireNonNull(shutdownStyle);
        if (width != 0 && (width < 20 || width > 120)) throw new IllegalArgumentException("Banner width must be auto or 20..120");
        if (customFile == null || !customFile.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,63}\\.txt") || customFile.contains(".."))
            throw new IllegalArgumentException("Unsafe custom banner filename");
    }
    public static BrandingSettings defaults() {
        return new BrandingSettings(true, true, Style.ORIGINAL_3D, CharsetMode.UNICODE, Alignment.CENTER, 0,
                Fallback.COMPACT, Colors.AUTO, ColorMode.ANSI16, BannerTheme.named("ncea-original", null),
                new Information(true,true,true,true,true,true,true,true), true, ShutdownStyle.COMPACT, "custom-banner.txt");
    }
}
