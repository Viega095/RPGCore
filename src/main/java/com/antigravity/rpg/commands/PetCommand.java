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

        if (args.length == 0 || args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("gui")) {
            if (core.getPetManagementGUI() != null) {
                core.getPetManagementGUI().open(player);
            } else {
                petManager.summonPet(player, "wolf");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("summon")) {
            String type = args.length > 1 ? args[1] : "WOLF";
            petManager.summonPet(player, type);
            return true;
        }

        if (args[0].equalsIgnoreCase("dismiss") || args[0].equalsIgnoreCase("despawn")) {
            petManager.dismissPet(player);
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "🐾 Comandos de Mascota: §e/pet §7(abre el menú) | §e/pet summon <tipo> §7| §e/pet dismiss");
        return true;
    }
}
