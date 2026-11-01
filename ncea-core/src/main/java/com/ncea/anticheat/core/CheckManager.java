package com.ncea.anticheat.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class CheckManager {
    private final List<Check> checks = new ArrayList<>();
    public void register(Check check) {
        if (checks.stream().anyMatch(c -> c.id().equals(check.id()))) throw new IllegalArgumentException("Duplicate check: " + check.id());
        checks.add(check);
    }
    public List<Check> checks() { return List.copyOf(checks); }
    public List<Flag> inspect(PlayerData data, Observation event) {
        return inspect(data, event, id -> true);
    }
    public List<Flag> inspect(PlayerData data, Observation event, Predicate<String> enabled) {
        List<Flag> flags = null;
        for (Check check : checks) {
            if (!enabled.test(check.id())) continue;
            Optional<String> evidence = check.inspect(data, event);
            if (evidence.isPresent()) {
                if (flags == null) flags = new ArrayList<>(2);
                flags.add(new Flag(check.id(), evidence.get()));
            }
        }
        return flags == null ? List.of() : flags;
    }
    public record Flag(String check, String evidence) {}
}
