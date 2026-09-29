package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager implements Manager {

    private RPGCore core;
    private FileConfiguration config;
    private File itemsFile;
    private FileConfiguration itemsConfig;

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
        loadConfig();
        loadItemsConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();
        saveItemsConfig();
    }

    public void loadConfig() {
        core.saveDefaultConfig();
        core.reloadConfig();
        config = core.getConfig();
    }

    public void saveConfig() {
        core.saveConfig();
    }

    private void loadItemsConfig() {
        itemsFile = new File(core.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            core.saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    public void saveItemsConfig() {
        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getItemsConfig() {
        return itemsConfig;
    }
}
