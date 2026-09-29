package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.gui.BlacksmithGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForgeCommand implements CommandExecutor {

    private final RPGCore core;
    private final BlacksmithGUI blacksmithGUI;

    public ForgeCommand(RPGCore core, BlacksmithGUI blacksmithGUI) {
        this.core = core;
        this.blacksmithGUI = blacksmithGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo los jugadores pueden usar la forja.");
            return true;
        }

        blacksmithGUI.open(player);
        return true;
    }
}
