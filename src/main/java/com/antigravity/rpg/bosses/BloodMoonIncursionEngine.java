package com.antigravity.rpg.bosses;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BloodMoonIncursionEngine implements Listener {

    private final RPGCore plugin;
    private boolean active = false;
    private int durationSeconds = 0;
    private BossBar bloodMoonBar = null;
    private BukkitRunnable incursionTask = null;

    public BloodMoonIncursionEngine(RPGCore plugin) {
        this.plugin = plugin;
    }

    public boolean isActive() {
        return active;
    }

    public void startBloodMoon(int durationSec) {
        if (active) return;
        this.active = true;
        this.durationSeconds = durationSec;

        this.bloodMoonBar = Bukkit.createBossBar(
                "§4🩸 §c§lINCURSIÓN DE LA LUNA DE SANGRE §4🩸 §f(Mobs x2 Poder & Botín Épico)",
                BarColor.RED,
                BarStyle.SOLID
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            bloodMoonBar.addPlayer(p);
            p.sendTitle("§4🩸 ¡LUNA DE SANGRE! 🩸", "§cEl cielo se tiñe de carmesí... Mobs empoderados", 10, 80, 20);
            p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.7f, 0.6f);
        }

        incursionTask = new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (!active) {
                    cancel();
                    return;
                }

                elapsed++;
                int remaining = durationSeconds - elapsed;
                double progress = Math.max(0.0, Math.min(1.0, (double) remaining / durationSeconds));
                if (bloodMoonBar != null) {
                    bloodMoonBar.setProgress(progress);
                    bloodMoonBar.setTitle("§4🩸 §c§lINCURSIÓN DE LA LUNA DE SANGRE §4🩸 §e(" + remaining + "s restantes)");
                }

                // Ambient red dust & lightning flashes across players
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Location loc = p.getLocation().add((Math.random() - 0.5) * 20, 2 + Math.random() * 5, (Math.random() - 0.5) * 20);
                    p.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.fromRGB(220, 20, 20), 1.5f));

                    if (Math.random() < 0.05) {
                        p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.4f, 0.6f);
                    }
                }

                if (remaining <= 0) {
                    stopBloodMoon();
                    cancel();
                }
            }
        };
        incursionTask.runTaskTimer(plugin, 20L, 20L);
    }

    public void stopBloodMoon() {
        if (!active) return;
        this.active = false;
        if (bloodMoonBar != null) {
            bloodMoonBar.removeAll();
            bloodMoonBar = null;
        }
        if (incursionTask != null) {
            incursionTask.cancel();
            incursionTask = null;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("§a🌅 LA LUNA DE SANGRE HA TERMINADO", "§7La calma regresa a las tierras", 10, 60, 15);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        }
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        if (!active) return;
        if (event.getEntity() instanceof Monster monster) {
            // Empower monster with +100% health and red flame particles
            double maxHp = monster.getMaxHealth() * 2.0;
            monster.setMaxHealth(maxHp);
            monster.setHealth(maxHp);
            monster.setCustomName("§4✦ §c" + monster.getName() + " Sanguinario §4✦");
            monster.setCustomNameVisible(true);
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (!active) return;
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null && entity instanceof Monster) {
            // Drop Blood Gems
            if (Math.random() < 0.40) {
                ItemStack bloodGem = createBloodGem(1 + (int)(Math.random() * 2));
                event.getDrops().add(bloodGem);
                killer.sendMessage("§4🩸 §c¡Has recolectado una Gema de Sangre Carmesí!");
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.8f);
            }
        }
    }

    public static ItemStack createBloodGem(int amount) {
        ItemStack gem = new ItemStack(Material.REDSTONE, amount);
        ItemMeta meta = gem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§4§l💎 Gema de Sangre Carmesí");
            meta.setLore(Arrays.asList(
                    "§7Cristalizada durante la Incursión de la Luna de Sangre.",
                    "§c• Esencia de poder ancestral",
                    "§6• Moneda de canje para reliquias míticas"
            ));
            gem.setItemMeta(meta);
        }
        return gem;
    }
}
