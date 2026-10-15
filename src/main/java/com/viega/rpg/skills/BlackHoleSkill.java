package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BlackHoleSkill extends Skill {

    public BlackHoleSkill() {
        super("BlackHole", "Invoca un vórtice gravitatorio que succiona enemigos y colapsa en un pulso arcano", 60, 12000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Location targetLoc = player.getTargetBlockExact(20) != null ?
                player.getTargetBlockExact(20).getLocation().add(0, 1.5, 0) :
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(8));

        double radius = 5.0 + (level * 0.5);
        double explosionDamage = 14.0 + (level * 4.0);

        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.2f, 0.5f);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("RPGCore");
        if (plugin == null) return false;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 20) { // 1 segundo de vórtice y luego detona
                    cancel();
                    // Detonación
                    targetLoc.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 150, 1.5, 1.5, 1.5, 0.5);
                    targetLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, targetLoc, 80, 1.0, 1.0, 1.0, 0.1);
                    targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.5f);

                    int hits = 0;
                    for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, radius, radius, radius)) {
                        if (entity instanceof LivingEntity target && entity != player) {
                            target.damage(explosionDamage, player);
                            hits++;
                        }
                    }
                    if (hits > 0 && player.isOnline()) {
                        player.sendMessage("§5§l✦ Agujero Negro§d colapsó afectando a §f" + hits + "§d enemigos por §c" + (int) explosionDamage + "§d de daño.");
                    }
                    return;
                }

                // Efecto de succión constante
                targetLoc.getWorld().spawnParticle(Particle.SQUID_INK, targetLoc, 20, 0.3, 0.3, 0.3, 0.05);
                targetLoc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, targetLoc, 25, 0.8, 0.8, 0.8, 0.1);
                targetLoc.getWorld().playSound(targetLoc, Sound.BLOCK_BEACON_AMBIENT, 0.6f, 0.5f);

                for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, radius, radius, radius)) {
                    if (entity instanceof LivingEntity target && entity != player) {
                        Vector pull = targetLoc.toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.4);
                        target.setVelocity(pull);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);

        return true;
    }
}
