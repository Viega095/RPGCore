package com.antigravity.rpg.gui;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import com.antigravity.rpg.models.RPGClass;
import com.antigravity.rpg.models.Rarity;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ClassCraftingStation implements Listener {

    public static class CraftingHolder implements InventoryHolder {
        private final RPGClass viewingClass;

        public CraftingHolder(RPGClass viewingClass) {
            this.viewingClass = viewingClass;
        }

        public RPGClass getViewingClass() {
            return viewingClass;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class MaterialRequirement {
        public final Material material;
        public final int amount;
        public final String name;

        public MaterialRequirement(Material material, int amount, String name) {
            this.material = material;
            this.amount = amount;
            this.name = name;
        }
    }

    public static class ClassRecipe {
        public final int tier;
        public final RPGClass rpgClass;
        public final String id;
        public final String name;
        public final Material icon;
        public final Rarity rarity;
        public final int levelReq;
        public final double statValue1;
        public final String statName1;
        public final double statValue2;
        public final String statName2;
        public final double specialStat;
        public final String specialName;
        public final List<MaterialRequirement> requirements;

        public ClassRecipe(int tier, RPGClass rpgClass, String id, String name, Material icon, Rarity rarity,
                           int levelReq, double statValue1, String statName1, double statValue2, String statName2,
                           double specialStat, String specialName, List<MaterialRequirement> requirements) {
            this.tier = tier;
            this.rpgClass = rpgClass;
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.rarity = rarity;
            this.levelReq = levelReq;
            this.statValue1 = statValue1;
            this.statName1 = statName1;
            this.statValue2 = statValue2;
            this.statName2 = statName2;
            this.specialStat = specialStat;
            this.specialName = specialName;
            this.requirements = requirements;
        }
    }

    private final RPGCore core;
    private final Map<RPGClass, List<ClassRecipe>> classRecipes = new HashMap<>();

    public ClassCraftingStation(RPGCore core) {
        this.core = core;
        registerAllRecipes();
    }

    private void registerAllRecipes() {
        // --- WARRIOR RECIPES ---
        List<ClassRecipe> warrior = new ArrayList<>();
        warrior.add(new ClassRecipe(1, RPGClass.WARRIOR, "w_t1_sword", "Espada de Hierro Templado", Material.IRON_SWORD,
                Rarity.COMMON, 1, 6, "Fuerza", 3, "Defensa", 0, "",
                List.of(new MaterialRequirement(Material.IRON_INGOT, 4, "Lingote de Hierro"),
                        new MaterialRequirement(Material.STICK, 2, "Palo"),
                        new MaterialRequirement(Material.LEATHER, 1, "Cuero"))));

        warrior.add(new ClassRecipe(2, RPGClass.WARRIOR, "w_t2_greatsword", "Espadón del Defensor Pesado", Material.DIAMOND_SWORD,
                Rarity.UNCOMMON, 15, 14, "Fuerza", 8, "Defensa", 0.05, "Robo de Vida",
                List.of(new MaterialRequirement(Material.DIAMOND, 6, "Diamante"),
                        new MaterialRequirement(Material.IRON_INGOT, 8, "Lingote de Hierro"),
                        new MaterialRequirement(Material.COAL_BLOCK, 2, "Bloque de Carbón"))));

        warrior.add(new ClassRecipe(3, RPGClass.WARRIOR, "w_t3_obsidian", "Hoja de Obsidiana Ígnea", Material.NETHERITE_SWORD,
                Rarity.RARE, 30, 26, "Fuerza", 15, "Defensa", 0.12, "Robo de Vida",
                List.of(new MaterialRequirement(Material.OBSIDIAN, 12, "Obsidiana"),
                        new MaterialRequirement(Material.NETHERITE_INGOT, 2, "Lingote de Netherite"),
                        new MaterialRequirement(Material.BLAZE_POWDER, 8, "Polvo de Blaze"))));

        warrior.add(new ClassRecipe(4, RPGClass.WARRIOR, "w_t4_destroyer", "Mandoble del Destructor de Almas", Material.NETHERITE_SWORD,
                Rarity.EPIC, 45, 45, "Fuerza", 24, "Defensa", 0.25, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.NETHERITE_INGOT, 4, "Lingote de Netherite"),
                        new MaterialRequirement(Material.ECHO_SHARD, 6, "Fragmento de Eco"),
                        new MaterialRequirement(Material.NETHER_STAR, 1, "Estrella del Nether"))));

        warrior.add(new ClassRecipe(5, RPGClass.WARRIOR, "w_t5_excalibur", "Excalibur Celestial Abisal", Material.NETHERITE_SWORD,
                Rarity.MYTHIC, 60, 75, "Fuerza", 40, "Defensa", 0.40, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.NETHER_STAR, 2, "Estrella del Nether"),
                        new MaterialRequirement(Material.DRAGON_BREATH, 8, "Aliento de Dragón"),
                        new MaterialRequirement(Material.NETHERITE_BLOCK, 2, "Bloque de Netherite"))));
        classRecipes.put(RPGClass.WARRIOR, warrior);

        // --- MAGE RECIPES ---
        List<ClassRecipe> mage = new ArrayList<>();
        mage.add(new ClassRecipe(1, RPGClass.MAGE, "m_t1_wand", "Báculo del Aprendiz Elemental", Material.BLAZE_ROD,
                Rarity.COMMON, 1, 15, "Mana Máximo", 6, "Inteligencia", 0, "",
                List.of(new MaterialRequirement(Material.STICK, 4, "Palo"),
                        new MaterialRequirement(Material.LAPIS_LAZULI, 8, "Lapislázuli"),
                        new MaterialRequirement(Material.AMETHYST_SHARD, 2, "Esquirla de Amatista"))));

        mage.add(new ClassRecipe(2, RPGClass.MAGE, "m_t2_spark", "Vara de Chispas Arcanas", Material.BLAZE_ROD,
                Rarity.UNCOMMON, 15, 45, "Mana Máximo", 15, "Inteligencia", 0.10, "Daño Mágico",
                List.of(new MaterialRequirement(Material.BLAZE_ROD, 4, "Vara de Blaze"),
                        new MaterialRequirement(Material.REDSTONE, 16, "Polvo de Redstone"),
                        new MaterialRequirement(Material.AMETHYST_SHARD, 8, "Esquirla de Amatista"))));

        mage.add(new ClassRecipe(3, RPGClass.MAGE, "m_t3_astral", "Cetro de la Tormenta Astral", Material.END_ROD,
                Rarity.RARE, 30, 90, "Mana Máximo", 28, "Inteligencia", 0.20, "Daño Mágico",
                List.of(new MaterialRequirement(Material.END_ROD, 2, "Vara del End"),
                        new MaterialRequirement(Material.ENDER_PEARL, 8, "Perla de Ender"),
                        new MaterialRequirement(Material.PRISMARINE_SHARD, 12, "Fragmento de Prismarina"))));

        mage.add(new ClassRecipe(4, RPGClass.MAGE, "m_t4_void", "Orbe del Vacío Eterno", Material.HEART_OF_THE_SEA,
                Rarity.EPIC, 45, 160, "Mana Máximo", 45, "Inteligencia", 0.35, "Daño Mágico",
                List.of(new MaterialRequirement(Material.HEART_OF_THE_SEA, 1, "Corazón del Mar"),
                        new MaterialRequirement(Material.GHAST_TEAR, 6, "Lágrima de Ghast"),
                        new MaterialRequirement(Material.AMETHYST_BLOCK, 4, "Bloque de Amatista"))));

        mage.add(new ClassRecipe(5, RPGClass.MAGE, "m_t5_singularity", "Códice de la Singularidad Cósmica", Material.NETHER_STAR,
                Rarity.MYTHIC, 60, 300, "Mana Máximo", 80, "Inteligencia", 0.60, "Daño Mágico",
                List.of(new MaterialRequirement(Material.NETHER_STAR, 2, "Estrella del Nether"),
                        new MaterialRequirement(Material.DRAGON_BREATH, 8, "Aliento de Dragón"),
                        new MaterialRequirement(Material.LAPIS_BLOCK, 8, "Bloque de Lapislázuli"))));
        classRecipes.put(RPGClass.MAGE, mage);

        // --- ARCHER RECIPES ---
        List<ClassRecipe> archer = new ArrayList<>();
        archer.add(new ClassRecipe(1, RPGClass.ARCHER, "a_t1_bow", "Arco de Caza del Explorador", Material.BOW,
                Rarity.COMMON, 1, 8, "Destreza", 4, "Fuerza", 0.05, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.STICK, 3, "Palo"),
                        new MaterialRequirement(Material.STRING, 3, "Hilo"),
                        new MaterialRequirement(Material.FEATHER, 4, "Pluma"))));

        archer.add(new ClassRecipe(2, RPGClass.ARCHER, "a_t2_wind", "Arco Compuesto de Viento Veloz", Material.BOW,
                Rarity.UNCOMMON, 15, 18, "Destreza", 10, "Fuerza", 0.15, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.BOW, 1, "Arco"),
                        new MaterialRequirement(Material.GOLD_INGOT, 6, "Lingote de Oro"),
                        new MaterialRequirement(Material.FEATHER, 12, "Pluma"))));

        archer.add(new ClassRecipe(3, RPGClass.ARCHER, "a_t3_crossbow", "Ballesta del Cazador de Sombras", Material.CROSSBOW,
                Rarity.RARE, 30, 32, "Destreza", 18, "Fuerza", 0.25, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.CROSSBOW, 1, "Ballesta"),
                        new MaterialRequirement(Material.IRON_INGOT, 8, "Lingote de Hierro"),
                        new MaterialRequirement(Material.ENDER_PEARL, 6, "Perla de Ender"))));

        archer.add(new ClassRecipe(4, RPGClass.ARCHER, "a_t4_frost", "Arco Rúnico de Viento Helado", Material.BOW,
                Rarity.EPIC, 45, 52, "Destreza", 26, "Fuerza", 0.35, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.BOW, 1, "Arco"),
                        new MaterialRequirement(Material.NETHERITE_INGOT, 3, "Lingote de Netherite"),
                        new MaterialRequirement(Material.PACKED_ICE, 16, "Hielo Compacto"),
                        new MaterialRequirement(Material.ECHO_SHARD, 4, "Fragmento de Eco"))));

        archer.add(new ClassRecipe(5, RPGClass.ARCHER, "a_t5_divine", "Viento del Juicio Divino Ancestral", Material.BOW,
                Rarity.MYTHIC, 60, 85, "Destreza", 45, "Fuerza", 0.50, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.NETHER_STAR, 2, "Estrella del Nether"),
                        new MaterialRequirement(Material.DRAGON_BREATH, 8, "Aliento de Dragón"),
                        new MaterialRequirement(Material.NETHERITE_BLOCK, 1, "Bloque de Netherite"))));
        classRecipes.put(RPGClass.ARCHER, archer);

        // --- ASSASSIN / ROGUE RECIPES ---
        List<ClassRecipe> assassin = new ArrayList<>();
        assassin.add(new ClassRecipe(1, RPGClass.ASSASSIN, "as_t1_dagger", "Daga de Hierro Envenenada", Material.IRON_SWORD,
                Rarity.COMMON, 1, 7, "Destreza", 3, "Fuerza", 0.08, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.IRON_INGOT, 3, "Lingote de Hierro"),
                        new MaterialRequirement(Material.SPIDER_EYE, 2, "Ojo de Araña"),
                        new MaterialRequirement(Material.LEATHER, 2, "Cuero"))));

        assassin.add(new ClassRecipe(2, RPGClass.ASSASSIN, "as_t2_stiletto", "Estilete de las Sombras", Material.DIAMOND_SWORD,
                Rarity.UNCOMMON, 15, 18, "Destreza", 8, "Fuerza", 0.18, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.DIAMOND, 4, "Diamante"),
                        new MaterialRequirement(Material.INK_SAC, 8, "Saco de Tinta"),
                        new MaterialRequirement(Material.GOLD_INGOT, 4, "Lingote de Oro"))));

        assassin.add(new ClassRecipe(3, RPGClass.ASSASSIN, "as_t3_reaper", "Garras del Segador Silencioso", Material.NETHERITE_SWORD,
                Rarity.RARE, 30, 32, "Destreza", 15, "Fuerza", 0.30, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.NETHERITE_INGOT, 2, "Lingote de Netherite"),
                        new MaterialRequirement(Material.OBSIDIAN, 10, "Obsidiana"),
                        new MaterialRequirement(Material.GHAST_TEAR, 4, "Lágrima de Ghast"))));

        assassin.add(new ClassRecipe(4, RPGClass.ASSASSIN, "as_t4_darkvortex", "Filo del Vórtice Oscuro", Material.NETHERITE_SWORD,
                Rarity.EPIC, 45, 55, "Destreza", 24, "Fuerza", 0.40, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.NETHERITE_INGOT, 4, "Lingote de Netherite"),
                        new MaterialRequirement(Material.ECHO_SHARD, 8, "Fragmento de Eco"),
                        new MaterialRequirement(Material.ENDER_EYE, 8, "Ojo de Ender"))));

        assassin.add(new ClassRecipe(5, RPGClass.ASSASSIN, "as_t5_whisper", "Muerte Susurrante del Abismo", Material.NETHERITE_SWORD,
                Rarity.MYTHIC, 60, 90, "Destreza", 40, "Fuerza", 0.55, "Golpe Crítico",
                List.of(new MaterialRequirement(Material.NETHER_STAR, 2, "Estrella del Nether"),
                        new MaterialRequirement(Material.DRAGON_BREATH, 8, "Aliento de Dragón"),
                        new MaterialRequirement(Material.NETHERITE_BLOCK, 2, "Bloque de Netherite"))));
        classRecipes.put(RPGClass.ASSASSIN, assassin);
    }

    public void open(Player player) {
        PlayerData data = core.getManagerHandler().get(com.antigravity.rpg.managers.PlayerManager.class).getData(player.getUniqueId());
        RPGClass userClass = (data != null && data.getRpgClass() != RPGClass.NONE) ? data.getRpgClass() : RPGClass.WARRIOR;
        openClassView(player, userClass);
    }

    public void openClassView(Player player, RPGClass viewClass) {
        Inventory inv = Bukkit.createInventory(new CraftingHolder(viewClass), 54, "§8⚒ §6Forja de Clase: §e" + viewClass.name() + " §8⚒");

        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 54; i++) inv.setItem(i, border);

        // Top Navigation Tabs (WARRIOR, MAGE, ARCHER, ASSASSIN)
        int[] classSlots = {2, 3, 5, 6};
        RPGClass[] classes = {RPGClass.WARRIOR, RPGClass.MAGE, RPGClass.ARCHER, RPGClass.ASSASSIN};
        Material[] icons = {Material.IRON_SWORD, Material.BLAZE_ROD, Material.BOW, Material.SHEARS};

        for (int i = 0; i < classes.length; i++) {
            RPGClass c = classes[i];
            boolean selected = (c == viewClass);
            ItemStack tab = new ItemStack(icons[i]);
            ItemMeta tMeta = tab.getItemMeta();
            if (tMeta != null) {
                tMeta.setDisplayName((selected ? "§a§l▶ " : "§7") + "§6Clase: §e" + c.name());
                tMeta.setLore(Arrays.asList(
                        "§7Recetas de equipo y armas especializadas.",
                        "",
                        selected ? "§a[PESTAÑA SELECCIONADA]" : "§e▶ Haz clic para ver recetas de esta clase"
                ));
                tab.setItemMeta(tMeta);
            }
            inv.setItem(classSlots[i], tab);
        }

        // Center Guide Info
        ItemStack guide = new ItemStack(Material.ANVIL);
        ItemMeta gMeta = guide.getItemMeta();
        if (gMeta != null) {
            gMeta.setDisplayName("§e§l⚒ Forja de Progresión de Clases");
            gMeta.setLore(Arrays.asList(
                    "§7Forja armamento de alto calibre dividido en 5 Tiers:",
                    "  §f• Tier 1: §aBásico §7(Nivel 1+)",
                    "  §f• Tier 2: §2Reforzado §7(Nivel 15+)",
                    "  §f• Tier 3: §9Arcano Raro §7(Nivel 30+)",
                    "  §f• Tier 4: §5Épico Legendario §7(Nivel 45+)",
                    "  §f• Tier 5: §6Mítico Supremo §7(Nivel 60+)",
                    "",
                    "§eHaz clic en cualquier receta para forjarla si tienes los materiales."
            ));
            guide.setItemMeta(gMeta);
        }
        inv.setItem(4, guide);

        // Render Recipes for viewClass
        List<ClassRecipe> recipes = classRecipes.getOrDefault(viewClass, Collections.emptyList());
        int[] recipeSlots = {20, 22, 24, 30, 32};

        for (int i = 0; i < recipes.size() && i < recipeSlots.length; i++) {
            ClassRecipe recipe = recipes.get(i);
            ItemStack item = new ItemStack(recipe.icon);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(recipe.rarity.getColor() + "✦ Tier " + recipe.tier + ": " + recipe.name);

                List<String> lore = new ArrayList<>();
                lore.add("§7Rareza: " + recipe.rarity.getColor() + recipe.rarity.name());
                lore.add("§7Clase Requerida: §e" + recipe.rpgClass.name());
                lore.add("§7Nivel Requerido: §b" + recipe.levelReq);
                lore.add("");
                lore.add("§6✦ Atributos del Equipo:");
                lore.add("  §a+" + (int) recipe.statValue1 + " " + recipe.statName1);
                lore.add("  §a+" + (int) recipe.statValue2 + " " + recipe.statName2);
                if (recipe.specialStat > 0) {
                    lore.add("  §d+" + (int) (recipe.specialStat * 100) + "% " + recipe.specialName);
                }
                lore.add("");
                lore.add("§e✦ Materiales de Fabricación Requeridos:");

                boolean hasAll = true;
                for (MaterialRequirement req : recipe.requirements) {
                    int count = countItem(player, req.material);
                    boolean enough = count >= req.amount;
                    if (!enough) hasAll = false;
                    lore.add("  " + (enough ? "§a✔ " : "§c✖ ") + "§7" + req.amount + "x " + req.name + " §8(" + count + "/" + req.amount + ")");
                }

                lore.add("");
                if (hasAll) {
                    lore.add("§a✔ [MATERIALES COMPLETOS - HAZ CLIC PARA FORJAR]");
                } else {
                    lore.add("§c✖ [MATERIALES INSUFICIENTES]");
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(recipeSlots[i], item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof CraftingHolder holder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        // Switch Class tabs
        if (slot == 2) openClassView(player, RPGClass.WARRIOR);
        else if (slot == 3) openClassView(player, RPGClass.MAGE);
        else if (slot == 5) openClassView(player, RPGClass.ARCHER);
        else if (slot == 6) openClassView(player, RPGClass.ASSASSIN);

        // Click recipe slots
        int[] recipeSlots = {20, 22, 24, 30, 32};
        List<ClassRecipe> recipes = classRecipes.getOrDefault(holder.getViewingClass(), Collections.emptyList());

        for (int i = 0; i < recipeSlots.length && i < recipes.size(); i++) {
            if (slot == recipeSlots[i]) {
                ClassRecipe recipe = recipes.get(i);
                craftRecipe(player, recipe, holder.getViewingClass());
                break;
            }
        }
    }

    private void craftRecipe(Player player, ClassRecipe recipe, RPGClass currentViewing) {
        for (MaterialRequirement req : recipe.requirements) {
            if (countItem(player, req.material) < req.amount) {
                player.sendMessage(ChatColor.RED + "✖ No tienes suficientes materiales: " + req.name + " (Necesitas " + req.amount + ")");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
        }

        // Deduct materials
        for (MaterialRequirement req : recipe.requirements) {
            removeItems(player, req.material, req.amount);
        }

        // Create custom RPG equipment item
        ItemStack crafted = new ItemStack(recipe.icon);
        ItemMeta meta = crafted.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(recipe.rarity.getColor() + "§l" + recipe.name);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Rareza: " + recipe.rarity.getColor() + recipe.rarity.name());
            lore.add("§7Clase Requerida: §e" + recipe.rpgClass.name());
            lore.add("§7Nivel Requerido: §b" + recipe.levelReq);
            lore.add("");
            lore.add("§6Estadísticas de Forja:");
            lore.add("  §a+" + (int) recipe.statValue1 + " " + recipe.statName1);
            lore.add("  §a+" + (int) recipe.statValue2 + " " + recipe.statName2);
            if (recipe.specialStat > 0) {
                lore.add("  §d+" + (int) (recipe.specialStat * 100) + "% " + recipe.specialName);
            }
            lore.add("");
            lore.add("§8[Forjado en la Estación de Clases]");
            meta.setLore(lore);

            NamespacedKey keyId = new NamespacedKey(core, "rpg_item_id");
            NamespacedKey keyClass = new NamespacedKey(core, "class_req");
            NamespacedKey keyLvl = new NamespacedKey(core, "level_req");

            meta.getPersistentDataContainer().set(keyId, PersistentDataType.STRING, recipe.id);
            meta.getPersistentDataContainer().set(keyClass, PersistentDataType.STRING, recipe.rpgClass.name());
            meta.getPersistentDataContainer().set(keyLvl, PersistentDataType.INTEGER, recipe.levelReq);

            crafted.setItemMeta(meta);
        }

        player.getInventory().addItem(crafted);
        player.sendMessage(ChatColor.GOLD + "✨ ¡Has forjado con éxito: " + recipe.rarity.getColor() + recipe.name + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.4f);

        Location loc = player.getLocation().add(0, 1, 0);
        loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 30, 0.5, 0.5, 0.5, 0.1);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 20, 0.4, 0.4, 0.4, 0.05);

        openClassView(player, currentViewing);
    }

    private int countItem(Player player, Material mat) {
        int count = 0;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && is.getType() == mat) {
                count += is.getAmount();
            }
        }
        return count;
    }

    private void removeItems(Player player, Material mat, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack is = contents[i];
            if (is != null && is.getType() == mat) {
                if (is.getAmount() <= remaining) {
                    remaining -= is.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    is.setAmount(is.getAmount() - remaining);
                    remaining = 0;
                }
                if (remaining <= 0) break;
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CraftingHolder) {
            event.setCancelled(true);
        }
    }
}
