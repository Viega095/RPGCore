package com.viega.rpg.events;

import com.viega.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Calendar;

public class GlobalEventScheduler {

    private final RPGCore plugin;
    private BukkitTask schedulerTask = null;
    private int lastDispatchedSlot = -1; // 0 for :00, 1 for :15, 2 for :30, 3 for :45

    public GlobalEventScheduler(RPGCore plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (schedulerTask != null) schedulerTask.cancel();

        schedulerTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 100L, 200L); // check every 10 seconds (200 ticks)
    }

    public void stop() {
        if (schedulerTask != null) {
            schedulerTask.cancel();
            schedulerTask = null;
        }
    }

    private void tick() {
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int currentSlot = minute / 15; // 0: 00-14, 1: 15-29, 2: 30-44, 3: 45-59
        int minuteInSlot = minute % 15;

        // Trigger only during the first 2 minutes of the slot (:00-:01, :15-:16, :30-:31, :45-:46)
        if (minuteInSlot <= 1) {
            if (lastDispatchedSlot != currentSlot) {
                lastDispatchedSlot = currentSlot;

                if (GlobalEventLock.isEventActive()) {
                    plugin.getLogger().info("[Programador Horario] Intervalo :" + (currentSlot * 15) + 
                            " omitido porque ya hay un evento en curso: " + GlobalEventLock.getActiveEventTitle());
                    return;
                }

                switch (currentSlot) {
                    case 0:
                        // :00 EconomyFish
                        if (!Bukkit.getPluginManager().isPluginEnabled("FishingEconomy")) {
                            break;
                        }
                        plugin.getLogger().info("[Programador Horario] Disparando Torneo de Pesca Abisal (:00)...");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fish tournament start 10");
                        break;
                    case 1:
                        // :15 FlorArcana
                        if (!Bukkit.getPluginManager().isPluginEnabled("FloraArcana")) {
                            break;
                        }
                        plugin.getLogger().info("[Programador Horario] Disparando Florecimiento Astral (:15)...");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "flora event bloom 600");
                        break;
                    case 2:
                        // :30 RPGCore
                        plugin.getLogger().info("[Programador Horario] Disparando Incursión de la Luna de Sangre (:30)...");
                        plugin.getBloodMoonIncursionEngine().startBloodMoon(600);
                        break;
                    case 3:
                        // :45 Gambling
                        if (!Bukkit.getPluginManager().isPluginEnabled("VieGambling")) {
                            break;
                        }
                        plugin.getLogger().info("[Programador Horario] Disparando Hora Dorada del Casino (:45)...");
                        Bukkit.broadcastMessage(ChatColor.GOLD + "🎰 ══════════════════════════════════════════");
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "✦ ¡LA HORA DORADA DEL CASINO HA COMENZADO! ✦");
                        Bukkit.broadcastMessage(ChatColor.WHITE + "Multiplicadores +50% en mesas de ruleta, tragamonedas y craps");
                        Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════");
                        GlobalEventLock.acquireLock(plugin, "Hora Dorada del Casino", 600);
                        break;
                }
            }
        }
    }
}
