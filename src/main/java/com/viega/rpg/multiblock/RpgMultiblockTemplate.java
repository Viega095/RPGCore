package com.viega.rpg.multiblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class RpgMultiblockTemplate {

    public static class BlockOffset {
        private final int dx;
        private final int dy;
        private final int dz;

        public BlockOffset(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }

        public int getDz() {
            return dz;
        }

        public Location toLocation(Location base) {
            return base.clone().add(dx, dy, dz);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockOffset that = (BlockOffset) o;
            return dx == that.dx && dy == that.dy && dz == that.dz;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dx, dy, dz);
        }
    }

    private final RpgMultiblockType type;
    private final Map<BlockOffset, Material> relativeBlocks = new LinkedHashMap<>();
    private final double entityOffsetY;
    private final List<String> hologramLines = new ArrayList<>();

    public RpgMultiblockTemplate(RpgMultiblockType type) {
        this.type = type;
        this.entityOffsetY = computeEntityOffsetY(type);
        initializeBlocks();
        initializeHologramLines();
    }

    private double computeEntityOffsetY(RpgMultiblockType type) {
        return switch (type) {
            case LEGENDARY_ANVIL -> 1.8;
            case CLASS_ASCENSION_ALTAR -> 1.9;
            case DUNGEON_STATION -> 1.8;
            case RUNIC_JEWELRY_FORGE -> 1.8;
            case ANCIENT_RUNIC_OBELISK -> 2.6;
            case TITAN_FORGE -> 2.2;
            case VOID_RIFT_GATEWAY -> 2.4;
        };
    }

    private void initializeHologramLines() {
        switch (type) {
            case LEGENDARY_ANVIL:
                hologramLines.add("§6§l⚒ YUNQUE LEGENDARIO DE LOS TITANES ⚒");
                hologramLines.add("§e[Clic Derecho: Forja de Armamento Mítico]");
                break;
            case CLASS_ASCENSION_ALTAR:
                hologramLines.add("§d§l✦ ALTAR DE ASCENSIÓN DE CLASE ✦");
                hologramLines.add("§b[Clic Derecho: Despertar de Habilidades Celestiales]");
                break;
            case DUNGEON_STATION:
                hologramLines.add("§5§l🌀 ESTACIÓN DE MAZMORRAS Y GRIETAS 🌀");
                hologramLines.add("§d[Clic Derecho: Incursión a Mazmorras & Torre del Abismo]");
                break;
            case RUNIC_JEWELRY_FORGE:
                hologramLines.add("§6§l💎 FORJA DE JOYERÍA Y GEMAS RÚNICAS 💎");
                hologramLines.add("§e[Clic Derecho: Engarce, Fusión y Joyas Arcanas]");
                break;
            case ANCIENT_RUNIC_OBELISK:
                hologramLines.add("§b§l✦ OBELISCO RÚNICO ANCESTRAL ✦");
                hologramLines.add("§e[Clic Derecho: Canalizar Bendición Celestial]");
                break;
            case TITAN_FORGE:
                hologramLines.add("§6§l🔥 FORJA RÚNICA DEL TITÁN HERRERO 🔥");
                hologramLines.add("§e[Clic Derecho: Reparación Legendaria & Bendición del Titán]");
                break;
            case VOID_RIFT_GATEWAY:
                hologramLines.add("§5§l✦ GRIETA DEL VACÍO INTERDIMENSIONAL ✦");
                hologramLines.add("§d[Clic Derecho: Desafío a la Torre Infinita del Abismo]");
                break;
        }
    }

    private void initializeBlocks() {
        switch (type) {
            case LEGENDARY_ANVIL:
                // Base platform at y = 0 (3x3)
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.POLISHED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.POLISHED_BLACKSTONE_BRICKS);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.POLISHED_BLACKSTONE_BRICKS);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.POLISHED_BLACKSTONE_BRICKS);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.POLISHED_BLACKSTONE_BRICKS);

                // Layer y = 1: Core Anvil at center and decorative lanterns
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.ANVIL);
                relativeBlocks.put(new BlockOffset(1, 1, 1), Material.SOUL_LANTERN);
                relativeBlocks.put(new BlockOffset(-1, 1, 1), Material.SOUL_LANTERN);
                relativeBlocks.put(new BlockOffset(1, 1, -1), Material.SOUL_LANTERN);
                relativeBlocks.put(new BlockOffset(-1, 1, -1), Material.SOUL_LANTERN);
                break;

            case CLASS_ASCENSION_ALTAR:
                // Base platform at y = 0 (3x3)
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.PURPUR_BLOCK);
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.AMETHYST_BLOCK);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.AMETHYST_BLOCK);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.AMETHYST_BLOCK);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.AMETHYST_BLOCK);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.PURPUR_PILLAR);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.PURPUR_PILLAR);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.PURPUR_PILLAR);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.PURPUR_PILLAR);

                // Layer y = 1: Core Respawn Anchor and Amethyst clusters
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.RESPAWN_ANCHOR);
                relativeBlocks.put(new BlockOffset(1, 1, 1), Material.AMETHYST_CLUSTER);
                relativeBlocks.put(new BlockOffset(-1, 1, 1), Material.AMETHYST_CLUSTER);
                relativeBlocks.put(new BlockOffset(1, 1, -1), Material.AMETHYST_CLUSTER);
                relativeBlocks.put(new BlockOffset(-1, 1, -1), Material.AMETHYST_CLUSTER);
                break;

            case DUNGEON_STATION:
                // Base platform at y = 0 (3x3)
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.POLISHED_BLACKSTONE);
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.OBSIDIAN);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.OBSIDIAN);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.OBSIDIAN);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.OBSIDIAN);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.CRYING_OBSIDIAN);

                // Layer y = 1: Core Lodestone and End Rod beacons
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.LODESTONE);
                relativeBlocks.put(new BlockOffset(1, 1, 1), Material.END_ROD);
                relativeBlocks.put(new BlockOffset(-1, 1, 1), Material.END_ROD);
                relativeBlocks.put(new BlockOffset(1, 1, -1), Material.END_ROD);
                relativeBlocks.put(new BlockOffset(-1, 1, -1), Material.END_ROD);
                break;

            case RUNIC_JEWELRY_FORGE:
                // Base platform at y = 0 (3x3)
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.POLISHED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.GOLD_BLOCK);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.GOLD_BLOCK);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.AMETHYST_BLOCK);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.AMETHYST_BLOCK);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.CHISELED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.CHISELED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.CHISELED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.CHISELED_DEEPSLATE);

                // Layer y = 1: Core Smithing table, Lanterns and Amethyst clusters
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.SMITHING_TABLE);
                relativeBlocks.put(new BlockOffset(1, 1, 1), Material.LANTERN);
                relativeBlocks.put(new BlockOffset(-1, 1, 1), Material.LANTERN);
                relativeBlocks.put(new BlockOffset(1, 1, -1), Material.AMETHYST_CLUSTER);
                relativeBlocks.put(new BlockOffset(-1, 1, -1), Material.AMETHYST_CLUSTER);
                break;

            case ANCIENT_RUNIC_OBELISK:
                // Base at y = 0 (3x3)
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.POLISHED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.POLISHED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.POLISHED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.POLISHED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.CHISELED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.CHISELED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.CHISELED_DEEPSLATE);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.CHISELED_DEEPSLATE);

                // Pillar shaft at y = 1 and y = 2
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.CRYING_OBSIDIAN);
                relativeBlocks.put(new BlockOffset(0, 2, 0), Material.LODESTONE);

                // Four corner beacon lights at y = 1
                relativeBlocks.put(new BlockOffset(1, 1, 0), Material.END_ROD);
                relativeBlocks.put(new BlockOffset(-1, 1, 0), Material.END_ROD);
                relativeBlocks.put(new BlockOffset(0, 1, 1), Material.END_ROD);
                relativeBlocks.put(new BlockOffset(0, 1, -1), Material.END_ROD);

                // Apex cap at y = 3
                relativeBlocks.put(new BlockOffset(0, 3, 0), Material.AMETHYST_CLUSTER);
                break;

            case TITAN_FORGE:
                // Base at 0,0,0
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.NETHERITE_BLOCK);

                // Base platform 3x3 at y = 0
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.POLISHED_BLACKSTONE);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.POLISHED_BLACKSTONE);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.POLISHED_BLACKSTONE);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.POLISHED_BLACKSTONE);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.MAGMA_BLOCK);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.MAGMA_BLOCK);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.MAGMA_BLOCK);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.MAGMA_BLOCK);

                // Four corner pillars at y = 1
                relativeBlocks.put(new BlockOffset(1, 1, 1), Material.NETHER_BRICK_FENCE);
                relativeBlocks.put(new BlockOffset(-1, 1, 1), Material.NETHER_BRICK_FENCE);
                relativeBlocks.put(new BlockOffset(1, 1, -1), Material.NETHER_BRICK_FENCE);
                relativeBlocks.put(new BlockOffset(-1, 1, -1), Material.NETHER_BRICK_FENCE);

                // Anvil and forge center at y = 1
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.ANVIL);
                break;

            case VOID_RIFT_GATEWAY:
                // Base at 0,0,0
                relativeBlocks.put(new BlockOffset(0, 0, 0), Material.CRYING_OBSIDIAN);

                // Surrounding basalt & soul sand
                relativeBlocks.put(new BlockOffset(1, 0, 0), Material.POLISHED_BASALT);
                relativeBlocks.put(new BlockOffset(-1, 0, 0), Material.POLISHED_BASALT);
                relativeBlocks.put(new BlockOffset(0, 0, 1), Material.POLISHED_BASALT);
                relativeBlocks.put(new BlockOffset(0, 0, -1), Material.POLISHED_BASALT);
                relativeBlocks.put(new BlockOffset(1, 0, 1), Material.SOUL_SAND);
                relativeBlocks.put(new BlockOffset(-1, 0, 1), Material.SOUL_SAND);
                relativeBlocks.put(new BlockOffset(1, 0, -1), Material.SOUL_SAND);
                relativeBlocks.put(new BlockOffset(-1, 0, -1), Material.SOUL_SAND);

                // Four corner soul lanterns at y = 1
                relativeBlocks.put(new BlockOffset(1, 1, 1), Material.SOUL_LANTERN);
                relativeBlocks.put(new BlockOffset(-1, 1, 1), Material.SOUL_LANTERN);
                relativeBlocks.put(new BlockOffset(1, 1, -1), Material.SOUL_LANTERN);
                relativeBlocks.put(new BlockOffset(-1, 1, -1), Material.SOUL_LANTERN);

                // Center core ender portal frame at y = 1
                relativeBlocks.put(new BlockOffset(0, 1, 0), Material.ENDER_CHEST);
                break;
        }
    }

    /**
     * Comprueba si el espacio en el mundo está despejado para desplegar la estructura.
     */
    public boolean checkClearance(Location baseLoc) {
        World world = baseLoc.getWorld();
        if (world == null) return false;

        for (BlockOffset offset : relativeBlocks.keySet()) {
            Location loc = offset.toLocation(baseLoc);
            if (loc.getBlockY() < world.getMinHeight() || loc.getBlockY() >= world.getMaxHeight()) {
                return false;
            }
            Block block = world.getBlockAt(loc);
            Material mat = block.getType();
            if (mat == Material.BEDROCK || mat == Material.BARRIER || mat == Material.END_PORTAL ||
                    mat == Material.END_PORTAL_FRAME || mat == Material.COMMAND_BLOCK ||
                    mat == Material.STRUCTURE_BLOCK) {
                return false;
            }
        }
        return true;
    }

    public RpgMultiblockType getType() {
        return type;
    }

    public Map<BlockOffset, Material> getRelativeBlocks() {
        return Collections.unmodifiableMap(relativeBlocks);
    }

    public double getEntityOffsetY() {
        return entityOffsetY;
    }

    public List<String> getHologramLines() {
        return Collections.unmodifiableList(hologramLines);
    }
}
