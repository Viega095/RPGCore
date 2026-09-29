package com.antigravity.rpg.skills;

import com.antigravity.rpg.models.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class OmnivampirismSkill extends Skill {

    public OmnivampirismSkill() {
        super("Omnivampirism", "Drain life from enemies around you", 75, 30000, 1);
    }

    @Override
    public boolean cast(Player player, int level) {
        Location loc = player.getLocation();
        double radius = 6.0;
        double damage = 15.0;
        double healAmount = 0;

        // Visual Effects
        player.getWorld().spawnParticle(Particle.REDSTONE, loc, 100, 3, 1, 3,
                new Particle.DustOptions(org.bukkit.Color.RED, 2));
        player.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 0.5f, 0.5f);

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;
                target.damage(damage, player);

                // Draw line from target to player
                // (Simplified particle line)

                healAmount += (damage * 0.5); // Heal 50% of damage dealt
            }
        }

        if (healAmount > 0) {
            double newHealth = Math.min(
                    player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue(),
                    player.getHealth() + healAmount);
            player.setHealth(newHealth);
            player.sendMessage("§c§lOmnivampirism§c healed you for " + (int) healAmount + " HP!");
        } else {
            player.sendMessage("§cNo targets nearby to drain!");
            return false;
        }

        return true;
    }
}
