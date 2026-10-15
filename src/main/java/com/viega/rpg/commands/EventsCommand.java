package com.viega.rpg.commands;

import com.viega.rpg.RPGCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventsCommand implements CommandExecutor {

    private final RPGCore plugin;

    public EventsCommand(RPGCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        plugin.getGlobalEventsGUI().openGUI(player);
        return true;
    }
}
