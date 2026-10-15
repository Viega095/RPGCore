package com.viega.rpg.gui;

import com.viega.rpg.RPGCore;
import com.viega.rpg.bosses.BloodMoonIncursionEngine;
import com.viega.rpg.events.GlobalEventLock;
import com.viega.rpg.multiblock.RpgMultiblockType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuiteMasterAdminGUI implements Listener {

    public static class SuiteHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final RPGCore plugin;

    public SuiteMasterAdminGUI(RPGCore plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new SuiteHolder(), 54, "§8✦ Suite Maestra Multi-Plugin ✦");

        boolean hasFish = Bukkit.getPluginManager().isPluginEnabled("FishingEconomy");
        boolean hasFlora = Bukkit.getPluginManager().isPluginEnabled("FloraArcana");
        boolean hasCasino = Bukkit.getPluginManager().isPluginEnabled("VieGambling");
        boolean hasRpg = Bukkit.getPluginManager().isPluginEnabled("RPGCore");

        // Fondo de cristales negros
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, border);
        }

        // Fila 1: Cabecera dinámica
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Centro de control y diagnóstico integrado.");
        headerLore.add("");
        if (hasFish) headerLore.add("§b• EconomyFish §a[Activo]");
        if (hasFlora) headerLore.add("§d• FloraArcana §a[Activo]");
        if (hasCasino) headerLore.add("§6• VieGambling §a[Activo]");
        if (hasRpg) headerLore.add("§c• RPGCore §a[Activo]");
        headerLore.add("");
        headerLore.add("§eHaz clic en cualquier opción para probar.");
        inv.setItem(4, createItem(Material.NETHER_STAR, "§6§l✦ PANEL MAESTRO MULTI-PLUGIN ✦", headerLore));

        // Fila 2: Test Labs
        if (hasFish) {
            inv.setItem(10, createItem(Material.FISHING_ROD, "§b🎣 Abrir Test Lab: EconomyFish", Arrays.asList(
                    "§7Accede a la suite completa de pruebas",
                    "§7de cañas, cebos, peces y torneos.",
                    "§aComando: /fish test"
            )));
        }
        if (hasFlora) {
            inv.setItem(12, createItem(Material.ALLIUM, "§d🌸 Abrir Test Lab: FloraArcana", Arrays.asList(
                    "§7Accede al laboratorio de alquimia,",
                    "§7elixires, flores y transmutaciones.",
                    "§aComando: /flora test"
            )));
        }
        if (hasCasino) {
            inv.setItem(14, createItem(Material.GOLD_INGOT, "§6🎰 Abrir Test Lab: VieGambling", Arrays.asList(
                    "§7Accede al centro de pruebas del Casino,",
                    "§7mesas 3D, tiradas y jackpots.",
                    "§aComando: /casino test"
            )));
        }
        if (hasRpg) {
            inv.setItem(16, createItem(Material.NETHERITE_SWORD, "§c⚔ Abrir Test Lab: RPGCore", Arrays.asList(
                    "§7Accede al laboratorio RPG de clases,",
                    "§7ítems, forjas, mazmorras y raids.",
                    "§aComando: /rpg test"
            )));
        }

        // Fila 3: Compendios de Contenido
        if (hasFish) {
            inv.setItem(19, createItem(Material.BOOK, "§b📖 Guía de Contenido: EconomyFish", Arrays.asList(
                    "§7Ver compendio de especies, biomas y cañas.",
                    "§aComando: /fish guia"
            )));
        }
        if (hasFlora) {
            inv.setItem(21, createItem(Material.BOOK, "§d📖 Guía de Contenido: FloraArcana", Arrays.asList(
                    "§7Ver compendio botánico y recetas mágicas.",
                    "§aComando: /flora guia"
            )));
        }
        if (hasCasino) {
            inv.setItem(23, createItem(Material.BOOK, "§6📖 Guía de Contenido: VieGambling", Arrays.asList(
                    "§7Ver compendio de mesas y juegos de azar.",
                    "§aComando: /casino guia"
            )));
        }
        if (hasRpg) {
            inv.setItem(25, createItem(Material.BOOK, "§c📖 Guía de Contenido: RPGCore", Arrays.asList(
                    "§7Ver compendio de clases, habilidades y forja.",
                    "§aComando: /rpg guia"
            )));
        }

        // Fila 4: Acciones Globales y Sinergias
        inv.setItem(28, createItem(Material.CHEST, "§e🎁 Otorgar Kit de Sinergia Cruzada", Arrays.asList(
                "§7Recibe en tu inventario todos los ítems",
                "§7conectados entre los 4 plugins:",
                "§b• Escama de Megalodón (Pesca -> RPG)",
                "§5• Tentáculo de Kraken (Pesca -> RPG)",
                "§4• Gema de Sangre Carmesí (RPG -> FlorArcana)",
                "§d• Fragmento del Vacío (RPG -> FlorArcana)",
                "§6• Moneda de la Fortuna (Casino -> Todos)"
        )));

        inv.setItem(30, createItem(Material.CLOCK, "§e⏰ Agenda de Eventos Globales", Arrays.asList(
                "§7Abre el menú interactivo con el cronograma",
                "§7de eventos horarios y estado en tiempo real.",
                "§aComando: /eventos"
        )));

        inv.setItem(32, createItem(Material.BLAZE_POWDER, "§6⚡ Disparar Siguiente Evento Horario", Arrays.asList(
                "§7Fuerza la ejecución inmediata del evento",
                "§7asignado al intervalo de la hora actual."
        )));

        inv.setItem(34, createItem(Material.LAVA_BUCKET, "§c🧹 Limpieza de Estructuras (Radio 30)", Arrays.asList(
                "§7Desmonta todas las estructuras cercanas de",
                "§7todos los plugins y reembolsa sus bloques crafteables.",
                "§e¡Cero residuos en el mundo!"
        )));

        // Fila 5: Nuevas Estructuras Multibloque
        if (hasFlora) {
            inv.setItem(37, createItem(Material.AMETHYST_CLUSTER, "§d✦ [FlorArcana] Destilador Celestial", Arrays.asList(
                    "§7Obtén el bloque de despliegue para el",
                    "§7Destilador de Esencias Celestiales.",
                    "§aColocar: Autoconstruye estructura 3D",
                    "§cRomper: Desmonta y reembolsa el ítem al 100%"
            )));
        }

        if (hasFish) {
            inv.setItem(39, createItem(Material.MANGROVE_PROPAGULE, "§b✦ [EconomyFish] Muelle de Trampas", Arrays.asList(
                    "§7Obtén el bloque de despliegue para el",
                    "§7Muelle de Trampas de Cangrejos pasivo.",
                    "§aColocar: Autoconstruye estructura costera",
                    "§cRomper: Desmonta y reembolsa el ítem al 100%"
            )));
        }

        if (hasCasino) {
            inv.setItem(41, createItem(Material.GREEN_CARPET, "§6✦ [VieGambling] Mesa BlackJack 3D", Arrays.asList(
                    "§7Obtén la mesa multiblock de BlackJack 3D.",
                    "§aColocar: Autoconstruye mesa con tapete",
                    "§cRomper: Desmonta y reembolsa el ítem al 100%"
            )));
        }

        if (hasRpg) {
            inv.setItem(43, createItem(Material.CRYING_OBSIDIAN, "§c✦ [RPGCore] Grieta del Vacío (Void Gateway)", Arrays.asList(
                    "§7Obtén la estructura rúnica que abre",
                    "§7el portal a las pruebas del vacío.",
                    "§aColocar: Autoconstruye portal oscuro",
                    "§cRomper: Desmonta y reembolsa el ítem al 100%"
            )));
        }

        // Fila 6: Botón de cierre
        inv.setItem(49, createItem(Material.BARRIER, "§c✖ Cerrar Suite"));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.8f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SuiteHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        boolean hasFish = Bukkit.getPluginManager().isPluginEnabled("FishingEconomy");
        boolean hasFlora = Bukkit.getPluginManager().isPluginEnabled("FloraArcana");
        boolean hasCasino = Bukkit.getPluginManager().isPluginEnabled("VieGambling");
        boolean hasRpg = Bukkit.getPluginManager().isPluginEnabled("RPGCore");

        switch (slot) {
            case 10:
                if (!hasFish) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "fish test");
                break;
            case 12:
                if (!hasFlora) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "flora test");
                break;
            case 14:
                if (!hasCasino) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "casino test");
                break;
            case 16:
                if (!hasRpg) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "rpg test");
                break;
            case 19:
                if (!hasFish) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "fish guia");
                break;
            case 21:
                if (!hasFlora) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "flora guia");
                break;
            case 23:
                if (!hasCasino) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "casino guia");
                break;
            case 25:
                if (!hasRpg) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "rpg guia");
                break;
            case 28:
                giveSynergyKit(player);
                break;
            case 30:
                player.closeInventory();
                plugin.getGlobalEventsGUI().openGUI(player);
                break;
            case 32:
                player.closeInventory();
                plugin.getGlobalEventsGUI().openGUI(player);
                break;
            case 34:
                int count = plugin.getRpgMultiblockManager().dismantleAllNearby(player.getLocation(), 30.0, player);
                player.sendMessage(ChatColor.GREEN + "🧹 [Suite] ¡Desmontadas " + count + " estructuras de RPG en 30 bloques a la redonda!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1.2f);
                break;
            case 37:
                if (!hasFlora) return;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "flora give " + player.getName() + " CELESTIAL_ESSENCE_DISTILLER 1");
                player.sendMessage(ChatColor.GREEN + "✔ [Suite] Entregado Destilador de Esencias Celestiales.");
                break;
            case 39:
                if (!hasFish) return;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fish multiblock give " + player.getName() + " CRAB_TRAP_DOCK 1");
                player.sendMessage(ChatColor.GREEN + "✔ [Suite] Entregado Muelle de Trampas de Cangrejos.");
                break;
            case 41:
                if (!hasCasino) return;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "casino give " + player.getName() + " BLACKJACK_TABLE_3D 1");
                player.sendMessage(ChatColor.GREEN + "✔ [Suite] Entregada Mesa de BlackJack 3D.");
                break;
            case 43:
                if (!hasRpg) return;
                ItemStack voidGateway = RpgMultiblockType.VOID_RIFT_GATEWAY.createItemStack(plugin);
                player.getInventory().addItem(voidGateway);
                player.sendMessage(ChatColor.GREEN + "✔ [Suite] Entregada Grieta del Vacío (Void Gateway).");
                break;
            case 49:
                player.closeInventory();
                break;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof SuiteHolder) {
            event.setCancelled(true);
        }
    }

    private void giveSynergyKit(Player player) {
        // 1. Escama de Megalodón
        ItemStack scale = new ItemStack(Material.PRISMARINE_CRYSTALS, 2);
        ItemMeta sMeta = scale.getItemMeta();
        if (sMeta != null) {
            sMeta.setDisplayName("§b§l✦ Escama de Megalodón Ancestral ✦");
            sMeta.setLore(Arrays.asList(
                    "§7Reliquia marina de gran resistencia.",
                    "§9Úsala en la §6Forja de Titanes (RPG) §9para imbuir armaduras."
            ));
            sMeta.getPersistentDataContainer().set(
                    new NamespacedKey("fishingeconomy", "megalodon_scale"),
                    org.bukkit.persistence.PersistentDataType.BYTE,
                    (byte) 1
            );
            scale.setItemMeta(sMeta);
        }

        // 2. Tentáculo del Kraken
        ItemStack tentacle = new ItemStack(Material.PRISMARINE_SHARD, 2);
        ItemMeta tMeta = tentacle.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName("§5§l✦ Tentáculo del Kraken Abisal ✦");
            tMeta.setLore(Arrays.asList(
                    "§7Reliquia orgánica obtenida del terror del océano.",
                    "§9Úsala en la §6Forja de Titanes (RPG) §9para afinidad abisal."
            ));
            tMeta.getPersistentDataContainer().set(
                    new NamespacedKey("fishingeconomy", "kraken_tentacle"),
                    org.bukkit.persistence.PersistentDataType.BYTE,
                    (byte) 1
            );
            tentacle.setItemMeta(tMeta);
        }

        // 3. Gema de Sangre Carmesí
        ItemStack bloodGem = BloodMoonIncursionEngine.createBloodGem(4);

        // 4. Fragmento del Vacío
        ItemStack fragment = new ItemStack(Material.AMETHYST_SHARD, 2);
        ItemMeta fMeta = fragment.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName("§5§l✦ Fragmento del Vacío de Xylar ✦");
            fMeta.setLore(Arrays.asList(
                    "§7Cristal arrancado del Soberano del Vacío.",
                    "§aTransmutable en la Piedra Filosofal (FlorArcana)."
            ));
            fMeta.getPersistentDataContainer().set(
                    new NamespacedKey("rpg", "void_fragment"),
                    org.bukkit.persistence.PersistentDataType.BYTE,
                    (byte) 1
            );
            fragment.setItemMeta(fMeta);
        }

        // 5. Moneda de la Fortuna
        ItemStack fortuneCoin = new ItemStack(Material.SUNFLOWER, 3);
        ItemMeta cMeta = fortuneCoin.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName("§6§l✦ Moneda de la Fortuna Suprema ✦");
            cMeta.setLore(Arrays.asList(
                    "§7Acuñada en los sótanos del Casino.",
                    "§eCanjeable por botines raros de todos los plugins."
            ));
            cMeta.getPersistentDataContainer().set(
                    new NamespacedKey("gamblingplugin", "destiny_token"),
                    org.bukkit.persistence.PersistentDataType.BYTE,
                    (byte) 1
            );
            fortuneCoin.setItemMeta(cMeta);
        }

        player.getInventory().addItem(scale, tentacle, bloodGem, fragment, fortuneCoin);
        player.sendMessage(ChatColor.GOLD + "🎁 [Suite] ¡Kit completo de Sinergia Cruzada entregado con éxito!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.4f);
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
