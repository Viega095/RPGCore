package com.antigravity.rpg.models;

public enum Rarity {
    COMMON("§f", 1.0),
    UNCOMMON("§a", 1.2),
    RARE("§9", 1.5),
    EPIC("§5", 2.0),
    LEGENDARY("§6", 3.0);

    private final String color;
    private final double statMultiplier;

    Rarity(String color, double statMultiplier) {
        this.color = color;
        this.statMultiplier = statMultiplier;
    }

    public String getColor() {
        return color;
    }

    public double getStatMultiplier() {
        return statMultiplier;
    }
}
