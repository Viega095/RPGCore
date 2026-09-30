package com.antigravity.rpg.skills;

import com.antigravity.rpg.RPGCore;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomSkillComboEngine implements Listener {

    private final RPGCore core;
    private final Map<UUID, String> lastSkillUsed = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSkillTimestamp = new ConcurrentHashMap<>();

    public CustomSkillComboEngine(RPGCore core) {
        this.core = core;
    }

    public void recordSkillUsage(Player player, String skillName) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        String prev = lastSkillUsed.get(uuid);
        long prevTime = lastSkillTimestamp.getOrDefault(uuid, 0L);

        lastSkillUsed.put(uuid, skillName);
        lastSkillTimestamp.put(uuid, now);

        if (prev != null && (now - prevTime) < 3500) {
            checkAndExecuteCombo(player, prev, skillName);
        }
    }

    private void checkAndExecuteCombo(Player player, String first, String second) {
        // Mage Combo: Fireball + Dash -> Infernal Flame Trail
        if (first.equalsIgnoreCase("fireball") && second.equalsIgnoreCase("dash")) {
            executeInfernalTrail(player);
        }
    }

    private void executeInfernalTrail(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§6⚡ ¡COMBO DESATADO: §e§lESTELA ÍGNEA INFERNAL§6!"));
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1.2f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ >= 6) {
                    cancel();
                    return;
                }
                Location loc = player.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc.add(0, 0.3, 0), 20, 0.4, 0.2, 0.4, 0.05);
                loc.getWorld().spawnParticle(Particle.LAVA, loc, 5, 0.3, 0.1, 0.3, 0.01);

                for (org.bukkit.entity.Entity e : player.getNearbyEntities(3, 3, 3)) {
                    if (e instanceof LivingEntity living && !(e instanceof Player)) {
                        living.damage(10.0, player);
                        living.setFireTicks(80);
                    }
                }
            }
        }.runTaskTimer(core, 0L, 5L);
    }

    @EventHandler
    public void onMeleeAfterDash(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity target) {
            UUID uuid = player.getUniqueId();
            String prev = lastSkillUsed.get(uuid);
            long prevTime = lastSkillTimestamp.getOrDefault(uuid, 0L);

            if ("dash".equalsIgnoreCase(prev) && (System.currentTimeMillis() - prevTime) < 2500) {
                lastSkillUsed.remove(uuid); // consume combo
                event.setDamage(event.getDamage() * 2.0);

                Location loc = target.getLocation();
                loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc.add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.1);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
                loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 0.8f);

                // Shockwave Cleave
                for (org.bukkit.entity.Entity e : target.getNearbyEntities(4, 4, 4)) {
                    if (e instanceof LivingEntity living && !e.equals(player) && !e.equals(target)) {
                        living.damage(event.getFinalDamage() * 0.7, player);
                        Vector knock = living.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.8).setY(0.3);
                        living.setVelocity(knock);
                    }
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§c⚡ ¡COMBO DESATADO: §4§lHENDIDURA DE CHOQUE§c!"));
            }
        }
    }
}
