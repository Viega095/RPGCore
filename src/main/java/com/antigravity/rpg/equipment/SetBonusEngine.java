package com.antigravity.rpg.equipment;

import com.antigravity.rpg.RPGCore;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class SetBonusEngine {

    public enum ArmorSet {
        SHADOW_ASSASSIN("Asesino de Sombras", "§8§l[SET DE SOMBRAS]", "+20% Prob. Crítica", "+50% Daño por la Espalda", "Invisibilidad al matar"),
        DRAGON_SLAYER("Matadragones", "§c§l[SET MATADRAGONES]", "+15% Daño Físico", "+300 Vida Máxima", "Inmunidad al Fuego y Magma"),
        CELESTIAL_ARCHMAGE("Archimago Celestial", "§b§l[SET CELESTIAL]", "+250 Maná Máximo", "+25% Poder de Hechizo", "Escudo Arcano Pasivo");

        public final String name;
        public final String tag;
        public final String twoPieceBonus;
        public final String fourPieceBonus;
        public final String fullSetBonus;

        ArmorSet(String name, String tag, String twoPieceBonus, String fourPieceBonus, String fullSetBonus) {
            this.name = name;
            this.tag = tag;
            this.twoPieceBonus = twoPieceBonus;
            this.fourPieceBonus = fourPieceBonus;
            this.fullSetBonus = fullSetBonus;
        }
    }

    private final RPGCore core;
    public final NamespacedKey SET_TAG_KEY;

    public SetBonusEngine(RPGCore core) {
        this.core = core;
        this.SET_TAG_KEY = new NamespacedKey(core, "rpg_armor_set");
    }

    public Map<ArmorSet, Integer> countEquippedSets(Player player) {
        Map<ArmorSet, Integer> counts = new HashMap<>();
        ItemStack[] armor = player.getInventory().getArmorContents();

        for (ItemStack item : armor) {
            if (item != null && item.hasItemMeta()) {
                String setTag = item.getItemMeta().getPersistentDataContainer().get(SET_TAG_KEY, PersistentDataType.STRING);
                if (setTag != null) {
                    try {
                        ArmorSet set = ArmorSet.valueOf(setTag);
                        counts.put(set, counts.getOrDefault(set, 0) + 1);
                    } catch (Exception ignored) {}
                }
            }
        }
        return counts;
    }

    public void checkPlayerSetBonuses(Player player) {
        Map<ArmorSet, Integer> sets = countEquippedSets(player);
        for (Map.Entry<ArmorSet, Integer> entry : sets.entrySet()) {
            ArmorSet set = entry.getKey();
            int count = entry.getValue();

            if (count >= 4) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ChatColor.GOLD + "✦ [Set 4/4: " + set.name + "] " + set.fullSetBonus));
            } else if (count >= 2) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ChatColor.YELLOW + "✦ [Set 2/4: " + set.name + "] " + set.twoPieceBonus));
            }
        }
    }
}
