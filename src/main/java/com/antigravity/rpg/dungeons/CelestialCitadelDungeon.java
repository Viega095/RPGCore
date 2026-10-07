package com.antigravity.rpg.dungeons;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CelestialCitadelDungeon implements Listener {

    public static class CitadelSession {
        public final UUID id = UUID.randomUUID();
        public final Player leader;
        public final Location center;
        public int phase = 1;
        public int pillarsRemaining = 4;
        public final List<ArmorStand> pillarStands = new ArrayList<>();
        public final List<LivingEntity> spawnedEnemies = new ArrayList<>();
        public LivingEntity archangelBoss = null;
        public BossBar raidBar = null;
        public BukkitRunnable ticker = null;
        public int timeRemaining = 300; // 5 minutes

        public CitadelSession(Player leader, Location center) {
            this.leader = leader;
            this.center = center.clone();
        }
    }

    private final RPGCore plugin;
    private final Map<UUID, CitadelSession> activeSessions = new ConcurrentHashMap<>();

    public CelestialCitadelDungeon(RPGCore plugin) {
        this.plugin = plugin;
    }

    public boolean startCitadel(Player player) {
        if (activeSessions.containsKey(player.getUniqueId())) {
            player.sendMessage("§c✖ Ya tienes una incursión en La Ciudadela Celestial activa.");
            return false;
        }

        Location center = player.getLocation();
        CitadelSession session = new CitadelSession(player, center);
        activeSessions.put(player.getUniqueId(), session);

        session.raidBar = Bukkit.createBossBar(
                "§6⚡ §e§lLA CIUDADELA CELESTIAL §8| §fFase 1: Destruye los 4 Pilares Rúnicos",
                BarColor.YELLOW,
                BarStyle.SEGMENTED_6
        );
        session.raidBar.addPlayer(player);

        player.sendTitle("§6🏰 LA CIUDADELA CELESTIAL", "§eFase 1: Destruye los 4 Pilares Elementales", 10, 80, 20);
        player.playSound(center, Sound.ITEM_GOAT_HORN_SOUND_0, 1f, 0.8f);

        // Spawn 4 Elemental Pillars around the arena
        double[][] pillarOffsets = {
                {6, 0, 6}, {-6, 0, 6}, {6, 0, -6}, {-6, 0, -6}
        };
        Material[] pillarMats = {Material.SEA_LANTERN, Material.REDSTONE_BLOCK, Material.AMETHYST_BLOCK, Material.EMERALD_BLOCK};
        String[] pillarNames = {"§b✦ Pilar del Océano ✦", "§c✦ Pilar del Fuego ✦", "§d✦ Pilar del Vacío ✦", "§a✦ Pilar de la Tierra ✦"};

        for (int i = 0; i < 4; i++) {
            Location pLoc = center.clone().add(pillarOffsets[i][0], 0, pillarOffsets[i][2]);
            ArmorStand stand = (ArmorStand) center.getWorld().spawnEntity(pLoc.clone().add(0, -0.5, 0), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setCustomName(pillarNames[i]);
            stand.setCustomNameVisible(true);
            stand.getEquipment().setHelmet(new ItemStack(pillarMats[i]));
            session.pillarStands.add(stand);

            pLoc.getWorld().spawnParticle(Particle.PORTAL, pLoc, 30, 0.5, 1.0, 0.5, 0.1);
        }

        // Ticker loop
        session.ticker = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    failCitadel(session, "Has caído en combate.");
                    cancel();
                    return;
                }

                ticks++;
                if (ticks % 20 == 0) {
                    session.timeRemaining--;
                    if (session.timeRemaining <= 0) {
                        failCitadel(session, "Se ha agotado el tiempo de la incursión.");
                        cancel();
                        return;
                    }
                }

                // Render arena boundary
                for (int deg = 0; deg < 360; deg += 30) {
                    double rad = Math.toRadians(deg);
                    double bx = center.getX() + 14.0 * Math.cos(rad);
                    double bz = center.getZ() + 14.0 * Math.sin(rad);
                    Location bLoc = new Location(center.getWorld(), bx, center.getY() + 0.2, bz);
                    bLoc.getWorld().spawnParticle(Particle.END_ROD, bLoc, 1, 0, 0, 0, 0);
                }

                // Pillar visual beams
                if (session.phase == 1) {
                    for (ArmorStand pillar : session.pillarStands) {
                        if (pillar != null && pillar.isValid()) {
                            pillar.getWorld().spawnParticle(Particle.CRIT_MAGIC, pillar.getLocation().add(0, 1.5, 0), 2, 0.2, 0.5, 0.2, 0.05);
                        }
                    }
                }

                // Phase 3 Archangel Meteor attacks
                if (session.phase == 3 && session.archangelBoss != null && session.archangelBoss.isValid()) {
                    if (ticks % 60 == 0) {
                        // Meteor barrage on player location
                        Location target = player.getLocation();
                        player.sendMessage("§c⚠ ¡El Arcángel Caído conjura una Lluvia de Meteoros Celestiales!");
                        player.playSound(target, Sound.ENTITY_BLAZE_SHOOT, 1f, 0.5f);

                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            if (player.isOnline() && !player.isDead()) {
                                target.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, target, 1);
                                target.getWorld().spawnParticle(Particle.FLAME, target, 40, 1.5, 0.5, 1.5, 0.1);
                                target.getWorld().playSound(target, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.9f);
                                if (player.getLocation().distance(target) <= 3.5) {
                                    player.damage(14.0, session.archangelBoss);
                                }
                            }
                        }, 25L);
                    }
                }
            }
        };
        session.ticker.runTaskTimer(plugin, 0L, 1L);

        return true;
    }

    public void advanceToPhase2(CitadelSession session) {
        session.phase = 2;
        session.raidBar.setTitle("§6⚡ §e§lLA CIUDADELA CELESTIAL §8| §fFase 2: Purga la Guardia Celestial");
        session.raidBar.setColor(BarColor.PINK);

        Player player = session.leader;
        player.sendTitle("§d⚔ FASE 2: GUARDIA CELESTIAL", "§7Derrota a los guardianes antes de que el Arcángel despierte", 10, 60, 15);
        player.playSound(session.center, Sound.ENTITY_WITHER_SPAWN, 1f, 1.2f);

        // Spawn 4 elite celestial knights
        for (int i = 0; i < 4; i++) {
            double angle = (Math.PI / 2.0) * i;
            Location loc = session.center.clone().add(Math.cos(angle) * 5.0, 0, Math.sin(angle) * 5.0);
            WitherSkeleton knight = (WitherSkeleton) loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
            knight.setCustomName("§e✦ Guardián Celestial Valquiria ✦");
            knight.setCustomNameVisible(true);
            knight.setMaxHealth(120.0);
            knight.setHealth(120.0);
            knight.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
            knight.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
            session.spawnedEnemies.add(knight);
        }
    }

    public void advanceToPhase3(CitadelSession session) {
        session.phase = 3;
        session.raidBar.setTitle("§6👑 §e§lJEFE FINAL: EL ARCÁNGEL CAÍDO URIEL §8| §c1,800 HP");
        session.raidBar.setColor(BarColor.PURPLE);

        Player player = session.leader;
        player.sendTitle("§6👑 ¡EL ARCÁNGEL CAÍDO URIEL!", "§cEl regente de la Ciudadela Celestial ha descendido", 10, 80, 20);
        player.playSound(session.center, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.5f);

        Location bossLoc = session.center.clone().add(0, 1.5, 0);
        Warden boss = (Warden) bossLoc.getWorld().spawnEntity(bossLoc, EntityType.WARDEN);
        boss.setCustomName("§6✦ §e§lARCÁNGEL CAÍDO URIEL §6[REID MÍTICO] ✦");
        boss.setCustomNameVisible(true);
        boss.setMaxHealth(1800.0);
        boss.setHealth(1800.0);
        session.archangelBoss = boss;
    }

    public void completeCitadel(CitadelSession session) {
        activeSessions.remove(session.leader.getUniqueId());
        if (session.raidBar != null) session.raidBar.removeAll();
        if (session.ticker != null) session.ticker.cancel();

        Player player = session.leader;
        player.sendTitle("§a🏆 ¡CIUDADELA CELESTIAL PURGADA! 🏆", "§eHas conquistado el santuario divino", 10, 80, 20);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        player.spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 100, 1.0, 1.5, 1.0, 0.2);

        // Grant Mythic Reward: Celestial Wings & Divine Core
        ItemStack wings = createCelestialWings();
        ItemStack core = createCelestialCore(3);
        if (!player.getInventory().addItem(wings).isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), wings);
        }
        if (!player.getInventory().addItem(core).isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), core);
        }

        Bukkit.broadcastMessage("§6🏰✨ §l¡" + player.getName() + " §eha purgado §6La Ciudadela Celestial §ey obtenido las §f§lAlas de Luz Pura§6!");
    }

    public void failCitadel(CitadelSession session, String reason) {
        activeSessions.remove(session.leader.getUniqueId());
        if (session.raidBar != null) session.raidBar.removeAll();
        if (session.ticker != null) session.ticker.cancel();

        for (ArmorStand stand : session.pillarStands) {
            if (stand != null && stand.isValid()) stand.remove();
        }
        for (LivingEntity e : session.spawnedEnemies) {
            if (e != null && e.isValid()) e.remove();
        }
        if (session.archangelBoss != null && session.archangelBoss.isValid()) {
            session.archangelBoss.remove();
        }

        Player player = session.leader;
        player.sendMessage("§c☠ [CIUDADELA FALLIDA] " + reason);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.7f);
    }

    @EventHandler
    public void onDamagePillar(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand stand) {
            for (CitadelSession session : activeSessions.values()) {
                if (session.phase == 1 && session.pillarStands.contains(stand)) {
                    event.setCancelled(true);
                    session.pillarStands.remove(stand);
                    stand.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, stand.getLocation().add(0, 1, 0), 5);
                    stand.getWorld().playSound(stand.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1.4f);
                    stand.remove();

                    session.pillarsRemaining--;
                    session.leader.sendMessage("§a✔ ¡Pilar Rúnico destruido! Restantes: §e" + session.pillarsRemaining + "/4");
                    session.raidBar.setProgress(Math.max(0.0, (4.0 - session.pillarsRemaining) / 4.0));

                    if (session.pillarsRemaining <= 0) {
                        advanceToPhase2(session);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEnemyDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        for (CitadelSession session : activeSessions.values()) {
            if (session.phase == 2 && session.spawnedEnemies.contains(entity)) {
                session.spawnedEnemies.remove(entity);
                if (session.spawnedEnemies.isEmpty()) {
                    advanceToPhase3(session);
                }
                return;
            }

            if (session.phase == 3 && session.archangelBoss != null && session.archangelBoss.equals(entity)) {
                completeCitadel(session);
                return;
            }
        }
    }

    public static ItemStack createCelestialWings() {
        ItemStack item = new ItemStack(Material.ELYTRA);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§l✦ Alas de Luz Pura ✦");
            meta.setLore(Arrays.asList(
                    "§7Forjadas con plumas del Arcángel Uriel.",
                    "§a✦ Otorga: §fVuelo Mítico + Impulso Divino",
                    "§6✦ Pasiva: §e+30% Velocidad y Reducción de Daño de Caída",
                    "§d✦ Rareza: §5MÍTICA DIVINA"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createCelestialCore(int amount) {
        ItemStack item = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l⭐ Núcleo de Ascensión Celestial");
            meta.setLore(Arrays.asList(
                    "§7Condensación de energía divina pura.",
                    "§b• Utilizado en la Forja Mítica y Crafteos de Clase"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}
