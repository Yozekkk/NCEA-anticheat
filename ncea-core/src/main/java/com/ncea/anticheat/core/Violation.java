package com.ncea.anticheat.core;

public final class Violation {
    private double level;
    private long updated;
    private String evidence = "";
    public double level() { return level; }
    public String evidence() { return evidence; }
    public double add(double weight, double decayPerSecond, long now, String reason) {
        if (!Double.isFinite(weight) || weight <= 0 || !Double.isFinite(decayPerSecond) || decayPerSecond < 0) throw new IllegalArgumentException();
        if (updated != 0 && now >= updated) level = Math.max(0, level - (now - updated) / 1000.0 * decayPerSecond);
        level += weight;
        updated = now;
        evidence = reason;
        return level;
    }
    public double current(double decayPerSecond, long now) {
        if (!Double.isFinite(decayPerSecond) || decayPerSecond < 0) throw new IllegalArgumentException();
        return updated == 0 ? 0 : Math.max(0, level - Math.max(0, now - updated) / 1000.0 * decayPerSecond);
    }
}
