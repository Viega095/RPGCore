package com.viega.rpg.gui;

import com.viega.rpg.RPGCore;
import com.viega.rpg.managers.BlacksmithManager;
import com.viega.rpg.managers.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlacksmithGUI implements Listener {

    public static class BlacksmithHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final RPGCore core;
    private final BlacksmithManager blacksmithManager;
    private final ItemManager itemManager;

    private static final int ITEM_SLOT = 13;
    private static final int UPGRADE_BUTTON_SLOT = 22;

    public BlacksmithGUI(RPGCore core, BlacksmithManager blacksmithManager, ItemManager itemManager) {
        this.core = core;
        this.blacksmithManager = blacksmithManager;
        this.itemManager = itemManager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new BlacksmithHolder(), 36, ChatColor.DARK_RED + "⚒ Yunque de Forja y Encanto");

        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            border.setItemMeta(meta);
        }

        for (int i = 0; i < 36; i++) {
            if (i != ITEM_SLOT && i != UPGRADE_BUTTON_SLOT) {
                inv.setItem(i, border);
            }
        }

        updateUpgradeButton(inv, null);
        player.openInventory(inv);
    }

    private void updateUpgradeButton(Inventory inv, ItemStack item) {
        ItemStack btn = new ItemStack(Material.ANVIL);
        ItemMeta meta = btn.getItemMeta();
        if (meta != null) {
            if (item == null || itemManager.getItemId(item) == null) {
                meta.setDisplayName(ChatColor.YELLOW + "Coloca un arma/armadura en la casilla superior");
            } else {
                int level = blacksmithManager.getEnhancementLevel(item);
                double chance = blacksmithManager.getSuccessChance(level);
                meta.setDisplayName(ChatColor.GOLD + "✦ Forjar Mejora: " + ChatColor.GREEN + "+" + (level + 1) + " ✦");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Nivel actual: " + ChatColor.YELLOW + "+" + level);
                lore.add(ChatColor.GRAY + "Probabilidad de éxito: " + ChatColor.AQUA + String.format("%.0f%%", chance * 100));
                if (level >= 7) {
                    lore.add(ChatColor.RED + "⚠ Si falla, puede degradarse a +" + (level - 1));
                }
                lore.add("");
                lore.add(ChatColor.YELLOW + "¡Haz clic para forjar!");
                meta.setLore(lore);
            }
            btn.setItemMeta(meta);
        }
        inv.setItem(UPGRADE_BUTTON_SLOT, btn);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof BlacksmithHolder)) return;

        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();

        // Shift click protection: safely move RPG item into ITEM_SLOT if empty, else cancel
        if (event.isShiftClick()) {
            if (rawSlot >= 36) {
                event.setCancelled(true);
                ItemStack clicked = event.getCurrentItem();
                if (clicked != null && clicked.getType() != Material.AIR && itemManager.getItemId(clicked) != null) {
                    ItemStack current = event.getView().getTopInventory().getItem(ITEM_SLOT);
                    if (current == null || current.getType() == Material.AIR) {
                        event.getView().getTopInventory().setItem(ITEM_SLOT, clicked.clone());
                        event.setCurrentItem(null);
                        updateUpgradeButton(event.getView().getTopInventory(), clicked);
                    }
                }
                return;
            }
        }

        if (rawSlot == UPGRADE_BUTTON_SLOT) {
            event.setCancelled(true);
            ItemStack target = event.getView().getTopInventory().getItem(ITEM_SLOT);
            if (target != null && itemManager.getItemId(target) != null) {
                blacksmithManager.upgradeItem(player, target);
                updateUpgradeButton(event.getView().getTopInventory(), target);
            } else {
                player.sendMessage(ChatColor.RED + "✖ Debes colocar un objeto RPG en el hueco superior.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.6f);
            }
        } else if (rawSlot >= 0 && rawSlot < 36 && rawSlot != ITEM_SLOT) {
            event.setCancelled(true);
        } else {
            Bukkit.getScheduler().runTaskLater(core, () -> {
                ItemStack current = event.getView().getTopInventory().getItem(ITEM_SLOT);
                updateUpgradeButton(event.getView().getTopInventory(), current);
            }, 1L);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof BlacksmithHolder)) return;

        // Cancel dragging if any slot belongs to top inventory borders/buttons
        if (event.getRawSlots().stream().anyMatch(slot -> slot < 36 && slot != ITEM_SLOT)) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTaskLater(core, () -> {
            ItemStack current = event.getView().getTopInventory().getItem(ITEM_SLOT);
            updateUpgradeButton(event.getView().getTopInventory(), current);
        }, 1L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof BlacksmithHolder)) return;

        Player player = (Player) event.getPlayer();
        ItemStack leftover = event.getInventory().getItem(ITEM_SLOT);
        if (leftover != null && leftover.getType() != Material.AIR) {
            event.getInventory().setItem(ITEM_SLOT, null);
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(leftover);
            if (!overflow.isEmpty()) {
                for (ItemStack remaining : overflow.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), remaining);
                }
            }
        }
    }
}
