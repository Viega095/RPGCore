package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ShadowDanceSkill extends Skill {

    public ShadowDanceSkill() {
        super("ShadowDance", "Te teletransporta tras la espalda del objetivo asestando un golpe crítico", 40, 7000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        double maxRange = 12.0;
        LivingEntity target = null;
        double closest = Double.MAX_VALUE;

        for (Entity e : player.getNearbyEntities(maxRange, maxRange, maxRange)) {
            if (e instanceof LivingEntity le && e != player) {
                double dist = player.getLocation().distance(le.getLocation());
                if (dist < closest) {
                    closest = dist;
                    target = le;
                }
            }
        }

        if (target == null) {
            player.sendMessage("§c¡No hay objetivos cercanos para ejecutar Danza de las Sombras!");
            return false;
        }

        Location oldLoc = player.getLocation();
        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, oldLoc.add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.05);

        // Calcular posición a espaldas del objetivo
        Vector behindDir = target.getLocation().getDirection().normalize().multiply(-1.5);
        Location behind = target.getLocation().clone().add(behindDir);
        behind.setDirection(target.getLocation().toVector().subtract(behind.toVector()).normalize());

        player.teleport(behind);

        double damage = 14.0 + (level * 5.0);
        target.damage(damage, player);

        player.getWorld().spawnParticle(Particle.SQUID_INK, target.getLocation().add(0, 1, 0), 30, 0.4, 0.4, 0.4, 0.1);
        player.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 25, 0.3, 0.3, 0.3, 0.2);
        player.playSound(behind, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.4f);
        player.playSound(behind, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.2f, 0.8f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1));
        player.sendMessage("§8§l✦ Danza de las Sombras§7 asestó un golpe crítico por la espalda causando §c" + (int) damage + "§7 de daño.");
        return true;
    }
}
