package com.antigravity.rpg.models;

import org.bukkit.entity.Player;

public abstract class Skill {

    protected String name;
    protected String description;
    protected double manaCost;
    protected long cooldown; // in milliseconds
    protected int maxLevel;

    public Skill(String name, String description, double manaCost, long cooldown, int maxLevel) {
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.maxLevel = maxLevel;
    }

    /**
     * Execute the skill logic
     * 
     * @param player The player casting the skill
     * @param level  The skill level (1-5)
     * @return true if skill was successfully cast
     */
    public abstract boolean cast(Player player, int level);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getManaCost(int level) {
        // Scale mana cost slightly with level
        return manaCost * (1 + (level - 1) * 0.1);
    }

    public long getCooldown(int level) {
        // Reduce cooldown with level
        return (long) (cooldown * (1 - (level - 1) * 0.05));
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
