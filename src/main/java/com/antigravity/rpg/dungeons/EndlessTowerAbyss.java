package com.antigravity.rpg.dungeons;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EndlessTowerAbyss implements Listener {

    public static class AbyssSession {
        public final Player player;
        public final Location centerLocation;
        public int currentFloor = 1;
        public final List<UUID> aliveMobs = new ArrayList<>();
        public boolean active = true;
        public BukkitTask boundaryTask;

        public AbyssSession(Player player, int startingFloor, Location centerLocation) {
            this.player = player;
            this.currentFloor = startingFloor;
            this.centerLocation = centerLocation.clone();
        }
    }

    private final RPGCore core;
    private final Map<UUID, AbyssSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> highestFloorRecords = new ConcurrentHashMap<>();

    public EndlessTowerAbyss(RPGCore core) {
        this.core = core;
    }

    public int getHighestFloor(UUID uuid) {
        return highestFloorRecords.getOrDefault(uuid, 1);
    }

    public boolean isPlayerInAbyss(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    public void startAbyss(Player player) {
        if (isPlayerInAbyss(player)) {
            player.sendMessage(ChatColor.RED + "✖ Ya tienes una ascensión al Abismo activa.");
            return;
        }

        int startFloor = 1;
        AbyssSession session = new AbyssSession(player, startFloor, player.getLocation());
        activeSessions.put(player.getUniqueId(), session);

        player.sendMessage("§5╔════════════════════════════════════════════════╗");
        player.sendMessage("§5║    " + ChatColor.LIGHT_PURPLE + "🌀 TORRE INFINITA: EL ABISMO SOMBRÍO" + ChatColor.DARK_PURPLE + "    ║");
        player.sendMessage("§5╚════════════════════════════════════════════════╝");
        player.sendMessage("§7¡Comenzando desafío en el §dPiso " + startFloor + "§7!");
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1f, 1.2f);

        // Start Boundary & Containment Task
        session.boundaryTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!session.active || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location center = session.centerLocation;
                // Draw circular boundary
                for (int deg = 0; deg < 360; deg += 18) {
                    double rad = Math.toRadians(deg);
                    double bx = center.getX() + 14.0 * Math.cos(rad);
                    double bz = center.getZ() + 14.0 * Math.sin(rad);
                    Location pLoc = new Location(center.getWorld(), bx, center.getY() + 0.3, bz);
                    pLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, pLoc, 1, 0, 0, 0, 0);
                }

                // Check and contain mobs + force player aggro
                for (UUID mobId : session.aliveMobs) {
                    org.bukkit.entity.Entity e = Bukkit.getEntity(mobId);
                    if (e instanceof LivingEntity living && living.isValid() && !living.isDead()) {
                        if (living.getLocation().distance(center) > 13.5) {
                            living.teleport(center.clone().add((Math.random() - 0.5) * 6, 0, (Math.random() - 0.5) * 6));
                            living.getWorld().spawnParticle(Particle.PORTAL, living.getLocation(), 15, 0.2, 0.2, 0.2, 0.05);
                        }
                        if (living instanceof Mob mobEntity) {
                            mobEntity.setTarget(player);
                        }
                    }
                }
            }
        }.runTaskTimer(core, 20L, 20L);

        spawnFloorWave(session);
    }

    private void spawnFloorWave(AbyssSession session) {
        Player player = session.player;
        Location spawnCenter = session.centerLocation;
        int floor = session.currentFloor;

        player.sendMessage("§6⚔ ¡Piso " + floor + "! Derrota a todos los enemigos abisales.");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.5f);

        int mobCount = 3 + (floor / 2);
        boolean isBossFloor = (floor % 5 == 0);

        if (isBossFloor) {
            LivingEntity boss = (LivingEntity) spawnCenter.getWorld().spawnEntity(spawnCenter, EntityType.WITHER_SKELETON);
            boss.setCustomName("§4§l☠ GUARDIÁN ABISAL DEL PISO " + floor);
            boss.setCustomNameVisible(true);
            double hp = 100.0 + (floor * 35.0);
            if (boss.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
                boss.setHealth(hp);
            }
            if (boss instanceof Mob mob) {
                mob.setTarget(player);
            }
            session.aliveMobs.add(boss.getUniqueId());
        } else {
            for (int i = 0; i < mobCount; i++) {
                Location mobLoc = spawnCenter.clone().add((Math.random() - 0.5) * 10, 0, (Math.random() - 0.5) * 10);
                EntityType type = (floor > 10) ? EntityType.PIGLIN_BRUTE : EntityType.ZOMBIE;
                LivingEntity mob = (LivingEntity) spawnCenter.getWorld().spawnEntity(mobLoc, type);
                mob.setCustomName("§c[Abyss Lv." + (floor * 2) + "] Engendro Sombrío");
                mob.setCustomNameVisible(true);
                double hp = 20.0 + (floor * 8.0);
                if (mob.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                    mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
                    mob.setHealth(hp);
                }
                if (mob instanceof Mob mobEntity) {
                    mobEntity.setTarget(player);
                }
                session.aliveMobs.add(mob.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        for (AbyssSession session : activeSessions.values()) {
            if (session.aliveMobs.remove(entity.getUniqueId())) {
                if (session.aliveMobs.isEmpty()) {
                    completeFloor(session);
                }
                break;
            }
        }
    }

    private void completeFloor(AbyssSession session) {
        Player player = session.player;
        int completed = session.currentFloor;

        int oldRecord = highestFloorRecords.getOrDefault(player.getUniqueId(), 1);
        if (completed > oldRecord) {
            highestFloorRecords.put(player.getUniqueId(), completed);
        }

        player.sendMessage("§a✔ ¡Piso " + completed + " conquistado!");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);

        // Floor Rewards
        player.getInventory().addItem(new ItemStack(Material.EMERALD, Math.max(1, completed / 2)));
        if (completed % 5 == 0) {
            player.getInventory().addItem(new ItemStack(Material.NETHERITE_SCRAP, 1));
            player.sendMessage("§6🎁 ¡Recompensa de Jefe de Piso obtenida!");
        }

        session.currentFloor++;

        // Next Floor Countdown
        new BukkitRunnable() {
            @Override
            public void run() {
                if (session.active && player.isOnline()) {
                    spawnFloorWave(session);
                }
            }
        }.runTaskLater(core, 60L);
    }

    public void leaveAbyss(Player player) {
        AbyssSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) {
            session.active = false;
            if (session.boundaryTask != null) {
                session.boundaryTask.cancel();
            }
            for (UUID u : session.aliveMobs) {
                org.bukkit.entity.Entity e = Bukkit.getEntity(u);
                if (e != null && e.isValid()) e.remove();
            }
            player.sendMessage(ChatColor.YELLOW + "Has abandonado tu ascensión a la Torre del Abismo.");
        }
    }
}
