package com.antigravity.rpg.progression;

import com.antigravity.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.*;

public class AstralConstellationEngine implements Listener {

    public static class AstralHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class ConstellationProfile {
        public int celestialShards = 5;
        public Set<String> unlockedNodes = new HashSet<>();
    }

    private final RPGCore plugin;
    private final Map<UUID, ConstellationProfile> profiles = new HashMap<>();

    public AstralConstellationEngine(RPGCore plugin) {
        this.plugin = plugin;
    }

    public ConstellationProfile getProfile(UUID uuid) {
        return profiles.computeIfAbsent(uuid, k -> new ConstellationProfile());
    }

    public void openConstellationGUI(Player player) {
        ConstellationProfile prof = getProfile(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(new AstralHolder(), 54, "§8🌌 §dConstelaciones Astrales §8🌌");

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, createPane(Material.BLACK_STAINED_GLASS_PANE));
        }

        // Star Nodes
        // Slot 11: Constelación del Fénix
        boolean fenixUnlocked = prof.unlockedNodes.contains("FENIX");
        inv.setItem(11, createStarNode(Material.FIRE_CHARGE, "§6⭐ Constelación del Fénix",
                Arrays.asList("§7Efecto: §eRenacer con 35% HP al morir", "§7(Enfriamiento: 5 minutos)", "",
                        fenixUnlocked ? "§a✔ DESBLOQUEADA" : "§eCosto: 2 Fragmentos Celestiales",
                        "", fenixUnlocked ? "§7Ya activa en tu aura" : "§a▶ Clic para despertar estrella")));

        // Slot 15: Constelación de Orión
        boolean orionUnlocked = prof.unlockedNodes.contains("ORION");
        inv.setItem(15, createStarNode(Material.SPECTRAL_ARROW, "§b⭐ Constelación de Orión",
                Arrays.asList("§7Efecto: §b+30% Daño Crítico & Flechas Perforantes", "",
                        orionUnlocked ? "§a✔ DESBLOQUEADA" : "§eCosto: 2 Fragmentos Celestiales",
                        "", orionUnlocked ? "§7Ya activa en tu aura" : "§a▶ Clic para despertar estrella")));

        // Slot 29: Constelación de Chronos
        boolean chronosUnlocked = prof.unlockedNodes.contains("CHRONOS");
        inv.setItem(29, createStarNode(Material.CLOCK, "§d⭐ Constelación de Chronos",
                Arrays.asList("§7Efecto: §d-20% Enfriamiento de Habilidades RPG", "",
                        chronosUnlocked ? "§a✔ DESBLOQUEADA" : "§eCosto: 3 Fragmentos Celestiales",
                        "", chronosUnlocked ? "§7Ya activa en tu aura" : "§a▶ Clic para despertar estrella")));

        // Slot 33: Constelación de la Égida Cósmica
        boolean aegisUnlocked = prof.unlockedNodes.contains("AEGIS");
        inv.setItem(33, createStarNode(Material.SHIELD, "§e⭐ Constelación de la Égida Cósmica",
                Arrays.asList("§7Efecto: §6-15% Daño recibido de Jefes y Élites", "",
                        aegisUnlocked ? "§a✔ DESBLOQUEADA" : "§eCosto: 3 Fragmentos Celestiales",
                        "", aegisUnlocked ? "§7Ya activa en tu aura" : "§a▶ Clic para despertar estrella")));

        // Center Cosmic Shards Info (Slot 49)
        inv.setItem(49, createBtn(Material.NETHER_STAR, "§b✦ Fragmentos Celestiales: §f" + prof.celestialShards,
                Arrays.asList("§7Obtén más fragmentos derrotando", "§7jefes de incursión y pisos del Abismo.")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 1.6f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AstralHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            ConstellationProfile prof = getProfile(player.getUniqueId());

            if (slot == 11) { // Fenix
                unlockNode(player, prof, "FENIX", 2);
            } else if (slot == 15) { // Orion
                unlockNode(player, prof, "ORION", 2);
            } else if (slot == 29) { // Chronos
                unlockNode(player, prof, "CHRONOS", 3);
            } else if (slot == 33) { // Aegis
                unlockNode(player, prof, "AEGIS", 3);
            }
        }
    }

    private void unlockNode(Player player, ConstellationProfile prof, String nodeId, int cost) {
        if (prof.unlockedNodes.contains(nodeId)) {
            player.sendMessage(ChatColor.YELLOW + "Esta estrella ya está desbloqueada en tu mapa estelar.");
            return;
        }

        if (prof.celestialShards < cost) {
            player.sendMessage(ChatColor.RED + "No tienes suficientes Fragmentos Celestiales (" + cost + " requeridos).");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1f);
            return;
        }

        prof.celestialShards -= cost;
        prof.unlockedNodes.add(nodeId);

        player.sendTitle("§d✨ ¡ESTRELLA DESPERTADA!", "§eConstelación " + nodeId + " activa", 10, 50, 15);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.4f);
        player.sendMessage(ChatColor.GOLD + "🌌 [Astral] ¡Has canalizado el poder estelar y desbloqueado la pasiva cósmica!");
        openConstellationGUI(player);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AstralHolder) {
            event.setCancelled(true);
        }
    }

    private ItemStack createStarNode(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private ItemStack createBtn(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
