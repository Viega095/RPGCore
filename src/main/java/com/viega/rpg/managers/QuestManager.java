package com.viega.rpg.managers;

import com.viega.rpg.RPGCore;
import com.viega.rpg.models.Quest;
import com.viega.rpg.models.QuestObjective;
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
        // Misión normal
        Quest killZombies = new Quest("kill_zombies_1", "Cazador de Zombis", "Elimina 10 zombis en el mundo", Quest.QuestType.NORMAL);
        QuestObjective objective = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 10);
        objective.setTargetEntity(EntityType.ZOMBIE);
        killZombies.setObjective(objective);
        killZombies.setXpReward(350);
        killZombies.setLevelRequirement(1);
        registerQuest(killZombies);

        // Misión diaria
        Quest dailyMobs = new Quest("daily_kill_10", "Exterminio Diario", "Elimina 10 monstruos hostiles de cualquier tipo",
                Quest.QuestType.DAILY);
        QuestObjective dailyObj = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 10);
        dailyMobs.setObjective(dailyObj);
        dailyMobs.setXpReward(500);
        registerQuest(dailyMobs);

        // Desafíos de mazmorra y estructuras
        Quest cryptDungeon = new Quest("challenge_crypt", "Desafío: Cripta Abisal", "Supera los horrores de la Cripta Abisal", Quest.QuestType.DUNGEON);
        QuestObjective cryptObj = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 15);
        cryptObj.setTargetEntity(EntityType.WITHER_SKELETON);
        cryptDungeon.setObjective(cryptObj);
        cryptDungeon.setXpReward(2500);
        cryptDungeon.setLevelRequirement(5);
        registerQuest(cryptDungeon);

        Quest raidDragon = new Quest("challenge_raid_boss", "Cacería de Jefes: Dragón Infernal", "Enfréntate al Dragón Infernal en incursión", Quest.QuestType.NORMAL);
        QuestObjective dragonObj = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 1);
        dragonObj.setTargetEntity(EntityType.ENDER_DRAGON);
        raidDragon.setObjective(dragonObj);
        raidDragon.setXpReward(5000);
        raidDragon.setLevelRequirement(10);
        registerQuest(raidDragon);

        Quest ascensionQuest = new Quest("challenge_ascension", "Despertar Heroico", "Despierta tu clase en el Altar de Ascensión", Quest.QuestType.NORMAL);
        QuestObjective ascObj = new QuestObjective(QuestObjective.ObjectiveType.KILL_MOBS, 5);
        ascensionQuest.setObjective(ascObj);
        ascensionQuest.setXpReward(4000);
        ascensionQuest.setLevelRequirement(10);
        registerQuest(ascensionQuest);
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

        player.sendMessage("§a✔ ¡Misión completada: §e" + quest.getName() + "§a!");
        player.sendMessage("§e+" + (int) quest.getXpReward() + " XP de Clase");
    }

    public List<Quest> getActiveQuests(UUID uuid) {
        List<Quest> quests = new ArrayList<>();
        List<String> activeIds = activeQuests.get(uuid);
        if (activeIds != null) {
            for (String id : activeIds) {
                Quest q = registeredQuests.get(id);
                if (q != null)
                    quests.add(q);
            }
        }
        return quests;
    }

    public boolean hasCompletedQuest(UUID uuid, String questId) {
        Set<String> completed = completedQuests.get(uuid);
        return completed != null && completed.contains(questId);
    }
}
