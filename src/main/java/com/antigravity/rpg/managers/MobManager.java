package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.RPGMob;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MobManager implements Manager {

    private RPGCore core;
    private final Map<String, RPGMob> registeredMobs = new HashMap<>();
    private final Random random = new Random();
    private NamespacedKey mobIdKey;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.mobIdKey = new NamespacedKey(core, "rpg_mob_id");
        registerDefaultMobs();
    }

    @Override
    public void onDisable() {
        registeredMobs.clear();
    }

    private void registerDefaultMobs() {
        RPGMob zombieLvl1 = new RPGMob("zombie_lvl1", "Rotten Walker", EntityType.ZOMBIE, 1);
        zombieLvl1.addDrop("iron_sword_warrior", 0.05);
        registerMob(zombieLvl1);

        RPGMob skeletonLvl5 = new RPGMob("skeleton_lvl5", "Bone Archer", EntityType.SKELETON, 5);
        skeletonLvl5.setDamage(8);
        skeletonLvl5.setMaxHealth(50);
        skeletonLvl5.addDrop("bow_archer", 0.1);
        registerMob(skeletonLvl5);
    }

    public void registerMob(RPGMob mob) {
        registeredMobs.put(mob.getId(), mob);
    }

    public RPGMob getMob(String id) {
        return registeredMobs.get(id);
    }

    public void spawnMob(String id, org.bukkit.Location location) {
        RPGMob rpgMob = registeredMobs.get(id);
        if (rpgMob == null)
            return;

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, rpgMob.getType());
        applyMobStats(entity, rpgMob);
    }

    public void applyMobStats(LivingEntity entity, RPGMob rpgMob) {
        entity.setCustomName("§c[Lv." + rpgMob.getLevel() + "] " + rpgMob.getName());
        entity.setCustomNameVisible(true);

        // Set Health
        if (entity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(rpgMob.getMaxHealth());
            entity.setHealth(rpgMob.getMaxHealth());
        }

        // Set persistent data
        entity.getPersistentDataContainer().set(mobIdKey, PersistentDataType.STRING, rpgMob.getId());
    }

    public RPGMob getMobFromEntity(Entity entity) {
        if (!entity.getPersistentDataContainer().has(mobIdKey, PersistentDataType.STRING))
            return null;
        String id = entity.getPersistentDataContainer().get(mobIdKey, PersistentDataType.STRING);
        return registeredMobs.get(id);
    }
}
