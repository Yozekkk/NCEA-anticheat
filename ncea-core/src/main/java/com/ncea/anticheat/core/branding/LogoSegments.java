package com.ncea.anticheat.core.branding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Ordered UTF-8 SGR segments of the user-supplied NCEA AC source. */
public final class LogoSegments {
    public record Segment(String style, String text) {}
    private static final Set<String> STYLES = Set.of("P", "D", "N", "PB", "DB");
    private static final List<List<Segment>> ORIGINAL = load();
    private LogoSegments() {}
    public static List<List<Segment>> original() { return ORIGINAL; }
    public static List<String> plainRows() {
        return ORIGINAL.stream().map(row -> row.stream().map(Segment::text).reduce("", String::concat)).toList();
    }
    private static List<List<Segment>> load() {
        try (InputStream stream = LogoSegments.class.getResourceAsStream("/branding/ncea-ac-original.segments")) {
            if (stream == null) throw new IllegalStateException("Missing original logo resource");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            List<List<Segment>> rows = new ArrayList<>();
            List<Segment> current = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (line.equals("@row")) { current = new ArrayList<>(); rows.add(current); continue; }
                int tab = line.indexOf('\t');
                if (current == null || tab < 1) throw new IllegalStateException("Malformed logo resource");
                String style = line.substring(0, tab), text = line.substring(tab + 1);
                if (!STYLES.contains(style) || text.isEmpty() || text.codePoints().anyMatch(c -> Character.isISOControl(c)))
                    throw new IllegalStateException("Unsafe logo segment");
                current.add(new Segment(style, text));
            }
            if (rows.size() != 7 || rows.stream().anyMatch(row -> row.stream().mapToInt(s -> VisualWidth.of(s.text())).sum() != 77))
                throw new IllegalStateException("Original logo geometry changed");
            return rows.stream().map(List::copyOf).toList();
        } catch (java.io.IOException exception) { throw new IllegalStateException("Cannot read logo resource", exception); }
    }
}
