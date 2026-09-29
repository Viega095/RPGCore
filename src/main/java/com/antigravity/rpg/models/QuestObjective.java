package com.antigravity.rpg.models;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class QuestObjective {

    private ObjectiveType type;
    private EntityType targetEntity;
    private Material targetMaterial;
    private int targetAmount;
    private int currentAmount;

    public QuestObjective(ObjectiveType type, int targetAmount) {
        this.type = type;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public ObjectiveType getType() {
        return type;
    }

    public EntityType getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(EntityType entity) {
        this.targetEntity = entity;
    }

    public Material getTargetMaterial() {
        return targetMaterial;
    }

    public void setTargetMaterial(Material material) {
        this.targetMaterial = material;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int amount) {
        this.currentAmount = amount;
    }

    public void incrementProgress(int amount) {
        this.currentAmount += amount;
    }

    public boolean isComplete() {
        return currentAmount >= targetAmount;
    }

    public double getProgress() {
        return (double) currentAmount / targetAmount;
    }

    public enum ObjectiveType {
        KILL_MOBS,
        COLLECT_ITEMS,
        TALK_TO_NPC
    }
}
