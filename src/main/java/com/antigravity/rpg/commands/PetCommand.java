package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.PetCompanionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PetCommand implements CommandExecutor {

    private final RPGCore core;
    private final PetCompanionManager petManager;

    public PetCommand(RPGCore core, PetCompanionManager petManager) {
        this.core = core;
        this.petManager = petManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("summon")) {
            petManager.summonPet(player, "wolf");
            return true;
        }

        if (args[0].equalsIgnoreCase("dismiss")) {
            petManager.dismissPet(player);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /pet <summon|dismiss>");
        return true;
    }
}
