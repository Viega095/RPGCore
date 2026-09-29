package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import com.antigravity.rpg.models.RPGClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;
    private StatManager statManager;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
    }

    public void setManagers(PlayerManager pm, StatManager sm) {
        this.playerManager = pm;
        this.statManager = sm;
    }

    @Override
    public void onDisable() {

    }

    public void selectClass(UUID uuid, RPGClass rpgClass) {
        if (playerManager == null)
            return;
        PlayerData data = playerManager.getData(uuid);

        data.setRpgClass(rpgClass);
        // Reset stats or apply base stats for the class
        applyClassStats(data, rpgClass);

        if (statManager != null) {
            // Recalculate stats? For now direct set is fine
        }

        playerManager.save(uuid);
    }

    private void applyClassStats(PlayerData data, RPGClass rpgClass) {
        switch (rpgClass) {
            case WARRIOR:
                data.setStrength(15);
                data.setDefense(10);
                data.setIntelligence(5);
                data.setDexterity(8);
                data.setMaxMana(50);
                break;
            case MAGE:
                data.setStrength(5);
                data.setDefense(5);
                data.setIntelligence(20);
                data.setDexterity(8);
                data.setMaxMana(200);
                break;
            case ARCHER:
                data.setStrength(10);
                data.setDefense(5);
                data.setIntelligence(8);
                data.setDexterity(15);
                data.setMaxMana(80);
                break;
            default:
                break;
        }
        data.setCurrentMana(data.getMaxMana());
    }
}
