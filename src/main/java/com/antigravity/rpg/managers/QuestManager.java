package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.Quest;
import com.antigravity.rpg.models.QuestObjective;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class QuestManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;
    private LevelManager levelManager;
    private ItemManager itemManager;

    private final Map<String, Quest> registeredQuests = new HashMap<>();
    private final Map<UUID, List<String>> activeQuests = new HashMap<>();
    private final Map<UUID, Set<String>> completedQuests = new HashMap<>();

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        registerDefaultQuests();
    }

    public void setManagers(PlayerManager pm, LevelManager lm, ItemManager im) {
        this.playerManager = pm;
        this.levelManager = lm;
        this.itemManager = im;
    }

    @Override
    public void onDisable() {

    }

    private void registerDefaultQuests() {
        // Example quest: Kill 10 zombies
        Quest killZombies = new Quest("kill_zombies_1", "Zombie Slayer", "Kill 10 zombies", Quest.QuestType.NORMAL);
        QuestObjective objective = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 10);
        objective.setTargetEntity(EntityType.ZOMBIE);
        killZombies.setObjective(objective);
        killZombies.setXpReward(150);
        killZombies.setLevelRequirement(1);
        registerQuest(killZombies);

        // Daily quest
        Quest dailyMobs = new Quest("daily_kill_10", "Daily Extermination", "Kill 10 mobs of any type",
                Quest.QuestType.DAILY);
        QuestObjective dailyObj = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 10);
        dailyMobs.setObjective(dailyObj);
        dailyMobs.setXpReward(200);
        registerQuest(dailyMobs);
    }

    public void registerQuest(Quest quest) {
        registeredQuests.put(quest.getId(), quest);
    }

    public void startQuest(UUID uuid, String questId) {
        Quest quest = registeredQuests.get(questId);
        if (quest == null)
            return;

        activeQuests.computeIfAbsent(uuid, k -> new ArrayList<>()).add(questId);
    }

    public void completeQuest(UUID uuid, String questId, Player player) {
        Quest quest = registeredQuests.get(questId);
        if (quest == null)
            return;

        // Remove from active
        List<String> active = activeQuests.get(uuid);
        if (active != null) {
            active.remove(questId);
        }

        // Add to completed
        completedQuests.computeIfAbsent(uuid, k -> new HashSet<>()).add(questId);

        // Give rewards
        if (levelManager != null) {
            levelManager.addXp(player, quest.getXpReward());
        }

        if (quest.getItemReward() != null && itemManager != null) {
            player.getInventory().addItem(itemManager.createItemStack(quest.getItemReward()));
        }

        player.sendMessage("§a✔ Quest Completed: " + quest.getName());
        player.sendMessage("§e+" + quest.getXpReward() + " XP");
    }

    public List<Quest> getActiveQuests(UUID uuid) {
        List<Quest> quests = new ArrayList<>();
        List<String> activeIds = activeQuests.get(uuid);
        if (activeIds != null) {
            for (String id : activeIds) {
                Quest quest = registeredQuests.get(id);
                if (quest != null)
                    quests.add(quest);
            }
        }
        return quests;
    }

    public boolean isQuestActive(UUID uuid, String questId) {
        List<String> active = activeQuests.get(uuid);
        return active != null && active.contains(questId);
    }

    public Collection<Quest> getAvailableQuests() {
        return registeredQuests.values();
    }
}
