package com.antigravity.rpg.skills;

import com.antigravity.rpg.models.Skill;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealSkill extends Skill {

    public HealSkill() {
        super("Heal", "Restore health", 40, 8000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double healAmount = 4.0 + (level * 2.0); // Scale heal with level

        double newHealth = Math.min(maxHealth, player.getHealth() + healAmount);
        player.setHealth(newHealth);

        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);

        return true;
    }
}
