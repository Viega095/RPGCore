package com.viega.rpg.managers;

import com.viega.rpg.RPGCore;

public interface Manager {
    void onEnable(RPGCore core);

    void onDisable();
}
