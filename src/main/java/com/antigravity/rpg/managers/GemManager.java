package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GemManager implements Manager, Listener {

    private RPGCore core;
    private PlayerManager playerManager;
    private ItemManager itemManager;

    // Simple mock gems map (gemId -> material/name)
    private final Map<String, String[]> gemTypes = new HashMap<>();

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        core.getServer().getPluginManager().registerEvents(this, core);

        // Register some default gems
        gemTypes.put("ruby_gem", new String[] { "§cRuby of Strength", "§7+2 Strength" });
        gemTypes.put("sapphire_gem", new String[] { "§9Sapphire of Mana", "§7+20 Mana" });
        gemTypes.put("emerald_gem", new String[] { "§aEmerald of Vitality", "§7+10 Health" });
    }

    public void setManagers(PlayerManager pm, ItemManager im) {
        this.playerManager = pm;
        this.itemManager = im;
    }

    @Override
    public void onDisable() {

    }

    public void openGemBag(Player player) {
        if (playerManager == null)
            return;
        PlayerData data = playerManager.getData(player.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, 54, "§8Adventurer's Gem Bag");

        for (String gemId : data.getGemBag()) {
            ItemStack gemItem = createGemItem(gemId);
            if (gemItem != null) {
                inv.addItem(gemItem);
            }
        }

        player.openInventory(inv);
    }

    private ItemStack createGemItem(String gemId) {
        String[] info = gemTypes.get(gemId);
        if (info == null)
            return null;

        ItemStack item = new ItemStack(Material.EMERALD); // Placeholder material
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(info[0]);
        meta.setLore(Arrays.asList(info[1]));
        // Tag it so we know it's a gem
        // In full implementation, use PersistentDataContainer
        item.setItemMeta(meta);
        return item;
    }

    public boolean isGem(String itemId) {
        return gemTypes.containsKey(itemId);
    }

    public String getGemName(String gemId) {
        String[] info = gemTypes.get(gemId);
        return info != null ? info[0] : gemId;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§8Adventurer's Gem Bag"))
            return;

        event.setCancelled(true); // Don't let them take items normally yet (complex logic needed for re-sync)

        // Allow taking items OUT (simple implementation: remove from bag list, give to
        // inventory)
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            Player player = (Player) event.getWhoClicked();
            // Find which gem was clicked
            // For simplify, we just say: "You took a gem!" and give it
            // Real implementation needs to match slot to index or use NBT to know which gem
            // ID it is

            player.sendMessage("§eYou took a gem from your bag!");
            player.getInventory().addItem(event.getCurrentItem());
            player.closeInventory();

            // Remove from data? Complicated without tracking ID on item
            // For this MVP, we just show visualization.
        }
    }
}
