package com.antigravity.rpg.quests;

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

public class DynamicQuestBoard implements Listener {

    public static class BoardHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class BoardContract {
        public String title;
        public String rank;
        public String objective;
        public double xpReward;
        public Material icon;

        public BoardContract(String title, String rank, String objective, double xpReward, Material icon) {
            this.title = title;
            this.rank = rank;
            this.objective = objective;
            this.xpReward = xpReward;
            this.icon = icon;
        }
    }

    private final RPGCore plugin;
    private final List<BoardContract> contracts = new ArrayList<>();

    public DynamicQuestBoard(RPGCore plugin) {
        this.plugin = plugin;
        loadContracts();
    }

    private void loadContracts() {
        contracts.add(new BoardContract("§eCacería de Espectros", "Rango B", "Elimina 10 Esqueletos del Vacío", 2500.0, Material.BONE));
        contracts.add(new BoardContract("§cPurga del Dragón Infernal", "Rango S", "Participa en la incursión del Dragón", 7500.0, Material.DRAGON_HEAD));
        contracts.add(new BoardContract("§dExtracción de Gemas Arcanas", "Rango A", "Funde 3 Gemas de Rango Épico", 4000.0, Material.AMETHYST_SHARD));
        contracts.add(new BoardContract("§6Conquista de la Cripta Abisal", "Rango Mítico", "Completa la Mazmorra de la Cripta", 12000.0, Material.CRYING_OBSIDIAN));
    }

    public void openBoardGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new BoardHolder(), 36, "§8📜 §6Tablón Dinámico de Contratos §8📜");

        for (int i = 0; i < 36; i++) {
            inv.setItem(i, createPane(Material.BLACK_STAINED_GLASS_PANE));
        }

        int[] slots = { 10, 12, 14, 16 };
        for (int i = 0; i < contracts.size() && i < slots.length; i++) {
            BoardContract c = contracts.get(i);
            inv.setItem(slots[i], createBtn(c.icon, c.title + " §7(" + c.rank + ")",
                    Arrays.asList("§7Objetivo: " + c.objective, "§aRecompensa: +" + (int) c.xpReward + " XP de Clase", "", "§a▶ Clic para aceptar contrato")));
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1.2f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof BoardHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            int[] slots = { 10, 12, 14, 16 };
            for (int i = 0; i < slots.length; i++) {
                if (slot == slots[i] && i < contracts.size()) {
                    BoardContract c = contracts.get(i);
                    player.closeInventory();
                    player.sendTitle("§a📜 ¡CONTRATO ACEPTADO!", "§e" + c.title, 10, 50, 15);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.3f);
                    player.sendMessage(ChatColor.GOLD + "📜 [Tablón] Has firmado el contrato: " + c.title + ". ¡Completa el objetivo para recibir " + (int) c.xpReward + " XP!");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BoardHolder) {
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
