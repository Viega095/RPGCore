package com.viega.rpg.gui;

import com.viega.rpg.RPGCore;
import com.viega.rpg.managers.PlayerManager;
import com.viega.rpg.managers.TalentManager;
import com.viega.rpg.models.PlayerData;
import com.viega.rpg.models.RPGClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TalentTreeGUI implements Listener {

    public static class TalentHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

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

        Inventory inv = Bukkit.createInventory(new TalentHolder(), 27, ChatColor.DARK_PURPLE + "✦ Árbol de Talentos: " + userClass.name());

        // Filler panes
        ItemStack border = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

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
                lore.add(ChatColor.GRAY + "Bono Especial:");
                lore.add("  " + ChatColor.translateAlternateColorCodes('&', spec.bonusDescription));
                lore.add("");
                lore.add(isSelected ? ChatColor.GREEN + "✔ Especialización seleccionada" : ChatColor.YELLOW + "¡Haz clic para especializarte!");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slots[i], item);
        }

        // Info icon in slot 4
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName("§b§l✦ ESPECIALIZACIONES DE CLASE ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§7Cada especialización otorga un bono");
            lore.add("§7pasivo permanente adaptado a tu estilo.");
            lore.add("§7Clase actual: §e" + userClass.name());
            iMeta.setLore(lore);
            info.setItemMeta(iMeta);
        }
        inv.setItem(4, info);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof TalentHolder)) return;
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
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof TalentHolder) {
            event.setCancelled(true);
        }
    }
}
