package com.ncea.anticheat.core.branding;

import com.ncea.anticheat.core.StartupBanner;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Data-only template expansion. No commands, scripts, or expression evaluation. */
public final class CustomBannerTemplate {
    public static final int MAX_BYTES = 8192;
    private static final Pattern TOKEN = Pattern.compile("\\$\\{([^}]+)}");
    private CustomBannerTemplate() {}
    public static List<String> expand(String template, StartupBanner.Info info) {
        if (template == null || template.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES)
            throw new IllegalArgumentException("Custom banner exceeds 8192 bytes");
        String normalized = template.replace("\r\n", "\n");
        if (normalized.codePoints().anyMatch(cp -> cp == 27 || cp == 0x9B || Character.isISOControl(cp) && cp != '\n'))
            throw new IllegalArgumentException("Control sequence in custom banner");
        Map<String,String> values = Map.of("version", info.version(), "platform", info.paper(),
                "checks", Integer.toString(info.activeChecks()), "java", Integer.toString(info.javaVersion()),
                "mode", info.detectionMode(), "status", info.status().name(),
                "startup_ms", Long.toString(info.startupMillis()), "studio", "NCEA Studio");
        List<String> result = new ArrayList<>();
        for (String line : normalized.split("\n", -1)) {
            if (result.size() == 20) throw new IllegalArgumentException("Custom banner exceeds 20 lines");
            Matcher matcher = TOKEN.matcher(line); StringBuffer output = new StringBuffer();
            while (matcher.find()) {
                String value = values.get(matcher.group(1));
                if (value == null) throw new IllegalArgumentException("Unknown custom banner variable: " + matcher.group(1));
                matcher.appendReplacement(output, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(output);
            String expanded = output.toString();
            if (VisualWidth.of(expanded) > 120) throw new IllegalArgumentException("Custom banner line exceeds 120 columns");
            result.add(expanded);
        }
        if (!result.isEmpty() && result.get(result.size()-1).isEmpty()) result.remove(result.size()-1);
        return List.copyOf(result);
    }
}
