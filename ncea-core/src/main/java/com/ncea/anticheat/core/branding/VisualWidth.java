package com.ncea.anticheat.core.branding;

/** Display-cell width for supported banner text; ANSI SGR is ignored. */
public final class VisualWidth {
    private VisualWidth() {}
    public static int of(String text) {
        int width = 0;
        for (int i = 0; i < text.length();) {
            if (text.charAt(i) == 27 && i + 1 < text.length() && text.charAt(i+1) == '[') {
                int end = text.indexOf('m', i + 2);
                if (end >= 0 && text.substring(i+2,end).matches("[0-9;]*")) { i = end + 1; continue; }
            }
            int cp = text.codePointAt(i); i += Character.charCount(cp);
            int type = Character.getType(cp);
            if (type == Character.NON_SPACING_MARK || type == Character.ENCLOSING_MARK || type == Character.FORMAT) continue;
            width += wide(cp) ? 2 : 1;
        }
        return width;
    }
    private static boolean wide(int cp) {
        return cp >= 0x1100 && (cp <= 0x115F || cp >= 0x2E80 && cp <= 0xA4CF
                || cp >= 0xAC00 && cp <= 0xD7A3 || cp >= 0xF900 && cp <= 0xFAFF
                || cp >= 0xFE10 && cp <= 0xFE6F || cp >= 0xFF01 && cp <= 0xFF60
                || cp >= 0xFFE0 && cp <= 0xFFE6 || cp >= 0x1F300 && cp <= 0x1FAFF);
    }
}
