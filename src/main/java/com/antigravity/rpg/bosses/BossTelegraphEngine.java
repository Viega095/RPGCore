package com.antigravity.rpg.bosses;

import com.antigravity.rpg.RPGCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BossTelegraphEngine {

    private final RPGCore core;

    public BossTelegraphEngine(RPGCore core) {
        this.core = core;
    }

    public void drawCircleTelegraph(Location center, double radius, int durationTicks, Runnable onExplode) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 2;
                if (ticks >= durationTicks) {
                    this.cancel();
                    center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center, 2);
                    center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 1.0f);
                    if (onExplode != null) {
                        onExplode.run();
                    }
                    return;
                }

                // Draw perimeter circle
                int points = (int) (radius * 12);
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    Location pLoc = center.clone().add(x, 0.1, z);
                    pLoc.getWorld().spawnParticle(Particle.REDSTONE, pLoc, 1, 0, 0, 0,
                            new Particle.DustOptions(Color.RED, 1.4f));
                }

                // Pulsing center indicator
                if (ticks % 6 == 0) {
                    center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.5f + (ticks / (float) durationTicks));
                }
            }
        }.runTaskTimer(core, 0L, 2L);
    }

    public void drawConeTelegraph(Location origin, Vector direction, double angleDegrees, double length, int durationTicks, Runnable onHit) {
        Vector dirNorm = direction.clone().setY(0).normalize();
        double halfAngleRad = Math.toRadians(angleDegrees / 2.0);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 2;
                if (ticks >= durationTicks) {
                    this.cancel();
                    origin.getWorld().playSound(origin, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.2f, 1.2f);
                    if (onHit != null) {
                        onHit.run();
                    }
                    return;
                }

                // Draw cone boundary rays
                for (double d = 1.0; d <= length; d += 1.0) {
                    Vector leftRay = rotateVector(dirNorm, halfAngleRad).multiply(d);
                    Vector rightRay = rotateVector(dirNorm, -halfAngleRad).multiply(d);

                    Location lLoc = origin.clone().add(leftRay).add(0, 0.1, 0);
                    Location rLoc = origin.clone().add(rightRay).add(0, 0.1, 0);

                    lLoc.getWorld().spawnParticle(Particle.REDSTONE, lLoc, 1, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 1.2f));
                    rLoc.getWorld().spawnParticle(Particle.REDSTONE, rLoc, 1, 0, 0, 0, new Particle.DustOptions(Color.ORANGE, 1.2f));
                }
            }
        }.runTaskTimer(core, 0L, 2L);
    }

    private Vector rotateVector(Vector v, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double x = v.getX() * cos - v.getZ() * sin;
        double z = v.getX() * sin + v.getZ() * cos;
        return new Vector(x, v.getY(), z);
    }
}
