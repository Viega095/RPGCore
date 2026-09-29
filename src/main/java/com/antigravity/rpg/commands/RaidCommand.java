package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.bosses.RaidBossEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaidCommand implements CommandExecutor {

    private final RPGCore core;
    private final RaidBossEngine raidBossEngine;

    public RaidCommand(RPGCore core, RaidBossEngine raidBossEngine) {
        this.core = core;
        this.raidBossEngine = raidBossEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("spawn") && player.isOp()) {
            raidBossEngine.spawnWorldBoss(player.getLocation(), "Señor de la Ceniza Abisal", 50000.0);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /raid spawn");
        return true;
    }
}
