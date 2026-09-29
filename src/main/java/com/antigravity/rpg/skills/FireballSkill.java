package com.antigravity.rpg.skills;

import com.antigravity.rpg.models.Skill;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FireballSkill extends Skill {

    public FireballSkill() {
        super("Fireball", "Launch a fireball that explodes on impact", 30, 5000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        // Launch fireball in the direction player is looking
        Vector direction = player.getLocation().getDirection();
        Fireball fireball = player.getWorld().spawn(
                player.getEyeLocation().add(direction.multiply(2)),
                Fireball.class);

        fireball.setShooter(player);
        fireball.setDirection(direction);
        fireball.setYield((float) (1.0f + level * 0.3f)); // Scale damage with level

        return true;
    }
}
