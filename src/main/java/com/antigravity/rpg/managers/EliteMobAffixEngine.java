package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.RPGMob;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EliteMobAffixEngine implements Manager, Listener {

    public enum Affix {
        MOLTEN("§6[Molten]", ChatColor.GOLD, Particle.FLAME),
        VORTEX("§5[Vortex]", ChatColor.DARK_PURPLE, Particle.PORTAL),
        ELECTRIFIED("§b[Electrified]", ChatColor.AQUA, Particle.ELECTRIC_SPARK),
        SHIELDED("§f[Shielded]", ChatColor.WHITE, Particle.SPELL_WITCH),
        VAMPIRIC("§c[Vampiric]", ChatColor.RED, Particle.HEART);

        private final String tag;
        private final ChatColor color;
        private final Particle particle;

        Affix(String tag, ChatColor color, Particle particle) {
            this.tag = tag;
            this.color = color;
            this.particle = particle;
        }

        public String getTag() {
            return tag;
        }

        public ChatColor getColor() {
            return color;
        }

        public Particle getParticle() {
            return particle;
        }
    }

    private RPGCore core;
    private NamespacedKey eliteAffixKey;
    private final Map<UUID, Set<Affix>> activeElites = new ConcurrentHashMap<>();
    private final Map<UUID, Long> shieldedCooldown = new ConcurrentHashMap<>();

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.eliteAffixKey = new NamespacedKey(core, "elite_affixes");
        core.getServer().getPluginManager().registerEvents(this, core);
        startAuraTask();
    }

    @Override
    public void onDisable() {
        activeElites.clear();
    }

    private void startAuraTask() {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                Iterator<Map.Entry<UUID, Set<Affix>>> it = activeElites.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<UUID, Set<Affix>> entry = it.next();
                    Entity entity = Bukkit.getEntity(entry.getKey());
                    if (entity == null || !entity.isValid() || entity.isDead()) {
                        it.remove();
                        continue;
                    }

                    if (entity instanceof LivingEntity living) {
                        Location loc = living.getLocation().add(0, 1.0, 0);
                        Set<Affix> affixes = entry.getValue();

                        for (Affix affix : affixes) {
                            loc.getWorld().spawnParticle(affix.getParticle(), loc, 4, 0.4, 0.4, 0.4, 0.02);

                            // Vortex Pull every 6 seconds (120 ticks)
                            if (affix == Affix.VORTEX && ticks % 120 == 0) {
                                for (Entity nearby : living.getNearbyEntities(10, 10, 10)) {
                                    if (nearby instanceof Player targetPlayer) {
                                        Vector pull = living.getLocation().toVector().subtract(targetPlayer.getLocation().toVector()).normalize().multiply(0.8).setY(0.3);
                                        targetPlayer.setVelocity(pull);
                                        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 0.5f);
                                        targetPlayer.sendMessage(ChatColor.DARK_PURPLE + "⚡ ¡La fuerza del Vórtice te arrastra hacia el Élite!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(core, 20L, 10L);
    }

    public LivingEntity spawnEliteMob(Location loc, String mobId, Affix... affixes) {
        LivingEntity entity = core.getManagerHandler().getManager(MobManager.class).spawnMob(mobId, loc);
        if (entity == null) {
            entity = (LivingEntity) loc.getWorld().spawnEntity(loc, org.bukkit.entity.EntityType.ZOMBIE);
        }

        Set<Affix> affixSet = new HashSet<>(Arrays.asList(affixes));
        if (affixSet.isEmpty()) {
            Affix[] all = Affix.values();
            affixSet.add(all[new Random().nextInt(all.length)]);
        }

        // Boost HP and Damage
        double hp = entity.getMaxHealth() * 3.0;
        if (entity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
            entity.setHealth(hp);
        }

        // Title with Affixes
        StringBuilder nameBuilder = new StringBuilder("§6⭐ ");
        for (Affix a : affixSet) {
            nameBuilder.append(a.getTag()).append(" ");
        }
        nameBuilder.append(entity.getCustomName() != null ? entity.getCustomName() : "§cElite Mob");
        entity.setCustomName(nameBuilder.toString());
        entity.setCustomNameVisible(true);

        activeElites.put(entity.getUniqueId(), affixSet);
        return entity;
    }

    @EventHandler
    public void onEliteTakeDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity living && activeElites.containsKey(living.getUniqueId())) {
            Set<Affix> affixes = activeElites.get(living.getUniqueId());

            // Shielded logic
            if (affixes.contains(Affix.SHIELDED)) {
                long now = System.currentTimeMillis();
                long lastShield = shieldedCooldown.getOrDefault(living.getUniqueId(), 0L);
                if (now - lastShield < 5000) { // Shield active for 5s
                    event.setDamage(event.getDamage() * 0.25);
                    living.getWorld().spawnParticle(Particle.SPELL_WITCH, living.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
                    if (event.getDamager() instanceof Player p) {
                        p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1.2f);
                    }
                } else if (now - lastShield > 12000) {
                    shieldedCooldown.put(living.getUniqueId(), now);
                    living.getWorld().spawnParticle(Particle.TOTEM, living.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.2);
                }
            }

            // Molten retaliation
            if (affixes.contains(Affix.MOLTEN) && event.getDamager() instanceof Player p) {
                p.setFireTicks(80);
                p.getWorld().spawnParticle(Particle.LAVA, p.getLocation().add(0, 1, 0), 8, 0.3, 0.3, 0.3, 0.1);
            }

            // Electrified retaliation
            if (affixes.contains(Affix.ELECTRIFIED) && event.getDamager() instanceof Player p) {
                p.damage(4.0);
                p.getWorld().strikeLightningEffect(p.getLocation());
                p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.5f);
            }
        }

        // Vampiric attack
        if (event.getDamager() instanceof LivingEntity damager && activeElites.containsKey(damager.getUniqueId())) {
            Set<Affix> affixes = activeElites.get(damager.getUniqueId());
            if (affixes.contains(Affix.VAMPIRIC)) {
                double heal = event.getDamage() * 0.35;
                double newHp = Math.min(damager.getMaxHealth(), damager.getHealth() + heal);
                damager.setHealth(newHp);
                damager.getWorld().spawnParticle(Particle.HEART, damager.getLocation().add(0, 1.5, 0), 4, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }

    @EventHandler
    public void onEliteDeath(EntityDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (activeElites.containsKey(living.getUniqueId())) {
            activeElites.remove(living.getUniqueId());
            shieldedCooldown.remove(living.getUniqueId());

            // Bonus Elite Loot Drops
            event.setDroppedExp(event.getDroppedExp() * 4);
            living.getWorld().dropItemNaturally(living.getLocation(), new ItemStack(Material.GOLD_INGOT, 8 + new Random().nextInt(10)));
            living.getWorld().dropItemNaturally(living.getLocation(), new ItemStack(Material.EMERALD, 3 + new Random().nextInt(5)));
            living.getWorld().spawnParticle(Particle.TOTEM, living.getLocation().add(0, 1, 0), 50, 0.8, 0.8, 0.8, 0.3);
            living.getWorld().playSound(living.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

            if (living.getKiller() != null) {
                living.getKiller().sendMessage(ChatColor.GOLD + "✦ ¡Has derrotado a un Monstruo Élite y reclamado su botín aumentado!");
            }
        }
    }
}
