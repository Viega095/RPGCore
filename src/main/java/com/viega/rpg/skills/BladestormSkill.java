package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BladestormSkill extends Skill {

    public BladestormSkill() {
        super("Bladestorm", "Gira desatando un torbellino de cuchillas que daña en 360° a los enemigos cercanos", 35, 6000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Location center = player.getLocation();
        double radius = 3.5 + (level * 0.5);
        double damage = 8.0 + (level * 3.0);
        int hitCount = 0;

        // Visual swirl effect
        for (int i = 0; i < 360; i += 30) {
            double radians = Math.toRadians(i);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;
            Location particleLoc = center.clone().add(x, 0.8, z);
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLoc, 1, 0, 0, 0, 0);
            player.getWorld().spawnParticle(Particle.CRIT, particleLoc, 2, 0.2, 0.2, 0.2, 0.1);
        }

        player.playSound(center, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.2f, 1.0f);
        player.playSound(center, Sound.ITEM_TRIDENT_RIPTIDE_2, 0.8f, 1.4f);

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity target && entity != player) {
                target.damage(damage, player);
                Vector knockback = target.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.6);
                knockback.setY(0.2);
                target.setVelocity(knockback);
                hitCount++;
            }
        }

        if (hitCount > 0) {
            player.sendMessage("§6§l✦ Torbellino de Cuchillas§e impactó a §f" + hitCount + "§e enemigos por §c" + (int) damage + "§e de daño.");
        }
        return true;
    }
}
