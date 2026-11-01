package com.ncea.anticheat.core.branding;

import java.util.Locale;

/** Immutable foreground and reference-background palette. No terminal background is assumed. */
public record BannerTheme(String name, String primary, String shadow, String accent, String background) {
    public BannerTheme {
        primary = hex(primary); shadow = hex(shadow); accent = hex(accent); background = hex(background);
    }
    public static BannerTheme named(String name, BannerTheme custom) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "ncea-original" -> new BannerTheme("ncea-original", "#5555FF", "#0000AA", "#AAAAAA", "#000000");
            case "ncea-purple" -> new BannerTheme("ncea-purple", "#AA55FF", "#5500AA", "#AAAAAA", "#000000");
            case "ncea-cyan" -> new BannerTheme("ncea-cyan", "#55FFFF", "#0055AA", "#AAAAAA", "#000000");
            case "monochrome" -> new BannerTheme("monochrome", "#AAAAAA", "#555555", "#AAAAAA", "#000000");
            case "custom" -> custom;
            default -> throw new IllegalArgumentException("Unknown theme: " + name);
        };
    }
    public static String hex(String value) {
        if (value == null || !value.matches("#[0-9A-Fa-f]{6}")) throw new IllegalArgumentException("Invalid HEX color: " + value);
        return value.toUpperCase(Locale.ROOT);
    }
    public int rgb(String value) { return Integer.parseInt(value.substring(1), 16); }
}
