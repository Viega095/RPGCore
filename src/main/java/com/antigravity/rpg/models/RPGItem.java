package com.antigravity.rpg.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RPGItem {

    private String id;
    private Material material;
    private String displayName;
    private Rarity rarity;
    private int levelRequirement;
    private RPGClass classRequirement;
    private Map<String, Double> stats;
    private int gemSlots;

    public RPGItem(String id, Material material, String displayName, Rarity rarity) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.rarity = rarity;
        this.levelRequirement = 1;
        this.classRequirement = RPGClass.NONE;
        this.stats = new HashMap<>();
        this.gemSlots = 0;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(int level) {
        this.levelRequirement = level;
    }

    public RPGClass getClassRequirement() {
        return classRequirement;
    }

    public void setClassRequirement(RPGClass rpgClass) {
        this.classRequirement = rpgClass;
    }

    public Map<String, Double> getStats() {
        return stats;
    }

    public void addStat(String stat, double value) {
        stats.put(stat, value);
    }

    public int getGemSlots() {
        return gemSlots;
    }

    public void setGemSlots(int slots) {
        this.gemSlots = slots;
    }

    public ItemStack toItemStack() {
        // This will be implemented in ItemManager
        return new ItemStack(material);
    }
}
