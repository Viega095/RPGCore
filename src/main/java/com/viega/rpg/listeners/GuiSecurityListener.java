package com.viega.rpg.listeners;

import com.viega.rpg.RPGCore;
import com.viega.rpg.gui.*;
import com.viega.rpg.equipment.ArtifactRelicEngine;
import com.viega.rpg.guilds.GuildStrongholdSiegeManager;
import com.viega.rpg.managers.AttributePointManager;
import com.viega.rpg.managers.ClassAscensionManager;
import com.viega.rpg.managers.GemManager;
import com.viega.rpg.progression.AstralConstellationEngine;
import com.viega.rpg.quests.DynamicQuestBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public class GuiSecurityListener implements Listener {

    private final RPGCore plugin;

    public GuiSecurityListener(RPGCore plugin) {
        this.plugin = plugin;
    }

    private boolean isProtectedHolder(InventoryHolder holder) {
        return holder instanceof BlacksmithGUI.BlacksmithHolder
                || holder instanceof TalentTreeGUI.TalentHolder
                || holder instanceof GemManager.GemBagHolder
                || holder instanceof RPGTestLabGUI.LabHolder
                || holder instanceof ClassCraftingStation.CraftingHolder
                || holder instanceof GemSocketingGUI.GemSocketHolder
                || holder instanceof PetManagementGUI.PetHolder
                || holder instanceof ArtifactRelicEngine.RelicHolder
                || holder instanceof GuildStrongholdSiegeManager.GuildHolder
                || holder instanceof ClassAscensionManager.AscensionHolder
                || holder instanceof AttributePointManager.StatsHolder
                || holder instanceof AstralConstellationEngine.AstralHolder
                || holder instanceof DynamicQuestBoard.BoardHolder;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUniversalInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder != null && isProtectedHolder(holder)) {
            // Si el arrastre intersecta casillas del inventario superior (excepto casos de trabajo especiales que ya lo gestionan)
            int topSize = event.getView().getTopInventory().getSize();
            if (event.getRawSlots().stream().anyMatch(slot -> slot < topSize)) {
                // Para BlacksmithHolder y GemSocketHolder sus respectivos listeners permiten slots específicos de trabajo
                if (!(holder instanceof BlacksmithGUI.BlacksmithHolder) && !(holder instanceof GemSocketingGUI.GemSocketHolder)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
