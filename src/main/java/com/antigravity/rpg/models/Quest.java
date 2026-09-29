package com.antigravity.rpg.models;

public class Quest {

    private String id;
    private String name;
    private String description;
    private QuestType type;
    private QuestObjective objective;
    private int xpReward;
    private String itemReward;
    private int levelRequirement;

    public Quest(String id, String name, String description, QuestType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.xpReward = 100;
        this.levelRequirement = 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QuestType getType() {
        return type;
    }

    public QuestObjective getObjective() {
        return objective;
    }

    public void setObjective(QuestObjective objective) {
        this.objective = objective;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xp) {
        this.xpReward = xp;
    }

    public String getItemReward() {
        return itemReward;
    }

    public void setItemReward(String itemId) {
        this.itemReward = itemId;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(int level) {
        this.levelRequirement = level;
    }

    public enum QuestType {
        NORMAL,
        DAILY,
        CHAIN
    }
}
