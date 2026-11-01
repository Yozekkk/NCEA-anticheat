package com.ncea.anticheat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class InfrastructureTest {
    @Test void playerRegistryRemovesAndClears() {
        PlayerRegistry r=new PlayerRegistry(); UUID id=UUID.randomUUID();
        PlayerData first=r.get(id); assertSame(first,r.get(id)); assertEquals(1,r.size());
        r.remove(id); assertEquals(0,r.size()); assertNotSame(first,r.get(id));
        r.clear(); assertEquals(0,r.size());
    }
    @Test void alertsThrottlePerCheck() {
        PlayerData d=new PlayerData(UUID.randomUUID());
        assertTrue(d.mayAlert("speed",1000,3000));
        assertFalse(d.mayAlert("speed",2000,3000));
        assertTrue(d.mayAlert("fly",2000,3000));
        assertTrue(d.mayAlert("speed",4000,3000));
    }
    @Test void disabledChecksDoNotChangeState() {
        PlayerData d=new PlayerData(UUID.randomUUID());
        BasicChecks.create().inspect(d,new Observation(Observation.Kind.ATTACK,1000,0,0,false,5,0,true),id->false);
        assertEquals(0,d.attacks.size());
        assertEquals(0,d.lastAttack);
    }
    @Test void resetClearsTransientCheckState() {
        PlayerData d=new PlayerData(UUID.randomUUID()); d.hovering=8;d.lastPlace=100;d.lastAttack=200;d.attacks.add(200L);
        d.resetCheckState();
        assertEquals(0,d.hovering);assertEquals(0,d.lastPlace);assertEquals(0,d.lastAttack);assertTrue(d.attacks.isEmpty());
    }
    @ParameterizedTest @ValueSource(doubles={0,-1,Double.NaN,Double.POSITIVE_INFINITY})
    void violationRejectsBadWeights(double value) {
        Violation v=new Violation();assertThrows(IllegalArgumentException.class,()->v.add(value,0,1000,"bad"));
    }
    @Test void currentRejectsInvalidDecay() {
        Violation v=new Violation();v.add(1,0,1000,"ok");
        assertThrows(IllegalArgumentException.class,()->v.current(Double.NaN,2000));
        assertThrows(IllegalArgumentException.class,()->v.current(-1,2000));
    }
}
