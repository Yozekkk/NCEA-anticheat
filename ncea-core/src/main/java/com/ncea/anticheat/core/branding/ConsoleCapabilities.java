package com.ncea.anticheat.core.branding;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

/** Conservative terminal capability detection. Explicit ON/UNICODE settings override auto mode. */
public record ConsoleCapabilities(boolean ansi, boolean unicode) {
    public static ConsoleCapabilities detect(boolean interactive, Map<String,String> environment, Charset charset) {
        String term = environment.getOrDefault("TERM", "");
        boolean ansi = interactive && !term.isBlank() && !term.equalsIgnoreCase("dumb")
                && !environment.containsKey("NO_COLOR");
        boolean unicode = charset.name().toUpperCase(Locale.ROOT).contains("UTF")
                && !"C".equalsIgnoreCase(environment.getOrDefault("LC_ALL", ""));
        return new ConsoleCapabilities(ansi, unicode);
    }
}
