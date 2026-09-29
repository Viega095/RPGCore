package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;

public interface Manager {
    void onEnable(RPGCore core);

    void onDisable();
}
