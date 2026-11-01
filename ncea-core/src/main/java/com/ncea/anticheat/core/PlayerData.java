package com.ncea.anticheat.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Confined to the Paper primary thread; deleted on disconnect. */
public final class PlayerData {
    public final UUID id;
    public long lastMove, lastPlace, lastBreak, sampledDuration;
    public double distanceWindow, verticalWindow;
    public double lastX, lastY, lastZ;
    public boolean hasLastPosition;
    public long lastAttack;
    public final Map<String, Long> lastAlerts = new HashMap<>();
    public int hovering;
    public final Deque<Long> attacks = new ArrayDeque<>();
    public final Map<String, Violation> violations = new HashMap<>();
    public PlayerData(UUID id) { this.id = id; }
    public boolean mayAlert(String check, long now, long cooldownMillis) {
        Long previous = lastAlerts.get(check);
        if (previous != null && now - previous < cooldownMillis) return false;
        lastAlerts.put(check, now);
        return true;
    }
    public void resetCheckState() {
        hovering = 0;
        attacks.clear();
        lastAttack = 0;
        lastPlace = 0;
        MovementSampler.reset(this, 0);
    }
}
