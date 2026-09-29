package com.antigravity.rpg.models;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class RPGMob {

    private String id;
    private String name;
    private EntityType type;
    private int level;
    private double maxHealth;
    private double damage;
    private int xpReward;
    private Map<String, Double> dropTable; // Item ID -> Chance

    public RPGMob(String id, String name, EntityType type, int level) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.maxHealth = 20 + (level * 5);
        this.damage = 2 + (level * 1.5);
        this.xpReward = 10 + (level * 5);
        this.dropTable = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EntityType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double health) {
        this.maxHealth = health;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xp) {
        this.xpReward = xp;
    }

    public Map<String, Double> getDropTable() {
        return dropTable;
    }

    public void addDrop(String itemId, double chance) {
        dropTable.put(itemId, chance);
    }
}
