package com.ncea.anticheat.core;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CoreTest {
    @Test void decayAndEvidence() {
        Violation v = new Violation();
        assertEquals(2, v.add(2, .5, 1000, "first"));
        assertEquals(1.5, v.current(.5, 2000));
        assertEquals(2.5, v.add(1, .5, 2000, "second"));
        assertEquals("second", v.evidence());
    }
    @Test void speedAndFly() {
        PlayerData d = new PlayerData(UUID.randomUUID()); d.lastMove=1000;
        CheckManager manager=BasicChecks.create();
        assertTrue(manager.inspect(d,new Observation(Observation.Kind.MOVE,1050,1.2,0,true,0,0,true)).stream().anyMatch(f->f.check().equals("speed")));
        assertTrue(manager.inspect(d,new Observation(Observation.Kind.MOVE,1100,.2,0,true,0,0,false)).isEmpty());
        for(int i=0;i<9;i++) assertTrue(manager.inspect(d,new Observation(Observation.Kind.MOVE,1200+i*50,0,0,false,0,0,true)).stream().noneMatch(f->f.check().equals("fly")));
        assertTrue(manager.inspect(d,new Observation(Observation.Kind.MOVE,1650,0,0,false,0,0,true)).stream().anyMatch(f->f.check().equals("fly")));
    }
    @Test void reachThreshold() {
        PlayerData d=new PlayerData(UUID.randomUUID()); CheckManager m=BasicChecks.create();
        assertTrue(m.inspect(d,new Observation(Observation.Kind.ATTACK,1000,0,0,false,4.0,0,true)).isEmpty());
        assertTrue(m.inspect(d,new Observation(Observation.Kind.ATTACK,1100,0,0,false,5.0,0,true)).stream().anyMatch(f->f.check().equals("reach")));
    }
    @Test void autoclickerAndFastPlace() {
        PlayerData d=new PlayerData(UUID.randomUUID()); CheckManager manager=BasicChecks.create();
        for(int i=0;i<21;i++) assertTrue(manager.inspect(d,new Observation(Observation.Kind.ATTACK,1000+i*40,0,0,false,3,0,true)).isEmpty());
        assertTrue(manager.inspect(d,new Observation(Observation.Kind.ATTACK,1840,0,0,false,3,0,true)).stream().anyMatch(f->f.check().equals("autoclicker")));
        d.lastPlace=2000;
        assertTrue(manager.inspect(d,new Observation(Observation.Kind.PLACE,2040,0,0,false,0,0,true)).stream().anyMatch(f->f.check().equals("fastplace")));
        assertTrue(manager.inspect(d,new Observation(Observation.Kind.PLACE,2200,0,0,false,0,0,true)).isEmpty());
    }
    @Test void managerRejectsDuplicate() {
        CheckManager m=BasicChecks.create();
        assertThrows(IllegalArgumentException.class, ()->m.register(BasicChecks.create().checks().get(0)));
    }
}
