package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class PiercingShotSkill extends Skill {

    public PiercingShotSkill() {
        super("PiercingShot", "Dispara una flecha mística supersónica que atraviesa a todos los enemigos a su paso", 35, 6000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();
        double maxDist = 25.0;
        double damage = 12.0 + (level * 4.0);

        player.playSound(start, Sound.ENTITY_ARROW_SHOOT, 1.2f, 1.6f);
        player.playSound(start, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.6f, 1.8f);

        Set<LivingEntity> pierced = new HashSet<>();

        for (double d = 0.5; d <= maxDist; d += 0.5) {
            Location pLoc = start.clone().add(direction.clone().multiply(d));
            player.getWorld().spawnParticle(Particle.CRIT, pLoc, 2, 0.05, 0.05, 0.05, 0.02);
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, pLoc, 1, 0, 0, 0, 0);

            if (pLoc.getBlock().getType().isSolid()) {
                break;
            }

            for (Entity e : player.getWorld().getNearbyEntities(pLoc, 1.2, 1.2, 1.2)) {
                if (e instanceof LivingEntity target && e != player && !pierced.contains(target)) {
                    pierced.add(target);
                    target.damage(damage, player);
                    target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }

        if (!pierced.isEmpty()) {
            player.sendMessage("§a§l✦ Disparo Perforante§7 atravesó a §e" + pierced.size() + "§7 enemigos infligiendo §c" + (int) damage + "§7 de daño.");
        }
        return true;
    }
}
