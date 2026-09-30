package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MysticCrowQuestGiver implements Listener {

    public static class MysticBounty {
        public final String title;
        public final String objective;
        public final int targetCount;
        public final int rewardXp;
        public final String rewardItem;

        public MysticBounty(String title, String objective, int targetCount, int rewardXp, String rewardItem) {
            this.title = title;
            this.objective = objective;
            this.targetCount = targetCount;
            this.rewardXp = rewardXp;
            this.rewardItem = rewardItem;
        }
    }

    private final RPGCore core;
    private final Map<UUID, MysticBounty> activePlayerBounties = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> bountyProgress = new ConcurrentHashMap<>();
    private final Map<UUID, LivingEntity> activeCrows = new ConcurrentHashMap<>();

    private final List<MysticBounty> bounties = Arrays.asList(
            new MysticBounty("Purgación de Sombras", "Elimina 8 Monstruos en el Abismo o Mazmorras", 8, 800, "Gema Legendaria de Poder"),
            new MysticBounty("Caza del Devorador", "Derrota a 1 Jefe de Incursión o Monstruo Élite", 1, 1500, "Fragmento Rúnico Ancestral"),
            new MysticBounty("Ofrenda de Almas", "Completa 2 Oleadas en la Torre del Abismo", 2, 1200, "Caja de Reliquia Mítica"),
            new MysticBounty("La Cosecha Silenciosa", "Realiza 15 Golpes Críticos en Combate", 15, 650, "Poción de Esencia Arcana")
    );

    public MysticCrowQuestGiver(RPGCore core) {
        this.core = core;
    }

    public void spawnCrowNearPlayer(Player player) {
        dismissCrow(player);

        Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(2.5)).add(0, 1.2, 0);
        World world = loc.getWorld();
        if (world == null) return;

        Bat crow = (Bat) world.spawnEntity(loc, EntityType.BAT);
        crow.setCustomName("§8✦ §5Cuervo Místico de las Sombras §8✦");
        crow.setCustomNameVisible(true);
        crow.setAI(false);
        crow.setInvulnerable(true);

        activeCrows.put(player.getUniqueId(), crow);

        // Ambient spawn effects
        world.spawnParticle(Particle.PORTAL, loc, 40, 0.4, 0.4, 0.4, 0.5);
        world.spawnParticle(Particle.DRAGON_BREATH, loc, 25, 0.3, 0.3, 0.3, 0.05);
        world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 15, 0.2, 0.2, 0.2, 0.02);
        player.playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 0.8f, 1.8f);
        player.playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 1f, 0.8f);

        // Mystic Dialogues
        MysticBounty bounty = bounties.get(new Random().nextInt(bounties.size()));
        activePlayerBounties.put(player.getUniqueId(), bounty);
        bountyProgress.put(player.getUniqueId(), 0);

        player.sendMessage("");
        player.sendMessage("§8[§5Cuervo Místico§8] §d¡Cawww! Saludos mortal... Las sombras susurran tu destino.");
        player.sendMessage("§8[§5Cuervo Místico§8] §7He venido a entregarte una misión de las profundidades:");
        player.sendMessage("  §6✦ Misión: §e" + bounty.title);
        player.sendMessage("  §7Objetivo: §f" + bounty.objective);
        player.sendMessage("  §aRecompensa: §b+" + bounty.rewardXp + " XP §7y §6" + bounty.rewardItem);
        player.sendMessage("§8[§5Cuervo Místico§8] §d¡Cumple con tu destino antes de que las sombras reclamen tu alma!");
        player.sendMessage("");

        // Auto fly away after 12 seconds
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!crow.isValid() || !player.isOnline()) {
                    cancel();
                    return;
                }
                ticks++;
                if (ticks > 240) { // 12 seconds
                    flyAway(crow, player);
                    cancel();
                } else {
                    world.spawnParticle(Particle.SMOKE_NORMAL, crow.getLocation().add(0, 0.2, 0), 2, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }.runTaskTimer(core, 1L, 1L);
    }

    private void flyAway(LivingEntity crow, Player player) {
        if (crow != null && crow.isValid()) {
            Location loc = crow.getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 0.5, 0.5, 0.5, 0.1);
            loc.getWorld().spawnParticle(Particle.PORTAL, loc, 30, 0.5, 0.5, 0.5, 0.3);
            player.playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 1f, 1.2f);
            crow.remove();
            activeCrows.remove(player.getUniqueId());
            player.sendMessage("§8[§5Cuervo Místico§8] §7El cuervo de las sombras bate sus alas y se disuelve en el vacío...");
        }
    }

    public void dismissCrow(Player player) {
        LivingEntity crow = activeCrows.remove(player.getUniqueId());
        if (crow != null && crow.isValid()) {
            crow.remove();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        LivingEntity crow = activeCrows.get(player.getUniqueId());
        if (crow != null && crow.equals(event.getRightClicked())) {
            event.setCancelled(true);
            MysticBounty bounty = activePlayerBounties.get(player.getUniqueId());
            if (bounty != null) {
                int prog = bountyProgress.getOrDefault(player.getUniqueId(), 0);
                player.sendMessage("§8[§5Cuervo Místico§8] §7Tu progreso en §e" + bounty.title + "§7: §a" + prog + "/" + bounty.targetCount);
                player.playSound(player.getLocation(), Sound.ENTITY_BAT_AMBIENT, 1f, 1.2f);
            }
        }
    }

    public void advanceBounty(Player player, int amount) {
        MysticBounty bounty = activePlayerBounties.get(player.getUniqueId());
        if (bounty == null) return;

        int current = bountyProgress.getOrDefault(player.getUniqueId(), 0) + amount;
        bountyProgress.put(player.getUniqueId(), current);

        if (current >= bounty.targetCount) {
            // Completed!
            activePlayerBounties.remove(player.getUniqueId());
            bountyProgress.remove(player.getUniqueId());

            core.getManagerHandler().get(LevelManager.class).addXp(player, bounty.rewardXp);
            player.sendMessage("");
            player.sendMessage("§6✦ §5¡CONTRATO DEL CUERVO MÍSTICO COMPLETADO! §6✦");
            player.sendMessage("§7Has reclamado: §b+" + bounty.rewardXp + " XP §7y §6" + bounty.rewardItem + "!");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.4f);

            Location loc = player.getLocation().add(0, 1, 0);
            loc.getWorld().spawnParticle(Particle.TOTEM, loc, 40, 0.5, 0.5, 0.5, 0.2);
            loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 25, 0.4, 0.4, 0.4, 0.05);
        } else {
            player.sendMessage("§8[§5Cuervo Místico§8] §7Progreso de misión actualizado: §e" + current + "/" + bounty.targetCount);
        }
    }
}
