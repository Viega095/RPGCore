package com.viega.rpg.multiblock;

import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RpgMultiblockTest {

    @Test
    @DisplayName("Verificar metadatos, nombres en español y materiales base de los 3 multibloques")
    public void testMultiblockTypeProperties() {
        assertEquals("LEGENDARY_ANVIL", RpgMultiblockType.LEGENDARY_ANVIL.getId());
        assertEquals("CLASS_ASCENSION_ALTAR", RpgMultiblockType.CLASS_ASCENSION_ALTAR.getId());
        assertEquals("DUNGEON_STATION", RpgMultiblockType.DUNGEON_STATION.getId());

        assertTrue(RpgMultiblockType.LEGENDARY_ANVIL.getDisplayName().contains("Yunque"));
        assertTrue(RpgMultiblockType.CLASS_ASCENSION_ALTAR.getDisplayName().contains("Altar"));
        assertTrue(RpgMultiblockType.DUNGEON_STATION.getDisplayName().contains("Estación"));

        assertEquals(Material.ANVIL, RpgMultiblockType.LEGENDARY_ANVIL.getBaseMaterial());
        assertEquals(Material.RESPAWN_ANCHOR, RpgMultiblockType.CLASS_ASCENSION_ALTAR.getBaseMaterial());
        assertEquals(Material.LODESTONE, RpgMultiblockType.DUNGEON_STATION.getBaseMaterial());

        for (RpgMultiblockType type : RpgMultiblockType.values()) {
            assertNotNull(type.getLore());
            assertFalse(type.getLore().isEmpty());
            assertTrue(type.getLore().stream().anyMatch(l -> l.contains("desmonta al 100%")));
        }
    }

    @Test
    @DisplayName("Verificar resolución de tipo desde ID")
    public void testMultiblockTypeFromId() {
        assertSame(RpgMultiblockType.LEGENDARY_ANVIL, RpgMultiblockType.fromId("LEGENDARY_ANVIL"));
        assertSame(RpgMultiblockType.CLASS_ASCENSION_ALTAR, RpgMultiblockType.fromId("CLASS_ASCENSION_ALTAR"));
        assertSame(RpgMultiblockType.DUNGEON_STATION, RpgMultiblockType.fromId("DUNGEON_STATION"));

        // Case insensitivity
        assertSame(RpgMultiblockType.LEGENDARY_ANVIL, RpgMultiblockType.fromId("legendary_anvil"));
        assertNull(RpgMultiblockType.fromId("UNKNOWN_STRUCTURE"));
        assertNull(RpgMultiblockType.fromId(null));
    }

    @Test
    @DisplayName("Verificar plantillas tridimensionales y bloques relativos de las estructuras")
    public void testMultiblockTemplates() {
        for (RpgMultiblockType type : RpgMultiblockType.values()) {
            RpgMultiblockTemplate template = new RpgMultiblockTemplate(type);
            Map<RpgMultiblockTemplate.BlockOffset, Material> blocks = template.getRelativeBlocks();

            assertNotNull(blocks);
            assertFalse(blocks.isEmpty());
            assertTrue(blocks.size() >= 9, "Cada estructura debe tener una base y núcleo tridimensional");

            // Comprobar que existe el bloque central en capa 1
            RpgMultiblockTemplate.BlockOffset coreOffset = new RpgMultiblockTemplate.BlockOffset(0, 1, 0);
            assertTrue(blocks.containsKey(coreOffset), "Debe contener el núcleo en (0, 1, 0)");
            assertEquals(type.getBaseMaterial(), blocks.get(coreOffset), "El núcleo debe coincidir con el material base");

            // Comprobar hologramas
            assertNotNull(template.getHologramLines());
            assertFalse(template.getHologramLines().isEmpty());
            assertTrue(template.getEntityOffsetY() > 1.0);
        }
    }

    @Test
    @DisplayName("Verificar lógica de BlockOffset")
    public void testBlockOffsetLogic() {
        RpgMultiblockTemplate.BlockOffset o1 = new RpgMultiblockTemplate.BlockOffset(1, 0, -1);
        RpgMultiblockTemplate.BlockOffset o2 = new RpgMultiblockTemplate.BlockOffset(1, 0, -1);
        RpgMultiblockTemplate.BlockOffset o3 = new RpgMultiblockTemplate.BlockOffset(0, 1, 0);

        assertEquals(o1, o2);
        assertNotEquals(o1, o3);
        assertEquals(o1.hashCode(), o2.hashCode());

        assertEquals(1, o1.getDx());
        assertEquals(0, o1.getDy());
        assertEquals(-1, o1.getDz());
    }

    @Test
    @DisplayName("Verificar serialización de claves de localización")
    public void testLocationKeyFormatting() {
        // Null checks
        assertEquals("", RpgMultiblockManager.toLocationKey(null));
    }
}
