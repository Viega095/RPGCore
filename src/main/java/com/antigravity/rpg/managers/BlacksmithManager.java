package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.RPGItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlacksmithManager implements Manager {

    private RPGCore core;
    private ItemManager itemManager;
    private NamespacedKey levelKey;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.levelKey = new NamespacedKey(core, "rpg_enhancement_level");
    }

    public void setItemManager(ItemManager im) {
        this.itemManager = im;
    }

    @Override
    public void onDisable() {
    }

    public int getEnhancementLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(levelKey, PersistentDataType.INTEGER, 0);
    }

    public double getSuccessChance(int currentLevel) {
        if (currentLevel >= 15) return 0.0;
        if (currentLevel < 3) return 1.0; // 100%
        if (currentLevel < 6) return 0.85; // 85%
        if (currentLevel < 9) return 0.60; // 60%
        if (currentLevel < 12) return 0.35; // 35%
        return 0.15; // 15% for +13 -> +15
    }

    public boolean upgradeItem(Player player, ItemStack item) {
        if (item == null || itemManager == null) return false;
        String id = itemManager.getItemId(item);
        if (id == null) {
            player.sendMessage(ChatColor.RED + "✖ Este objeto no es un artefacto RPG mejorable.");
            return false;
        }

        int currentLevel = getEnhancementLevel(item);
        if (currentLevel >= 15) {
            player.sendMessage(ChatColor.YELLOW + "✦ ¡Este objeto ya alcanzó el nivel máximo (+15)!");
            return false;
        }

        double chance = getSuccessChance(currentLevel);
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll <= chance) {
            // Success!
            int nextLevel = currentLevel + 1;
            applyEnhancementLevel(item, nextLevel);

            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.4f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.8f);
            player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation().add(0, 1, 0), 30, 0.4, 0.4, 0.4, 0.1);

            player.sendMessage(ChatColor.GREEN + "✨ ¡Éxito! Tu arma ha sido mejorada a " +
                    ChatColor.GOLD + ChatColor.BOLD + "+" + nextLevel + " ⭐");
            return true;
        } else {
            // Failure
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 0.8f);
            player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);

            if (currentLevel > 7) {
                int downgraded = currentLevel - 1;
                applyEnhancementLevel(item, downgraded);
                player.sendMessage(ChatColor.RED + "✖ ¡El encantamiento falló! El objeto ha bajado a " +
                        ChatColor.YELLOW + "+" + downgraded);
            } else {
                player.sendMessage(ChatColor.RED + "✖ ¡El encantamiento falló! El nivel del objeto se mantiene en +" + currentLevel);
            }
            return false;
        }
    }

    public void applyEnhancementLevel(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);

        String id = itemManager.getItemId(item);
        RPGItem rpgItem = itemManager.getItem(id);
        if (rpgItem == null) return;

        String prefix = level > 0 ? "§6[+" + level + "] " : "";
        meta.setDisplayName(prefix + rpgItem.getRarity().getColor() + rpgItem.getDisplayName());

        // Rebuild Lore with Enhanced Stats
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Rarity: " + rpgItem.getRarity().getColor() + rpgItem.getRarity().name());

        if (rpgItem.getLevelRequirement() > 1) {
            lore.add("§7Level Required: §e" + rpgItem.getLevelRequirement());
        }

        lore.add("");
        lore.add("§6Stats " + (level > 0 ? "§e(+" + (level * 8) + "% por mejora):" : ":"));
        double multiplier = 1.0 + (level * 0.08);

        for (Map.Entry<String, Double> entry : rpgItem.getStats().entrySet()) {
            String statName = entry.getKey().replace("_", " ");
            statName = statName.substring(0, 1).toUpperCase() + statName.substring(1);
            double val = entry.getValue() * multiplier;

            if (entry.getKey().equals("crit_chance") || entry.getKey().equals("life_steal")) {
                lore.add("  §a+" + String.format("%.1f", val * 100) + "% " + statName);
            } else {
                lore.add("  §a+" + String.format("%.1f", val) + " " + statName);
            }
        }

        if (rpgItem.getGemSlots() > 0) {
            lore.add("");
            lore.add("§d◆ Gem Slots: " + rpgItem.getGemSlots());
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
