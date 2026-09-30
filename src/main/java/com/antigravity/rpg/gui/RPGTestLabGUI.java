package com.antigravity.rpg.gui;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.EliteMobAffixEngine;
import com.antigravity.rpg.managers.LevelManager;
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

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, createPane(Material.RED_STAINED_GLASS_PANE));
            }
        }

        // Slot 10: Altar de Ascensión de Clase
        inv.setItem(10, createBtn(Material.NETHER_STAR, "§6⚡ Altar de Ascensión & Despertar de Clase",
                Arrays.asList("§7Abre el altar para despertar a", "§7Berserker, Archimago, Francotirador...", "", "§6▶ Haz clic para abrir")));

        // Slot 11: Invocar Dragón Infernal (World Boss)
        inv.setItem(11, createBtn(Material.DRAGON_HEAD, "§c🐉 Invocar Dragón Infernal (Raid Boss)",
                Arrays.asList("§7Spawnea al jefe mundial de incursión", "§7con telegrafiado de ataques y 5,000 HP.", "", "§c▶ Haz clic para invocar")));

        // Slot 12: Spawn Élite Ígneo (Molten)
        inv.setItem(12, createBtn(Material.MAGMA_CREAM, "§6🔥 Spawnear Zombie Élite Ígneo (Molten)",
                Arrays.asList("§7Spawnea un monstruo élite que deja", "§7un rastro de fuego abrasador.", "", "§6▶ Haz clic para spawnear")));

        // Slot 13: Spawn Élite Vórtice (Vortex)
        inv.setItem(13, createBtn(Material.ENDER_PEARL, "§5🌀 Spawnear Esqueleto Élite Vórtice",
                Arrays.asList("§7Spawnea un monstruo élite que atrae", "§7a todos los jugadores cercanos.", "", "§5▶ Haz clic para spawnear")));

        // Slot 14: Spawn Élite Electrificado
        inv.setItem(14, createBtn(Material.LIGHTNING_ROD, "§b⚡ Spawnear Mob Élite Electrificado",
                Arrays.asList("§7Spawnea un monstruo élite que lanza", "§7rayos cósmicos sobre sus agresores.", "", "§b▶ Haz clic para spawnear")));

        // Slot 15: Entrar a Mazmorra Cripta Abisal
        inv.setItem(15, createBtn(Material.CRYING_OBSIDIAN, "§5🏰 Entrar a Mazmorra: Cripta Abisal",
                Arrays.asList("§7Inicia una mazmorra instanciada con", "§7anillo de partículas y oleadas de mobs.", "", "§5▶ Haz clic para entrar")));

        // Slot 16: Desafiar Torre Infinita del Abismo
        inv.setItem(16, createBtn(Material.RESPAWN_ANCHOR, "§d🗼 Desafiar Torre Infinita del Abismo",
                Arrays.asList("§7Desafía la torre con escalado infinito", "§7y jefes de piso cada 5 niveles.", "", "§d▶ Haz clic para desafiar")));

        // Slot 19: Forja de Progresión de Clases
        inv.setItem(19, createBtn(Material.ANVIL, "§e🛡️ Forja de Progresión de Clases Tiers 1-5",
                Arrays.asList("§7Abre la estación para forjar armamento", "§7avanzado para cada clase RPG.", "", "§e▶ Haz clic para abrir")));

        // Slot 20: Invocar Cuervo Místico
        inv.setItem(20, createBtn(Material.FEATHER, "§8🦅 Invocar Cuervo Místico de Misiones",
                Arrays.asList("§7Spawnea al cuervo emisario para", "§7recibir misiones y contratos abisales.", "", "§8▶ Haz clic para invocar")));

        // Slot 21: Compañero de Combate (Pet)
        inv.setItem(21, createBtn(Material.BONE, "§6🐺 Invocar Compañero de Combate (Pet)",
                Arrays.asList("§7Invoca a tu familiar espiritual.", "§e(Shift + Clic para abrir menú de estadísticas)", "", "§6▶ Haz clic para invocar")));

        // Slot 22: Asignación de Atributos
        inv.setItem(22, createBtn(Material.GOLDEN_APPLE, "§a📊 Asignación de Puntos de Atributo",
                Arrays.asList("§7Distribuye puntos en Fuerza, Destreza,", "§7Inteligencia, Vitalidad y Defensa.", "", "§a▶ Haz clic para abrir")));

        // Slot 23: Altar de Infusión de Reliquias
        inv.setItem(23, createBtn(Material.ENCHANTING_TABLE, "§9🔮 Altar de Infusión de Reliquias",
                Arrays.asList("§7Imbuye tus armas con poderes sagrados,", "§7ígneos o arcanos combinando items.", "", "§9▶ Haz clic para abrir")));

        // Slot 24: Engarce y Fusión de Gemas
        inv.setItem(24, createBtn(Material.AMETHYST_SHARD, "§d💎 Engarce & Fusión de Gemas",
                Arrays.asList("§7Incrusta gemas arcanas en ranuras", "§7o fusiona 3 gemas en un rango superior.", "", "§d▶ Haz clic para abrir")));

        // Slot 25: Árbol de Talentos
        inv.setItem(25, createBtn(Material.OAK_SAPLING, "§a🌳 Árbol de Talentos de Clase",
                Arrays.asList("§7Desbloquea habilidades pasivas y", "§7especializaciones de combate.", "", "§a▶ Haz clic para abrir")));

        // Slot 28: Reliquias y Artefactos Míticos
        inv.setItem(28, createBtn(Material.TOTEM_OF_UNDYING, "§6🏺 Panel de Reliquias y Artefactos",
                Arrays.asList("§7Equipa el Corazón del Fénix, el", "§7Martillo Mjölnir o la Égida Sagrada.", "", "§6▶ Haz clic para abrir")));

        // Slot 29: Yunque de Reforja de Modificadores
        inv.setItem(29, createBtn(Material.SMITHING_TABLE, "§3🔨 Yunque de Reforja de Modificadores",
                Arrays.asList("§7Modifica atributos aleatorios y", "§7prefijos arcanos de tus armaduras.", "", "§3▶ Haz clic para abrir")));

        // Slot 30: Yunque de Mejora +1 a +10
        inv.setItem(30, createBtn(Material.IRON_BLOCK, "§7🛠️ Yunque de Mejora Blacksmith (+1 a +10)",
                Arrays.asList("§7Mejora armas y armaduras con", "§7piedras de forja y probabilidad de éxito.", "", "§7▶ Haz clic para abrir")));

        // Slot 31: Dar Espada Vampírica Legendaria
        inv.setItem(31, createBtn(Material.DIAMOND_SWORD, "§4⚔️ Dar Espada Vampírica Legendaria",
                Arrays.asList("§7Recibe la Vampiric Blade con efecto", "§7de Omnivampirismo y robo de vida.", "", "§4▶ Haz clic para recibir")));

        // Slot 32: Contrato de Asesino Renegado
        inv.setItem(32, createBtn(Material.WRITABLE_BOOK, "§c📜 Obtener Contrato de Caza y Recompensa",
                Arrays.asList("§7Acepta un contrato de mercenario para", "§7eliminar objetivos por oro y reliquias.", "", "§c▶ Haz clic para obtener")));

        // Slot 33: Añadir 10,000 XP de Clase
        inv.setItem(33, createBtn(Material.EXPERIENCE_BOTTLE, "§e📈 Añadir 10,000 XP de Clase",
                Arrays.asList("§7Otorga 10,000 puntos de experiencia", "§7para subir de nivel inmediatamente.", "", "§e▶ Haz clic para subir nivel")));

        // Slot 34: Selección de Clases
        inv.setItem(34, createBtn(Material.BEACON, "§b👑 Menú de Selección de Clases",
                Arrays.asList("§7Cambia tu clase a Guerrero, Mago,", "§7Arquero, Paladín o Asesino.", "", "§b▶ Haz clic para abrir")));

        // Slot 48: Auto-Update Check
        inv.setItem(48, createBtn(Material.EXPERIENCE_BOTTLE, "§a🔄 Probar Auto-Update en GitHub",
                Arrays.asList("§7Verifica nuevas versiones y commits en", "§7GitHub sin salir del juego.", "", "§a▶ Haz clic para verificar")));

        // Slot 50: Live Hot-Reload
        inv.setItem(50, createBtn(Material.REDSTONE_TORCH, "§c⚡ Recarga en Caliente (Hot-Reload)",
                Arrays.asList("§7Recarga configs, clases, talentos y mobs", "§7en <50ms sin reiniciar el servidor.", "", "§c▶ Haz clic para recargar")));

        // Center bottom
        inv.setItem(49, createBtn(Material.NETHER_STAR, "§c§l✦ PANEL MAESTRO DE PRUEBAS DE RPG",
                Arrays.asList("§7Haz clic en cualquier funcionalidad", "§7para disparar mecánicas y eventos al instante.")));

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
            case 10: // Altar Ascensión
                player.closeInventory();
                plugin.getClassAscensionManager().openAscensionGUI(player);
                break;

            case 11: // World Boss
                player.closeInventory();
                plugin.getRaidBossEngine().spawnWorldBoss(player.getLocation(), "INFERNAL_DRAGON", 5000.0);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡Raid Boss Dragón Infernal invocado!");
                break;

            case 12: // Elite Molten
                player.closeInventory();
                plugin.getEliteMobAffixEngine().spawnEliteMob(player.getLocation(), "zombie_lvl1", EliteMobAffixEngine.Affix.MOLTEN);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡Zombie Élite Ígneo invocado!");
                break;

            case 13: // Elite Vortex
                player.closeInventory();
                plugin.getEliteMobAffixEngine().spawnEliteMob(player.getLocation(), "skeleton_lvl5", EliteMobAffixEngine.Affix.VORTEX);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡Esqueleto Élite Vórtice invocado!");
                break;

            case 14: // Elite Electrified
                player.closeInventory();
                plugin.getEliteMobAffixEngine().spawnEliteMob(player.getLocation(), "zombie_lvl1", EliteMobAffixEngine.Affix.ELECTRIFIED);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡Mob Élite Electrificado invocado!");
                break;

            case 15: // Dungeon
                player.closeInventory();
                player.performCommand("dungeon start");
                break;

            case 16: // Abyss
                player.closeInventory();
                player.performCommand("abyss start");
                break;

            case 19: // Craft
                player.closeInventory();
                player.performCommand("craft");
                break;

            case 20: // Crow
                player.closeInventory();
                player.performCommand("crow");
                break;

            case 21: // Pet
                player.closeInventory();
                player.performCommand("pet");
                break;

            case 22: // Stats
                player.closeInventory();
                player.performCommand("stats");
                break;

            case 23: // Altar
                player.closeInventory();
                player.performCommand("altar");
                break;

            case 24: // GemSocket
                player.closeInventory();
                player.performCommand("gemsocket");
                break;

            case 25: // Talents
                player.closeInventory();
                player.performCommand("talents");
                break;

            case 28: // Artifacts
                player.closeInventory();
                player.performCommand("artifacts");
                break;

            case 29: // Reforge
                player.closeInventory();
                player.performCommand("reforge");
                break;

            case 30: // Forge
                player.closeInventory();
                player.performCommand("forge");
                break;

            case 31: // Vampiric Blade
                player.closeInventory();
                player.performCommand("rpg item vampiric_blade");
                break;

            case 32: // Contract
                player.closeInventory();
                player.performCommand("rpgcontract get");
                break;

            case 33: // XP
                LevelManager lm = plugin.getManagerHandler().get(LevelManager.class);
                if (lm != null) {
                    lm.addXp(player, 10000);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
                }
                break;

            case 34: // Class Selection
                player.closeInventory();
                player.performCommand("class");
                break;

            case 48: // Update
                player.closeInventory();
                plugin.getUpdateManager().checkUpdate(player, true);
                break;

            case 50: // Reload
                player.closeInventory();
                plugin.getUpdateManager().performLiveReload(player);
                break;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof LabHolder) {
            event.setCancelled(true);
        }
    }
}
