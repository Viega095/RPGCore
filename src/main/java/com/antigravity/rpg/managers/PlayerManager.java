package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.PlayerData;
import com.antigravity.rpg.models.RPGClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Manager {

    private RPGCore core;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private File playersDir;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        this.playersDir = new File(core.getDataFolder(), "players");
        if (!playersDir.exists()) {
            playersDir.mkdirs();
        }
    }

    @Override
    public void onDisable() {
        saveAll();
    }

    public PlayerData getData(UUID uuid) {
        if (!playerDataMap.containsKey(uuid)) {
            load(uuid);
        }
        return playerDataMap.get(uuid);
    }

    public void load(UUID uuid) {
        File file = new File(playersDir, uuid + ".yml");
        PlayerData data = new PlayerData(uuid);

        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            data.setRpgClass(RPGClass.valueOf(config.getString("class", "NONE")));
            data.setLevel(config.getInt("level", 1));
            data.setXp(config.getDouble("xp", 0));
            data.setStrength(config.getDouble("stats.strength", 10));
            data.setIntelligence(config.getDouble("stats.intelligence", 10));
            data.setDexterity(config.getDouble("stats.dexterity", 10));
            data.setDefense(config.getDouble("stats.defense", 0));
            java.util.List<String> gems = config.getStringList("gemBag");
            if (gems != null) {
                for (String gem : gems) {
                    data.addGem(gem);
                }
            }
        }

        playerDataMap.put(uuid, data);
    }

    public void save(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null)
            return;

        File file = new File(playersDir, uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("class", data.getRpgClass().name());
        config.set("level", data.getLevel());
        config.set("xp", data.getXp());
        config.set("stats.strength", data.getStrength());
        config.set("stats.intelligence", data.getIntelligence());
        config.set("stats.dexterity", data.getDexterity());
        config.set("stats.defense", data.getDefense());
        config.set("gemBag", data.getGemBag());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        for (UUID uuid : playerDataMap.keySet()) {
            save(uuid);
        }
    }

    public void unload(UUID uuid) {
        save(uuid);
        playerDataMap.remove(uuid);
    }
}
