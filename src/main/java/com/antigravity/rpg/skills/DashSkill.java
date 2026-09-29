package com.antigravity.rpg.skills;

import com.antigravity.rpg.models.Skill;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DashSkill extends Skill {

    public DashSkill() {
        super("Dash", "Dash forward quickly", 20, 3000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Vector direction = player.getLocation().getDirection().normalize();
        direction.setY(0.3); // Slight upward boost

        double power = 1.5 + (level * 0.2); // Scale speed with level
        player.setVelocity(direction.multiply(power));

        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);

        return true;
    }
}
