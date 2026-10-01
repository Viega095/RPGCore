package com.antigravity.rpg.bosses;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MythicWorldRaidEngine implements Listener {

    private final RPGCore plugin;
    private LivingEntity activeBoss = null;
    private BossBar bossBar = null;
    private int phase = 1;
    private double maxHealth = 1800.0;
    private final Map<UUID, Double> damageDealt = new ConcurrentHashMap<>();

    public MythicWorldRaidEngine(RPGCore plugin) {
        this.plugin = plugin;
    }

    public boolean isRaidActive() {
        return activeBoss != null && activeBoss.isValid() && !activeBoss.isDead();
    }

    public void spawnVoidSovereign(Location loc) {
        if (isRaidActive()) {
            activeBoss.remove();
        }

        this.damageDealt.clear();
        this.phase = 1;

        LivingEntity boss = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.WITHER);
        boss.setCustomName("§5✦ §d§lSOBERANO DEL VACÍO XYLAR §5[FASE 1] ✦");
        boss.setCustomNameVisible(true);
        boss.setMaxHealth(maxHealth);
        boss.setHealth(maxHealth);
        this.activeBoss = boss;

        this.bossBar = Bukkit.createBossBar(
                "§5✦ §dSOBERANO DEL VACÍO XYLAR §f(100%)",
                BarColor.PURPLE,
                BarStyle.SEGMENTED_10
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
            p.sendTitle("§5🐉 ¡JEFE MUNDIAL INVOCADO!", "§dEl Soberano del Vacío Xylar ha emergido", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.7f);
        }

        // Periodic Attack & Telegraph Task
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!isRaidActive()) {
                    if (bossBar != null) bossBar.removeAll();
                    cancel();
                    return;
                }

                ticks++;
                double hp = activeBoss.getHealth();
                double pct = Math.max(0.0, Math.min(1.0, hp / maxHealth));
                bossBar.setProgress(pct);
                bossBar.setTitle("§5✦ §dSOBERANO DEL VACÍO XYLAR §f(" + (int) (pct * 100) + "%) §c[FASE " + phase + "]");

                // Phase transitions
                if (hp < maxHealth * 0.35 && phase < 3) {
                    phase = 3;
                    activeBoss.setCustomName("§4✦ §c§lSOBERANO DEL VACÍO XYLAR §4[ENFURECIDO - FASE 3] ✦");
                    loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 1f, 1.5f);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ChatColor.DARK_RED + "☠ [Raid Boss] ¡Xylar entra en FASE 3! Lluvia de meteoros del vacío desatada.");
                    }
                } else if (hp < maxHealth * 0.70 && phase < 2) {
                    phase = 2;
                    activeBoss.setCustomName("§5✦ §d§lSOBERANO DEL VACÍO XYLAR §5[FASE 2 - VÓRTICE] ✦");
                    loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 1.2f);
                }

                // Telegraph AoE Attack every 6 seconds
                if (ticks % 120 == 0) {
                    executeTelegraphedRaidAttack(activeBoss.getLocation());
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }

    private void executeTelegraphedRaidAttack(Location center) {
        // Red telegraph ring on ground
        for (int i = 0; i < 20; i++) {
            double angle = i * Math.PI / 10;
            double x = center.getX() + 6 * Math.cos(angle);
            double z = center.getZ() + 6 * Math.sin(angle);
            center.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, new Location(center.getWorld(), x, center.getY() + 0.2, z),
                    3, new Particle.DustTransition(Color.RED, Color.BLACK, 1.5f));
        }

        // Detonate after 1.5s
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (center.getWorld() == null) return;
            center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center, 2);
            center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);

            for (Player p : center.getWorld().getPlayers()) {
                if (p.getLocation().distanceSquared(center) <= 36) { // within 6 blocks
                    p.damage(12.0);
                    p.sendMessage(ChatColor.RED + "💥 ¡Has sido golpeado por la Falla del Vacío de Xylar!");
                }
            }
        }, 30L);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(activeBoss) && event.getDamager() instanceof Player player) {
            damageDealt.merge(player.getUniqueId(), event.getFinalDamage(), Double::sum);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().equals(activeBoss)) {
            if (bossBar != null) bossBar.removeAll();

            Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");
            Bukkit.broadcastMessage(ChatColor.GOLD + "🐉   " + ChatColor.LIGHT_PURPLE + "¡EL SOBERANO DEL VACÍO XYLAR HA SIDO DERROTADO!" + ChatColor.GOLD + "   🐉");
            Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");

            // Award top contributors
            List<Map.Entry<UUID, Double>> top = new ArrayList<>(damageDealt.entrySet());
            top.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            for (int i = 0; i < Math.min(3, top.size()); i++) {
                Player p = Bukkit.getPlayer(top.get(i).getKey());
                if (p != null && p.isOnline()) {
                    p.sendMessage(ChatColor.GREEN + "🏆 [Raid Rank #" + (i + 1) + "] Daño infligido: " + (int) (double) top.get(i).getValue());
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
                    p.getInventory().addItem(new ItemStack(Material.NETHER_STAR));
                }
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");
            activeBoss = null;
        }
    }
}
