package com.antigravity.rpg.listeners;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final RPGCore core;
    private final PlayerManager playerManager;

    public PlayerListener(RPGCore core, PlayerManager playerManager) {
        this.core = core;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerManager.load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerManager.unload(event.getPlayer().getUniqueId());
    }
}
