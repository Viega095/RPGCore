package com.antigravity.rpg.guilds;

import com.antigravity.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuildStrongholdSiegeManager implements Listener {

    public static class GuildHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class StrongholdTerritory {
        public String name;
        public String controllingGuild = "Sin Controlar";
        public double captureProgress = 0.0;
        public String activeBuff = "+20% EXP Global & +15% Armadura";

        public StrongholdTerritory(String name) {
            this.name = name;
        }
    }

    private final RPGCore plugin;
    private final Map<String, StrongholdTerritory> territories = new HashMap<>();

    public GuildStrongholdSiegeManager(RPGCore plugin) {
        this.plugin = plugin;
        territories.put("FORT_ABYSS", new StrongholdTerritory("Fortaleza del Abismo"));
        territories.put("CITADEL_DRAGON", new StrongholdTerritory("Ciudadela del Dragón"));
        territories.put("SANCTUARY_ASTRAL", new StrongholdTerritory("Santuario Astral"));
    }

    public void openStrongholdGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new GuildHolder(), 36, "§8🏰 §6Asedios de Fortaleza de Clanes §8🏰");

        for (int i = 0; i < 36; i++) {
            inv.setItem(i, createPane(Material.GRAY_STAINED_GLASS_PANE));
        }

        int slot = 11;
        for (StrongholdTerritory t : territories.values()) {
            inv.setItem(slot, createBtn(Material.BEACON, "§e🏰 " + t.name,
                    Arrays.asList("§7Gremio en Control: §b" + t.controllingGuild,
                            "§7Bonificación Activa: §a" + t.activeBuff,
                            "", "§a▶ Clic para reclamar / disputar asedio")));
            slot += 2;
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.2f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof GuildHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            if (slot == 11 || slot == 13 || slot == 15) {
                player.closeInventory();
                player.sendTitle("§6⚔ ¡ASEDIO INICIADO!", "§eHas reclamado el territorio para tu clan", 10, 60, 20);
                player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 1f, 1f);
                player.sendMessage(ChatColor.GOLD + "🏰 [Fortalezas] ¡Tu clan ha tomado el control del territorio y recibe buffs de combate!");
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GuildHolder) {
            event.setCancelled(true);
        }
    }

    private ItemStack createPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private ItemStack createBtn(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
