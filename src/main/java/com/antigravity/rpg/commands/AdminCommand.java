package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.*;
import com.antigravity.rpg.models.RPGMob;
import org.bukkit.Bukkit;
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
        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /rpg <addxp/item/spawnmob>");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "addxp":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /rpg addxp <player> <amount>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[2]);
                    levelManager.addXp(target, amount);
                    sender.sendMessage("§aAdded " + amount + " XP to " + target.getName());
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number.");
                }
                break;

            case "item":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Players only.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /rpg item <id>");
                    return true;
                }
                String itemId = args[1];
                ItemStack item = itemManager.createItemStack(itemId);
                if (item == null) {
                    sender.sendMessage("§cItem ID not found.");
                    return true;
                }
                ((Player) sender).getInventory().addItem(item);
                sender.sendMessage("§aGave item: " + itemId);
                break;

            case "spawnmob":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Players only.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /rpg spawnmob <id>");
                    return true;
                }
                String mobId = args[1];
                RPGMob mob = mobManager.getMob(mobId);
                if (mob == null) {
                    sender.sendMessage("§cMob ID not found.");
                    return true;
                }
                mobManager.spawnMob(mobId, ((Player) sender).getLocation());
                sender.sendMessage("§aSpawned mob: " + mobId);
                break;

            default:
                sender.sendMessage("§cUnknown subcommand.");
                break;
        }

        return true;
    }
}
