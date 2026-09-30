package com.antigravity.rpg.dungeons;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.ItemManager;
import com.antigravity.rpg.managers.MobManager;
import com.antigravity.rpg.managers.PlayerManager;
import com.antigravity.rpg.models.RPGItem;
import com.antigravity.rpg.models.RPGMob;
import com.antigravity.rpg.models.Rarity;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonEngine {

    public enum DungeonDifficulty {
        CRYPTS_OF_DESPAIR("Criptas de la Desesperación", 1, 5, 20, Material.BONE),
        INFERNAL_SPIRE("Aguja Infernal", 20, 35, 30, Material.BLAZE_POWDER),
        ABYSSAL_SANCTUM("Santuario Abisal del Caos", 40, 60, 45, Material.ENDER_EYE);

        public final String name;
        public final int minLevel;
        public final int maxLevel;
        public final int timeLimitSeconds;
        public final Material icon;

        DungeonDifficulty(String name, int minLevel, int maxLevel, int timeLimitSeconds, Material icon) {
            this.name = name;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.timeLimitSeconds = timeLimitSeconds;
            this.icon = icon;
        }
    }

    public static class DungeonSession {
        public final UUID dungeonId = UUID.randomUUID();
        public final Player player;
        public final DungeonDifficulty difficulty;
        public final Location arenaLocation;
        public int currentWave = 1;
        public int maxWaves = 4;
        public int timeRemaining;
        public final List<LivingEntity> spawnedMobs = new ArrayList<>();
        public boolean completed = false;
        public BukkitTask ticker;

        public DungeonSession(Player player, DungeonDifficulty difficulty, Location arenaLocation) {
            this.player = player;
            this.difficulty = difficulty;
            this.arenaLocation = arenaLocation;
            this.timeRemaining = difficulty.timeLimitSeconds;
        }
    }

    private final RPGCore core;
    private final PlayerManager playerManager;
    private final MobManager mobManager;
    private final ItemManager itemManager;
    private final Map<UUID, DungeonSession> activeSessions = new ConcurrentHashMap<>();

    public DungeonEngine(RPGCore core, PlayerManager pm, MobManager mm, ItemManager im) {
        this.core = core;
        this.playerManager = pm;
        this.mobManager = mm;
        this.itemManager = im;
    }

    public boolean startDungeon(Player player, DungeonDifficulty diff) {
        if (activeSessions.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "✖ Ya te encuentras dentro de una mazmorra activa.");
            return false;
        }

        Location arenaLoc = player.getLocation();
        DungeonSession session = new DungeonSession(player, diff, arenaLoc);
        activeSessions.put(player.getUniqueId(), session);

        player.sendMessage(ChatColor.DARK_PURPLE + "========================================");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "⚔ [MAZMORRA INICIADA] " + ChatColor.GOLD + diff.name);
        player.sendMessage(ChatColor.GRAY + "Sobrevive a " + session.maxWaves + " oleadas de monstruos de élite.");
        player.sendMessage(ChatColor.YELLOW + "Tiempo límite: " + diff.timeLimitSeconds + "s");
        player.sendMessage(ChatColor.DARK_PURPLE + "========================================");

        player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 1f, 0.9f);

        spawnWave(session);

        session.ticker = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    failDungeon(session, "Has perecido en el abismo.");
                    cancel();
                    return;
                }

                session.timeRemaining--;

                // Render circular arena particle boundary
                Location center = session.arenaLocation;
                for (int deg = 0; deg < 360; deg += 18) {
                    double rad = Math.toRadians(deg);
                    double bx = center.getX() + 14.0 * Math.cos(rad);
                    double bz = center.getZ() + 14.0 * Math.sin(rad);
                    Location pLoc = new Location(center.getWorld(), bx, center.getY() + 0.3, bz);
                    pLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, pLoc, 1, 0, 0, 0, 0);
                }

                // Contain mobs and force aggro to the player
                for (LivingEntity mob : session.spawnedMobs) {
                    if (mob.isValid() && !mob.isDead()) {
                        if (mob.getLocation().distance(center) > 13.5) {
                            mob.teleport(center.clone().add((Math.random() - 0.5) * 6, 0, (Math.random() - 0.5) * 6));
                            mob.getWorld().spawnParticle(Particle.PORTAL, mob.getLocation(), 15, 0.2, 0.2, 0.2, 0.05);
                        }
                        if (mob instanceof org.bukkit.entity.Mob mobEntity) {
                            mobEntity.setTarget(player);
                        }
                    }
                }

                // Actionbar status
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ChatColor.RED + "⚡ Oleada: " + session.currentWave + "/" + session.maxWaves +
                        " §8| §eEnemigos restantes: " + getAliveCount(session) +
                        " §8| §cTiempo: " + session.timeRemaining + "s"));

                // Check wave clear
                session.spawnedMobs.removeIf(e -> e.isDead() || !e.isValid());
                if (session.spawnedMobs.isEmpty()) {
                    if (session.currentWave < session.maxWaves) {
                        session.currentWave++;
                        player.sendMessage(ChatColor.GREEN + "✔ ¡Oleada superada! Preparando Oleada " + session.currentWave + "...");
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
                        spawnWave(session);
                    } else {
                        completeDungeon(session);
                        cancel();
                        return;
                    }
                }

                if (session.timeRemaining <= 0) {
                    failDungeon(session, "¡Se ha agotado el tiempo de la mazmorra!");
                    cancel();
                }
            }
        }.runTaskTimer(core, 20L, 20L);

        return true;
    }

    private int getAliveCount(DungeonSession session) {
        int count = 0;
        for (LivingEntity entity : session.spawnedMobs) {
            if (entity.isValid() && !entity.isDead()) count++;
        }
        return count;
    }

    private void spawnWave(DungeonSession session) {
        Location center = session.arenaLocation;
        int mobCount = 2 + (session.currentWave * 2);

        for (int i = 0; i < mobCount; i++) {
            double angle = (2 * Math.PI / mobCount) * i;
            double radius = 4.0 + (session.currentWave * 0.5);
            Location spawnLoc = center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);

            RPGMob template = getRandomDungeonMob(session.difficulty, session.currentWave);
            if (template != null) {
                LivingEntity entity = mobManager.spawnMob(template.getId(), spawnLoc);
                if (entity != null) {
                    session.spawnedMobs.add(entity);
                    spawnLoc.getWorld().spawnParticle(Particle.PORTAL, spawnLoc, 25, 0.3, 0.5, 0.3, 0.1);
                }
            }
        }

        if (session.currentWave == session.maxWaves) {
            // Boss announcement
            session.player.sendMessage(ChatColor.RED + "☠ ¡EL JEFE DE LA MAZMORRA HA ENTRADO EN LA ARENA!");
            session.player.playSound(center, Sound.ENTITY_WITHER_SPAWN, 1f, 0.6f);
        }
    }

    private RPGMob getRandomDungeonMob(DungeonDifficulty diff, int wave) {
        Collection<RPGMob> allMobs = mobManager.getAllMobs();
        if (allMobs.isEmpty()) return null;
        List<RPGMob> list = new ArrayList<>(allMobs);
        return list.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(list.size()));
    }

    public void completeDungeon(DungeonSession session) {
        session.completed = true;
        activeSessions.remove(session.player.getUniqueId());

        Player player = session.player;
        player.sendMessage(ChatColor.GOLD + "========================================");
        player.sendMessage(ChatColor.GREEN + "🏆 ¡MAZMORRA COMPLETADA CON ÉXITO! 🏆");
        player.sendMessage(ChatColor.YELLOW + "Has purgado la amenaza de " + session.difficulty.name);
        player.sendMessage(ChatColor.GOLD + "========================================");

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        player.spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 100, 0.8, 1.2, 0.8, 0.3);

        // Dungeon Chest Reward Drops
        RPGItem rewardItem = itemManager.getItem("abyssal_blade");
        if (rewardItem != null) {
            ItemStack stack = itemManager.createItemStack("abyssal_blade");
            if (stack != null) {
                player.getInventory().addItem(stack);
            }
            player.sendMessage(ChatColor.AQUA + "🎁 Recompensa obtenida: " + rewardItem.getDisplayName());
        }
    }

    public void failDungeon(DungeonSession session, String reason) {
        activeSessions.remove(session.player.getUniqueId());
        // Clean up remaining mobs
        for (LivingEntity mob : session.spawnedMobs) {
            if (mob.isValid()) mob.remove();
        }

        Player player = session.player;
        player.sendMessage(ChatColor.RED + "☠ [MAZMORRA FALLIDA] " + reason);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.7f);
    }

    public void cancelSession(UUID playerId) {
        DungeonSession session = activeSessions.remove(playerId);
        if (session != null) {
            if (session.ticker != null) session.ticker.cancel();
            for (LivingEntity mob : session.spawnedMobs) {
                if (mob.isValid()) mob.remove();
            }
        }
    }

    public DungeonSession getSession(UUID playerId) {
        return activeSessions.get(playerId);
    }
}
