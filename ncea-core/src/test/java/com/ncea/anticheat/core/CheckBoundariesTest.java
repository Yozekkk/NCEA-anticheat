package com.ncea.anticheat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CheckBoundariesTest {
    private final CheckManager checks=BasicChecks.create();
    private PlayerData data() { return new PlayerData(UUID.randomUUID()); }
    @ParameterizedTest
    @CsvSource({"4.49,false","4.5,false","4.5001,true","NaN,false","-1,false"})
    void reachBoundary(String distance, boolean flag) {
        double reach=Double.parseDouble(distance);PlayerData d=data();
        assertEquals(flag,checks.inspect(d,new Observation(Observation.Kind.ATTACK,1000,0,0,false,reach,0,true)).stream().anyMatch(f->f.check().equals("reach")));
    }
    @Test void autoclickerIgnoresDuplicateAndSparseAttacks() {
        PlayerData d=data();
        for(int i=1;i<=25;i++) checks.inspect(d,new Observation(Observation.Kind.ATTACK,i*120,0,0,false,3,0,true));
        assertTrue(d.attacks.size()<22);
        int before=d.attacks.size();
        checks.inspect(d,new Observation(Observation.Kind.ATTACK,3000,0,0,false,3,0,true));
        assertEquals(before,d.attacks.size());
    }
    @Test void fastPlaceRequiresPositiveIntervalAndSafeContext() {
        PlayerData d=data();d.lastPlace=1000;
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.PLACE,1000,0,0,false,0,0,true)).isEmpty());
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.PLACE,1010,0,0,false,0,0,false)).isEmpty());
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.PLACE,1055,0,0,false,0,0,true)).isEmpty());
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.PLACE,1054,0,0,false,0,0,true)).stream().anyMatch(f->f.check().equals("fastplace")));
    }
    @Test void flyResetsAfterJumpOrFall() {
        PlayerData d=data();
        for(int i=0;i<9;i++) checks.inspect(d,new Observation(Observation.Kind.MOVE,1000+i*50,0,0,false,0,0,true));
        assertEquals(9,d.hovering);
        checks.inspect(d,new Observation(Observation.Kind.MOVE,1450,0,-0.1,false,0,0,true));
        assertEquals(0,d.hovering);
        checks.inspect(d,new Observation(Observation.Kind.MOVE,1500,0,0,false,0,0,true));
        assertEquals(1,d.hovering);
        checks.inspect(d,new Observation(Observation.Kind.MOVE,1550,0,Double.NaN,false,0,0,true));
        assertEquals(0,d.hovering);
    }
    @Test void highPingAndGraceAreSafeContext() {
        PlayerData d=data();d.lastMove=1000;
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.MOVE,1050,10,0,false,0,0,false)).isEmpty());
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.ATTACK,1060,0,0,false,10,0,false)).isEmpty());
        assertTrue(checks.inspect(d,new Observation(Observation.Kind.PLACE,1070,0,0,false,0,0,false)).isEmpty());
    }
}
