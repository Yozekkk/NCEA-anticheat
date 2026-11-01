package com.ncea.anticheat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MovementSamplerTest {
    private PlayerData player() { return new PlayerData(UUID.randomUUID()); }
    @ParameterizedTest(name="{0}")
    @CsvSource({"walk,0.2,false", "sprint,0.4,false", "speedHack,1.0,true"})
    void speedWindows(String name, double distance, boolean flagged) {
        PlayerData d=player(); CheckManager c=BasicChecks.create();
        assertTrue(MovementSampler.sample(d,1000,0,64,0,0,64,0,false,true).isEmpty());
        Observation o=MovementSampler.sample(d,1050,0,64,0,distance,64,0,true,true).orElseThrow();
        assertEquals(flagged,c.inspect(d,o).stream().anyMatch(f->f.check().equals("speed")),name);
    }
    @Test void repeatedPositionAndLagWindow() {
        PlayerData d=player(); MovementSampler.sample(d,1000,0,64,0,0.1,64,0,true,true);
        assertTrue(MovementSampler.sample(d,1020,0.1,64,0,0.1,64,0,true,true).isEmpty());
        assertEquals(0,d.distanceWindow);
        assertTrue(MovementSampler.sample(d,1400,0.1,64,0,10,64,0,true,true).isEmpty());
        assertEquals(0,d.distanceWindow);
    }
    @Test void teleportVelocityAndLowTpsResetWindow() {
        PlayerData d=player(); MovementSampler.sample(d,1000,0,64,0,0.1,64,0,true,true);
        MovementSampler.sample(d,1020,0.1,64,0,0.5,64,0,true,true);
        assertTrue(d.distanceWindow>0);
        assertTrue(MovementSampler.sample(d,1030,0.5,64,0,8,80,0,false,false).isEmpty());
        assertEquals(0,d.distanceWindow);
        assertEquals(0,d.hovering);
        assertTrue(MovementSampler.sample(d,1075,8,80,0,8.2,80,0,true,true).isPresent());
    }
    @Test void invalidCoordinatesAreDropped() {
        PlayerData d=player();
        assertTrue(MovementSampler.sample(d,1000,0,64,0,Double.NaN,64,0,false,true).isEmpty());
        assertEquals(0,d.distanceWindow);
    }
    @Test void aggregatesVerticalMotionBeforeFly() {
        PlayerData d=player(); CheckManager c=BasicChecks.create();
        MovementSampler.sample(d,1000,0,64,0,0.1,64,0,false,true);
        MovementSampler.sample(d,1020,0.1,64,0,0.2,64.2,0,false,true);
        Observation jump=MovementSampler.sample(d,1050,0.2,64.2,0,0.3,64.2,0,false,true).orElseThrow();
        assertEquals(0.2,jump.vertical(),1e-9);
        assertTrue(c.inspect(d,jump).stream().noneMatch(f->f.check().equals("fly")));
        assertEquals(0,d.hovering);
    }
    @Test void discontinuousPluginMotionDoesNotCountAsSpeed() {
        PlayerData d=player();CheckManager c=BasicChecks.create();
        MovementSampler.sample(d,1000,0,64,0,0.1,64,0,true,true);
        assertTrue(MovementSampler.sample(d,1050,20,64,0,21,64,0,true,true).isEmpty());
        assertEquals(0,d.distanceWindow);
        Observation next=MovementSampler.sample(d,1100,21,64,0,21.2,64,0,true,true).orElseThrow();
        assertTrue(c.inspect(d,next).stream().noneMatch(f->f.check().equals("speed")));
    }
}
