package com.antigravity.rpg.equipment;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.ItemManager;
import com.antigravity.rpg.models.RPGItem;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReforgeManager {

    public enum ReforgeModifier {
        GODLY("Piadoso", "§6§l[PIADOSO]", "+12% Prob. Crítica, +25% Daño Crítico"),
        DEMONIC("Demoníaco", "§4§l[DEMONÍACO]", "+15% Daño Físico, +8% Robo de Vida"),
        TITANIC("Titánico", "§2§l[TITÁNICO]", "+250 Vida Máxima, +40 Armadura"),
        ARCANE("Arcano", "§b§l[ARCANO]", "+150 Maná Máximo, +20% Poder de Hechizo"),
        FIERCE("Feroz", "§c§l[FEROZ]", "+15% Velocidad de Ataque, +10% Velocidad"),
        MYTHIC("Mítico", "§d§l[MÍTICO]", "+8% a Todas las Estadísticas, +10% EXP");

        public final String name;
        public final String prefix;
        public final String statSummary;

        ReforgeModifier(String name, String prefix, String statSummary) {
            this.name = name;
            this.prefix = prefix;
            this.statSummary = statSummary;
        }
    }

    private final RPGCore core;
    private final NamespacedKey REFORGE_KEY;
    private ItemManager itemManager;

    public ReforgeManager(RPGCore core) {
        this.core = core;
        this.REFORGE_KEY = new NamespacedKey(core, "rpg_reforge_modifier");
    }

    public void setItemManager(ItemManager im) {
        this.itemManager = im;
    }

    public boolean isReforged(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(REFORGE_KEY, PersistentDataType.STRING);
    }

    public ReforgeModifier getReforge(ItemStack item) {
        if (!isReforged(item)) return null;
        String val = item.getItemMeta().getPersistentDataContainer().get(REFORGE_KEY, PersistentDataType.STRING);
        try {
            return ReforgeModifier.valueOf(val);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean reforgeItem(Player player, ItemStack item) {
        if (item == null || itemManager == null) return false;
        String id = itemManager.getItemId(item);
        if (id == null) {
            player.sendMessage(ChatColor.RED + "✖ Solo puedes reforjar artefactos RPG válidos.");
            return false;
        }

        ReforgeModifier[] mods = ReforgeModifier.values();
        ReforgeModifier picked = mods[ThreadLocalRandom.current().nextInt(mods.length)];

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        meta.getPersistentDataContainer().set(REFORGE_KEY, PersistentDataType.STRING, picked.name());

        RPGItem rpgItem = itemManager.getItem(id);
        String baseName = rpgItem != null ? rpgItem.getDisplayName() : meta.getDisplayName();
        meta.setDisplayName(picked.prefix + " " + rpgItem.getRarity().getColor() + baseName);

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        // Strip previous reforge lines if any
        lore.removeIf(line -> line.contains("Reforja:"));
        lore.add("");
        lore.add("§d✦ Reforja: " + picked.prefix + " §7(" + picked.statSummary + ")");
        meta.setLore(lore);

        item.setItemMeta(meta);

        player.sendMessage(ChatColor.GOLD + "🔨 [Alquimista] ¡Has reforjado tu objeto con éxito a: " + picked.prefix + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.3f);
        return true;
    }
}
