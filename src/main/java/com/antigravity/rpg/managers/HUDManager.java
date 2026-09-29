package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HUDManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        startHUDTask();
    }

    public void setPlayerManager(PlayerManager pm) {
        this.playerManager = pm;
    }

    @Override
    public void onDisable() {

    }

    private void startHUDTask() {
        Bukkit.getScheduler().runTaskTimer(core, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendActionBar(player);
            }
        }, 10L, 20L); // Update every second
    }

    private void sendActionBar(Player player) {
        if (playerManager == null)
            return;
        PlayerData data = playerManager.getData(player.getUniqueId());
        if (data == null)
            return;

        String health = String.format("§c❤ %.0f/%.0f", player.getHealth(),
                player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
        String mana = String.format("§b⚡ %.0f/%.0f", data.getCurrentMana(), data.getMaxMana());
        String level = String.format("§eLv.%d", data.getLevel());

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(health + "   " + mana + "   " + level));
    }
}
