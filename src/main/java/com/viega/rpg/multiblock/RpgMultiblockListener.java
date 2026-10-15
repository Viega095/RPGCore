package com.viega.rpg.multiblock;

import com.viega.rpg.RPGCore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RpgMultiblockListener implements Listener {

    private final RPGCore plugin;
    private final RpgMultiblockManager manager;

    public RpgMultiblockListener(RPGCore plugin, RpgMultiblockManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        RpgMultiblockType type = manager.getTypeFromItem(item);
        if (type == null) return;

        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();

        boolean placed = manager.placeMultiblock(player, placedBlock.getLocation(), type);
        if (!placed) {
            event.setCancelled(true);
            return;
        }

        // Si se colocó con éxito y el jugador está en supervivencia, consumir 1 ítem
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack inHand = item.clone();
            inHand.setAmount(inHand.getAmount() - 1);
            player.getInventory().setItem(event.getHand(), inHand);
        }

        // Cancelar colocación vanilla para que el despliegue del multiblock sea 100% atómico
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (manager.isMultiblockBlock(block.getLocation())) {
            // Prohibir estrictamente drops vanilla de los bloques constituyentes (yunques, obsidiana, etc.)
            event.setDropItems(false);
            event.setExpToDrop(0);
            block.setType(Material.AIR, false);

            // Desmontar el 100% de la estructura y reembolsar el ítem crafteable original con PDC
            manager.dismantleAt(block.getLocation(), event.getPlayer());

            // Asegurar que el bloque roto quede destruido
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks()) {
            if (manager.isMultiblockBlock(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block b : event.getBlocks()) {
            if (manager.isMultiblockBlock(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> manager.isMultiblockBlock(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(b -> manager.isMultiblockBlock(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block clicked = event.getClickedBlock();
        ActiveRpgMultiblock multiblock = manager.getAt(clicked.getLocation());
        if (multiblock == null) return;

        // Cancelar interacción vanilla por defecto (evita interfaz vanilla de yunque, respawn anchor, etc.)
        event.setCancelled(true);

        Player player = event.getPlayer();
        switch (multiblock.getType()) {
            case LEGENDARY_ANVIL:
                player.performCommand("forge");
                break;

            case CLASS_ASCENSION_ALTAR:
                if (plugin.getClassAscensionManager() != null) {
                    plugin.getClassAscensionManager().openAscensionGUI(player);
                }
                break;

            case DUNGEON_STATION:
                player.performCommand("dungeon start");
                break;

            case RUNIC_JEWELRY_FORGE:
                player.performCommand("gemsocket");
                break;

            case ANCIENT_RUNIC_OBELISK:
                player.sendMessage("§b✦ [Obelisco Rúnico Ancestral] §aCanalizas el poder de las runas celestiales. Has recibido bendición y regeneración divina.");
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1.2f);
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1.5f);
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION, 300, 1));
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 300, 1));
                break;
        }
    }
}
