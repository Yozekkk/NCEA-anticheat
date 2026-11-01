package com.ncea.anticheat.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server-thread-owned player sessions. */
public final class PlayerRegistry {
    private final Map<UUID, PlayerData> players = new HashMap<>();
    public PlayerData get(UUID id) { return players.computeIfAbsent(id, PlayerData::new); }
    public void remove(UUID id) { players.remove(id); }
    public void clear() { players.clear(); }
    public int size() { return players.size(); }
}
