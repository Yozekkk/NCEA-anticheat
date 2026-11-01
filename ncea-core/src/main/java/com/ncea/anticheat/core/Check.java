package com.ncea.anticheat.core;

import java.util.Optional;

public interface Check {
    String id();
    Optional<String> inspect(PlayerData data, Observation observation);
}
