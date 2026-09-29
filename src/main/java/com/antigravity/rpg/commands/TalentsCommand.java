package com.antigravity.rpg.commands;

import com.antigravity.rpg.gui.TalentTreeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TalentsCommand implements CommandExecutor {

    private final TalentTreeGUI talentTreeGUI;

    public TalentsCommand(TalentTreeGUI talentTreeGUI) {
        this.talentTreeGUI = talentTreeGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo los jugadores pueden abrir el árbol de talentos.");
            return true;
        }

        talentTreeGUI.open(player);
        return true;
    }
}
