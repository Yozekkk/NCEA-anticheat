package com.ncea.anticheat.paper;

import com.ncea.anticheat.core.StartupBanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {
    @TempDir Path temp;
    private Settings settings() throws Exception {
        Files.copy(Path.of("src/main/resources/config.yml"),temp.resolve("config.yml"));
        Files.copy(Path.of("src/main/resources/checks.yml"),temp.resolve("checks.yml"));
        Settings settings=new Settings(temp.toFile(),Logger.getLogger("settings-test"));
        assertTrue(settings.load(),settings.validate());
        return settings;
    }
    @Test void defaultsAndBanner() throws Exception {
        Settings s=settings();
        assertEquals(5,s.activeChecks()); assertEquals(250,s.maxPing); assertEquals(18,s.minTps);
        assertEquals(StartupBanner.Style.THREE_D,s.banner().style());
    }
    @Test void reloadReplacesOnlyValidSnapshot() throws Exception {
        Settings s=settings();Path file=temp.resolve("checks.yml");
        Files.writeString(file,Files.readString(file).replaceFirst("enabled: true","enabled: false"));
        assertTrue(s.load());assertEquals(4,s.activeChecks());
        Files.writeString(file,Files.readString(file).replaceFirst("weight: 1.0","weight: -1.0"));
        assertFalse(s.load());assertEquals(4,s.activeChecks());assertTrue(s.validate().contains("ERROR"));
    }
    @Test void rejectsNonFiniteAndWrongTypes() throws Exception {
        Settings s=settings();Path file=temp.resolve("checks.yml");
        Files.writeString(file,Files.readString(file).replaceFirst("weight: 1.0","weight: .nan"));
        assertFalse(s.load());assertEquals(5,s.activeChecks());
        Files.writeString(file,Files.readString(file).replaceFirst("weight: .nan","weight: text"));
        assertFalse(s.load());
    }
    @Test void rejectsInvalidBannerStyleAndBoolean() throws Exception {
        Settings s=settings();Path file=temp.resolve("config.yml");String original=Files.readString(file);
        Files.writeString(file,original.replace("style: 3d","style: huge"));assertFalse(s.load());
        Files.writeString(file,original.replace("enabled: true","enabled: yesplease"));assertFalse(s.load());
    }
    @Test void disabledBannerCanReload() throws Exception {
        Settings s=settings();Path file=temp.resolve("config.yml");
        Files.writeString(file,Files.readString(file).replace("style: 3d","style: disabled"));
        assertTrue(s.load());assertEquals(StartupBanner.Style.DISABLED,s.banner().style());
    }
    @Test void permissionDefinitionsExist() throws Exception {
        String pluginYml=Files.readString(Path.of("src/main/resources/plugin.yml"));
        assertTrue(pluginYml.contains("nceaac.admin:"));
        assertTrue(pluginYml.contains("nceaac.alerts:"));
        assertTrue(pluginYml.contains("nceaac.bypass:"));
    }
    @Test void migratesOldConfigWithBackup() throws Exception {
        Settings s=settings();Path file=temp.resolve("config.yml");
        String original=Files.readString(file);Files.writeString(file,original.substring(0,original.indexOf("# Console startup banner")));
        assertTrue(s.load(),s.validate());
        assertEquals(StartupBanner.Style.THREE_D,s.banner().style());
        assertTrue(Files.readString(file).contains("show-startup-time"));
        try (var paths=Files.list(temp)) { assertEquals(1,paths.filter(p->p.getFileName().toString().startsWith("config.yml.bak-")).count()); }
    }
    @Test void malformedYamlIsRejectedWithoutReplacingLiveValues() throws Exception {
        Settings s=settings();
        Files.writeString(temp.resolve("checks.yml"),"schema-version: 1\nchecks: [\n");
        assertFalse(s.load());
        assertEquals(5,s.activeChecks());
        assertTrue(s.validate().startsWith("ERROR"));
    }
    @Test void numericLimitsAreChecked() throws Exception {
        Settings s=settings();Path file=temp.resolve("config.yml");String original=Files.readString(file);
        Files.writeString(file,original.replace("max-ping: 250","max-ping: 49"));assertFalse(s.load());
        Files.writeString(file,original.replace("max-ping: 250","max-ping: 1001"));assertFalse(s.load());
        Files.writeString(file,original.replace("min-tps: 18.0","min-tps: 20.1"));assertFalse(s.load());
        Files.writeString(file,original.replace("min-tps: 18.0","min-tps: .nan"));assertFalse(s.load());
        assertEquals(250,s.maxPing);assertEquals(18,s.minTps);
    }
}
