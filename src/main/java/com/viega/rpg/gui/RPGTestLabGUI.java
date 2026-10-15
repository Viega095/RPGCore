package com.viega.rpg.gui;

import com.viega.rpg.RPGCore;
import com.viega.rpg.managers.*;
import com.viega.rpg.models.PlayerData;
import com.viega.rpg.models.RPGClass;
import com.viega.rpg.multiblock.RpgMultiblockType;
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

import java.util.Arrays;
import java.util.List;

public class RPGTestLabGUI implements Listener {

    public static class LabHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final RPGCore plugin;

    public RPGTestLabGUI(RPGCore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new LabHolder(), 54, "§8🧪 §cLaboratorio de Pruebas: RPGCore §8🧪");

        // Paneles de borde
        ItemStack border = createPane(Material.RED_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // Título central superior
        inv.setItem(4, createBtn(Material.NETHER_STAR, "§c§l✦ PANEL MAESTRO DE PRUEBAS DE RPG ✦",
                Arrays.asList(
                        "§7Entorno integral de verificación en 1 clic.",
                        "§7Prueba estructuras, habilidades, mazmorras y atributos.",
                        "",
                        "§e▶ Haz clic en cualquier botón para ejecutar."
                )));

        // --- FILA 1: ÍTEMS DE ESTRUCTURAS CON PDC & PROGRESIONES MAESTRAS ---
        // Slot 10: Dar Yunque Legendario (PDC)
        inv.setItem(10, createBtn(Material.ANVIL, "§6§l✦ OBTENER: Yunque Legendario (PDC) ✦",
                Arrays.asList("§7Entrega 1 bloque crafteable con PDC.", "§7Colócalo en el mundo para construir.", "", "§6▶ Clic para recibir")));

        // Slot 11: Dar Altar de Ascensión (PDC)
        inv.setItem(11, createBtn(Material.RESPAWN_ANCHOR, "§d§l✦ OBTENER: Altar de Ascensión (PDC) ✦",
                Arrays.asList("§7Entrega 1 bloque crafteable con PDC.", "§7Colócalo en el mundo para construir.", "", "§d▶ Clic para recibir")));

        // Slot 12: Dar Estación de Mazmorras (PDC)
        inv.setItem(12, createBtn(Material.LODESTONE, "§5§l✦ OBTENER: Estación de Mazmorras (PDC) ✦",
                Arrays.asList("§7Entrega 1 bloque crafteable con PDC.", "§7Colócalo en el mundo para construir.", "", "§5▶ Clic para recibir")));

        // Slot 13: Desbloquear y Maximizar Habilidades
        inv.setItem(13, createBtn(Material.NETHERITE_SWORD, "§c§l✦ DESBLOQUEAR TODAS LAS HABILIDADES ✦",
                Arrays.asList("§7Desbloquea y sube a Nivel 5 todas las", "§7habilidades de combate (las 8 nuevas incluidas).", "", "§c▶ Clic para desbloquear")));

        // Slot 14: Nivel Máximo 100 y 100 Puntos de Atributo
        inv.setItem(14, createBtn(Material.EXPERIENCE_BOTTLE, "§a§l✦ NIVEL 100 & 100 ATRIBUTOS ✦",
                Arrays.asList("§7Establece tu nivel RPG en 100 y otorga", "§7100 puntos de atributos al instante.", "", "§a▶ Clic para maximizar")));

        // Slot 15: Despertar de Clase Instantáneo
        inv.setItem(15, createBtn(Material.TOTEM_OF_UNDYING, "§6§l✦ DESPERTAR DE CLASE INMEDIATO ✦",
                Arrays.asList("§7Aplica la ascensión legendaria correspondiente", "§7a tu clase activa de inmediato.", "", "§6▶ Clic para despertar")));

        // Slot 16: Paquete de Gemas Arcanas
        inv.setItem(16, createBtn(Material.EMERALD, "§a§l✦ PAQUETE DE GEMAS ARCANAS ✦",
                Arrays.asList("§7Entrega gemas (Rubí, Zafiro, Esmeralda,", "§7Topacio, Amatista) a tu inventario/bolsa.", "", "§a▶ Clic para recibir")));

        // Slot 17: Dar Forja de Joyería Rúnica (PDC) [NUEVO]
        inv.setItem(17, createBtn(Material.SMITHING_TABLE, "§6§l✦ OBTENER: Forja de Joyería (PDC) [NUEVO] ✦",
                Arrays.asList("§7Entrega 1 bloque crafteable con PDC.", "§7Colócalo en el mundo para construir.", "", "§6▶ Clic para recibir")));

        // --- FILA 2: DESPLIEGUE DIRECTO EN MUNDO & SISTEMAS CLAVE ---
        // Slot 19: Spawnear Yunque Legendario en Mundo
        inv.setItem(19, createBtn(Material.SMITHING_TABLE, "§6🔨 Spawnear Yunque Legendario en Mundo",
                Arrays.asList("§7Autoconstruye la estructura completa", "§7en tus coordenadas actuales.", "", "§6▶ Clic para desplegar")));

        // Slot 20: Spawnear Altar de Ascensión en Mundo
        inv.setItem(20, createBtn(Material.ENCHANTING_TABLE, "§d✨ Spawnear Altar de Ascensión en Mundo",
                Arrays.asList("§7Autoconstruye el círculo ritual de amatista", "§7en tus coordenadas actuales.", "", "§d▶ Clic para desplegar")));

        // Slot 21: Spawnear Estación de Mazmorras en Mundo
        inv.setItem(21, createBtn(Material.CRYING_OBSIDIAN, "§5🌀 Spawnear Estación de Mazmorras en Mundo",
                Arrays.asList("§7Autoconstruye el portal de magnetita", "§7en tus coordenadas actuales.", "", "§5▶ Clic para desplegar")));

        // Slot 22: Espada Vampírica Legendaria
        inv.setItem(22, createBtn(Material.DIAMOND_SWORD, "§4⚔️ Dar Espada Vampírica Legendaria",
                Arrays.asList("§7Recibe la hoja relicaria con la", "§7habilidad pasiva de Omnivampirismo.", "", "§4▶ Clic para recibir")));

        // Slot 23: Asignación de Atributos
        inv.setItem(23, createBtn(Material.GOLDEN_APPLE, "§e📊 Menú de Asignación de Atributos",
                Arrays.asList("§7Distribuye puntos en Fuerza, Agilidad,", "§7Inteligencia, Vitalidad y Defensa.", "", "§e▶ Clic para abrir")));

        // Slot 24: Árbol de Talentos de Clase
        inv.setItem(24, createBtn(Material.OAK_SAPLING, "§a🌳 Árbol de Talentos de Clase",
                Arrays.asList("§7Selecciona tu especialización de combate", "§7y bonificaciones pasivas.", "", "§a▶ Clic para abrir")));

        // Slot 25: Selección de Clases
        inv.setItem(25, createBtn(Material.BEACON, "§b👑 Menú de Selección de Clases",
                Arrays.asList("§7Cambia tu clase entre Guerrero, Mago,", "§7Arquero, Asesino o Paladín.", "", "§b▶ Clic para abrir")));

        // Slot 26: Spawnear Forja de Joyería en Mundo [NUEVO]
        inv.setItem(26, createBtn(Material.GOLD_BLOCK, "§6💎 Spawnear Forja de Joyería en Mundo [NUEVO]",
                Arrays.asList("§7Autoconstruye la orfebrería completa", "§7en tus coordenadas actuales.", "", "§6▶ Clic para desplegar")));

        // --- FILA 3: MAZMORRAS, JEFES Y CONTRATOS ---
        // Slot 28: Mazmorra Cripta Abisal
        inv.setItem(28, createBtn(Material.OBSIDIAN, "§5🏰 Iniciar Mazmorra: Cripta Abisal",
                Arrays.asList("§7Inicia una mazmorra instanciada con", "§7oleadas y jefes abisales.", "", "§5▶ Clic para entrar")));

        // Slot 29: Torre Infinita del Abismo
        inv.setItem(29, createBtn(Material.DARK_PRISMARINE, "§d🗼 Desafiar Torre Infinita del Abismo",
                Arrays.asList("§7Inicia la torre con escalado infinito", "§7y recompensas de piso.", "", "§d▶ Clic para desafiar")));

        // Slot 30: Raid Boss Dragón Infernal
        inv.setItem(30, createBtn(Material.DRAGON_HEAD, "§c🐉 Invocar Dragón Infernal (Raid Boss)",
                Arrays.asList("§7Invoca al jefe mundial de 5,000 HP", "§7con ataques telegrafiados.", "", "§c▶ Clic para invocar")));

        // Slot 31: Soberano del Vacío Xylar
        inv.setItem(31, createBtn(Material.WITHER_SKELETON_SKULL, "§5🐉 Invocar Soberano del Vacío Xylar",
                Arrays.asList("§7Invoca al jefe cósmico de 3 fases.", "", "§5▶ Clic para invocar")));

        // Slot 32: Spawn Mobs Élites
        inv.setItem(32, createBtn(Material.MAGMA_CREAM, "§6🔥 Spawnear Mobs Élites con Afijos",
                Arrays.asList("§7Invoca monstruos élite con afijos", "§7Ígneo, Vórtice y Electrificado.", "", "§6▶ Clic para invocar")));

        // Slot 33: Tablón Dinámico de Contratos
        inv.setItem(33, createBtn(Material.WRITABLE_BOOK, "§c📜 Tablón Dinámico de Contratos",
                Arrays.asList("§7Acepta contratos rotativos de rango", "§7B, A, S y Mítico.", "", "§c▶ Clic para abrir")));

        // Slot 34: Invocar Cuervo Místico
        inv.setItem(34, createBtn(Material.FEATHER, "§8🦅 Invocar Cuervo Místico de Misiones",
                Arrays.asList("§7Spawnea al emisario místico para misiones.", "", "§8▶ Clic para invocar")));

        // Slot 35: Kit Gemas Primordiales [NUEVO]
        inv.setItem(35, createBtn(Material.NETHER_STAR, "§e💎 Dar Kit Gemas Primordiales (x5 c/u) [NUEVO]",
                Arrays.asList("§7Rubí de Furia, Zafiro Arcano y", "§7Esmeralda Vampírica abisal.", "", "§e▶ Clic para recibir")));

        // --- FILA 4: MECÁNICAS AVANZADAS & LIMPIEZA ---
        // Slot 37: Engarce & Fusión de Gemas
        inv.setItem(37, createBtn(Material.AMETHYST_SHARD, "§d💎 Engarce & Fusión de Gemas",
                Arrays.asList("§7Abre la estación para incrustar o", "§7combinar gemas arcanas.", "", "§d▶ Clic para abrir")));

        // Slot 38: Compañero de Combate
        inv.setItem(38, createBtn(Material.BONE, "§6🐺 Invocar Compañero de Combate (Pet)",
                Arrays.asList("§7Abre la interfaz de gestión de mascotas.", "", "§6▶ Clic para abrir")));

        // Slot 39: Reliquias y Artefactos
        inv.setItem(39, createBtn(Material.GOLDEN_HORSE_ARMOR, "§6🏺 Panel de Reliquias y Artefactos",
                Arrays.asList("§7Equipa artefactos legendarios (Mjölnir,", "§7Corazón de Fénix, etc.).", "", "§6▶ Clic para abrir")));

        // Slot 40: Asedios de Fortaleza
        inv.setItem(40, createBtn(Material.SHIELD, "§6🏰 Asedios de Fortaleza de Clanes",
                Arrays.asList("§7Abre la disputa de territorios gremiales.", "", "§6▶ Clic para abrir")));

        // Slot 41: Constelaciones Astrales
        inv.setItem(41, createBtn(Material.ECHO_SHARD, "§d🌌 Árbol de Constelaciones Astrales",
                Arrays.asList("§7Abre la asignación de estrellas cósmicas.", "", "§d▶ Clic para abrir")));

        // Slot 42: Recarga en Caliente (Hot-Reload)
        inv.setItem(42, createBtn(Material.REDSTONE_TORCH, "§c⚡ Recarga en Caliente (Hot-Reload)",
                Arrays.asList("§7Recarga configs y datos en <50ms.", "", "§c▶ Clic para recargar")));

        // Slot 43: Desmontar Estructuras Cercanas (Limpieza de pruebas)
        inv.setItem(43, createBtn(Material.BARRIER, "§c🧹 Desmontar Estructuras Cercanas (Radio 15)",
                Arrays.asList("§7Desmonta y reembolsa todas las estructuras", "§7desplegables en un radio de 15 bloques.", "", "§c▶ Clic para limpiar")));

        // Slot 44: Despertar Sagrado / Crono [NUEVO]
        inv.setItem(44, createBtn(Material.CLOCK, "§b⚡ Despertar Sagrado / Crono [NUEVO]",
                Arrays.asList("§7Despierta al instante como Paladín de la Luz", "§7o Crono-Mago Temporal según tu clase.", "", "§b▶ Clic para despertar")));

        // --- FILA 6: NUEVA EXPANSIÓN (OBELISCO, DESPERTARES, GUÍA) ---
        // Slot 46: Dar Obelisco Rúnico Ancestral (PDC) [NUEVO]
        inv.setItem(46, createBtn(Material.CRYING_OBSIDIAN, "§b§l✦ OBTENER: Obelisco Rúnico (PDC) [NUEVO] ✦",
                Arrays.asList("§7Entrega 1 bloque crafteable con PDC.", "§7Colócalo en el mundo para construir.", "", "§b▶ Clic para recibir")));

        // Slot 47: Despertar Caballero del Dragón [NUEVO]
        inv.setItem(47, createBtn(Material.FIRE_CHARGE, "§6🐉 Despertar Caballero del Dragón [NUEVO]",
                Arrays.asList("§7Despierta al instante como Caballero del Dragón", "§7con fuego explosivo y escamas ancestrales.", "", "§6▶ Clic para despertar")));

        // Slot 48: Despertar Nigromante de Almas [NUEVO]
        inv.setItem(48, createBtn(Material.SOUL_LANTERN, "§5💀 Despertar Nigromante de Almas [NUEVO]",
                Arrays.asList("§7Despierta al instante como Nigromante de Almas", "§7con robo vampírico y ráfagas espectrales.", "", "§5▶ Clic para despertar")));

        // Slot 50: Guía y Compendio de Contenido RPG [NUEVO]
        inv.setItem(50, createBtn(Material.BOOK, "§6📖 Guía y Compendio de Contenido RPG [NUEVO]",
                Arrays.asList("§7Abre la enciclopedia de clases, despertar,", "§7estructuras 3D, mazmorras y gemas.", "", "§e▶ Clic para abrir el compendio")));

        // Slot 52: Spawnear Obelisco Rúnico directo en mundo [NUEVO]
        inv.setItem(52, createBtn(Material.PURPLE_GLAZED_TERRACOTTA, "§b🏛️ Spawnear Obelisco Rúnico en Mundo [NUEVO]",
                Arrays.asList("§7Autoconstruye el monolito rúnico", "§7en tus coordenadas actuales.", "", "§b▶ Clic para desplegar")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.2f);
    }

    private ItemStack createPane(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBtn(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof LabHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        switch (slot) {
            case 10: { // Dar Yunque Legendario (PDC)
                ItemStack item = plugin.getRpgMultiblockManager().createStructureItem(RpgMultiblockType.LEGENDARY_ANVIL);
                player.getInventory().addItem(item);
                player.sendMessage("§a[Lab] ✔ ¡Has recibido 1x " + RpgMultiblockType.LEGENDARY_ANVIL.getDisplayName() + " §acon metadatos PDC!");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;
            }

            case 11: { // Dar Altar de Ascensión (PDC)
                ItemStack item = plugin.getRpgMultiblockManager().createStructureItem(RpgMultiblockType.CLASS_ASCENSION_ALTAR);
                player.getInventory().addItem(item);
                player.sendMessage("§a[Lab] ✔ ¡Has recibido 1x " + RpgMultiblockType.CLASS_ASCENSION_ALTAR.getDisplayName() + " §acon metadatos PDC!");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;
            }

            case 12: { // Dar Estación de Mazmorras (PDC)
                ItemStack item = plugin.getRpgMultiblockManager().createStructureItem(RpgMultiblockType.DUNGEON_STATION);
                player.getInventory().addItem(item);
                player.sendMessage("§a[Lab] ✔ ¡Has recibido 1x " + RpgMultiblockType.DUNGEON_STATION.getDisplayName() + " §acon metadatos PDC!");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;
            }

            case 13: { // Desbloquear todas las habilidades a nivel 5
                SkillManager sm = plugin.getManagerHandler().get(SkillManager.class);
                if (sm != null) {
                    sm.unlockAllSkillsForPlayer(player.getUniqueId(), 5);
                    player.sendMessage("§a[Lab] ✔ ¡Todas las 12 habilidades de clase (incluidas las 8 nuevas) han sido desbloqueadas a Nivel 5!");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.5f);
                }
                break;
            }

            case 14: { // Nivel 100 y 100 Puntos de Atributo
                LevelManager lm = plugin.getManagerHandler().get(LevelManager.class);
                AttributePointManager apm = plugin.getManagerHandler().get(AttributePointManager.class);
                if (lm != null) {
                    lm.setLevel(player, 100);
                }
                if (apm != null) {
                    apm.addPoints(player.getUniqueId(), 100);
                }
                player.sendMessage("§a[Lab] ✔ ¡Establecido a Nivel 100 y otorgados +100 Puntos de Atributo!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                break;
            }

            case 15: { // Despertar de Clase Instantáneo
                ClassAscensionManager cam = plugin.getClassAscensionManager();
                PlayerManager pm = plugin.getManagerHandler().get(PlayerManager.class);
                if (cam != null && pm != null) {
                    PlayerData data = pm.getData(player.getUniqueId());
                    RPGClass rc = (data != null && data.getRpgClass() != null) ? data.getRpgClass() : RPGClass.WARRIOR;
                    ClassAscensionManager.AwakenedClass target = switch (rc) {
                        case MAGE -> ClassAscensionManager.AwakenedClass.VOID_ARCHMAGE;
                        case ARCHER -> ClassAscensionManager.AwakenedClass.CELESTIAL_SNIPER;
                        case ASSASSIN -> ClassAscensionManager.AwakenedClass.SHADOW_REAPER;
                        default -> ClassAscensionManager.AwakenedClass.BLOOD_BERSERKER;
                    };
                    cam.forceAscension(player, target);
                }
                break;
            }

            case 16: { // Paquete de Gemas Arcanas
                GemManager gm = plugin.getManagerHandler().get(GemManager.class);
                if (gm != null) {
                    String[] gems = { "ruby_gem", "sapphire_gem", "emerald_gem", "topaz_gem", "amethyst_gem" };
                    for (String g : gems) {
                        ItemStack item = gm.createGemItem(g);
                        if (item != null) player.getInventory().addItem(item);
                    }
                    player.sendMessage("§a[Lab] ✔ ¡Paquete de 5 Gemas Arcanas entregado a tu inventario!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.4f);
                }
                break;
            }

            case 17: { // Dar Forja de Joyería Rúnica (PDC) [NUEVO]
                ItemStack item = plugin.getRpgMultiblockManager().createStructureItem(RpgMultiblockType.RUNIC_JEWELRY_FORGE);
                player.getInventory().addItem(item);
                player.sendMessage("§a[Lab] ✔ ¡Has recibido 1x " + RpgMultiblockType.RUNIC_JEWELRY_FORGE.getDisplayName() + " §acon metadatos PDC!");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;
            }

            case 19: { // Spawnear Yunque Legendario directo en mundo
                player.closeInventory();
                plugin.getRpgMultiblockManager().placeMultiblock(player, player.getLocation().getBlock().getLocation(), RpgMultiblockType.LEGENDARY_ANVIL);
                break;
            }

            case 20: { // Spawnear Altar de Ascensión directo en mundo
                player.closeInventory();
                plugin.getRpgMultiblockManager().placeMultiblock(player, player.getLocation().getBlock().getLocation(), RpgMultiblockType.CLASS_ASCENSION_ALTAR);
                break;
            }

            case 21: { // Spawnear Estación de Mazmorras directo en mundo
                player.closeInventory();
                plugin.getRpgMultiblockManager().placeMultiblock(player, player.getLocation().getBlock().getLocation(), RpgMultiblockType.DUNGEON_STATION);
                break;
            }

            case 22: { // Dar Espada Vampírica
                player.closeInventory();
                player.performCommand("rpg item vampiric_blade");
                break;
            }

            case 23: { // Asignación de Atributos
                player.closeInventory();
                player.performCommand("stats");
                break;
            }

            case 24: { // Árbol de Talentos
                player.closeInventory();
                player.performCommand("talents");
                break;
            }

            case 25: { // Selección de Clases
                player.closeInventory();
                player.performCommand("class");
                break;
            }

            case 26: { // Spawnear Forja de Joyería directo en mundo [NUEVO]
                player.closeInventory();
                plugin.getRpgMultiblockManager().placeMultiblock(player, player.getLocation().getBlock().getLocation(), RpgMultiblockType.RUNIC_JEWELRY_FORGE);
                break;
            }

            case 28: { // Iniciar Mazmorra Cripta Abisal
                player.closeInventory();
                player.performCommand("dungeon start");
                break;
            }

            case 29: { // Torre del Abismo
                player.closeInventory();
                player.performCommand("abyss start");
                break;
            }

            case 30: { // Dragón Infernal Raid Boss
                player.closeInventory();
                plugin.getRaidBossEngine().spawnWorldBoss(player.getLocation(), "INFERNAL_DRAGON", 5000.0);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡Raid Boss Dragón Infernal invocado!");
                break;
            }

            case 31: { // Soberano del Vacío Xylar
                player.closeInventory();
                if (plugin.getMythicWorldRaidEngine() != null) {
                    plugin.getMythicWorldRaidEngine().spawnVoidSovereign(player.getLocation());
                }
                break;
            }

            case 32: { // Spawn Mobs Élites
                player.closeInventory();
                plugin.getEliteMobAffixEngine().spawnEliteMob(player.getLocation(), "zombie_lvl1", EliteMobAffixEngine.Affix.MOLTEN);
                plugin.getEliteMobAffixEngine().spawnEliteMob(player.getLocation(), "skeleton_lvl5", EliteMobAffixEngine.Affix.VORTEX);
                plugin.getEliteMobAffixEngine().spawnEliteMob(player.getLocation(), "zombie_lvl1", EliteMobAffixEngine.Affix.ELECTRIFIED);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡3 Mobs Élites invocados!");
                break;
            }

            case 33: { // Tablón de Contratos
                player.closeInventory();
                if (plugin.getDynamicQuestBoard() != null) {
                    plugin.getDynamicQuestBoard().openBoardGUI(player);
                }
                break;
            }

            case 34: { // Cuervo Místico
                player.closeInventory();
                player.performCommand("crow");
                break;
            }

            case 35: { // Kit Gemas Primordiales [NUEVO]
                GemManager gemMan = plugin.getManagerHandler().get(GemManager.class);
                if (gemMan != null) {
                    String[] primordialGems = { "ruby_fury", "arcane_sapphire", "vampiric_emerald" };
                    for (String g : primordialGems) {
                        ItemStack item = gemMan.createGemItem(g);
                        if (item != null) {
                            item.setAmount(5);
                            player.getInventory().addItem(item);
                        }
                    }
                    player.sendMessage("§a[Lab] ✔ ¡Kit de Gemas Primordiales (Rubí Furia, Zafiro Arcano, Esmeralda Vampírica x5 c/u) entregado!");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.6f);
                }
                break;
            }

            case 37: { // Engarce & Fusión de Gemas
                player.closeInventory();
                player.performCommand("gemsocket");
                break;
            }

            case 38: { // Pet Management
                player.closeInventory();
                player.performCommand("pet");
                break;
            }

            case 39: { // Artefactos
                player.closeInventory();
                player.performCommand("artifacts");
                break;
            }

            case 40: { // Fortalezas de Gremio
                player.closeInventory();
                if (plugin.getGuildStrongholdSiegeManager() != null) {
                    plugin.getGuildStrongholdSiegeManager().openStrongholdGUI(player);
                }
                break;
            }

            case 41: { // Constelaciones Astrales
                player.closeInventory();
                if (plugin.getAstralConstellationEngine() != null) {
                    plugin.getAstralConstellationEngine().openConstellationGUI(player);
                }
                break;
            }

            case 42: { // Recarga en caliente
                player.closeInventory();
                plugin.getUpdateManager().performLiveReload(player);
                break;
            }

            case 43: { // Limpiar estructuras cercanas
                int count = plugin.getRpgMultiblockManager().dismantleAllNearby(player.getLocation(), 15, player);
                player.sendMessage("§a[Lab] 🧹 ¡Se desmontaron " + count + " estructuras en un radio de 15 bloques!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
                break;
            }

            case 44: { // Despertar Sagrado / Crono [NUEVO]
                ClassAscensionManager cam = plugin.getClassAscensionManager();
                PlayerManager pm = plugin.getManagerHandler().get(PlayerManager.class);
                if (cam != null && pm != null) {
                    PlayerData data = pm.getData(player.getUniqueId());
                    RPGClass rc = (data != null && data.getRpgClass() != null) ? data.getRpgClass() : RPGClass.WARRIOR;
                    ClassAscensionManager.AwakenedClass target = (rc == RPGClass.MAGE)
                            ? ClassAscensionManager.AwakenedClass.CHRONO_ARCHMAGE
                            : ClassAscensionManager.AwakenedClass.HOLY_PALADIN;
                    cam.forceAscension(player, target);
                }
                break;
            }

            case 46: { // Dar Obelisco Rúnico Ancestral (PDC)
                ItemStack item = plugin.getRpgMultiblockManager().createStructureItem(RpgMultiblockType.ANCIENT_RUNIC_OBELISK);
                player.getInventory().addItem(item);
                player.sendMessage("§a[Lab] ✔ ¡Has recibido 1x " + RpgMultiblockType.ANCIENT_RUNIC_OBELISK.getDisplayName() + " §acon metadatos PDC!");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;
            }

            case 47: { // Despertar Caballero del Dragón
                ClassAscensionManager cam = plugin.getClassAscensionManager();
                if (cam != null) {
                    cam.forceAscension(player, ClassAscensionManager.AwakenedClass.DRAGON_KNIGHT);
                }
                break;
            }

            case 48: { // Despertar Nigromante de Almas
                ClassAscensionManager cam = plugin.getClassAscensionManager();
                if (cam != null) {
                    cam.forceAscension(player, ClassAscensionManager.AwakenedClass.SOUL_NECROMANCER);
                }
                break;
            }

            case 50: { // Abrir Guía y Compendio RPG
                player.closeInventory();
                new RPGContentGuideGUI(plugin).open(player);
                break;
            }

            case 52: { // Spawnear Obelisco Rúnico en Mundo
                player.closeInventory();
                plugin.getRpgMultiblockManager().placeMultiblock(player, player.getLocation().getBlock().getLocation(), RpgMultiblockType.ANCIENT_RUNIC_OBELISK);
                break;
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof LabHolder) {
            event.setCancelled(true);
        }
    }
}
