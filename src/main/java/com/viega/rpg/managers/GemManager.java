package com.viega.rpg.managers;

import com.viega.rpg.RPGCore;
import com.viega.rpg.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class GemManager implements Manager, Listener {

    public static class GemBagHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private RPGCore core;
    private PlayerManager playerManager;
    private ItemManager itemManager;
    private NamespacedKey gemIdKey;

    private final Map<String, String[]> gemTypes = new HashMap<>();

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.gemIdKey = new NamespacedKey(core, "rpg_gem_id");
        core.getServer().getPluginManager().registerEvents(this, core);

        // Registro de gemas en español
        gemTypes.put("ruby_gem", new String[] { "§cRubí de Fuerza", "§7+2 Fuerza" });
        gemTypes.put("sapphire_gem", new String[] { "§9Zafiro de Maná", "§7+20 Maná Máximo" });
        gemTypes.put("emerald_gem", new String[] { "§aEsmeralda de Vitalidad", "§7+10 Vida Máxima" });
        gemTypes.put("topaz_gem", new String[] { "§eTopacio de Destreza", "§7+2 Destreza" });
        gemTypes.put("amethyst_gem", new String[] { "§dAmatista Arcana", "§7+3 Inteligencia" });
        gemTypes.put("ruby_fury", new String[] { "§cRubí de Furia Primordial", "§7+10 Fuerza | +5% Daño Crítico" });
        gemTypes.put("arcane_sapphire", new String[] { "§9Zafiro Arcano Celestial", "§7+50 Maná Máximo | +10% Regen" });
        gemTypes.put("vampiric_emerald", new String[] { "§aEsmeralda Vampírica Abisal", "§7+25 Vida Máxima | +5% Robo de Vida" });
    }

    public void setManagers(PlayerManager pm, ItemManager im) {
        this.playerManager = pm;
        this.itemManager = im;
    }

    @Override
    public void onDisable() {

    }

    public void openGemBag(Player player) {
        if (playerManager == null) return;
        PlayerData data = playerManager.getData(player.getUniqueId());
        if (data == null) return;

        Inventory inv = Bukkit.createInventory(new GemBagHolder(), 54, "§8💎 Bolsa de Gemas del Aventurero");

        for (String gemId : data.getGemBag()) {
            ItemStack gemItem = createGemItem(gemId);
            if (gemItem != null) {
                inv.addItem(gemItem);
            }
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.8f, 1.2f);
    }

    public ItemStack createGemItem(String gemId) {
        String[] info = gemTypes.get(gemId);
        if (info == null) return null;

        Material mat = switch (gemId) {
            case "ruby_gem" -> Material.REDSTONE;
            case "ruby_fury" -> Material.REDSTONE_BLOCK;
            case "sapphire_gem" -> Material.LAPIS_LAZULI;
            case "arcane_sapphire" -> Material.LAPIS_BLOCK;
            case "emerald_gem" -> Material.EMERALD;
            case "vampiric_emerald" -> Material.EMERALD_BLOCK;
            case "topaz_gem" -> Material.GLOWSTONE_DUST;
            case "amethyst_gem" -> Material.AMETHYST_SHARD;
            default -> Material.EMERALD;
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(info[0]);
            meta.setLore(Arrays.asList(
                    info[1],
                    "",
                    "§8[Gema Arcana]",
                    "§e▶ Haz clic en la bolsa para extraerla a tu inventario."
            ));
            if (gemIdKey != null) {
                meta.getPersistentDataContainer().set(gemIdKey, PersistentDataType.STRING, gemId);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isGem(String itemId) {
        return gemTypes.containsKey(itemId);
    }

    public String getGemName(String gemId) {
        String[] info = gemTypes.get(gemId);
        return info != null ? info[0] : gemId;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof GemBagHolder)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int rawSlot = event.getRawSlot();

        // Extraer gema de la bolsa al inventario del jugador
        if (rawSlot >= 0 && rawSlot < 54) {
            ItemStack current = event.getView().getTopInventory().getItem(rawSlot);
            if (current != null && current.getType() != Material.AIR && current.hasItemMeta()) {
                ItemMeta meta = current.getItemMeta();
                String gemId = null;
                if (gemIdKey != null && meta.getPersistentDataContainer().has(gemIdKey, PersistentDataType.STRING)) {
                    gemId = meta.getPersistentDataContainer().get(gemIdKey, PersistentDataType.STRING);
                }

                PlayerData data = playerManager != null ? playerManager.getData(player.getUniqueId()) : null;
                if (data != null && gemId != null) {
                    data.removeGem(gemId);
                }

                event.getView().getTopInventory().setItem(rawSlot, null);

                Map<Integer, ItemStack> overflow = player.getInventory().addItem(current);
                if (!overflow.isEmpty()) {
                    for (ItemStack item : overflow.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                    }
                }

                player.sendMessage("§e✔ Has extraído una gema de tu bolsa.");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.3f);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GemBagHolder) {
            event.setCancelled(true);
        }
    }
}
