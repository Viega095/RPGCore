package com.viega.rpg.multiblock;

import com.viega.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public enum RpgMultiblockType {

    LEGENDARY_ANVIL(
            "LEGENDARY_ANVIL",
            "§6§lYunque Legendario de los Titanes",
            Material.ANVIL,
            Arrays.asList(
                    "§7Estructura RPG desplegable.",
                    "§7Colócala en el suelo para erigir el mítico",
                    "§7yunque de forja y encantamientos arcanos.",
                    "",
                    "§8▪ §aPlataforma de obsidiana y faroles de alma.",
                    "§8▪ §6Clic derecho para abrir la Forja Legendaria.",
                    "§8▪ §eRomper cualquier bloque desmonta al 100%.",
                    "§8▪ §eReembolsa el ítem original sin duplicación."
            )
    ),

    CLASS_ASCENSION_ALTAR(
            "CLASS_ASCENSION_ALTAR",
            "§d§lAltar de Ascensión de Clase",
            Material.RESPAWN_ANCHOR,
            Arrays.asList(
                    "§7Estructura RPG desplegable.",
                    "§7Colócala en el suelo para construir el",
                    "§7altar sagrado del despertar heroico.",
                    "",
                    "§8▪ §aCírculo de amatista y energía celestial.",
                    "§8▪ §dClic derecho para despertar tu clase.",
                    "§8▪ §eRomper cualquier bloque desmonta al 100%.",
                    "§8▪ §eReembolsa el ítem original sin duplicación."
            )
    ),

    DUNGEON_STATION(
            "DUNGEON_STATION",
            "§5§lEstación de Mazmorras y Grietas",
            Material.LODESTONE,
            Arrays.asList(
                    "§7Estructura RPG desplegable.",
                    "§7Colócala en el suelo para abrir un nexo",
                    "§7dimensional hacia mazmorras y la Torre del Abismo.",
                    "",
                    "§8▪ §aNexo de magnetita y obsidiana llorosa.",
                    "§8▪ §5Clic derecho para ingresar a mazmorras.",
                    "§8▪ §eRomper cualquier bloque desmonta al 100%.",
                    "§8▪ §eReembolsa el ítem original sin duplicación."
            )
    ),

    RUNIC_JEWELRY_FORGE(
            "RUNIC_JEWELRY_FORGE",
            "§6§lForja de Joyería y Gemas Rúnicas",
            Material.SMITHING_TABLE,
            Arrays.asList(
                    "§7Estructura RPG desplegable.",
                    "§7Colócala en el suelo para erigir la majestuosa",
                    "§7estación de orfebrería, engarce y fusión de gemas.",
                    "",
                    "§8▪ §aMesa de orfebrería, oro y cristales de amatista.",
                    "§8▪ §6Clic derecho para abrir la Forja de Gemas y Joyas.",
                    "§8▪ §eRomper cualquier bloque desmonta al 100%.",
                    "§8▪ §eReembolsa el ítem original sin duplicación."
            )
    ),

    ANCIENT_RUNIC_OBELISK(
            "ANCIENT_RUNIC_OBELISK",
            "§b§lObelisco Rúnico Ancestral",
            Material.CRYING_OBSIDIAN,
            Arrays.asList(
                    "§7Estructura RPG desplegable.",
                    "§7Colócala en el suelo para erigir un majestuoso",
                    "§7monolito con runas de poder celestial.",
                    "",
                    "§8▪ §aMonolito vertical con runas y amatista.",
                    "§8▪ §bClic derecho para canalizar bendiciones arcanas.",
                    "§8▪ §eRomper cualquier bloque desmonta al 100%.",
                    "§8▪ §eReembolsa el ítem original sin duplicación."
            )
    );

    private final String id;
    private final String displayName;
    private final Material baseMaterial;
    private final List<String> lore;

    RpgMultiblockType(String id, String displayName, Material baseMaterial, List<String> lore) {
        this.id = id;
        this.displayName = displayName;
        this.baseMaterial = baseMaterial;
        this.lore = lore;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public List<String> getLore() {
        return lore;
    }

    /**
     * Genera un ItemStack crafteable con metadatos PDC usando la clave "rpg_structure".
     */
    public ItemStack createItemStack(RPGCore plugin) {
        ItemStack item = new ItemStack(baseMaterial, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);

            NamespacedKey key = new NamespacedKey(plugin, "rpg_structure");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, this.id);

            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Registra una receta de crafteo balanceada en el servidor Spigot.
     */
    public void registerRecipe(RPGCore plugin) {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "rpg_recipe_" + id.toLowerCase());

        try {
            Bukkit.removeRecipe(recipeKey);
        } catch (Throwable ignored) {}

        ItemStack result = createItemStack(plugin);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);

        switch (this) {
            case LEGENDARY_ANVIL:
                // N: NETHERITE_INGOT, A: ANVIL, O: OBSIDIAN, G: GHAST_TEAR, C: CRYING_OBSIDIAN, S: AMETHYST_SHARD
                recipe.shape("NAN", "OGO", "CSC");
                recipe.setIngredient('N', Material.NETHERITE_INGOT);
                recipe.setIngredient('A', Material.ANVIL);
                recipe.setIngredient('O', Material.OBSIDIAN);
                recipe.setIngredient('G', Material.GHAST_TEAR);
                recipe.setIngredient('C', Material.CRYING_OBSIDIAN);
                recipe.setIngredient('S', Material.AMETHYST_SHARD);
                break;

            case CLASS_ASCENSION_ALTAR:
                // S: AMETHYST_SHARD, N: NETHER_STAR, E: ENDER_EYE, R: RESPAWN_ANCHOR, C: CRYING_OBSIDIAN, P: PURPUR_BLOCK
                recipe.shape("SNS", "ERE", "CPC");
                recipe.setIngredient('S', Material.AMETHYST_SHARD);
                recipe.setIngredient('N', Material.NETHER_STAR);
                recipe.setIngredient('E', Material.ENDER_EYE);
                recipe.setIngredient('R', Material.RESPAWN_ANCHOR);
                recipe.setIngredient('C', Material.CRYING_OBSIDIAN);
                recipe.setIngredient('P', Material.PURPUR_BLOCK);
                break;

            case DUNGEON_STATION:
                // P: ENDER_PEARL, L: LODESTONE, C: CRYING_OBSIDIAN, N: NETHER_STAR, O: OBSIDIAN, B: POLISHED_BLACKSTONE
                recipe.shape("PLP", "CNC", "OBO");
                recipe.setIngredient('P', Material.ENDER_PEARL);
                recipe.setIngredient('L', Material.LODESTONE);
                recipe.setIngredient('C', Material.CRYING_OBSIDIAN);
                recipe.setIngredient('N', Material.NETHER_STAR);
                recipe.setIngredient('O', Material.OBSIDIAN);
                recipe.setIngredient('B', Material.POLISHED_BLACKSTONE);
                break;

            case RUNIC_JEWELRY_FORGE:
                // G: GOLD_INGOT, S: SMITHING_TABLE, A: AMETHYST_SHARD, D: DIAMOND, B: POLISHED_DEEPSLATE
                recipe.shape("GDG", "ASA", "BBB");
                recipe.setIngredient('G', Material.GOLD_INGOT);
                recipe.setIngredient('D', Material.DIAMOND);
                recipe.setIngredient('S', Material.SMITHING_TABLE);
                recipe.setIngredient('A', Material.AMETHYST_SHARD);
                recipe.setIngredient('B', Material.POLISHED_DEEPSLATE);
                break;

            case ANCIENT_RUNIC_OBELISK:
                // C: CRYING_OBSIDIAN, N: NETHERITE_INGOT, A: AMETHYST_CLUSTER, L: LODESTONE, E: END_ROD
                recipe.shape("EAE", "LNL", "CCC");
                recipe.setIngredient('E', Material.END_ROD);
                recipe.setIngredient('A', Material.AMETHYST_CLUSTER);
                recipe.setIngredient('L', Material.LODESTONE);
                recipe.setIngredient('N', Material.NETHERITE_INGOT);
                recipe.setIngredient('C', Material.CRYING_OBSIDIAN);
                break;
        }

        try {
            Bukkit.addRecipe(recipe);
        } catch (Throwable e) {
            plugin.getLogger().warning("No se pudo registrar receta para " + id + ": " + e.getMessage());
        }
    }

    public static RpgMultiblockType fromId(String id) {
        if (id == null) return null;
        for (RpgMultiblockType type : values()) {
            if (type.getId().equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}
