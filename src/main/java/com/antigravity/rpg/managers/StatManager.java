package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        // We will need to get PlayerManager instance, but ManagerHandler manages this.
        // For now, we assume Managers are loaded and we can access them.
        // Ideally we pass dependencies or look them up.
        startRegenTask();
    }

    public void setPlayerManager(PlayerManager pm) {
        this.playerManager = pm;
    }

    @Override
    public void onDisable() {

    }

    private void startRegenTask() {
        Bukkit.getScheduler().runTaskTimer(core, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // Regen logic here
                if (playerManager != null) {
                    PlayerData data = playerManager.getData(player.getUniqueId());
                    if (data != null) {
                        double maxMana = data.getMaxMana(); // Should be calculated based on Intel
                        double current = data.getCurrentMana();
                        if (current < maxMana) {
                            data.setCurrentMana(Math.min(maxMana, current + (maxMana * 0.05))); // 5% regen
                        }
                    }
                }
            }
        }, 20L, 20L); // Every second
    }

    public void recalcStats(Player player) {
        // Logic to recalculate total stats from Base + Items + Buffs
        // Update PlayerData
    }
}
