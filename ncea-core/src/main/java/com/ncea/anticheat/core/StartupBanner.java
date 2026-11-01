package com.ncea.anticheat.core;

import com.ncea.anticheat.core.branding.BannerRenderer;
import com.ncea.anticheat.core.branding.BrandingSettings;
import com.ncea.anticheat.core.branding.ConsoleCapabilities;
import java.util.List;

/** Startup facts and compatibility facade for the shared NCEA AC renderer. */
public final class StartupBanner {
    public enum Style { CLASSIC, THREE_D, COMPACT, DISABLED }
    public enum Status { ENABLED, FAILED }
    public record Options(boolean enabled, Style style, boolean colors, boolean showVersion,
                          boolean showServer, boolean showChecks, boolean showStartupTime) {}
    public record Info(String version, String paper, int javaVersion, int activeChecks,
                       String detectionMode, long startupMillis, Status status) {}
    private StartupBanner() {}
    public static List<String> render(Options options, Info info) {
        BrandingSettings defaults = BrandingSettings.defaults();
        BrandingSettings.Style style = switch (options.style()) {
            case CLASSIC -> BrandingSettings.Style.ASCII;
            case THREE_D -> BrandingSettings.Style.ORIGINAL_3D;
            case COMPACT -> BrandingSettings.Style.COMPACT;
            case DISABLED -> BrandingSettings.Style.DISABLED;
        };
        BrandingSettings settings = new BrandingSettings(options.enabled(), true, style,
                BrandingSettings.CharsetMode.ASCII, BrandingSettings.Alignment.LEFT, 0,
                BrandingSettings.Fallback.ASCII, BrandingSettings.Colors.OFF,
                BrandingSettings.ColorMode.ANSI16, defaults.theme(),
                new BrandingSettings.Information(options.showVersion(), true, options.showServer(), true,
                        options.showChecks(), true, options.showStartupTime(), true), true,
                BrandingSettings.ShutdownStyle.COMPACT, defaults.customFile());
        return BannerRenderer.render(settings, info, new ConsoleCapabilities(false,false), null);
    }
}
