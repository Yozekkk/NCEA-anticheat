package com.ncea.anticheat.paper;

import com.ncea.anticheat.core.StartupBanner;
import com.ncea.anticheat.core.branding.BrandingSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

class BrandingLoaderTest {
    @TempDir Path temp;
    private BrandingLoader loader() {
        return new BrandingLoader(temp.toFile(),Logger.getLogger("branding-test"),
                () -> BrandingLoaderTest.class.getResourceAsStream("/branding.yml"));
    }
    private StartupBanner.Options legacy(boolean colors,StartupBanner.Style style) {
        return new StartupBanner.Options(true,style,colors,true,true,true,true);
    }
    @Test void defaultsCreateCommentedUtf8File() throws Exception {
        var loaded=loader().load(false,null);
        assertEquals(BrandingSettings.Style.ORIGINAL_3D,loaded.settings().style());
        assertEquals("#5555FF",loaded.settings().theme().primary());
        assertEquals(BrandingSettings.Colors.AUTO,loaded.settings().colors());
        assertTrue(Files.readString(temp.resolve("branding.yml")).contains("# NCEA AntiCheat console branding schema"));
    }
    @Test void migratesLegacyStyleAndColorsOnce() throws Exception {
        BrandingLoader loader=loader();
        var loaded=loader.load(true,legacy(false,StartupBanner.Style.COMPACT));
        assertEquals(BrandingSettings.Style.COMPACT,loaded.settings().style());
        assertEquals(BrandingSettings.Colors.OFF,loaded.settings().colors());
        assertEquals(BrandingSettings.Style.COMPACT,loader.load(false,legacy(true,StartupBanner.Style.THREE_D)).settings().style());
    }
    @Test void themeAndWidthReload() throws Exception {
        BrandingLoader loader=loader();loader.load(false,null);
        Path file=temp.resolve("branding.yml");String old=Files.readString(file);
        Files.writeString(file,old.replace("name: ncea-original","name: ncea-purple").replace("width: auto","width: 100"));
        var next=loader.load(false,null).settings();
        assertEquals("#AA55FF",next.theme().primary());assertEquals(100,next.width());
    }
    @Test void unquotedYamlOnOffColorsAreAccepted() throws Exception {
        BrandingLoader loader=loader();loader.load(false,null);
        Path file=temp.resolve("branding.yml");String original=Files.readString(file);
        Files.writeString(file,original.replace("colors: auto","colors: on"));
        assertEquals(BrandingSettings.Colors.ON,loader.load(false,null).settings().colors());
        Files.writeString(file,original.replace("colors: auto","colors: off"));
        assertEquals(BrandingSettings.Colors.OFF,loader.load(false,null).settings().colors());
    }
    @Test void invalidHexAndUnknownKeysRejectSnapshot() throws Exception {
        BrandingLoader loader=loader();var previous=loader.load(false,null);
        Path file=temp.resolve("branding.yml");String old=Files.readString(file);
        Files.writeString(file,old.replace("'#5555FF'","'#GGGGGG'"));assertThrows(IllegalArgumentException.class,()->loader.load(false,null));
        Files.writeString(file,old+"\n  typo: true\n");assertThrows(IllegalArgumentException.class,()->loader.load(false,null));
        assertEquals("#5555FF",previous.settings().theme().primary());
    }
    @Test void malformedYamlAndUnknownStyleReject() throws Exception {
        BrandingLoader loader=loader();loader.load(false,null);Path file=temp.resolve("branding.yml");String old=Files.readString(file);
        Files.writeString(file,"branding: [\n");assertThrows(IllegalArgumentException.class,()->loader.load(false,null));
        Files.writeString(file,old.replace("style: original-3d","style: giant"));assertThrows(IllegalArgumentException.class,()->loader.load(false,null));
    }
    @Test void rejectsOutOfRangeWidthAndUnsafeFilename() throws Exception {
        BrandingLoader loader=loader();loader.load(false,null);Path file=temp.resolve("branding.yml");String old=Files.readString(file);
        Files.writeString(file,old.replace("width: auto","width: 12"));assertThrows(IllegalArgumentException.class,()->loader.load(false,null));
        Files.writeString(file,old.replace("file: custom-banner.txt","file: ../bad.txt"));assertThrows(IllegalArgumentException.class,()->loader.load(false,null));
    }
    @Test void customFileLoadsAndBadFileFallsBack() throws Exception {
        BrandingLoader loader=loader();loader.load(false,null);Path file=temp.resolve("branding.yml");
        Files.writeString(file,Files.readString(file).replace("style: original-3d","style: custom"));
        Files.writeString(temp.resolve("custom-banner.txt"),"█ ${version} ${checks}\n");
        assertTrue(loader.load(false,null).customTemplate().contains("${version}"));
        Files.writeString(temp.resolve("custom-banner.txt"),"\u001B[31mBAD");
        assertNull(loader.load(false,null).customTemplate());
    }
}
