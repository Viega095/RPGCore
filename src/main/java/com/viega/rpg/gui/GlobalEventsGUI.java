package com.viega.rpg.gui;

import com.viega.rpg.RPGCore;
import com.viega.rpg.events.GlobalEventLock;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GlobalEventsGUI implements Listener {

    public static class GlobalEventsHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final RPGCore plugin;

    public GlobalEventsGUI(RPGCore plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new GlobalEventsHolder(), 27, "§8✦ Cronograma de Eventos ✦");

        // Fondo decorativo con paneles grises
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, border);
        }

        // Slot 4: Reloj del Servidor y Estado
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        inv.setItem(4, createItem(Material.CLOCK, "§e⏰ Reloj y Estado del Servidor", Arrays.asList(
                "§7Hora del Servidor: §f" + currentTime,
                "§7Sistema: §aCoordinador Centralizado",
                "§7Garantía: §eCero Superposiciones",
                "",
                "§6Los eventos rotan en horarios específicos",
                "§6con períodos de descanso intermedios."
        )));

        // Slot 10: Torneo de Pesca (:00)
        if (Bukkit.getPluginManager().isPluginEnabled("FishingEconomy")) {
            inv.setItem(10, createItem(Material.FISHING_ROD, "§b🎣 Minuto :00 - Marea y Torneo de Pesca", Arrays.asList(
                    "§7Plugin: §fEconomyFish",
                    "§7Horario: §bCada hora en punto (:00 - :10)",
                    "§7Duración: §f10 Minutos",
                    "",
                    "§e• Pesca de mayor peso o rapidez",
                    "§e• Invocación de Jefes: Kraken o Megalodón",
                    "§e• Recompensa: Dinero, Perlas y Escamas de Forja"
            )));
        }

        // Slot 12: Florecimiento Astral (:15)
        if (Bukkit.getPluginManager().isPluginEnabled("FloraArcana")) {
            inv.setItem(12, createItem(Material.ALLIUM, "§d🌸 Minuto :15 - Florecimiento Astral", Arrays.asList(
                    "§7Plugin: §fFlorArcana",
                    "§7Horario: §dCada hora en el minuto :15 (:15 - :25)",
                    "§7Duración: §f10 Minutos",
                    "",
                    "§e• Generación de Maná x2 en todas las flores",
                    "§e• Transmutaciones arcanas con descuento",
                    "§e• Recompensa: Esencias Celestiales y Rocío de Gaia"
            )));
        }

        // Slot 14: Luna de Sangre o Raid Boss (:30)
        inv.setItem(14, createItem(Material.NETHERITE_SWORD, "§c⚔ Minuto :30 - Incursión del Abismo", Arrays.asList(
                "§7Plugin: §fRPGCore",
                "§7Horario: §cCada hora en el minuto :30 (:30 - :40)",
                "§7Duración: §f10 Minutos",
                "",
                "§e• Incursión de Luna de Sangre o Despertar de Xylar",
                "§e• Mobs empoderados con botín épico",
                "§e• Recompensa: Gemas de Sangre y Fragmentos del Vacío"
        )));

        // Slot 16: Hora Dorada del Casino (:45)
        if (Bukkit.getPluginManager().isPluginEnabled("VieGambling")) {
            inv.setItem(16, createItem(Material.GOLD_BLOCK, "§6🎰 Minuto :45 - Hora Dorada del Casino", Arrays.asList(
                    "§7Plugin: §fVieGambling",
                    "§7Horario: §6Cada hora en el minuto :45 (:45 - :55)",
                    "§7Duración: §f10 Minutos",
                    "",
                    "§e• Multiplicadores de ganancias +50% en mesas 3D",
                    "§e• Alerta de Golpe a la Bóveda del Casino",
                    "§e• Recompensa: Pozo Jackpot y Monedas de la Fortuna"
            )));
        }

        // Slot 22: Estado del Evento en Tiempo Real
        if (GlobalEventLock.isEventActive()) {
            inv.setItem(22, createItem(Material.BEACON, "§a⚡ EVENTO GLOBAL EN VIVO", Arrays.asList(
                    "§7Evento: §e" + GlobalEventLock.getActiveEventTitle(),
                    "§7Plugin Anfitrión: §b" + GlobalEventLock.getActivePlugin(),
                    "§7Tiempo Restante: §f" + GlobalEventLock.getRemainingSeconds() + " segundos",
                    "",
                    "§a¡El evento está activo ahora mismo in-game!"
            )));
        } else {
            inv.setItem(22, createItem(Material.CAMPFIRE, "§a🕊 Período de Paz y Exploración", Arrays.asList(
                    "§7Estado: §fNingún evento activo",
                    "§7Próximo Evento Programado:",
                    "  " + GlobalEventLock.getNextScheduledEventName(),
                    "",
                    "§7Disfruta de la construcción y actividades normales."
            )));
        }

        // Slot 18 & 26: Opciones de Administración (si es OP)
        if (player.isOp()) {
            inv.setItem(18, createItem(Material.EMERALD, "§a⚡ [Admin] Forzar Siguiente Evento", Arrays.asList(
                    "§7Fuerza el inicio del evento programado",
                    "§7para la fase horaria actual."
            )));
            inv.setItem(26, createItem(Material.BARRIER, "§c✖ [Admin] Detener Evento Activo", Arrays.asList(
                    "§7Libera el cerrojo global y fuerza",
                    "§7el regreso a la fase de paz."
            )));
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.2f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GlobalEventsHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        if (slot == 26 && player.isOp()) {
            // Cancelar evento activo
            GlobalEventLock.clearLock();
            player.sendMessage(ChatColor.YELLOW + "⚠ [Eventos] Has forzado la finalización de los eventos activos y liberado el cerrojo global.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f, 1.5f);
            openGUI(player);
        } else if (slot == 18 && player.isOp()) {
            // Forzar inicio
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            if (minute < 15) {
                if (Bukkit.getPluginManager().isPluginEnabled("FishingEconomy")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fish tournament start 10");
                    player.sendMessage(ChatColor.GREEN + "✔ [Eventos] Disparado Torneo de Pesca.");
                } else {
                    player.sendMessage(ChatColor.RED + "✖ EconomyFish no está activo en este servidor.");
                }
            } else if (minute < 30) {
                if (Bukkit.getPluginManager().isPluginEnabled("FloraArcana")) {
                    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "flora event bloom 600");
                    player.sendMessage(ChatColor.GREEN + "✔ [Eventos] Disparado Florecimiento Astral.");
                } else {
                    player.sendMessage(ChatColor.RED + "✖ FloraArcana no está activo en este servidor.");
                }
            } else if (minute < 45) {
                plugin.getBloodMoonIncursionEngine().startBloodMoon(600);
                player.sendMessage(ChatColor.GREEN + "✔ [Eventos] Disparada Luna de Sangre.");
            } else {
                if (Bukkit.getPluginManager().isPluginEnabled("VieGambling")) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "🎰 ¡HORA DORADA DEL CASINO ACTIVADA POR ADMINISTRACIÓN!");
                    player.sendMessage(ChatColor.GREEN + "✔ [Eventos] Disparada Hora Dorada del Casino.");
                } else {
                    player.sendMessage(ChatColor.RED + "✖ VieGambling no está activo en este servidor.");
                }
            }
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GlobalEventsHolder) {
            event.setCancelled(true);
        }
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createItem(Material mat, String name) {
        return createItem(mat, name, null);
    }
}
