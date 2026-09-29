package com.antigravity.rpg.party;

import com.antigravity.rpg.RPGCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PartySynergyEngine {

    private final RPGCore core;

    public PartySynergyEngine(RPGCore core) {
        this.core = core;
    }

    public void triggerShatterCombo(Player attacker, LivingEntity target, double baseDamage) {
        Location loc = target.getLocation();
        double aoeDamage = baseDamage * 3.0;

        loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 50, 1.5, 1.0, 1.5, 0.1);
        loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1.5f, 0.6f);

        attacker.sendMessage(ChatColor.AQUA + "❄💥 ¡COMBO DE SINERGIA: SHATTER! (300% Daño en Área: " +
                String.format("%.1f", aoeDamage) + ")");

        for (LivingEntity nearby : loc.getWorld().getNearbyLivingEntities(loc, 5.0)) {
            if (!nearby.equals(attacker) && !(nearby instanceof Player)) {
                nearby.damage(aoeDamage, attacker);
            }
        }
    }

    public void triggerFirestormCombo(Player attacker, LivingEntity target) {
        Location loc = target.getLocation();
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 60, 2.0, 1.0, 2.0, 0.15);
        loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1.2f, 1f);

        attacker.sendMessage(ChatColor.GOLD + "🔥🌪 ¡COMBO DE SINERGIA: TORMENTA DE FUEGO!");
        for (LivingEntity nearby : loc.getWorld().getNearbyLivingEntities(loc, 4.0)) {
            if (!nearby.equals(attacker) && !(nearby instanceof Player)) {
                nearby.setFireTicks(100);
                nearby.damage(150.0, attacker);
            }
        }
    }
}
