package com.antigravity.rpg.listeners;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.*;
import com.antigravity.rpg.models.Quest;
import com.antigravity.rpg.models.QuestObjective;
import com.antigravity.rpg.models.RPGMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MobListener implements Listener {

    private final RPGCore core;
    private final MobManager mobManager;
    private final ItemManager itemManager;
    private final LevelManager levelManager;
    private final QuestManager questManager;
    private final Random random = new Random();

    public MobListener(RPGCore core, MobManager mobManager, ItemManager itemManager, LevelManager levelManager,
            QuestManager questManager) {
        this.core = core;
        this.mobManager = mobManager;
        this.itemManager = itemManager;
        this.levelManager = levelManager;
        this.questManager = questManager;
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        // Simple logic to replace vanilla mobs with RPG mobs randomly
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            LivingEntity entity = (LivingEntity) event.getEntity();

            // 20% chance to turn into a custom mob if not already one
            if (mobManager.getMobFromEntity(entity) == null && random.nextDouble() < 0.2) {
                // Determine which mob to spawn based on type
                // Simplified: just picking a default if type matches
                // In production: logic based on biome, distance from spawn, etc.
                if (entity.getType() == org.bukkit.entity.EntityType.ZOMBIE) {
                    mobManager.applyMobStats(entity, mobManager.getMob("zombie_lvl1"));
                } else if (entity.getType() == org.bukkit.entity.EntityType.SKELETON) {
                    mobManager.applyMobStats(entity, mobManager.getMob("skeleton_lvl5"));
                }
            }
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        RPGMob rpgMob = mobManager.getMobFromEntity(entity);

        if (killer != null && rpgMob != null) {
            // Give XP
            levelManager.addXp(killer, rpgMob.getXpReward());
            killer.sendMessage("§e+" + rpgMob.getXpReward() + " XP");

            // Handle Drops
            event.getDrops().clear(); // Clear vanilla drops
            Map<String, Double> dropTable = rpgMob.getDropTable();
            for (Map.Entry<String, Double> entry : dropTable.entrySet()) {
                if (random.nextDouble() < entry.getValue()) {
                    event.getDrops().add(itemManager.createItemStack(entry.getKey()));
                }
            }

            // Quest Progress Check
            handleQuestProgress(killer, rpgMob);
        }
    }

    private void handleQuestProgress(Player player, RPGMob mob) {
        // Iterate active quests for player
        List<Quest> quests = questManager.getActiveQuests(player.getUniqueId());
        for (Quest quest : quests) {
            QuestObjective objective = quest.getObjective();
            if (objective.getType() == QuestObjective.ObjectiveType.KILL_MOBS) {
                // Check if mob matches target
                if (objective.getTargetEntity() == mob.getType()) {
                    objective.incrementProgress(1);
                    player.sendMessage("§aQuest Progress: " + quest.getName() + " (" + objective.getCurrentAmount()
                            + "/" + objective.getTargetAmount() + ")");

                    if (objective.isComplete()) {
                        questManager.completeQuest(player.getUniqueId(), quest.getId(), player);
                    }
                }
            }
        }
    }
}
