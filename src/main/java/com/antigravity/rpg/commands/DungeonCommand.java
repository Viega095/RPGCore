package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.dungeons.DungeonEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DungeonCommand implements CommandExecutor {

    private final RPGCore core;
    private final DungeonEngine dungeonEngine;

    public DungeonCommand(RPGCore core, DungeonEngine dungeonEngine) {
        this.core = core;
        this.dungeonEngine = dungeonEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden entrar a mazmorras.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatColor.DARK_PURPLE + "=== 🏰 " + ChatColor.LIGHT_PURPLE + "PORTALES DE MAZMORRA Y RIFTS" + ChatColor.DARK_PURPLE + " ===");
            for (DungeonEngine.DungeonDifficulty diff : DungeonEngine.DungeonDifficulty.values()) {
                player.sendMessage(ChatColor.GOLD + "• " + diff.name + ChatColor.GRAY + " (Lv. " + diff.minLevel + " - " + diff.maxLevel + ") " +
                        ChatColor.YELLOW + "[Tiempo: " + diff.timeLimitSeconds + "s]");
            }
            player.sendMessage(ChatColor.GRAY + "Comando para ingresar: " + ChatColor.YELLOW + "/dungeon enter <crypts|infernal|abyssal>");
            return true;
        }

        if (args[0].equalsIgnoreCase("enter") && args.length >= 2) {
            String name = args[1].toLowerCase();
            DungeonEngine.DungeonDifficulty selected = null;
            if (name.contains("crypt")) selected = DungeonEngine.DungeonDifficulty.CRYPTS_OF_DESPAIR;
            else if (name.contains("infernal") || name.contains("spire")) selected = DungeonEngine.DungeonDifficulty.INFERNAL_SPIRE;
            else if (name.contains("abyss") || name.contains("sanctum")) selected = DungeonEngine.DungeonDifficulty.ABYSSAL_SANCTUM;

            if (selected == null) {
                player.sendMessage(ChatColor.RED + "Mazmorra no encontrada. Usa: crypts, infernal, abyssal.");
                return true;
            }

            dungeonEngine.startDungeon(player, selected);
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            dungeonEngine.cancelSession(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Has abandonado la mazmorra.");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /dungeon <list|enter|leave>");
        return true;
    }
}
