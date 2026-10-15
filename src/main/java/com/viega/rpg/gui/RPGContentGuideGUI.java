package com.viega.rpg.gui;

import com.viega.rpg.RPGCore;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;

public class RPGContentGuideGUI implements Listener {

    public static class GuideHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final RPGCore plugin;

    public RPGContentGuideGUI(RPGCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new GuideHolder(), 54, "§c§lRPGCore §8| §0Guía & Compendio");

        ItemStack border = createItem(Material.RED_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // Slot 4: Cabecera Principal
        inv.setItem(4, createItem(Material.NETHER_STAR, "§c§l✦ ENCICLOPEDIA HEROICA DE RPGCORE ✦",
                Arrays.asList(
                        "§7Compendio integral con todas las clases, ascensiones,",
                        "§7habilidades de combate, estructuras 3D, mazmorras y",
                        "§7sistemas de progresión activa en el reino.",
                        "",
                        "§e▶ Haz clic en cualquier sección para explorar."
                )));

        // Slot 10: Clases Primarias & Atributos
        inv.setItem(10, createItem(Material.BEACON, "§6👑 Clases Heroicas & Atributos",
                Arrays.asList(
                        "§75 sendas de especialización (/class):",
                        "§8▪ §cGuerrero: §7Tanque y luchador cuerpo a cuerpo robusto.",
                        "§8▪ §5Mago: §7Maestro del daño arcano en área y maná.",
                        "§8▪ §bArquero: §7Francotirador de precisión a larga distancia.",
                        "§8▪ §8Asesino: §7Especialista en golpes críticos por la espalda.",
                        "§8▪ §ePaladín: §7Defensor de la luz sagrada con curación.",
                        "",
                        "§e✦ Atributos (/attributes): §7Fuerza, Agilidad, Inteligencia,",
                        "§7Vitalidad y Defensa. Ganas puntos al subir de nivel (1-100)."
                )));

        // Slot 12: Despertar de Clase & Ascensiones
        inv.setItem(12, createItem(Material.TOTEM_OF_UNDYING, "§d⚡ Clases Despertadas (Ascensiones)",
                Arrays.asList(
                        "§7Poder divino para héroes experimentados (/ascension):",
                        "§8▪ §c✦ Berserker de Sangre: §7Robo de vida y daño salvaje.",
                        "§8▪ §5✦ Archimago del Vacío: §7Singularidades gravitatorias.",
                        "§8▪ §b✦ Francotirador Celestial: §7Rayos estelares perforantes.",
                        "§8▪ §8✦ Sombra Asesina Fantasmal: §7Sigilo y letalidad oscura.",
                        "§8▪ §e✦ Paladín de la Luz Sagrada: §7Ceguera y escudos divinos.",
                        "§8▪ §b✦ Crono-Mago Temporal: §7Distorsión temporal y lentitud.",
                        "§8▪ §6✦ Caballero del Dragón: §7Fuego dracónico y escamas.",
                        "§8▪ §5✦ Nigromante de Almas: §7Cosecha de vida y almas.",
                        "§8▪ §e✦ Templario de la Aurora Solar: §6[NUEVO] §7Fuego sagrado.",
                        "§8▪ §b✦ Espectro de la Tormenta: §6[NUEVO] §7Velocidad y relámpago."
                )));

        // Slot 14: Habilidades de Combate & Combos
        inv.setItem(14, createItem(Material.NETHERITE_SWORD, "§c⚔️ Habilidades de Combate & Combos",
                Arrays.asList(
                        "§7Habilidades activas y pasivas con escalado (/skills):",
                        "§8▪ §cTorbellino de Espadas §7y §6Golpe Sísmico.",
                        "§8▪ §5Agujero Negro Cósmico §7y §eRelámpago en Cadena.",
                        "§8▪ §cLluvia de Flechas §7y §bDisparo Perforante.",
                        "§8▪ §8Danza de Sombras, §4Desgarro §7y §bDeslizamiento.",
                        "§8▪ §4Omnivampirismo: §7Robo de salud universal.",
                        "",
                        "§6✦ Motor de Combos: §7Combina clics y ataques telegrafiados."
                )));

        // Slot 16: Multiblocks RPG Desplegables
        inv.setItem(16, createItem(Material.RESPAWN_ANCHOR, "§d🏛️ Multiblocks RPG Desplegables",
                Arrays.asList(
                        "§7Estructuras físicas crafteables con PDC único:",
                        "§8▪ §6Yunque Legendario de los Titanes §7- Forja mítica.",
                        "§8▪ §dAltar de Ascensión de Clase §7- Despertar heroico.",
                        "§8▪ §5Estación de Mazmorras §7- Portales dimensionales.",
                        "§8▪ §6Forja de Joyería y Gemas Rúnicas §7- Orfebrería.",
                        "§8▪ §bObelisco Rúnico Ancestral §7- Bendición rúnica.",
                        "§8▪ §6Forja Rúnica del Titán Herrero §6[NUEVO] §7- Reparación legendaria.",
                        "",
                        "§d✦ Zero-Dupe Garantizado: §7Romper cualquier bloque desmonta",
                        "§7al 100% la estructura y reembolsa exactamente el ítem original."
                )));

        // Slot 28: Forja de Gemas, Joyas & Reliquias
        inv.setItem(28, createItem(Material.SMITHING_TABLE, "§6💎 Orfebrería, Gemas & Reliquias",
                Arrays.asList(
                        "§7Mejora tu equipamiento al límite (/gemsocket):",
                        "§8▪ §cRubí de Furia: §7Aumento de daño porcentual.",
                        "§8▪ §9Zafiro Arcano: §7Bonificación de maná y daño mágico.",
                        "§8▪ §aEsmeralda Vampírica: §7Sifón de vitalidad.",
                        "§8▪ §eTopacio de Bastión: §7Resistencia y armadura.",
                        "§8▪ §6Artefactos Míticos: §7Mjölnir, Corazón de Fénix y más."
                )));

        // Slot 30: Mazmorras, Jefes Mundiales & Torre
        inv.setItem(30, createItem(Material.OBSIDIAN, "§5🏰 Mazmorras & Jefes de Incursión",
                Arrays.asList(
                        "§7Desafíos heroicos de alto nivel (/dungeon):",
                        "§8▪ §5Cripta Abisal: §7Mazmorra por salas con jefes.",
                        "§8▪ §dTorre Infinita del Abismo: §7Pisos con escalado infinito.",
                        "§8▪ §cDragón Infernal: §7Raid Boss mundial de 5,000 HP.",
                        "§8▪ §5Soberano del Vacío Xylar: §7Jefe cósmico de 3 fases.",
                        "§8▪ §6Monstruos Élite: §7Afijos Ígneo, Vórtice y Electrificado."
                )));

        // Slot 32: Comandos Rápidos del Jugador
        inv.setItem(32, createItem(Material.WRITABLE_BOOK, "§f📜 Guía de Comandos Rápidos",
                Arrays.asList(
                        "§7Comandos disponibles para jugadores:",
                        "§8▪ §e/rpg §7- Menú general del sistema.",
                        "§8▪ §e/rpg guia §7o §e/rpg contenido §7- Abre esta guía.",
                        "§8▪ §e/rpg test §7- Laboratorio de pruebas completas.",
                        "§8▪ §e/class §7- Menú de selección de clases.",
                        "§8▪ §e/attributes §7- Asignación de puntos de atributo.",
                        "§8▪ §e/skills §7- Árbol de habilidades y combos.",
                        "§8▪ §e/talents §7- Árbol de talentos pasivos.",
                        "§8▪ §e/forge §7- Forja mítica de armamento."
                )));

        // Slot 34: Seguridad & Protección Total
        inv.setItem(34, createItem(Material.SHIELD, "§2🛡️ Seguridad de Inventario & Zero-Dupe",
                Arrays.asList(
                        "§7Blindaje del sistema RPG:",
                        "§8▪ §aCero duplicación de bloques constitutivos.",
                        "§8▪ §aReembolso exacto del ítem crafteable original.",
                        "§8▪ §aProtección anti-robo y anti-arrastre en todas las GUIs."
                )));

        // Slot 40: Botón de Acceso al Laboratorio de Test
        inv.setItem(40, createItem(Material.BREWING_STAND, "§a§l🧪 ABRIR LABORATORIO DE PRUEBAS 🧪",
                Arrays.asList(
                        "§7Abre el panel maestro de 1-clic de RPGCore:",
                        "§7despierta clases al instante, spawnea jefes mundiales,",
                        "§7recibe los 5 multiblocks y prueba todo el arsenal.",
                        "",
                        "§a▶ Haz clic para abrir el Laboratorio de Test"
                )));

        // Slot 49: Botón de Cerrar
        inv.setItem(49, createItem(Material.BARRIER, "§c§l✖ Cerrar Compendio",
                Arrays.asList("§7Haz clic para salir del menú.")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1.2f);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createItem(Material material, String name) {
        return createItem(material, name, null);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuideHolder)) return;

        event.setCancelled(true); // Complete anti-theft GUI protection

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        if (slot >= 54) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (slot == 40) {
            player.closeInventory();
            new RPGTestLabGUI(plugin).open(player);
        } else if (slot == 49) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1f, 1f);
        } else {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.2f);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GuideHolder) {
            event.setCancelled(true);
        }
    }
}
