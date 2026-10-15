package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RpgSkillsTest {

    @Test
    @DisplayName("Verificar que las 8 nuevas habilidades están inicializadas correctamente")
    public void testNewSkillsProperties() {
        Skill[] skills = {
                new BladestormSkill(),
                new GroundSlamSkill(),
                new BlackHoleSkill(),
                new ChainLightningSkill(),
                new ArrowRainSkill(),
                new PiercingShotSkill(),
                new ShadowDanceSkill(),
                new EviscerateSkill()
        };

        for (Skill s : skills) {
            assertNotNull(s.getName());
            assertNotNull(s.getDescription());
            assertTrue(s.getManaCost(1) > 0, "El coste de maná debe ser mayor a 0");
            assertTrue(s.getCooldown(1) > 0, "El enfriamiento debe ser mayor a 0");
            assertEquals(5, s.getMaxLevel(), "El nivel máximo debe ser 5");

            // Comprobar escalado de maná con el nivel (mayor nivel = ligeramente más maná)
            assertTrue(s.getManaCost(5) >= s.getManaCost(1), "El coste de maná escala con el nivel");
            // Comprobar reducción de cooldown con el nivel
            assertTrue(s.getCooldown(5) <= s.getCooldown(1), "El enfriamiento se reduce con el nivel");
        }
    }

    @Test
    @DisplayName("Verificar nombres específicos de las habilidades")
    public void testSpecificSkillNames() {
        assertEquals("Bladestorm", new BladestormSkill().getName());
        assertEquals("GroundSlam", new GroundSlamSkill().getName());
        assertEquals("BlackHole", new BlackHoleSkill().getName());
        assertEquals("ChainLightning", new ChainLightningSkill().getName());
        assertEquals("ArrowRain", new ArrowRainSkill().getName());
        assertEquals("PiercingShot", new PiercingShotSkill().getName());
        assertEquals("ShadowDance", new ShadowDanceSkill().getName());
        assertEquals("Eviscerate", new EviscerateSkill().getName());
    }
}
