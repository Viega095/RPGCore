package com.viega.rpg.multiblock;

import com.viega.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RpgMultiblockManager {

    private final RPGCore plugin;
    private final NamespacedKey structureKey;
    private final Map<UUID, ActiveRpgMultiblock> activeMultiblocks = new ConcurrentHashMap<>();
    private final Map<String, UUID> blockToMultiblock = new ConcurrentHashMap<>();
    private File storageFile;
    private FileConfiguration storageConfig;

    public RpgMultiblockManager(RPGCore plugin) {
        this.plugin = plugin;
        this.structureKey = new NamespacedKey(plugin, "rpg_structure");
        initStorage();
    }

    public void init() {
        registerRecipes();
        loadFromDisk();
        plugin.getLogger().info("RpgMultiblockManager inicializado con " + activeMultiblocks.size() + " estructuras.");
    }

    public void shutdown() {
        saveToDisk();
    }

    public NamespacedKey getStructureKey() {
        return structureKey;
    }

    public void registerRecipes() {
        for (RpgMultiblockType type : RpgMultiblockType.values()) {
            type.registerRecipe(plugin);
        }
    }

    public ItemStack createStructureItem(RpgMultiblockType type) {
        return type.createItemStack(plugin);
    }

    public RpgMultiblockType getTypeFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(structureKey, PersistentDataType.STRING)) return null;
        String id = pdc.get(structureKey, PersistentDataType.STRING);
        return RpgMultiblockType.fromId(id);
    }

    public boolean isMultiblockBlock(Location loc) {
        if (loc == null) return false;
        return blockToMultiblock.containsKey(toLocationKey(loc));
    }

    public ActiveRpgMultiblock getAt(Location loc) {
        if (loc == null) return null;
        UUID id = blockToMultiblock.get(toLocationKey(loc));
        return id != null ? activeMultiblocks.get(id) : null;
    }

    public Map<UUID, ActiveRpgMultiblock> getActiveMultiblocks() {
        return Collections.unmodifiableMap(activeMultiblocks);
    }

    /**
     * Comprueba espacio libre, construye bloques, hologramas y guarda la estructura en memoria y disco.
     */
    public boolean placeMultiblock(Player player, Location placedLoc, RpgMultiblockType type) {
        RpgMultiblockTemplate template = new RpgMultiblockTemplate(type);

        if (!template.checkClearance(placedLoc)) {
            if (player != null) {
                player.sendMessage("§c[RPGCore] ¡No se puede desplegar la estructura aquí (límite del mundo o bloque protegido)!");
                player.playSound(placedLoc, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            }
            return false;
        }

        UUID instanceId = UUID.randomUUID();
        ActiveRpgMultiblock multiblock = new ActiveRpgMultiblock(instanceId, type, placedLoc);
        multiblock.build(plugin);

        activeMultiblocks.put(instanceId, multiblock);
        for (Location bLoc : multiblock.getConstituentBlocks()) {
            blockToMultiblock.put(toLocationKey(bLoc), instanceId);
        }

        saveToDisk();

        if (player != null) {
            player.sendMessage("§a[RPGCore] ✔ ¡Estructura " + type.getDisplayName() + " §adesplegada con éxito!");
            player.sendMessage("§7Haz clic derecho en el bloque central para usarla. Romper cualquier bloque la desmontará limpiamente.");
            player.playSound(placedLoc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.2f);
        }

        return true;
    }

    /**
     * Desmonta el multiblock al romper cualquiera de sus bloques.
     */
    public boolean dismantleAt(Location blockLoc, Player player) {
        if (blockLoc == null) return false;
        String key = toLocationKey(blockLoc);
        UUID instanceId = blockToMultiblock.remove(key);
        if (instanceId == null) return false;

        ActiveRpgMultiblock multiblock = activeMultiblocks.remove(instanceId);
        if (multiblock == null) return false;

        // Remover todos los bloques del índice
        for (Location loc : multiblock.getConstituentBlocks()) {
            blockToMultiblock.remove(toLocationKey(loc));
        }

        // Ejecutar desmontaje limpio al 100% y reembolso
        multiblock.dismantle(plugin, player);

        saveToDisk();
        return true;
    }

    /**
     * Desmonta todas las estructuras en un radio determinado (para limpieza de pruebas).
     */
    public int dismantleAllNearby(Location center, double radius, Player player) {
        if (center == null || center.getWorld() == null) return 0;
        List<ActiveRpgMultiblock> toRemove = new ArrayList<>();
        for (ActiveRpgMultiblock mb : activeMultiblocks.values()) {
            if (mb.getBaseLocation().getWorld().equals(center.getWorld()) &&
                    mb.getBaseLocation().distance(center) <= radius) {
                toRemove.add(mb);
            }
        }

        for (ActiveRpgMultiblock mb : toRemove) {
            activeMultiblocks.remove(mb.getInstanceId());
            for (Location loc : mb.getConstituentBlocks()) {
                blockToMultiblock.remove(toLocationKey(loc));
            }
            mb.dismantle(plugin, player);
        }

        saveToDisk();
        return toRemove.size();
    }

    private void initStorage() {
        storageFile = new File(plugin.getDataFolder(), "structures.yml");
        if (!storageFile.exists()) {
            try {
                if (storageFile.getParentFile() != null) {
                    storageFile.getParentFile().mkdirs();
                }
                storageFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear structures.yml para multiblocks: " + e.getMessage());
            }
        }
        storageConfig = YamlConfiguration.loadConfiguration(storageFile);
    }

    public synchronized void saveToDisk() {
        if (storageConfig == null || storageFile == null) return;
        storageConfig.set("structures", null);

        ConfigurationSection sec = storageConfig.createSection("structures");
        for (ActiveRpgMultiblock mb : activeMultiblocks.values()) {
            String idStr = mb.getInstanceId().toString();
            ConfigurationSection mbSec = sec.createSection(idStr);
            mbSec.set("type", mb.getType().getId());
            Location loc = mb.getBaseLocation();
            if (loc.getWorld() != null) {
                mbSec.set("world", loc.getWorld().getName());
                mbSec.set("x", loc.getBlockX());
                mbSec.set("y", loc.getBlockY());
                mbSec.set("z", loc.getBlockZ());
            }
        }

        try {
            storageConfig.save(storageFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error al guardar structures.yml: " + e.getMessage());
        }
    }

    public synchronized void loadFromDisk() {
        if (storageConfig == null) return;
        activeMultiblocks.clear();
        blockToMultiblock.clear();

        ConfigurationSection sec = storageConfig.getConfigurationSection("structures");
        if (sec == null) return;

        for (String key : sec.getKeys(false)) {
            try {
                UUID instanceId = UUID.fromString(key);
                ConfigurationSection mbSec = sec.getConfigurationSection(key);
                if (mbSec == null) continue;

                String typeId = mbSec.getString("type");
                RpgMultiblockType type = RpgMultiblockType.fromId(typeId);
                if (type == null) continue;

                String worldName = mbSec.getString("world");
                World world = worldName != null ? Bukkit.getWorld(worldName) : null;
                if (world == null) continue;

                int x = mbSec.getInt("x");
                int y = mbSec.getInt("y");
                int z = mbSec.getInt("z");
                Location baseLoc = new Location(world, x, y, z);

                ActiveRpgMultiblock mb = new ActiveRpgMultiblock(instanceId, type, baseLoc);
                RpgMultiblockTemplate template = new RpgMultiblockTemplate(type);

                for (RpgMultiblockTemplate.BlockOffset offset : template.getRelativeBlocks().keySet()) {
                    Location bLoc = offset.toLocation(baseLoc);
                    mb.addConstituentBlock(bLoc);
                    blockToMultiblock.put(toLocationKey(bLoc), instanceId);
                }

                activeMultiblocks.put(instanceId, mb);
            } catch (Throwable t) {
                plugin.getLogger().warning("No se pudo cargar estructura guardada (" + key + "): " + t.getMessage());
            }
        }
    }

    public static String toLocationKey(Location loc) {
        if (loc == null || loc.getWorld() == null) return "";
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }
}
