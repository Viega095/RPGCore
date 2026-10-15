package com.viega.rpg.skills;

import com.viega.rpg.models.Skill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChainLightningSkill extends Skill {

    public ChainLightningSkill() {
        super("ChainLightning", "Dispara un arco eléctrico que salta entre múltiples enemigos cercanos", 50, 9000, 5);
    }

    @Override
    public boolean cast(Player player, int level) {
        int maxJumps = 3 + (level / 2); // 3 a 5 saltos
        double damage = 9.0 + (level * 3.0);
        double range = 10.0;

        List<LivingEntity> hitTargets = new ArrayList<>();
        Location currentLoc = player.getEyeLocation();

        for (int jump = 0; jump < maxJumps; jump++) {
            LivingEntity nextTarget = null;
            double closestDist = Double.MAX_VALUE;

            for (Entity e : currentLoc.getWorld().getNearbyEntities(currentLoc, range, range, range)) {
                if (e instanceof LivingEntity candidate && e != player && !hitTargets.contains(candidate)) {
                    double dist = candidate.getLocation().distance(currentLoc);
                    if (dist < closestDist) {
                        closestDist = dist;
                        nextTarget = candidate;
                    }
                }
            }

            if (nextTarget == null) break;

            hitTargets.add(nextTarget);

            // Trazar línea de partículas de rayo
            Location targetLoc = nextTarget.getLocation().add(0, 1, 0);
            drawLightningLine(currentLoc, targetLoc);

            nextTarget.damage(damage, player);
            nextTarget.getWorld().strikeLightningEffect(targetLoc);
            nextTarget.getWorld().playSound(targetLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.6f);

            currentLoc = targetLoc;
        }

        if (hitTargets.isEmpty()) {
            player.sendMessage("§c¡No hay enemigos al alcance para la Cadena de Rayos!");
            return false;
        }

        player.sendMessage("§b§l✦ Cadena de Rayos§f saltó entre §e" + hitTargets.size() + "§f enemigos causando §c" + (int) damage + "§f de daño.");
        return true;
    }

    private void drawLightningLine(Location from, Location to) {
        int points = 12;
        for (int i = 0; i <= points; i++) {
            double ratio = (double) i / points;
            double x = from.getX() + (to.getX() - from.getX()) * ratio;
            double y = from.getY() + (to.getY() - from.getY()) * ratio;
            double z = from.getZ() + (to.getZ() - from.getZ()) * ratio;
            Location pLoc = new Location(from.getWorld(), x, y, z);
            from.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, pLoc, 3, 0.1, 0.1, 0.1, 0.05);
        }
    }
}
