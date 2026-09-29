package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
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
import java.util.concurrent.ConcurrentHashMap;

public class AttributePointManager implements Manager, Listener {

    private RPGCore core;
    private PlayerManager playerManager;
    private final Map<UUID, Integer> unspentPoints = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> vitalityPoints = new ConcurrentHashMap<>();

    public static class StatsHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.playerManager = core.getManagerHandler().getManager(PlayerManager.class);
        core.getServer().getPluginManager().registerEvents(this, core);
    }

    @Override
    public void onDisable() {
        unspentPoints.clear();
        vitalityPoints.clear();
    }

    public void addPoints(UUID uuid, int points) {
        unspentPoints.put(uuid, unspentPoints.getOrDefault(uuid, 0) + points);
    }

    public int getUnspentPoints(UUID uuid) {
        return unspentPoints.getOrDefault(uuid, 0);
    }

    public int getVitality(UUID uuid) {
        return vitalityPoints.getOrDefault(uuid, 0);
    }

    public void openStatsGUI(Player player) {
        PlayerData data = playerManager != null ? playerManager.getData(player.getUniqueId()) : null;
        if (data == null) return;

        Inventory inv = Bukkit.createInventory(new StatsHolder(), 36, "§8📊 Asignación de Atributos");

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 36; i++) inv.setItem(i, border);

        int available = getUnspentPoints(player.getUniqueId());

        // Header
        ItemStack summary = new ItemStack(Material.NETHER_STAR);
        ItemMeta sMeta = summary.getItemMeta();
        if (sMeta != null) {
            sMeta.setDisplayName("§e§l✦ Puntos de Atributo Disponibles: §a" + available + " ✦");
            sMeta.setLore(Arrays.asList(
                    "§7Nivel actual: §b" + data.getLevel(),
                    "§7Gana §e3 Puntos §7por cada nivel subido.",
                    "",
                    "§7Distribuye tus puntos sabiamente para potenciar",
                    "§7tu clase y estilo de combate."
            ));
            summary.setItemMeta(sMeta);
        }
        inv.setItem(4, summary);

        // Strength (Slot 10)
        inv.setItem(10, createStatItem(Material.DIAMOND_SWORD, "§c§lFuerza (STR)",
                "§7Aumenta daño físico cuerpo a cuerpo y golpe crítico.",
                (int) data.getStrength(), available > 0));

        // Intelligence (Slot 12)
        inv.setItem(12, createStatItem(Material.ENCHANTED_BOOK, "§9§lInteligencia (INT)",
                "§7Aumenta maná máximo y daño de habilidades mágicas.",
                (int) data.getIntelligence(), available > 0));

        // Dexterity (Slot 14)
        inv.setItem(14, createStatItem(Material.BOW, "§a§lDestreza (DEX)",
                "§7Aumenta velocidad de movimiento y evasión.",
                (int) data.getDexterity(), available > 0));

        // Defense (Slot 16)
        inv.setItem(16, createStatItem(Material.SHIELD, "§6§lDefensa (DEF)",
                "§7Aumenta resistencia a la armadura y mitigación de daño.",
                (int) data.getDefense(), available > 0));

        // Vitality (Slot 22)
        inv.setItem(22, createStatItem(Material.GOLDEN_APPLE, "§d§lVitalidad (VIT)",
                "§7Aumenta puntos de vida máxima y regeneración.",
                getVitality(player.getUniqueId()), available > 0));

        player.openInventory(inv);
    }

    private ItemStack createStatItem(Material mat, String name, String desc, int currentVal, boolean canAdd) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name + " §e[" + currentVal + "]");
            List<String> lore = new ArrayList<>();
            lore.add(desc);
            lore.add("");
            lore.add("§7Valor actual: §a+" + currentVal);
            if (canAdd) {
                lore.add("§e▶ Haz clic izquierdo para asignar +1 punto");
            } else {
                lore.add("§c✖ No tienes puntos de atributo disponibles");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof StatsHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        PlayerData data = playerManager != null ? playerManager.getData(player.getUniqueId()) : null;
        if (data == null) return;

        int available = getUnspentPoints(player.getUniqueId());
        int slot = event.getRawSlot();

        if (available <= 0) {
            if (slot == 10 || slot == 12 || slot == 14 || slot == 16 || slot == 22) {
                player.sendMessage(ChatColor.RED + "✖ No tienes puntos de atributo disponibles.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
            }
            return;
        }

        boolean upgraded = false;
        if (slot == 10) { // STR
            data.setStrength(data.getStrength() + 1);
            upgraded = true;
        } else if (slot == 12) { // INT
            data.setIntelligence(data.getIntelligence() + 1);
            data.setMaxMana(data.getMaxMana() + 5);
            upgraded = true;
        } else if (slot == 14) { // DEX
            data.setDexterity(data.getDexterity() + 1);
            upgraded = true;
        } else if (slot == 16) { // DEF
            data.setDefense(data.getDefense() + 1);
            upgraded = true;
        } else if (slot == 22) { // VIT
            vitalityPoints.put(player.getUniqueId(), getVitality(player.getUniqueId()) + 1);
            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0 + (getVitality(player.getUniqueId()) * 2.0));
            }
            upgraded = true;
        }

        if (upgraded) {
            unspentPoints.put(player.getUniqueId(), available - 1);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.6f);
            openStatsGUI(player);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof StatsHolder) {
            event.setCancelled(true);
        }
    }
}
