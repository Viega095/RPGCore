package com.viega.rpg.listeners;

import com.viega.rpg.RPGCore;
import com.viega.rpg.managers.ItemManager;
import com.viega.rpg.managers.PlayerManager;
import com.viega.rpg.models.PlayerData;
import com.viega.rpg.models.RPGClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    private final RPGCore core;
    private final ItemManager itemManager;
    private final PlayerManager playerManager;
    private com.viega.rpg.managers.SkillManager skillManager;

    public ItemListener(RPGCore core, ItemManager itemManager, PlayerManager playerManager) {
        this.core = core;
        this.itemManager = itemManager;
        this.playerManager = playerManager;
    }

    public void setSkillManager(com.viega.rpg.managers.SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item == null)
            return;

        if (!canUseItem(player, item)) {
            event.setCancelled(true);
            player.sendMessage("§c¡No cumples con los requisitos para usar este objeto!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null)
            return;

        if (!canUseItem(player, item)) {
            event.setCancelled(true);
            player.sendMessage("§c¡No cumples con los requisitos para usar este objeto!");
        }
    }

    private boolean canUseItem(Player player, ItemStack item) {
        PlayerData data = playerManager.getData(player.getUniqueId());
        if (data == null)
            return true;

        if (item.getType() == org.bukkit.Material.EMERALD) { // Placeholder check for gems
            return false;
        }

        // Check level requirement
        int levelReq = itemManager.getLevelRequirement(item);
        if (data.getLevel() < levelReq) {
            return false;
        }

        // Check class requirement
        RPGClass classReq = itemManager.getClassRequirement(item);
        if (classReq != RPGClass.NONE && data.getRpgClass() != classReq) {
            return false;
        }

        return true;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT") && event.getItem() != null) {
            ItemStack item = event.getItem();
            if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                for (String line : item.getItemMeta().getLore()) {
                    if (line.contains("Omnivampirism") || line.contains("Omnivampirismo")) {
                        if (skillManager != null) {
                            skillManager.castSkill(event.getPlayer(), "Omnivampirism");
                        }
                        break;
                    }
                }
            }
        }
    }
}
