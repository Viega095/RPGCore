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
            player.sendMessage("§a✦ ¡Subiste de nivel! Ahora eres Nivel " + currentLevel + " ✦");
            AttributePointManager apm = core != null ? core.getManagerHandler().get(AttributePointManager.class) : null;
            if (apm != null) {
                apm.addPoints(player.getUniqueId(), 3);
                player.sendMessage("§e✦ ¡Has recibido §63 Puntos de Atributo§e! Usa §b/stats §epara distribuirlos.");
            }
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
            requiredXp = getRequiredXp(currentLevel);
        }

        data.setXp(newXp);
    }

    public double getRequiredXp(int level) {
        return 100 * level * 1.2; // Simple curve
    }
}
