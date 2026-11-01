package com.ncea.anticheat.core;

import java.util.Optional;

/** Conservative checks. The Paper adapter suppresses checks in ambiguous environments. */
public final class BasicChecks {
    private BasicChecks() {}
    public static CheckManager create() {
        CheckManager manager = new CheckManager();
        manager.register(new Check() {
            public String id() { return "speed"; }
            public Optional<String> inspect(PlayerData d, Observation o) {
                if (o.kind() != Observation.Kind.MOVE || !o.safeEnvironment() || !Double.isFinite(o.horizontal())) return Optional.empty();
                long dt = d.sampledDuration > 0 ? d.sampledDuration : o.timeMillis() - d.lastMove;
                // One observation per at least 40 ms; do not mistake high event frequency for speed.
                if (dt < 40 || dt > 250) return Optional.empty();
                double perTick = o.horizontal() * 50.0 / dt;
                return perTick > 0.85 ? Optional.of("horizontal=" + perTick + " blocks/tick") : Optional.empty();
            }
        });
        manager.register(new Check() {
            public String id() { return "fly"; }
            public Optional<String> inspect(PlayerData d, Observation o) {
                if (o.kind() != Observation.Kind.MOVE) return Optional.empty();
                if (!o.safeEnvironment() || o.onGround() || !Double.isFinite(o.vertical()) || Math.abs(o.vertical()) > 0.03) d.hovering = 0;
                else d.hovering++;
                return d.hovering >= 10 && d.hovering % 5 == 0 ? Optional.of("hovering samples=" + d.hovering) : Optional.empty();
            }
        });
        manager.register(new Check() {
            public String id() { return "reach"; }
            public Optional<String> inspect(PlayerData d, Observation o) {
                return o.kind() == Observation.Kind.ATTACK && o.safeEnvironment() && Double.isFinite(o.reach()) && o.reach() > 4.5
                    ? Optional.of("distance=" + o.reach()) : Optional.empty();
            }
        });
        manager.register(new Check() {
            public String id() { return "autoclicker"; }
            public Optional<String> inspect(PlayerData d, Observation o) {
                if (o.kind() != Observation.Kind.ATTACK || !o.safeEnvironment()) return Optional.empty();
                if (o.timeMillis() <= d.lastAttack) return Optional.empty();
                d.lastAttack = o.timeMillis();
                d.attacks.addLast(o.timeMillis());
                while (!d.attacks.isEmpty() && o.timeMillis() - d.attacks.peekFirst() > 1000) d.attacks.removeFirst();
                while (d.attacks.size() > 64) d.attacks.removeFirst();
                return d.attacks.size() >= 22 ? Optional.of("attacks/second=" + d.attacks.size()) : Optional.empty();
            }
        });
        manager.register(new Check() {
            public String id() { return "fastplace"; }
            public Optional<String> inspect(PlayerData d, Observation o) {
                if (o.kind() != Observation.Kind.PLACE || !o.safeEnvironment()) return Optional.empty();
                return d.lastPlace > 0 && o.timeMillis() > d.lastPlace && o.timeMillis() - d.lastPlace < 55 ? Optional.of("placement interval=" + (o.timeMillis()-d.lastPlace) + "ms") : Optional.empty();
            }
        });
        return manager;
    }
}
