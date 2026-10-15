package com.viega.rpg.multiblock;

import com.viega.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ActiveRpgMultiblock {

    private final UUID instanceId;
    private final RpgMultiblockType type;
    private final Location baseLocation;
    private final Set<Location> constituentBlocks = new HashSet<>();
    private final List<UUID> spawnedEntityUuids = new ArrayList<>();

    public ActiveRpgMultiblock(UUID instanceId, RpgMultiblockType type, Location baseLocation) {
        this.instanceId = instanceId;
        this.type = type;
        this.baseLocation = baseLocation.clone();
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public RpgMultiblockType getType() {
        return type;
    }

    public Location getBaseLocation() {
        return baseLocation.clone();
    }

    public Location getCoreLocation() {
        // The core block is located at (0, 1, 0) relative to baseLocation
        return baseLocation.clone().add(0, 1, 0);
    }

    public Set<Location> getConstituentBlocks() {
        return Collections.unmodifiableSet(constituentBlocks);
    }

    public List<UUID> getSpawnedEntityUuids() {
        return Collections.unmodifiableList(spawnedEntityUuids);
    }

    public void addConstituentBlock(Location loc) {
        constituentBlocks.add(loc.getBlock().getLocation());
    }

    /**
     * Construye físicamente los bloques del multiblock y genera los hologramas de interacción.
     */
    public void build(RPGCore plugin) {
        World world = baseLocation.getWorld();
        if (world == null) return;

        RpgMultiblockTemplate template = new RpgMultiblockTemplate(type);

        // 1. Construir bloques físicos
        for (Map.Entry<RpgMultiblockTemplate.BlockOffset, Material> entry : template.getRelativeBlocks().entrySet()) {
            Location blockLoc = entry.getKey().toLocation(baseLocation);
            blockLoc.getBlock().setType(entry.getValue(), false);
            constituentBlocks.add(blockLoc.getBlock().getLocation());
        }

        // 2. Generar hologramas informativos encima del núcleo
        Location hologramCenter = baseLocation.clone().add(0.5, template.getEntityOffsetY(), 0.5);
        List<String> lines = template.getHologramLines();

        double lineSpacing = 0.28;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Location lineLoc = hologramCenter.clone().add(0, (lines.size() - 1 - i) * lineSpacing, 0);

            try {
                ArmorStand stand = world.spawn(lineLoc, ArmorStand.class, as -> {
                    as.setVisible(false);
                    as.setGravity(false);
                    as.setMarker(true);
                    as.setCustomName(line);
                    as.setCustomNameVisible(true);
                    as.setInvulnerable(true);
                    as.setCollidable(false);
                    as.setBasePlate(false);
                });
                spawnedEntityUuids.add(stand.getUniqueId());
            } catch (Throwable t) {
                plugin.getLogger().warning("No se pudo generar holograma para " + type.getId() + ": " + t.getMessage());
            }
        }
    }

    /**
     * Desmonta el 100% de la estructura: limpia bloques a AIR, elimina entidades y hologramas,
     * y reembolsa EXACTAMENTE 1 ítem original crafteable con PDC (CERO duplicación).
     */
    public void dismantle(RPGCore plugin, Player player) {
        World world = baseLocation.getWorld();

        // 1. Eliminar entidades rastreadas
        if (world != null) {
            for (UUID uid : spawnedEntityUuids) {
                Entity ent = Bukkit.getEntity(uid);
                if (ent != null && ent.isValid()) {
                    ent.remove();
                }
            }
        }
        spawnedEntityUuids.clear();

        // Limpiar cualquier ArmorStand residual en el área
        if (world != null) {
            for (Entity ent : world.getNearbyEntities(baseLocation, 5, 5, 5)) {
                if (ent instanceof ArmorStand) {
                    String customName = ent.getCustomName();
                    if (customName != null && (customName.contains("YUNQUE LEGENDARIO") ||
                            customName.contains("ALTAR DE ASCENSIÓN") ||
                            customName.contains("ESTACIÓN DE MAZMORRAS") ||
                            customName.contains("Clic Derecho:"))) {
                        ent.remove();
                    }
                }
            }
        }

        // 2. Eliminar 100% de los bloques físicos (cambiar a AIR sin soltar nada)
        for (Location loc : constituentBlocks) {
            if (loc.getWorld() != null) {
                loc.getBlock().setType(Material.AIR, false);
            }
        }
        constituentBlocks.clear();

        // 3. Reembolsar EXACTAMENTE 1 ítem original crafteable con PDC al jugador
        if (player != null && player.isOnline()) {
            ItemStack refundItem = type.createItemStack(plugin);
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(refundItem);
            if (!overflow.isEmpty()) {
                for (ItemStack remaining : overflow.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), remaining);
                }
            }
            player.sendMessage("§a✔ Estructura " + type.getDisplayName() + " §adesmontada limpiamente. Se te ha reembolsado el bloque original.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
        } else if (world != null) {
            ItemStack refundItem = type.createItemStack(plugin);
            world.dropItemNaturally(baseLocation, refundItem);
        }
    }
}
