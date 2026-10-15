package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class GroundSlamSkill extends Skill {

    public GroundSlamSkill() {
        super("GroundSlam", "Salta y azota el suelo provocando una onda sísmica expansiva y aturdimiento", 40, 8000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        Location loc = player.getLocation();
        double radius = 4.0 + (level * 0.5);
        double damage = 10.0 + (level * 4.0);

        // Impulso inicial
        player.setVelocity(new Vector(0, 0.45, 0));
        player.playSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 1f, 0.8f);

        // Impacto sísmico en el suelo tras pequeño lapso
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("RPGCore"), () -> {
            if (!player.isOnline()) return;
            Location impact = player.getLocation();

            impact.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, impact, 2, 0.5, 0.1, 0.5, 0.05);
            impact.getWorld().spawnParticle(Particle.BLOCK_CRACK, impact, 60, 2.0, 0.2, 2.0, org.bukkit.Material.STONE.createBlockData());
            impact.getWorld().playSound(impact, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.8f);
            impact.getWorld().playSound(impact, Sound.BLOCK_ANVIL_LAND, 1.0f, 0.6f);

            int affected = 0;
            for (Entity entity : impact.getWorld().getNearbyEntities(impact, radius, radius, radius)) {
                if (entity instanceof LivingEntity target && entity != player) {
                    target.damage(damage, player);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * (2 + level), 2));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 0));
                    affected++;
                }
            }

            if (affected > 0) {
                player.sendMessage("§6§l✦ Golpe Sísmico§e aturdió a §f" + affected + "§e enemigos infligiendo §c" + (int) damage + "§e de daño.");
            }
        }, 8L);

        return true;
    }
}
