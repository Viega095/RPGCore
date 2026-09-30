package com.antigravity.rpg.gui;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.managers.PetCompanionManager;
import com.antigravity.rpg.managers.PetCompanionManager.PetProfile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PetManagementGUI implements Listener {

    public static class PetHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final RPGCore core;
    private final PetCompanionManager petManager;

    public PetManagementGUI(RPGCore core, PetCompanionManager petManager) {
        this.core = core;
        this.petManager = petManager;
    }

    public void open(Player player) {
        PetProfile profile = petManager.getProfile(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(new PetHolder(), 45, "§8🐾 §6Perfil de Compañero Espiritual §8🐾");

        ItemStack border = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 45; i++) inv.setItem(i, border);

        // Center Profile Card (Slot 13)
        ItemStack card = new ItemStack(profile.type.icon);
        ItemMeta cMeta = card.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName("§e§l" + profile.type.displayName + " §7(Nv. " + profile.level + ")");
            int expReq = profile.getRequiredExp();
            int bars = (int) ((double) profile.currentExp / expReq * 10);
            StringBuilder expBar = new StringBuilder("§a");
            for (int i = 0; i < 10; i++) {
                if (i == bars) expBar.append("§7");
                expBar.append("█");
            }

            cMeta.setLore(Arrays.asList(
                    "§7Familiar espiritual vinculado a tu alma.",
                    "",
                    "§6✦ Estadísticas de Combate:",
                    "  §c• Daño Base: §f+" + profile.getDamage() + " ATK",
                    "  §b• Defensa Otorgada: §f+" + profile.getDefense() + " DEF",
                    "  §a• Habilidad Especial: " + profile.type.abilityDescription,
                    "",
                    "§e✦ Progreso de Nivel:",
                    "  " + expBar + " §f(" + profile.currentExp + "/" + expReq + " XP)",
                    "  §7Gana XP derrotando enemigos, mazmorras y contratos.",
                    "",
                    "§d✦ Nivel de Satisfacción / Comida: §f" + profile.satiety + "%",
                    profile.isFed() ? "§a✔ [BUFF ACTIVO: +25% Daño del Jugador]" : "§c✖ [HAMBRIENTO: Aliméntalo para activar buff]"
            ));
            card.setItemMeta(cMeta);
        }
        inv.setItem(13, card);

        // Feeding Slots (Slots 29: Feed Meat, 31: Feed Fish, 33: Feed Golden Apple)
        inv.setItem(29, createActionItem(Material.COOKED_BEEF, "§e🍖 Alimentar con Carne", Arrays.asList("§7Consume 1x Carne del inventario", "§a+30% Satisfacción", "§e▶ Haz clic para alimentar")));
        inv.setItem(31, createActionItem(Material.SALMON, "§b🐟 Alimentar con Pescado", Arrays.asList("§7Consume 1x Pescado del inventario", "§a+35% Satisfacción", "§e▶ Haz clic para alimentar")));
        inv.setItem(33, createActionItem(Material.GOLDEN_APPLE, "§6🍏 Banquete Dorado Mítico", Arrays.asList("§7Consume 1x Manzana Dorada", "§a+100% Satisfacción + 250 XP Pet", "§e▶ Haz clic para alimentar")));

        // Pet Switcher (Slots 37: Wolf, 38: Golem, 39: Hawk, 40: Phoenix, 41: Tiger)
        PetCompanionManager.PetType[] types = PetCompanionManager.PetType.values();
        int[] slots = {37, 38, 39, 40, 41};
        for (int i = 0; i < types.length && i < slots.length; i++) {
            PetCompanionManager.PetType t = types[i];
            boolean isEquipped = (profile.type == t);
            ItemStack pItem = new ItemStack(t.icon);
            ItemMeta pMeta = pItem.getItemMeta();
            if (pMeta != null) {
                pMeta.setDisplayName((isEquipped ? "§a✔ " : "§e") + t.displayName);
                pMeta.setLore(Arrays.asList(
                        "§7" + t.abilityDescription,
                        "",
                        isEquipped ? "§a[COMPAÑERO ACTUALMENTE INVOCADO]" : "§e▶ Clic para invocar este familiar"
                ));
                pItem.setItemMeta(pMeta);
            }
            inv.setItem(slots[i], pItem);
        }

        player.openInventory(inv);
    }

    private ItemStack createActionItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof PetHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        PetProfile profile = petManager.getProfile(player.getUniqueId());

        int slot = event.getRawSlot();
        if (slot == 29) {
            // Feed Meat
            if (removeOneItem(player, Material.COOKED_BEEF, Material.BEEF, Material.COOKED_PORKCHOP, Material.PORKCHOP, Material.COOKED_CHICKEN, Material.CHICKEN)) {
                profile.feed(30, 50);
                player.sendMessage(ChatColor.GREEN + "🍖 ¡Has alimentado a tu compañero con carne fresca!");
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1.2f);
                open(player);
            } else {
                player.sendMessage(ChatColor.RED + "✖ No tienes carne en tu inventario para alimentar a tu mascota.");
            }
        } else if (slot == 31) {
            // Feed Fish
            if (removeOneItem(player, Material.COD, Material.SALMON, Material.COOKED_COD, Material.COOKED_SALMON, Material.TROPICAL_FISH)) {
                profile.feed(35, 60);
                player.sendMessage(ChatColor.GREEN + "🐟 ¡Has alimentado a tu compañero con pescado!");
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1.2f);
                open(player);
            } else {
                player.sendMessage(ChatColor.RED + "✖ No tienes pescado en tu inventario para alimentar a tu mascota.");
            }
        } else if (slot == 33) {
            // Feed Golden Apple
            if (removeOneItem(player, Material.GOLDEN_APPLE)) {
                profile.feed(100, 250);
                player.sendMessage(ChatColor.GOLD + "🍏 ¡Banquete Dorado completado! Tu mascota ha ganado 250 XP y satisfacción máxima.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
                open(player);
            } else {
                player.sendMessage(ChatColor.RED + "✖ No tienes Manzanas Doradas en tu inventario.");
            }
        } else if (slot == 37) switchPet(player, profile, PetCompanionManager.PetType.WOLF);
        else if (slot == 38) switchPet(player, profile, PetCompanionManager.PetType.GOLEM);
        else if (slot == 39) switchPet(player, profile, PetCompanionManager.PetType.HAWK);
        else if (slot == 40) switchPet(player, profile, PetCompanionManager.PetType.PHOENIX);
        else if (slot == 41) switchPet(player, profile, PetCompanionManager.PetType.TIGER);
    }

    private void switchPet(Player player, PetProfile profile, PetCompanionManager.PetType newType) {
        profile.type = newType;
        petManager.summonPet(player, newType.name());
        player.sendMessage(ChatColor.GOLD + "✨ ¡Has cambiado tu compañero activo a: " + newType.displayName + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1.2f);
        open(player);
    }

    private boolean removeOneItem(Player player, Material... materials) {
        for (Material m : materials) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == m && item.getAmount() > 0) {
                    item.setAmount(item.getAmount() - 1);
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof PetHolder) {
            event.setCancelled(true);
        }
    }
}
