package com.ncea.anticheat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StartupBannerTest {
    private StartupBanner.Info info(StartupBanner.Status status) {
        return new StartupBanner.Info("1.0.0","1.20.1",17,5,"OBSERVE",42,status);
    }
    private StartupBanner.Options options(StartupBanner.Style style) {
        return new StartupBanner.Options(true,style,false,true,true,true,true);
    }
    @ParameterizedTest @EnumSource(value=StartupBanner.Style.class,names={"CLASSIC","THREE_D","COMPACT"})
    void includesDynamicFieldsAndAscii(StartupBanner.Style style) {
        List<String> lines=StartupBanner.render(options(style),info(StartupBanner.Status.ENABLED));
        String text=String.join("\n",lines);
        for(String fragment:List.of("NCEA AntiCheat","Version        1.0.0","NCEA Studio","Platform       Paper 1.20.1","Java           17","Active Checks  5","Mode           OBSERVE","Status         ENABLED","Startup        42 ms")) assertTrue(text.contains(fragment),fragment);
        assertTrue(lines.stream().allMatch(s->s.length()<=80 && s.chars().allMatch(c->c>=32 && c<=126)));
    }
    @Test void disabledAndFailedNeverClaimSuccess() {
        assertTrue(StartupBanner.render(options(StartupBanner.Style.DISABLED),info(StartupBanner.Status.ENABLED)).isEmpty());
        assertTrue(StartupBanner.render(new StartupBanner.Options(false,StartupBanner.Style.THREE_D,false,true,true,true,true),info(StartupBanner.Status.ENABLED)).isEmpty());
        String failed=String.join("\n",StartupBanner.render(options(StartupBanner.Style.THREE_D),info(StartupBanner.Status.FAILED)));
        assertTrue(failed.contains("Status         FAILED")); assertFalse(failed.contains("Status         ENABLED"));
    }
    @Test void togglesHideOptionalFields() {
        String text=String.join("\n",StartupBanner.render(new StartupBanner.Options(true,StartupBanner.Style.COMPACT,false,false,false,false,false),info(StartupBanner.Status.ENABLED)));
        assertFalse(text.contains("v1.0.0"));assertFalse(text.contains("Platform       "));assertFalse(text.contains("Active Checks  "));assertFalse(text.contains("Startup        "));
    }
}
