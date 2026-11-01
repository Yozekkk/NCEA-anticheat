package com.ncea.anticheat.core;

import java.util.Optional;

/** Groups move events into a measured window and drops ambiguous or repeated positions. */
public final class MovementSampler {
    private MovementSampler() {}

    public static Optional<Observation> sample(PlayerData data, long now,
            double fromX, double fromY, double fromZ, double toX, double toY, double toZ,
            boolean ground, boolean safe) {
        if (!Double.isFinite(fromX) || !Double.isFinite(fromY) || !Double.isFinite(fromZ)
                || !Double.isFinite(toX) || !Double.isFinite(toY) || !Double.isFinite(toZ) || !safe) {
            reset(data, now);
            return Optional.empty();
        }
        if (data.hasLastPosition && Math.abs(fromX - data.lastX) + Math.abs(fromY - data.lastY)
                + Math.abs(fromZ - data.lastZ) > 0.5) {
            resetWindow(data, now);
            data.lastX = toX; data.lastY = toY; data.lastZ = toZ;
            return Optional.empty();
        }
        if (data.hasLastPosition && data.lastX == toX && data.lastY == toY && data.lastZ == toZ) {
            return Optional.empty();
        }
        data.lastX = toX; data.lastY = toY; data.lastZ = toZ; data.hasLastPosition = true;
        if (data.lastMove == 0 || now <= data.lastMove || now - data.lastMove > 250) {
            resetWindow(data, now);
            return Optional.empty();
        }
        data.distanceWindow += Math.hypot(toX - fromX, toZ - fromZ);
        data.verticalWindow += toY - fromY;
        if (now - data.lastMove < 45) return Optional.empty();
        long duration = now - data.lastMove;
        Observation result = new Observation(Observation.Kind.MOVE, now, data.distanceWindow,
                data.verticalWindow, ground, 0, 0, true);
        resetWindow(data, now);
        data.sampledDuration = duration;
        return Optional.of(result);
    }

    public static void reset(PlayerData data, long now) {
        resetWindow(data, now);
        data.hasLastPosition = false;
        data.hovering = 0;
    }

    private static void resetWindow(PlayerData data, long now) {
        data.lastMove = now;
        data.sampledDuration = 0;
        data.distanceWindow = 0;
        data.verticalWindow = 0;
    }
}
