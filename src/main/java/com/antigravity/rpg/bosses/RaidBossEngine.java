package com.antigravity.rpg.bosses;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RaidBossEngine {

    public static class ActiveRaid {
        public final LivingEntity bossEntity;
        public final String bossName;
        public final double maxHealth;
        public final BossBar bossBar;
        public final Map<UUID, Double> playerDamage = new ConcurrentHashMap<>();
        public boolean enraged = false;

        public ActiveRaid(LivingEntity bossEntity, String bossName, double maxHealth, BossBar bossBar) {
            this.bossEntity = bossEntity;
            this.bossName = bossName;
            this.maxHealth = maxHealth;
            this.bossBar = bossBar;
        }
    }

    private final RPGCore core;
    private final Map<UUID, ActiveRaid> activeRaids = new ConcurrentHashMap<>();

    public RaidBossEngine(RPGCore core) {
        this.core = core;
    }

    public ActiveRaid spawnWorldBoss(Location loc, String name, double maxHp) {
        Wither boss = (Wither) loc.getWorld().spawnEntity(loc, EntityType.WITHER);
        boss.setCustomName("§4§l✦ " + name + " ✦");
        boss.setCustomNameVisible(true);
        boss.setMaxHealth(maxHp);
        boss.setHealth(maxHp);

        BossBar bossBar = Bukkit.createBossBar("§4§l" + name, BarColor.PURPLE, BarStyle.SEGMENTED_10);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        ActiveRaid raid = new ActiveRaid(boss, name, maxHp, bossBar);
        activeRaids.put(boss.getUniqueId(), raid);

        Bukkit.broadcastMessage(ChatColor.DARK_RED + "========================================");
        Bukkit.broadcastMessage(ChatColor.RED + "☠ ¡JEFE DE MUNDO INVOCADO: " + name + "!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Coordenadas: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "========================================");

        return raid;
    }

    public void recordDamage(UUID bossId, Player player, double damage) {
        ActiveRaid raid = activeRaids.get(bossId);
        if (raid == null) return;

        raid.playerDamage.put(player.getUniqueId(), raid.playerDamage.getOrDefault(player.getUniqueId(), 0.0) + damage);

        double currentHp = raid.bossEntity.getHealth();
        raid.bossBar.setProgress(Math.max(0.0, Math.min(1.0, currentHp / raid.maxHealth)));

        // Phase 2: Enrage at 25% HP
        if (!raid.enraged && (currentHp / raid.maxHealth) <= 0.25) {
            raid.enraged = true;
            raid.bossBar.setColor(BarColor.RED);
            Bukkit.broadcastMessage(ChatColor.RED + "⚡ ¡" + raid.bossName + " HA ENTRADO EN FASE DE ENFURECIMIENTO (ENRAGE)! (+50% Daño)");
            raid.bossEntity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, raid.bossEntity.getLocation(), 3);
        }
    }

    public void onBossDefeated(UUID bossId) {
        ActiveRaid raid = activeRaids.remove(bossId);
        if (raid == null) return;

        raid.bossBar.removeAll();

        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
        Bukkit.broadcastMessage(ChatColor.GREEN + "🏆 ¡EL JEFE DE MUNDO " + raid.bossName + " HA SIDO DERROTADO! 🏆");

        List<Map.Entry<UUID, Double>> sorted = new ArrayList<>(raid.playerDamage.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Map.Entry<UUID, Double> entry = sorted.get(i);
            Player p = Bukkit.getPlayer(entry.getKey());
            String name = (p != null) ? p.getName() : "Desconectado";
            Bukkit.broadcastMessage(ChatColor.YELLOW + "#" + (i + 1) + " " + ChatColor.WHITE + name +
                    " §8- §c" + String.format("%.0f", entry.getValue()) + " de Daño Aportado");
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
    }
}
