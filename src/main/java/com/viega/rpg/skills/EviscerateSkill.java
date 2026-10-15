package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EviscerateSkill extends Skill {

    public EviscerateSkill() {
        super("Eviscerate", "Ataque cuerpo a cuerpo fulminante que desgarra al objetivo provocando sangrado continuo", 45, 8000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        double range = 4.0;
        LivingEntity target = null;
        double closest = Double.MAX_VALUE;

        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof LivingEntity le && e != player) {
                double dist = player.getLocation().distance(le.getLocation());
                if (dist < closest) {
                    closest = dist;
                    target = le;
                }
            }
        }

        if (target == null) {
            player.sendMessage("§c¡No hay objetivos a quemarropa para ejecutar Golpe Eviscerador!");
            return false;
        }

        double burstDamage = 15.0 + (level * 5.0);
        target.damage(burstDamage, player);

        Location tLoc = target.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, tLoc, 2, 0.2, 0.2, 0.2, 0.1);
        player.getWorld().spawnParticle(Particle.REDSTONE, tLoc, 40, 0.4, 0.4, 0.4,
                new Particle.DustOptions(org.bukkit.Color.RED, 2));
        player.playSound(tLoc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.2f, 0.6f);
        player.playSound(tLoc, Sound.ENTITY_IRON_GOLEM_DAMAGE, 1.0f, 1.6f);

        final LivingEntity bleedTarget = target;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("RPGCore");
        if (plugin != null) {
            new BukkitRunnable() {
                int bleedTicks = 0;

                @Override
                public void run() {
                    if (bleedTicks++ >= 4 || !bleedTarget.isValid() || bleedTarget.isDead()) {
                        cancel();
                        return;
                    }
                    bleedTarget.damage(3.0 + level, player);
                    bleedTarget.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, bleedTarget.getLocation().add(0, 1, 0), 4, 0.2, 0.2, 0.2, 0.05);
                    bleedTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 1));
                }
            }.runTaskTimer(plugin, 20L, 20L);
        }

        player.sendMessage("§4§l✦ Golpe Eviscerador§c destrozó al objetivo causando §f" + (int) burstDamage + "§c de daño y hemorragia.");
        return true;
    }
}
