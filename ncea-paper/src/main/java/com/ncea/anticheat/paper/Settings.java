package com.ncea.anticheat.paper;

import com.ncea.anticheat.core.StartupBanner;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/** Validated snapshot. A failed reload leaves the previous settings active. */
final class Settings {
    record Rule(boolean enabled, double weight, double decay, double alertThreshold) {}
    private static final Set<String> CHECKS = Set.of("speed", "fly", "reach", "autoclicker", "fastplace");
    private final File dataFolder;
    private final Logger logger;
    private Map<String, Rule> rules = Map.of();
    private StartupBanner.Options banner = new StartupBanner.Options(true, StartupBanner.Style.THREE_D, false, true, true, true, true);
    int maxPing = 250;
    double minTps = 18;
    private String validation = "Not loaded";

    Settings(File dataFolder, Logger logger) { this.dataFolder = dataFolder; this.logger = logger; }
    Rule rule(String id) { return rules.getOrDefault(id, new Rule(false, 1, .1, 5)); }
    int activeChecks() { return (int) rules.values().stream().filter(Rule::enabled).count(); }
    StartupBanner.Options banner() { return banner; }
    String validate() { return validation; }

    boolean load() {
        try {
            File configFile = new File(dataFolder, "config.yml");
            YamlConfiguration main = YamlConfiguration.loadConfiguration(configFile);
            YamlConfiguration checks = YamlConfiguration.loadConfiguration(new File(dataFolder, "checks.yml"));
            if (integer(main, "schema-version", 1, 1) != 1 || integer(checks, "schema-version", 1, 1) != 1)
                throw new IllegalArgumentException("unsupported schema-version");
            int ping = integer(main, "max-ping", 50, 1000);
            double tps = number(main, "min-tps", 10, 20);
            boolean bannerEnabled = boolDefault(main, "console.banner.enabled", true);
            String styleText = main.getString("console.banner.style");
            StartupBanner.Style style = switch (styleText == null ? "" : styleText.toLowerCase(java.util.Locale.ROOT)) {
                case "classic" -> StartupBanner.Style.CLASSIC;
                case "", "3d" -> StartupBanner.Style.THREE_D;
                case "compact" -> StartupBanner.Style.COMPACT;
                case "disabled" -> StartupBanner.Style.DISABLED;
                default -> throw new IllegalArgumentException("invalid console.banner.style");
            };
            StartupBanner.Options newBanner = new StartupBanner.Options(bannerEnabled, style,
                    boolDefault(main, "console.banner.colors", false), boolDefault(main, "console.banner.show-version", true),
                    boolDefault(main, "console.banner.show-server", true), boolDefault(main, "console.banner.show-checks", true),
                    boolDefault(main, "console.banner.show-startup-time", true));
            Map<String, Rule> next = new HashMap<>();
            for (String id : CHECKS) {
                String path = "checks." + id + ".";
                next.put(id, new Rule(bool(checks, path + "enabled"), number(checks, path + "weight", Double.MIN_VALUE, 10),
                        number(checks, path + "decay", 0, 10), number(checks, path + "alert-threshold", 1, 100)));
            }
            persistMissingBannerKeys(main, configFile);
            rules = Map.copyOf(next); maxPing = ping; minTps = tps; banner = newBanner;
            validation = "OK: 2 files, " + activeChecks() + " active checks";
            return true;
        } catch (Exception ex) {
            validation = "ERROR: " + ex.getMessage();
            logger.warning(validation);
            return false;
        }
    }

    private static void persistMissingBannerKeys(YamlConfiguration yaml, File file) throws Exception {
        Map<String, Object> defaults = Map.of(
                "console.banner.enabled", true, "console.banner.style", "3d",
                "console.banner.colors", false, "console.banner.show-version", true,
                "console.banner.show-server", true, "console.banner.show-checks", true,
                "console.banner.show-startup-time", true);
        boolean changed = false;
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!yaml.contains(entry.getKey())) { yaml.set(entry.getKey(), entry.getValue()); changed = true; }
        }
        if (changed) {
            Files.copy(file.toPath(), new File(file.getParentFile(), "config.yml.bak-" + System.currentTimeMillis()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            yaml.save(file);
        }
    }
    private static boolean boolDefault(YamlConfiguration yaml, String path, boolean fallback) {
        return yaml.contains(path) ? bool(yaml, path) : fallback;
    }
    private static boolean bool(YamlConfiguration yaml, String path) {
        Object value = yaml.get(path);
        if (!(value instanceof Boolean b)) throw new IllegalArgumentException(path + " must be boolean");
        return b;
    }
    private static int integer(YamlConfiguration yaml, String path, int min, int max) {
        Object value = yaml.get(path);
        if (!(value instanceof Number n) || !Double.isFinite(n.doubleValue()) || n.doubleValue() != n.intValue()
                || n.intValue() < min || n.intValue() > max) throw new IllegalArgumentException(path + " out of range");
        return n.intValue();
    }
    private static double number(YamlConfiguration yaml, String path, double min, double max) {
        Object value = yaml.get(path);
        if (!(value instanceof Number n) || !Double.isFinite(n.doubleValue())
                || n.doubleValue() < min || n.doubleValue() > max) throw new IllegalArgumentException(path + " out of range");
        return n.doubleValue();
    }
}
