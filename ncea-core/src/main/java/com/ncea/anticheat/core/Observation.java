package com.ncea.anticheat.core;

/** Immutable, platform-neutral event measured on the server thread. */
public record Observation(Kind kind, long timeMillis, double horizontal, double vertical,
                          boolean onGround, double reach, float pitch, boolean safeEnvironment) {
    public enum Kind { MOVE, ATTACK, PLACE, BREAK }
}
