package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.RPGClass;
import com.antigravity.rpg.models.RPGItem;
import com.antigravity.rpg.models.Rarity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager implements Manager {

    private RPGCore core;
    private final Map<String, RPGItem> registeredItems = new HashMap<>();
    private NamespacedKey itemIdKey;
    private NamespacedKey levelReqKey;
    private NamespacedKey classReqKey;
    private NamespacedKey gemSlotsKey;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.itemIdKey = new NamespacedKey(core, "rpg_item_id");
        this.levelReqKey = new NamespacedKey(core, "level_req");
        this.classReqKey = new NamespacedKey(core, "class_req");
        this.gemSlotsKey = new NamespacedKey(core, "gem_slots");
        registerDefaultItems();
    }

    @Override
    public void onDisable() {
        registeredItems.clear();
    }

    private void registerDefaultItems() {
        // Warrior weapon
        RPGItem ironSword = new RPGItem("iron_sword_warrior", Material.IRON_SWORD, "Warrior's Blade", Rarity.COMMON);
        ironSword.setLevelRequirement(1);
        ironSword.setClassRequirement(RPGClass.WARRIOR);
        ironSword.addStat("strength", 5.0);
        ironSword.addStat("defense", 2.0);
        ironSword.setGemSlots(1);
        registerItem(ironSword);

        // Mage weapon
        RPGItem mageStaff = new RPGItem("blaze_rod_mage", Material.BLAZE_ROD, "Staff of Flames", Rarity.RARE);
        mageStaff.setLevelRequirement(5);
        mageStaff.setClassRequirement(RPGClass.MAGE);
        mageStaff.addStat("intelligence", 10.0);
        mageStaff.addStat("mana", 50.0);
        mageStaff.setGemSlots(2);
        registerItem(mageStaff);

        // Archer weapon
        RPGItem hunterBow = new RPGItem("bow_archer", Material.BOW, "Hunter's Bow", Rarity.UNCOMMON);
        hunterBow.setLevelRequirement(3);
        hunterBow.setClassRequirement(RPGClass.ARCHER);
        hunterBow.addStat("dexterity", 8.0);
        hunterBow.addStat("crit_chance", 0.15);
        hunterBow.setGemSlots(1);
        registerItem(hunterBow);

        // Legendary Weapon with Ability
        RPGItem vampiricBlade = new RPGItem("vampiric_blade", Material.DIAMOND_SWORD, "Vampiric Blade",
                Rarity.LEGENDARY);
        vampiricBlade.setLevelRequirement(10);
        vampiricBlade.setClassRequirement(RPGClass.WARRIOR);
        vampiricBlade.addStat("strength", 20.0);
        vampiricBlade.addStat("life_steal", 0.05);
        vampiricBlade.setGemSlots(3);
        // We need a way to add lore for the ability manually since the system doesn't
        // support active skills in RPGItem model yet
        // For MVP, we'll just let the generated lore happen, and maybe adding it via
        // NBT/Lore in createItemStack would be cleaner?
        // Actually, let's just register it and the ItemManager's Lore builder needs to
        // be aware, OR we hardcode it in ItemManager for now.
        registerItem(vampiricBlade);
    }

    public void registerItem(RPGItem item) {
        registeredItems.put(item.getId(), item);
    }

    public RPGItem getItem(String id) {
        return registeredItems.get(id);
    }

    public ItemStack createItemStack(String id) {
        RPGItem rpgItem = registeredItems.get(id);
        if (rpgItem == null)
            return null;

        ItemStack item = new ItemStack(rpgItem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return item;

        // Set display name
        meta.setDisplayName(rpgItem.getRarity().getColor() + rpgItem.getDisplayName());

        // Build lore
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Rarity: " + rpgItem.getRarity().getColor() + rpgItem.getRarity().name());

        if (rpgItem.getLevelRequirement() > 1) {
            lore.add("§7Level Required: §e" + rpgItem.getLevelRequirement());
        }

        if (rpgItem.getClassRequirement() != RPGClass.NONE) {
            lore.add("§7Class: §e" + rpgItem.getClassRequirement().name());
        }

        lore.add("");
        lore.add("§6Stats:");
        for (Map.Entry<String, Double> entry : rpgItem.getStats().entrySet()) {
            String statName = entry.getKey().replace("_", " ");
            statName = statName.substring(0, 1).toUpperCase() + statName.substring(1);

            if (entry.getKey().equals("crit_chance") || entry.getKey().equals("life_steal")) {
                lore.add("  §a+" + (entry.getValue() * 100) + "% " + statName);
            } else {
                lore.add("  §a+" + entry.getValue() + " " + statName);
            }
        }

        // Hardcoded ability lore for Vampiric Blade
        if (rpgItem.getId().equals("vampiric_blade")) {
            lore.add("");
            lore.add("§6Item Ability: §cOmnivampirism §e(Right Click)");
            lore.add("§7Drain life from nearby enemies.");
            lore.add("§7Mana Cost: §b75");
        }

        if (rpgItem.getGemSlots() > 0) {
            lore.add("");
            lore.add("§d◆ Gem Slots: " + rpgItem.getGemSlots());
        }

        meta.setLore(lore);

        // Store data in PDC
        meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, rpgItem.getId());
        meta.getPersistentDataContainer().set(levelReqKey, PersistentDataType.INTEGER, rpgItem.getLevelRequirement());
        meta.getPersistentDataContainer().set(classReqKey, PersistentDataType.STRING,
                rpgItem.getClassRequirement().name());
        meta.getPersistentDataContainer().set(gemSlotsKey, PersistentDataType.INTEGER, rpgItem.getGemSlots());

        item.setItemMeta(meta);
        return item;
    }

    public String getItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return null;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
    }

    public int getLevelRequirement(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return 0;
        ItemMeta meta = item.getItemMeta();
        Integer level = meta.getPersistentDataContainer().get(levelReqKey, PersistentDataType.INTEGER);
        return level != null ? level : 0;
    }

    public RPGClass getClassRequirement(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return RPGClass.NONE;
        ItemMeta meta = item.getItemMeta();
        String className = meta.getPersistentDataContainer().get(classReqKey, PersistentDataType.STRING);
        if (className == null)
            return RPGClass.NONE;
        try {
            return RPGClass.valueOf(className);
        } catch (IllegalArgumentException e) {
            return RPGClass.NONE;
        }
    }
}
