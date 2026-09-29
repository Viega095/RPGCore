package com.antigravity.rpg.equipment;

import com.antigravity.rpg.RPGCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantingAltarManager {

    private final RPGCore core;

    public EnchantingAltarManager(RPGCore core) {
        this.core = core;
    }

    public boolean infuseAltar(Player player, ItemStack weapon, ItemStack runestone) {
        if (weapon == null || runestone == null || weapon.getType().isAir() || runestone.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "✖ Debes colocar un arma en tu mano principal y una Piedra Rúnica en tu mano secundaria.");
            return false;
        }

        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return false;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("§5✦ Imbuición del Altar: §d+20% Daño Sagrado / Abisal");
        meta.setLore(lore);
        weapon.setItemMeta(meta);

        runestone.setAmount(runestone.getAmount() - 1);

        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_PURPLE + "✨ ¡El Altar Ancestral ha imbuido tu arma con poder cósmico!");
        player.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1.2f);
        loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc.add(0, 1, 0), 60, 0.5, 0.5, 0.5, 0.2);
        return true;
    }
}
