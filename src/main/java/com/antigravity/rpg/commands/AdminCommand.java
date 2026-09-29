package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.*;
import com.antigravity.rpg.models.RPGMob;
import com.antigravity.rpg.models.RPGClass;
import com.antigravity.rpg.utils.MessageUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdminCommand implements CommandExecutor {

    private final RPGCore core;
    private final LevelManager levelManager;
    private final ItemManager itemManager;
    private final MobManager mobManager;

    public AdminCommand(RPGCore core, LevelManager levelManager, ItemManager itemManager, MobManager mobManager) {
        this.core = core;
        this.levelManager = levelManager;
        this.itemManager = itemManager;
        this.mobManager = mobManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("guide") || args[0].equalsIgnoreCase("help")) {
            if (sender instanceof Player player) {
                sendInteractiveGuide(player);
            } else {
                sender.sendMessage("Uso: /rpg <addxp|item|spawnmob|spawnboss|guide>");
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("class")) {
            if (sender instanceof Player player) {
                if (args.length >= 2) {
                    try {
                        RPGClass chosen = RPGClass.valueOf(args[1].toUpperCase());
                        core.getClassManager().selectClass(player.getUniqueId(), chosen);
                        player.sendMessage(MessageUtils.color("&a¡Has seleccionado la clase &e" + chosen.name() + "&a!"));
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(MessageUtils.color("&cClase inválida. Opciones: WARRIOR, MAGE, ARCHER, ROGUE, PALADIN, ASSASSIN"));
                    }
                } else {
                    player.sendMessage(MessageUtils.color("&eUsa &6/class <WARRIOR|MAGE|ARCHER|ROGUE|PALADIN|ASSASSIN> &epara cambiar tu clase."));
                }
            }
            return true;
        }

        if (sub.equals("talents")) {
            if (sender instanceof Player player) {
                if (core.getTalentTreeGUI() != null) {
                    core.getTalentTreeGUI().open(player);
                }
            }
            return true;
        }

        if (sub.equals("altar")) {
            if (sender instanceof Player player) {
                if (core.getEnchantingAltarManager() != null) {
                    core.getEnchantingAltarManager().infuseAltar(player, player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand());
                }
            }
            return true;
        }

        if (sub.equals("pet")) {
            if (sender instanceof Player player) {
                if (core.getPetCompanionManager() != null) {
                    core.getPetCompanionManager().summonPet(player, "WOLF");
                }
            }
            return true;
        }

        if (sub.equals("dungeon")) {
            if (sender instanceof Player player) {
                if (core.getDungeonEngine() != null) {
                    core.getDungeonEngine().startDungeon(player, com.antigravity.rpg.dungeons.DungeonEngine.DungeonDifficulty.CRYPTS_OF_DESPAIR);
                }
            }
            return true;
        }

        // Admin commands
        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage(MessageUtils.color("&cNo tienes permiso para ejecutar comandos administrativos."));
            return true;
        }

        switch (sub) {
            case "addxp":
                if (args.length < 3) {
                    sender.sendMessage(MessageUtils.color("&cUso: /rpg addxp <jugador> <cantidad>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(MessageUtils.color("&cJugador no encontrado."));
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[2]);
                    levelManager.addXp(target, amount);
                    sender.sendMessage(MessageUtils.color("&aAñadidos " + amount + " XP a " + target.getName()));
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtils.color("&cNúmero inválido."));
                }
                break;

            case "item":
                if (!(sender instanceof Player playerSender)) {
                    sender.sendMessage("Solo jugadores.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(MessageUtils.color("&cUso: /rpg item <id> [jugador]"));
                    return true;
                }
                String itemId = args[1];
                Player targetPlayer = args.length >= 3 ? Bukkit.getPlayer(args[2]) : playerSender;
                if (targetPlayer == null) targetPlayer = playerSender;

                ItemStack item = itemManager.createItemStack(itemId);
                if (item == null) {
                    sender.sendMessage(MessageUtils.color("&cItem ID no encontrado: " + itemId));
                    return true;
                }
                targetPlayer.getInventory().addItem(item);
                sender.sendMessage(MessageUtils.color("&aEntregado item RPG: " + itemId + " a " + targetPlayer.getName()));
                targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;

            case "spawnmob":
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("Solo jugadores.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(MessageUtils.color("&cUso: /rpg spawnmob <id>"));
                    return true;
                }
                String mobId = args[1];
                RPGMob mob = mobManager.getMob(mobId);
                if (mob == null) {
                    sender.sendMessage(MessageUtils.color("&cMob ID no encontrado: " + mobId));
                    return true;
                }
                mobManager.spawnMob(mobId, p.getLocation());
                sender.sendMessage(MessageUtils.color("&aSpawned RPG Mob: " + mobId));
                break;

            case "spawnboss":
                if (!(sender instanceof Player pBoss)) {
                    sender.sendMessage("Solo jugadores.");
                    return true;
                }
                String bossType = args.length >= 2 ? args[1] : "INFERNAL_DRAGON";
                core.getRaidBossEngine().spawnWorldBoss(pBoss.getLocation(), bossType, 5000.0);
                sender.sendMessage(MessageUtils.color("&6✦ Raid Boss " + bossType + " invocado exitosamente!"));
                break;

            default:
                sender.sendMessage(MessageUtils.color("&cSubcomando desconocido. Usa /rpg guide"));
                break;
        }

        return true;
    }

    private void sendInteractiveGuide(Player player) {
        player.sendMessage(ChatColor.GOLD + "╔════════════════════════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║       " + ChatColor.RED + "⚔ GUÍA MAESTRA DE RPG CORE" + ChatColor.GOLD + "       ║");
        player.sendMessage(ChatColor.GOLD + "╚════════════════════════════════════════════════╝");
        player.sendMessage(ChatColor.GRAY + "Haz clic en las opciones interactivas para probarlas:");

        sendClickable(player, "§6▶ §eSelección de Clases RPG §7(/class)", "/class", "§aElige tu clase: Guerrero, Mago, Arquero, etc.");
        sendClickable(player, "§6▶ §eÁrbol de Talentos §7(/talents)", "/talents", "§aDesbloquea especializaciones y bonos pasivos");
        sendClickable(player, "§6▶ §eAltar de Fusión de Reliquias §7(/altar)", "/altar", "§aImbuye armas con daño sagrado o ígneo");
        sendClickable(player, "§6▶ §eYunque de Forja y Mejora §7(/forge)", "/forge", "§aMejora armas de +1 a +10");
        sendClickable(player, "§6▶ §eYunque de Reforja de Estadísticas §7(/reforge)", "/reforge", "§aModifica atributos aleatorios de armaduras");
        sendClickable(player, "§6▶ §eCompañero de Combate Espiritual §7(/pet)", "/pet", "§aInvoca a tu lobo o familiar de combate");
        sendClickable(player, "§6▶ §eMazmorras Instanciadas §7(/dungeon)", "/dungeon", "§aComienza una incursión en la Cripta Abisal");

        if (player.hasPermission("rpg.admin")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "⚡ [COMANDOS DE ADMIN Y TESTING]");
            sendClickable(player, "§d• Dar Espada Vampírica Legendaria", "/rpg item vampiric_blade", "§eRecibir Vampiric Blade con Omnivampirismo");
            sendClickable(player, "§d• Spawnear Zombie RPG Lv.1", "/rpg spawnmob zombie_lvl1", "§eSpawnear mob con estadísticas personalizadas");
            sendClickable(player, "§d• Invocar Jefe de Incursión", "/rpg spawnboss INFERNAL_DRAGON", "§eInvocar Dragón Infernal con telegrafiado de ataques");
            sendClickable(player, "§d• Añadir 1,000 XP de Clase", "/rpg addxp " + player.getName() + " 1000", "§eAñadir experiencia inmediatamente");
        }
        player.sendMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");
    }

    private void sendClickable(Player player, String text, String command, String hover) {
        TextComponent component = new TextComponent(text);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        player.spigot().sendMessage(component);
    }
}
