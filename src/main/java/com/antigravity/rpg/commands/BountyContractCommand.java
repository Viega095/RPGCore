package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.RogueBountyManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyContractCommand implements CommandExecutor {

    private final RPGCore core;
    private final RogueBountyManager bountyManager;

    public BountyContractCommand(RPGCore core, RogueBountyManager bountyManager) {
        this.core = core;
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("get")) {
            bountyManager.assignDailyContract(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("claim")) {
            bountyManager.claimContract(player);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /rpgcontract <get|claim>");
        return true;
    }
}
