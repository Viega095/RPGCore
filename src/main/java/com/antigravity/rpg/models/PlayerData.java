package com.antigravity.rpg.models;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private RPGClass rpgClass;
    private int level;
    private double xp;
    private double currentMana;
    private double maxMana;

    // Stats
    private double strength;
    private double intelligence;
    private double dexterity;
    private double defense;
    private java.util.List<String> gemBag;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.rpgClass = RPGClass.NONE;
        this.level = 1;
        this.xp = 0;
        this.currentMana = 100;
        this.maxMana = 100;
        this.strength = 10;
        this.intelligence = 10;
        this.dexterity = 10;
        this.defense = 0;
        this.gemBag = new java.util.ArrayList<>();
    }

    public java.util.List<String> getGemBag() {
        return gemBag;
    }

    public void addGem(String gemId) {
        gemBag.add(gemId);
    }

    public void removeGem(String gemId) {
        gemBag.remove(gemId);
    }

    public UUID getUuid() {
        return uuid;
    }

    public RPGClass getRpgClass() {
        return rpgClass;
    }

    public void setRpgClass(RPGClass rpgClass) {
        this.rpgClass = rpgClass;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public double getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(double intelligence) {
        this.intelligence = intelligence;
    }

    public double getDexterity() {
        return dexterity;
    }

    public void setDexterity(double dexterity) {
        this.dexterity = dexterity;
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }
}
