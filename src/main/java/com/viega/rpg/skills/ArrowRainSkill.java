package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowRainSkill extends Skill {

    public ArrowRainSkill() {
        super("ArrowRain", "Desata una lluvia celestial de flechas perforantes en un radio de 6 bloques", 45, 10000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Location targetCenter = player.getTargetBlockExact(25) != null ?
                player.getTargetBlockExact(25).getLocation() :
                player.getLocation().add(player.getLocation().getDirection().multiply(10));

        double radius = 6.0;
        double damage = 10.0 + (level * 3.5);
        Random random = new Random();

        // Efectos de lluvia de flechas
        for (int i = 0; i < 40; i++) {
            double offsetX = (random.nextDouble() - 0.5) * (radius * 2);
            double offsetZ = (random.nextDouble() - 0.5) * (radius * 2);
            Location spawn = targetCenter.clone().add(offsetX, 8.0, offsetZ);

            player.getWorld().spawnParticle(Particle.CRIT, spawn, 2, 0, -1, 0, 0.5);
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, spawn.clone().subtract(0, 4, 0), 2, 0.1, 0.1, 0.1, 0.05);
        }

        player.getWorld().playSound(targetCenter, Sound.ENTITY_ARROW_SHOOT, 1.5f, 1.2f);
        player.getWorld().playSound(targetCenter, Sound.ENTITY_ARROW_HIT, 1.2f, 0.8f);

        int hitCount = 0;
        for (Entity e : targetCenter.getWorld().getNearbyEntities(targetCenter, radius, radius, radius)) {
            if (e instanceof LivingEntity target && e != player) {
                target.damage(damage, player);
                hitCount++;
            }
        }

        player.sendMessage("§a§l✦ Lluvia de Flechas§7 impactó a §e" + hitCount + "§7 objetivos por §c" + (int) damage + "§7 de daño.");
        return true;
    }
}
