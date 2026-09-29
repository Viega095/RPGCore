package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LevelManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        // Dependency injection via setter or specific init method if needed later
    }

    public void setPlayerManager(PlayerManager pm) {
        this.playerManager = pm;
    }

    @Override
    public void onDisable() {
    }

    public void addXp(Player player, double amount) {
        if (playerManager == null)
            return;

        PlayerData data = playerManager.getData(player.getUniqueId());
        if (data == null)
            return;

        double newXp = data.getXp() + amount;
        int currentLevel = data.getLevel();
        double requiredXp = getRequiredXp(currentLevel);

        while (newXp >= requiredXp) {
            newXp -= requiredXp;
            currentLevel++;
            data.setLevel(currentLevel);
            player.sendMessage("§aLeveled Up! You are now level " + currentLevel);
            // Play sound or effect
            requiredXp = getRequiredXp(currentLevel);
        }

        data.setXp(newXp);
    }

    public double getRequiredXp(int level) {
        return 100 * level * 1.2; // Simple curve
    }
}
