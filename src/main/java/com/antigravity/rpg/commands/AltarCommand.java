package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.equipment.EnchantingAltarManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AltarCommand implements CommandExecutor {

    private final RPGCore core;
    private final EnchantingAltarManager altarManager;

    public AltarCommand(RPGCore core, EnchantingAltarManager altarManager) {
        this.core = core;
        this.altarManager = altarManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();

        if (main == null || off == null || main.getType().isAir() || off.getType().isAir()) {
            player.sendMessage(ChatColor.YELLOW + "Sostén tu arma en la mano principal y un material místico en la secundaria y usa /altar para imbuirla.");
            return true;
        }

        altarManager.infuseAltar(player, main, off);
        return true;
    }
}
