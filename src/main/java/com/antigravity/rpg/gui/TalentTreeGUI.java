package com.antigravity.rpg.gui;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.PlayerManager;
import com.antigravity.rpg.managers.TalentManager;
import com.antigravity.rpg.models.PlayerData;
import com.antigravity.rpg.models.RPGClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TalentTreeGUI implements Listener {

    private final RPGCore core;
    private final TalentManager talentManager;
    private final PlayerManager playerManager;

    public TalentTreeGUI(RPGCore core, TalentManager talentManager, PlayerManager playerManager) {
        this.core = core;
        this.talentManager = talentManager;
        this.playerManager = playerManager;
    }

    public void open(Player player) {
        PlayerData data = playerManager.getData(player.getUniqueId());
        RPGClass userClass = data != null ? data.getRpgClass() : RPGClass.WARRIOR;

        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "✦ Árbol de Talentos: " + userClass.name());

        List<TalentManager.Specialization> specs = talentManager.getAvailableSpecs(userClass);
        int[] slots = {11, 13, 15};

        TalentManager.Specialization current = talentManager.getSpecialization(player.getUniqueId());

        for (int i = 0; i < specs.size() && i < 3; i++) {
            TalentManager.Specialization spec = specs.get(i);
            boolean isSelected = spec.equals(current);

            ItemStack item = new ItemStack(isSelected ? Material.NETHER_STAR : Material.ENCHANTED_BOOK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD + "✦ " + ChatColor.YELLOW + spec.displayName +
                        (isSelected ? ChatColor.GREEN + " [ACTIVO]" : ""));
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Bono:");
                lore.add("  " + ChatColor.translateAlternateColorCodes('&', spec.bonusDescription));
                lore.add("");
                lore.add(isSelected ? ChatColor.GREEN + "✔ Especialización seleccionada" : ChatColor.YELLOW + "¡Haz clic para especializarte!");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slots[i], item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Árbol de Talentos")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        PlayerData data = playerManager.getData(player.getUniqueId());
        if (data == null) return;

        List<TalentManager.Specialization> specs = talentManager.getAvailableSpecs(data.getRpgClass());
        int slot = event.getRawSlot();

        int index = (slot == 11) ? 0 : (slot == 13 ? 1 : (slot == 15 ? 2 : -1));
        if (index >= 0 && index < specs.size()) {
            talentManager.setSpecialization(player, specs.get(index));
            open(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(org.bukkit.event.inventory.InventoryDragEvent event) {
        if (event.getView().getTitle().contains("Árbol de Talentos")) {
            event.setCancelled(true);
        }
    }
}
