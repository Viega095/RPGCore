package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.dungeons.KeystoneDungeonManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KeystoneCommand implements CommandExecutor {

    private final RPGCore core;
    private final KeystoneDungeonManager keystoneManager;

    public KeystoneCommand(RPGCore core, KeystoneDungeonManager keystoneManager) {
        this.core = core;
        this.keystoneManager = keystoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("give")) {
            int level = (args.length >= 2) ? Integer.parseInt(args[1]) : 1;
            ItemStack keystone = keystoneManager.createKeystone(level, KeystoneDungeonManager.DungeonAffix.SANGUINE);
            player.getInventory().addItem(keystone);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "✔ Has recibido una " + keystone.getItemMeta().getDisplayName());
            return true;
        }

        if (args[0].equalsIgnoreCase("activate")) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            keystoneManager.activateKeystoneRift(player, hand);
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Uso: /keystone <give [nivel]|activate>");
        return true;
    }
}
