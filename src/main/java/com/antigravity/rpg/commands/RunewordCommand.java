package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.equipment.RunewordEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class RunewordCommand implements CommandExecutor {

    private final RPGCore core;
    private final RunewordEngine runewordEngine;

    public RunewordCommand(RPGCore core, RunewordEngine runewordEngine) {
        this.core = core;
        this.runewordEngine = runewordEngine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatColor.GOLD + "=== 📜 " + ChatColor.YELLOW + "TOMO DE PALABRAS RÚNICAS" + ChatColor.GOLD + " ===");
            for (RunewordEngine.RunewordRecipe rw : runewordEngine.getAllRunewords()) {
                String formula = rw.recipe.stream().map(r -> r.runeName).collect(Collectors.joining(" + "));
                player.sendMessage(ChatColor.LIGHT_PURPLE + rw.displayName + " §8[" + ChatColor.YELLOW + formula + "§8]");
                player.sendMessage(ChatColor.GRAY + "   " + rw.effectDescription);
            }
            player.sendMessage(ChatColor.GRAY + "Para socketear: sostén el arma/armadura y escribe " + ChatColor.YELLOW + "/runewords socket");
            return true;
        }

        if (args[0].equalsIgnoreCase("socket")) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();

            if (mainHand == null || mainHand.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "Debes sostener un arma o armadura en tu mano principal.");
                return true;
            }

            if (!runewordEngine.isRune(offHand)) {
                player.sendMessage(ChatColor.RED + "Debes sostener una Runa en tu mano secundaria (offhand).");
                return true;
            }

            runewordEngine.socketRune(player, mainHand, offHand);
            return true;
        }

        if (args[0].equalsIgnoreCase("give") && args.length >= 2 && player.isOp()) {
            try {
                RunewordEngine.RuneType rune = RunewordEngine.RuneType.valueOf(args[1].toUpperCase());
                player.getInventory().addItem(runewordEngine.createRune(rune));
                player.sendMessage(ChatColor.GREEN + "Has recibido la Runa " + rune.runeName);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Runa desconocida.");
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /runewords <list|socket|give <rune>>");
        return true;
    }
}
