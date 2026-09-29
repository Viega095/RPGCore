package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import java.util.LinkedHashMap;
import java.util.Map;

public class ManagerHandler {

    private final RPGCore core;
    private final Map<Class<? extends Manager>, Manager> managers = new LinkedHashMap<>();

    public ManagerHandler(RPGCore core) {
        this.core = core;
    }

    public void register(Manager manager) {
        managers.put(manager.getClass(), manager);
    }

    public void enable() {
        managers.values().forEach(manager -> {
            try {
                manager.onEnable(core);
                core.getLogger().info("Enabled manager: " + manager.getClass().getSimpleName());
            } catch (Exception e) {
                core.getLogger().severe("Failed to enable manager: " + manager.getClass().getSimpleName());
                e.printStackTrace();
            }
        });
    }

    public void disable() {
        // Disable in reverse order
        java.util.List<Manager> list = new java.util.ArrayList<>(managers.values());
        java.util.Collections.reverse(list);
        list.forEach(manager -> {
            try {
                manager.onDisable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public <T extends Manager> T get(Class<T> clazz) {
        return clazz.cast(managers.get(clazz));
    }

    public <T extends Manager> T getManager(Class<T> clazz) {
        return get(clazz);
    }
}
