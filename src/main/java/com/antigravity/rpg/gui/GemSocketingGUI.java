package com.antigravity.rpg.gui;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.GemManager;
import com.antigravity.rpg.managers.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GemSocketingGUI implements Listener {

    private final RPGCore core;
    private final GemManager gemManager;
    private final ItemManager itemManager;
    private final NamespacedKey socketCountKey;

    public static final int ITEM_SLOT = 11;
    public static final int GEM_SLOT = 15;
    public static final int SOCKET_ACTION_SLOT = 13;

    public static final int FUSE_SLOT_1 = 29;
    public static final int FUSE_SLOT_2 = 33;
    public static final int FUSE_ACTION_SLOT = 31;

    public GemSocketingGUI(RPGCore core, GemManager gemManager, ItemManager itemManager) {
        this.core = core;
        this.gemManager = gemManager;
        this.itemManager = itemManager;
        this.socketCountKey = new NamespacedKey(core, "socketed_gems_count");
    }

    public static class GemSocketHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new GemSocketHolder(), 45, "§8💎 Engarce y Fusión de Gemas");

        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }

        for (int i = 0; i < 45; i++) {
            if (i != ITEM_SLOT && i != GEM_SLOT && i != SOCKET_ACTION_SLOT
                    && i != FUSE_SLOT_1 && i != FUSE_SLOT_2 && i != FUSE_ACTION_SLOT) {
                inv.setItem(i, border);
            }
        }

        // Socket Action Button
        ItemStack socketBtn = new ItemStack(Material.ANVIL);
        ItemMeta sMeta = socketBtn.getItemMeta();
        if (sMeta != null) {
            sMeta.setDisplayName("§b§l✦ ENGARZAR GEMA ✦");
            sMeta.setLore(Arrays.asList(
                    "§7Coloca tu equipo a la izquierda [Slot 11]",
                    "§7y una gema a la derecha [Slot 15].",
                    "",
                    "§e▶ Haz clic para incrustar la gema en el objeto."
            ));
            socketBtn.setItemMeta(sMeta);
        }
        inv.setItem(SOCKET_ACTION_SLOT, socketBtn);

        // Fuse Action Button
        ItemStack fuseBtn = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta fMeta = fuseBtn.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName("§d§l✦ FUSIONAR GEMAS ✦");
            fMeta.setLore(Arrays.asList(
                    "§7Coloca 2 gemas idénticas en las casillas inferiores.",
                    "",
                    "§e▶ Haz clic para transmutarlas en una gema superior."
            ));
            fuseBtn.setItemMeta(fMeta);
        }
        inv.setItem(FUSE_ACTION_SLOT, fuseBtn);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof GemSocketHolder)) return;

        int rawSlot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();

        if (rawSlot == SOCKET_ACTION_SLOT) {
            event.setCancelled(true);
            handleSocketing(player, event.getInventory());
            return;
        }

        if (rawSlot == FUSE_ACTION_SLOT) {
            event.setCancelled(true);
            handleFusion(player, event.getInventory());
            return;
        }

        if (rawSlot >= 0 && rawSlot < 45) {
            if (rawSlot != ITEM_SLOT && rawSlot != GEM_SLOT && rawSlot != FUSE_SLOT_1 && rawSlot != FUSE_SLOT_2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GemSocketHolder) {
            for (int slot : event.getRawSlots()) {
                if (slot >= 0 && slot < 45) {
                    if (slot != ITEM_SLOT && slot != GEM_SLOT && slot != FUSE_SLOT_1 && slot != FUSE_SLOT_2) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof GemSocketHolder)) return;

        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        int[] slotsToReturn = {ITEM_SLOT, GEM_SLOT, FUSE_SLOT_1, FUSE_SLOT_2};
        for (int slot : slotsToReturn) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                if (!player.getInventory().addItem(item).isEmpty()) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
        }
    }

    private void handleSocketing(Player player, Inventory inv) {
        ItemStack equip = inv.getItem(ITEM_SLOT);
        ItemStack gem = inv.getItem(GEM_SLOT);

        if (equip == null || equip.getType() == Material.AIR || !equip.hasItemMeta()) {
            player.sendMessage(ChatColor.RED + "✖ Coloca un objeto de equipamiento en la casilla izquierda.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
            return;
        }

        if (gem == null || gem.getType() == Material.AIR || !gem.hasItemMeta()) {
            player.sendMessage(ChatColor.RED + "✖ Coloca una gema válida en la casilla derecha.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
            return;
        }

        ItemMeta eMeta = equip.getItemMeta();
        if (eMeta == null) return;

        int maxSockets = 3;
        int currentSockets = eMeta.getPersistentDataContainer().getOrDefault(socketCountKey, PersistentDataType.INTEGER, 0);

        if (currentSockets >= maxSockets) {
            player.sendMessage(ChatColor.RED + "✖ Este objeto ya ha alcanzado el límite máximo de engarces (" + maxSockets + ").");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        String gemDisplayName = gem.getItemMeta().hasDisplayName() ? gem.getItemMeta().getDisplayName() : gem.getType().name();

        List<String> lore = eMeta.getLore() != null ? new ArrayList<>(eMeta.getLore()) : new ArrayList<>();
        lore.add("§d ◆ Engarce [" + (currentSockets + 1) + "/" + maxSockets + "]: " + gemDisplayName);
        eMeta.setLore(lore);
        eMeta.getPersistentDataContainer().set(socketCountKey, PersistentDataType.INTEGER, currentSockets + 1);
        equip.setItemMeta(eMeta);

        // Consume 1 gem
        gem.setAmount(gem.getAmount() - 1);
        if (gem.getAmount() <= 0) {
            inv.setItem(GEM_SLOT, null);
        }

        player.sendMessage(ChatColor.GREEN + "✔ ¡Gema engarzada con éxito en tu objeto!");
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.8f);
    }

    private void handleFusion(Player player, Inventory inv) {
        ItemStack g1 = inv.getItem(FUSE_SLOT_1);
        ItemStack g2 = inv.getItem(FUSE_SLOT_2);

        if (g1 == null || g2 == null || g1.getType() == Material.AIR || g2.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "✖ Debes colocar 2 gemas en las casillas inferiores para fusionarlas.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
            return;
        }

        if (g1.getType() != g2.getType()) {
            player.sendMessage(ChatColor.RED + "✖ Ambas gemas deben ser del mismo tipo para fusionarse.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
            return;
        }

        // Consume 1 from each
        g1.setAmount(g1.getAmount() - 1);
        if (g1.getAmount() <= 0) inv.setItem(FUSE_SLOT_1, null);
        g2.setAmount(g2.getAmount() - 1);
        if (g2.getAmount() <= 0) inv.setItem(FUSE_SLOT_2, null);

        // Generate Upgraded Gem
        ItemStack fusedGem = new ItemStack(Material.NETHER_STAR);
        ItemMeta fMeta = fusedGem.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName("§6§l✦ Gema Radiante Primordial ✦");
            fMeta.setLore(Arrays.asList(
                    "§7Fusión Alquímica Perfecta",
                    "§d+15% a todas las estadísticas",
                    "§e+50 Poder de Maná",
                    "",
                    "§7Lista para ser engarzada en equipo mítico."
            ));
            fusedGem.setItemMeta(fMeta);
        }

        if (!player.getInventory().addItem(fusedGem).isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), fusedGem);
        }

        player.sendMessage(ChatColor.LIGHT_PURPLE + "✨ ¡Fusión Alquímica Exitosa! Has forjado una Gema Radiante Primordial.");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.5f);
    }
}
