package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.equipment.ReforgeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReforgeCommand implements CommandExecutor {

    private final RPGCore core;
    private final ReforgeManager reforgeManager;

    public ReforgeCommand(RPGCore core, ReforgeManager reforgeManager) {
        this.core = core;
        this.reforgeManager = reforgeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "Sostén el objeto que deseas reforjar en tu mano principal.");
            return true;
        }

        reforgeManager.reforgeItem(player, hand);
        return true;
    }
}
