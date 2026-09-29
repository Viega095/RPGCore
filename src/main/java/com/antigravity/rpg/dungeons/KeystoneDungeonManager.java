package com.antigravity.rpg.dungeons;

import com.antigravity.rpg.RPGCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class KeystoneDungeonManager {

    public enum DungeonAffix {
        SANGUINE("Sanguino", "§cLos enemigos dejan charcos de sangre al morir que dañan y curan."),
        VOLCANIC("Volcánico", "§6Erupciones de magma emergen periódicamente bajo los jugadores."),
        TYRANNICAL("Tiránico", "§4Los Jefes poseen +60% de salud y +30% de daño de habilidades."),
        BOLSTERING("Potenciador", "§eAl morir un esbirro, potencia el daño de sus aliados cercanos.");

        public final String name;
        public final String description;

        DungeonAffix(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    private final RPGCore core;
    public final NamespacedKey KEYSTONE_LEVEL_KEY;
    public final NamespacedKey KEYSTONE_AFFIX_KEY;

    public KeystoneDungeonManager(RPGCore core) {
        this.core = core;
        this.KEYSTONE_LEVEL_KEY = new NamespacedKey(core, "rpg_keystone_level");
        this.KEYSTONE_AFFIX_KEY = new NamespacedKey(core, "rpg_keystone_affix");
    }

    public ItemStack createKeystone(int level, DungeonAffix affix) {
        ItemStack item = new ItemStack(Material.TRIAL_KEY);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§l✦ Llave Mítica de Mazmorra +" + level + " ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§7Nivel de Desafío: §e+" + level);
            lore.add("§7Afijo Activo: " + affix.name);
            lore.add("§8" + affix.description);
            lore.add("");
            lore.add("§dRecompensas: +" + (level * 25) + "% Calidad de Botín");
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(KEYSTONE_LEVEL_KEY, PersistentDataType.INTEGER, level);
            meta.getPersistentDataContainer().set(KEYSTONE_AFFIX_KEY, PersistentDataType.STRING, affix.name());
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isKeystone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEYSTONE_LEVEL_KEY, PersistentDataType.INTEGER);
    }

    public int getKeystoneLevel(ItemStack item) {
        if (!isKeystone(item)) return 0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(KEYSTONE_LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }

    public void activateKeystoneRift(Player player, ItemStack keystone) {
        if (!isKeystone(keystone)) {
            player.sendMessage(ChatColor.RED + "✖ Debes sostener una Llave Mítica válida.");
            return;
        }

        int level = getKeystoneLevel(keystone);
        player.sendMessage(ChatColor.DARK_PURPLE + "⚡ ¡RIFT MÍTICO +" + level + " ACTIVADO!");
        player.sendMessage(ChatColor.GRAY + "Los enemigos son ahora más feroces. ¡Comienza el desafío cronometrado!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1.2f);
    }
}
