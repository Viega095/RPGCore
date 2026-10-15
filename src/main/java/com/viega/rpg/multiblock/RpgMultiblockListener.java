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
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

            case TITAN_FORGE:
                player.sendMessage("§6🔥 [Forja Rúnica del Titán Herrero] §eEl fuego primordial del Titán repara tu arma y te bendice con fuerza y resistencia titánica.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
                player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 0.8f);
                ItemStack held = player.getInventory().getItemInMainHand();
                if (held != null && held.getType() != Material.AIR && held.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable dmg) {
                    dmg.setDamage(0);
                    held.setItemMeta(dmg);
                }
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE, 600, 1));
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 600, 1));

                // Sinergia Cruzada con EconomyFish: Escama de Megalodón o Tentáculo de Kraken
                boolean upgraded = false;
                for (ItemStack invItem : player.getInventory().getContents()) {
                    if (invItem != null && invItem.hasItemMeta()) {
                        org.bukkit.persistence.PersistentDataContainer pdc = invItem.getItemMeta().getPersistentDataContainer();
                        boolean isScale = pdc.has(new NamespacedKey("fishingeconomy", "megalodon_scale"), org.bukkit.persistence.PersistentDataType.BYTE);
                        boolean isTentacle = pdc.has(new NamespacedKey("fishingeconomy", "kraken_tentacle"), org.bukkit.persistence.PersistentDataType.BYTE);
                        if (isScale || isTentacle) {
                            if (held != null && held.getType().name().contains("CHESTPLATE") || held != null && held.getType().name().contains("HELMET") ||
                                    held != null && held.getType().name().contains("LEGGINGS") || held != null && held.getType().name().contains("BOOTS")) {
                                invItem.setAmount(invItem.getAmount() - 1);
                                org.bukkit.inventory.meta.ItemMeta hMeta = held.getItemMeta();
                                if (hMeta != null) {
                                    List<String> lore = hMeta.hasLore() ? new ArrayList<>(hMeta.getLore()) : new ArrayList<>();
                                    lore.add("§9✦ Bendición de las Profundidades ✦ (+15% Resistencia Elemental)");
                                    hMeta.setLore(lore);
                                    hMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "abyssal_blessing"), org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
                                    held.setItemMeta(hMeta);
                                }
                                player.getWorld().strikeLightningEffect(clicked.getLocation());
                                player.sendMessage("§9🌊 ¡[Sinergia] Has imbuido tu armadura con la Bendición de las Profundidades gracias a las reliquias oceánicas!");
                                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
                                upgraded = true;
                                break;
                            }
                        }
                    }
                }
                if (!upgraded && held != null && held.getType() != Material.AIR) {
                    player.sendMessage("§7Tip: Sostén una armadura y lleva una §bEscama de Megalodón §7o §5Tentáculo de Kraken §7para imbuirla.");
                }
                break;

            case VOID_RIFT_GATEWAY:
                player.sendMessage("§5✦ [Grieta del Vacío] §dEl portal resuena con energía abisal... Transportándote a las pruebas del Vacío.");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.7f);
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 1.5f);
                player.getWorld().spawnParticle(Particle.PORTAL, clicked.getLocation().add(0.5, 1.5, 0.5), 50, 0.5, 0.5, 0.5, 0.2);
                player.performCommand("abyss start");
                break;
        }
    }
}
