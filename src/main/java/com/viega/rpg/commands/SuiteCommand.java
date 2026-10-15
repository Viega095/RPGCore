package com.viega.rpg.commands;

import com.viega.rpg.RPGCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuiteCommand implements CommandExecutor {

    private final RPGCore plugin;

    public SuiteCommand(RPGCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        if (!player.hasPermission("rpg.admin") && !player.isOp()) {
            player.sendMessage("§cNo tienes permiso para acceder al panel maestro de la suite.");
            return true;
        }

        plugin.getSuiteMasterAdminGUI().openGUI(player);
        return true;
    }
}
