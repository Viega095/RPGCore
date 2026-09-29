package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.ClassManager;
import com.antigravity.rpg.models.RPGClass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassCommand implements CommandExecutor {

    private final RPGCore core;
    private final ClassManager classManager;

    public ClassCommand(RPGCore core, ClassManager classManager) {
        this.core = core;
        this.classManager = classManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§eAvailable Classes: " + Arrays.stream(RPGClass.values())
                    .filter(c -> c != RPGClass.NONE)
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
            player.sendMessage("§eUsage: /class <classname>");
            return true;
        }

        String className = args[0].toUpperCase();
        try {
            RPGClass rpgClass = RPGClass.valueOf(className);
            if (rpgClass == RPGClass.NONE) {
                player.sendMessage("§cInvalid class.");
                return true;
            }

            classManager.selectClass(player.getUniqueId(), rpgClass);
            player.sendMessage("§aYou have selected §e" + rpgClass.name() + "§a class!");

        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid class. Options: " + Arrays.stream(RPGClass.values())
                    .filter(c -> c != RPGClass.NONE)
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
        }

        return true;
    }
}
