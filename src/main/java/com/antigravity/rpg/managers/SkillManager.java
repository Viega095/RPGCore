package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import com.antigravity.rpg.models.Skill;
import org.bukkit.entity.Player;

import java.util.*;

public class SkillManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;

    private final Map<String, Skill> registeredSkills = new HashMap<>();
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> skillLevels = new HashMap<>();

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        registerSkills();
    }

    public void setPlayerManager(PlayerManager pm) {
        this.playerManager = pm;
    }

    @Override
    public void onDisable() {
        cooldowns.clear();
        skillLevels.clear();
    }

    private void registerSkills() {
        // Skills will be registered here
        // Example: registerSkill(new FireballSkill());
    }

    public void registerSkill(Skill skill) {
        registeredSkills.put(skill.getName().toLowerCase(), skill);
    }

    public boolean castSkill(Player player, String skillName) {
        Skill skill = registeredSkills.get(skillName.toLowerCase());
        if (skill == null) {
            player.sendMessage("§cSkill not found!");
            return false;
        }

        // Get skill level
        int level = getSkillLevel(player.getUniqueId(), skillName);
        if (level == 0) {
            player.sendMessage("§cYou haven't learned this skill!");
            return false;
        }

        // Check cooldown
        if (isOnCooldown(player.getUniqueId(), skillName)) {
            long remaining = getRemainingCooldown(player.getUniqueId(), skillName);
            player.sendMessage("§cSkill on cooldown! " + (remaining / 1000) + "s remaining.");
            return false;
        }

        // Check mana
        if (playerManager != null) {
            PlayerData data = playerManager.getData(player.getUniqueId());
            double manaCost = skill.getManaCost(level);
            if (data.getCurrentMana() < manaCost) {
                player.sendMessage("§cNot enough mana! (Need " + manaCost + ", have " + data.getCurrentMana() + ")");
                return false;
            }

            // Cast skill
            if (skill.cast(player, level)) {
                data.setCurrentMana(data.getCurrentMana() - manaCost);
                setCooldown(player.getUniqueId(), skillName, skill.getCooldown(level));
                player.sendMessage("§aCast " + skill.getName() + "!");
                return true;
            }
        }

        return false;
    }

    public void setCooldown(UUID uuid, String skillName, long cooldownMs) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(skillName.toLowerCase(), System.currentTimeMillis() + cooldownMs);
    }

    public boolean isOnCooldown(UUID uuid, String skillName) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null)
            return false;

        Long endTime = playerCooldowns.get(skillName.toLowerCase());
        if (endTime == null)
            return false;

        return System.currentTimeMillis() < endTime;
    }

    public long getRemainingCooldown(UUID uuid, String skillName) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null)
            return 0;

        Long endTime = playerCooldowns.get(skillName.toLowerCase());
        if (endTime == null)
            return 0;

        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public int getSkillLevel(UUID uuid, String skillName) {
        return skillLevels.computeIfAbsent(uuid, k -> new HashMap<>())
                .getOrDefault(skillName.toLowerCase(), 0);
    }

    public void setSkillLevel(UUID uuid, String skillName, int level) {
        Skill skill = registeredSkills.get(skillName.toLowerCase());
        if (skill != null) {
            int maxLevel = skill.getMaxLevel();
            level = Math.min(level, maxLevel);
        }
        skillLevels.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(skillName.toLowerCase(), level);
    }

    public Collection<Skill> getRegisteredSkills() {
        return registeredSkills.values();
    }
}
