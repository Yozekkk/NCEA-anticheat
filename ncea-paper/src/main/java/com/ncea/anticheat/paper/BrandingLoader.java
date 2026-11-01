package com.ncea.anticheat.paper;

import com.ncea.anticheat.core.StartupBanner;
import com.ncea.anticheat.core.branding.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

/** Validates branding.yml and returns a new snapshot without mutating a live renderer. */
final class BrandingLoader {
    record Loaded(BrandingSettings settings, String customTemplate) {}
    private static final Set<String> KEYS = Set.of(
            "schema-version", "branding.enabled", "branding.banner.enabled", "branding.banner.style",
            "branding.banner.charset", "branding.banner.alignment", "branding.banner.width", "branding.banner.fallback",
            "branding.console.colors", "branding.console.color-mode", "branding.theme.name",
            "branding.theme.custom.primary", "branding.theme.custom.shadow", "branding.theme.custom.accent",
            "branding.theme.custom.background", "branding.information.show-version", "branding.information.show-studio",
            "branding.information.show-platform", "branding.information.show-java", "branding.information.show-checks",
            "branding.information.show-mode", "branding.information.show-startup-time", "branding.information.show-status",
            "branding.shutdown.enabled", "branding.shutdown.style", "branding.custom.file");
    private final File folder;
    private final Logger logger;
    private final Supplier<InputStream> resource;
    BrandingLoader(File folder, Logger logger, Supplier<InputStream> resource) {
        this.folder = folder; this.logger = logger; this.resource = resource;
    }
    Loaded load(boolean migrateLegacy, StartupBanner.Options old) throws Exception {
        File file = new File(folder, "branding.yml");
        if (!file.exists()) {
            folder.mkdirs();
            try (InputStream input = resource.get()) {
                if (input == null) throw new IllegalStateException("Missing bundled branding.yml");
                Files.copy(input, file.toPath());
            }
            if (migrateLegacy && old != null) {
                YamlConfiguration migration = readYaml(file);
                migration.set("branding.banner.enabled", old.enabled());
                migration.set("branding.banner.style", switch (old.style()) {
                    case THREE_D -> "original-3d"; case CLASSIC -> "ascii";
                    case COMPACT -> "compact"; case DISABLED -> "disabled";
                });
                migration.set("branding.console.colors", old.colors() ? "on" : "off");
                migration.set("branding.information.show-version", old.showVersion());
                migration.set("branding.information.show-platform", old.showServer());
                migration.set("branding.information.show-checks", old.showChecks());
                migration.set("branding.information.show-startup-time", old.showStartupTime());
                migration.save(file);
                logger.info("Migrated legacy console.banner settings to branding.yml");
            }
        }
        YamlConfiguration yaml = readYaml(file);
        for (String key : yaml.getKeys(true)) {
            if (!(yaml.get(key) instanceof ConfigurationSection) && !KEYS.contains(key))
                throw new IllegalArgumentException("Unknown branding.yml key: " + key);
        }
        if (integer(yaml,"schema-version") != 1) throw new IllegalArgumentException("Unsupported branding.yml schema-version");
        BannerTheme custom = new BannerTheme("custom", text(yaml,"branding.theme.custom.primary"),
                text(yaml,"branding.theme.custom.shadow"), text(yaml,"branding.theme.custom.accent"),
                text(yaml,"branding.theme.custom.background"));
        BannerTheme theme = BannerTheme.named(text(yaml,"branding.theme.name"), custom);
        BrandingSettings.Information info = new BrandingSettings.Information(
                bool(yaml,"branding.information.show-version"), bool(yaml,"branding.information.show-studio"),
                bool(yaml,"branding.information.show-platform"), bool(yaml,"branding.information.show-java"),
                bool(yaml,"branding.information.show-checks"), bool(yaml,"branding.information.show-mode"),
                bool(yaml,"branding.information.show-startup-time"), bool(yaml,"branding.information.show-status"));
        BrandingSettings settings = new BrandingSettings(bool(yaml,"branding.enabled"),
                bool(yaml,"branding.banner.enabled"),
                choice(yaml,"branding.banner.style", Map.of("original-3d",BrandingSettings.Style.ORIGINAL_3D,
                        "monochrome",BrandingSettings.Style.MONOCHROME,"ascii",BrandingSettings.Style.ASCII,
                        "compact",BrandingSettings.Style.COMPACT,"custom",BrandingSettings.Style.CUSTOM,
                        "disabled",BrandingSettings.Style.DISABLED)),
                choice(yaml,"branding.banner.charset", Map.of("auto",BrandingSettings.CharsetMode.AUTO,
                        "unicode",BrandingSettings.CharsetMode.UNICODE,"ascii",BrandingSettings.CharsetMode.ASCII)),
                choice(yaml,"branding.banner.alignment", Map.of("left",BrandingSettings.Alignment.LEFT,"center",BrandingSettings.Alignment.CENTER)),
                width(yaml),
                choice(yaml,"branding.banner.fallback", Map.of("compact",BrandingSettings.Fallback.COMPACT,
                        "ascii",BrandingSettings.Fallback.ASCII,"disabled",BrandingSettings.Fallback.DISABLED)),
                colors(yaml),
                choice(yaml,"branding.console.color-mode", Map.of("ansi16",BrandingSettings.ColorMode.ANSI16,
                        "ansi256",BrandingSettings.ColorMode.ANSI256,"truecolor",BrandingSettings.ColorMode.TRUECOLOR)),
                theme, info, bool(yaml,"branding.shutdown.enabled"),
                choice(yaml,"branding.shutdown.style", Map.of("compact",BrandingSettings.ShutdownStyle.COMPACT,
                        "single-line",BrandingSettings.ShutdownStyle.SINGLE_LINE)),
                text(yaml,"branding.custom.file"));
        String template = null;
        if (settings.style() == BrandingSettings.Style.CUSTOM) {
            try {
                File customFile = new File(folder, settings.customFile());
                if (Files.size(customFile.toPath()) > CustomBannerTemplate.MAX_BYTES)
                    throw new IllegalArgumentException("Custom banner exceeds 8192 bytes");
                template = Files.readString(customFile.toPath(), StandardCharsets.UTF_8);
                CustomBannerTemplate.expand(template, new StartupBanner.Info("1.0.0","1.20.1",17,5,"OBSERVE",1,StartupBanner.Status.ENABLED));
            } catch (Exception ex) {
                template = null;
                logger.warning("Custom banner unavailable; compact fallback: " + ex.getMessage());
            }
        }
        return new Loaded(settings, template);
    }
    private static YamlConfiguration readYaml(File file) throws Exception {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (org.bukkit.configuration.InvalidConfigurationException ex) {
            throw new IllegalArgumentException("Invalid branding.yml YAML: " + ex.getMessage(), ex);
        }
        return yaml;
    }
    private static boolean bool(YamlConfiguration yaml, String path) {
        Object value=yaml.get(path);
        if (!(value instanceof Boolean b)) throw new IllegalArgumentException(path + " must be boolean");
        return b;
    }
    private static String text(YamlConfiguration yaml, String path) {
        Object value=yaml.get(path);
        if (!(value instanceof String s) || s.isBlank()) throw new IllegalArgumentException(path + " must be a non-empty string");
        return s;
    }
    private static int integer(YamlConfiguration yaml,String path) {
        Object value=yaml.get(path);
        if (!(value instanceof Number n) || n.doubleValue()!=n.intValue()) throw new IllegalArgumentException(path + " must be integer");
        return n.intValue();
    }
    private static int width(YamlConfiguration yaml) {
        Object value=yaml.get("branding.banner.width");
        if ("auto".equals(value)) return 0;
        if (!(value instanceof Number n) || n.doubleValue()!=n.intValue()) throw new IllegalArgumentException("branding.banner.width must be auto or integer");
        return n.intValue();
    }
    private static BrandingSettings.Colors colors(YamlConfiguration yaml) {
        Object value = yaml.get("branding.console.colors");
        // Bukkit's YAML 1.1 parser reads unquoted "on" and "off" as booleans.
        if (value instanceof Boolean b) return b ? BrandingSettings.Colors.ON : BrandingSettings.Colors.OFF;
        return choice(yaml, "branding.console.colors", Map.of("auto", BrandingSettings.Colors.AUTO,
                "on", BrandingSettings.Colors.ON, "off", BrandingSettings.Colors.OFF));
    }
    private static <T> T choice(YamlConfiguration yaml,String path,Map<String,T> options) {
        String value=text(yaml,path);
        T result=options.get(value.toLowerCase(java.util.Locale.ROOT));
        if (result==null) throw new IllegalArgumentException("Invalid " + path + ": " + value);
        return result;
    }
}
