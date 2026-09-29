package com.antigravity.rpg.equipment;

import com.antigravity.rpg.RPGCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class RunewordEngine {

    public enum RuneType {
        EL("El", 1),
        ELD("Eld", 2),
        TIR("Tir", 3),
        NEF("Nef", 4),
        ETH("Eth", 5),
        ITH("Ith", 6),
        TAL("Tal", 7),
        RAL("Ral", 8),
        ORT("Ort", 9),
        THUL("Thul", 10),
        SOL("Sol", 11),
        SHAEL("Shael", 13),
        DOL("Dol", 14),
        HEL("Hel", 15),
        IO("Io", 16),
        LUM("Lum", 17),
        KO("Ko", 18),
        FAL("Fal", 19),
        LEM("Lem", 20),
        PUL("Pul", 21),
        UM("Um", 22),
        MAL("Mal", 23),
        IST("Ist", 24),
        GUL("Gul", 25),
        VEX("Vex", 26),
        OHM("Ohm", 27),
        LO("Lo", 28),
        SUR("Sur", 29),
        BER("Ber", 30),
        JAH("Jah", 31),
        CHAM("Cham", 32),
        ZOD("Zod", 33);

        public final String runeName;
        public final int tier;

        RuneType(String runeName, int tier) {
            this.runeName = runeName;
            this.tier = tier;
        }
    }

    public static class RunewordRecipe {
        public final String id;
        public final String displayName;
        public final List<RuneType> recipe;
        public final String effectDescription;

        public RunewordRecipe(String id, String displayName, List<RuneType> recipe, String effectDescription) {
            this.id = id;
            this.displayName = displayName;
            this.recipe = recipe;
            this.effectDescription = effectDescription;
        }
    }

    private final RPGCore core;
    public final NamespacedKey RUNE_ID_KEY;
    public final NamespacedKey RUNE_SLOTS_KEY;
    public final NamespacedKey ACTIVE_RUNEWORD_KEY;

    private final Map<String, RunewordRecipe> runewords = new HashMap<>();

    public RunewordEngine(RPGCore core) {
        this.core = core;
        this.RUNE_ID_KEY = new NamespacedKey(core, "rpg_rune_id");
        this.RUNE_SLOTS_KEY = new NamespacedKey(core, "rpg_item_runes");
        this.ACTIVE_RUNEWORD_KEY = new NamespacedKey(core, "rpg_active_runeword");

        registerDefaultRunewords();
    }

    private void registerDefaultRunewords() {
        runewords.put("ENIGMA", new RunewordRecipe(
                "ENIGMA",
                "§d§l✦ ENIGMA ✦",
                List.of(RuneType.JAH, RuneType.ITH, RuneType.BER),
                "§b+Teletransporte Espacial §8| §e+2 Todas las Habilidades §8| §a+15% Velocidad"
        ));

        runewords.put("GRIEF", new RunewordRecipe(
                "GRIEF",
                "§c§l✦ GRIEF ✦",
                List.of(RuneType.ETH, RuneType.TIR, RuneType.LO, RuneType.MAL, RuneType.RAL),
                "§c+350 Daño Físico Puro §8| §6Ignora Defensa de Objetivo §8| §e+20% Velocidad de Ataque"
        ));

        runewords.put("BOTD", new RunewordRecipe(
                "BOTD",
                "§4§l✦ BREATH OF THE DYING ✦",
                List.of(RuneType.VEX, RuneType.HEL, RuneType.EL, RuneType.ELD, RuneType.ZOD, RuneType.ETH),
                "§c+50% Robo de Vida §8| §dIndestructible §8| §5Nova Venenosa al matar"
        ));

        runewords.put("FORTITUDE", new RunewordRecipe(
                "FORTITUDE",
                "§9§l✦ FORTITUDE ✦",
                List.of(RuneType.EL, RuneType.SOL, RuneType.DOL, RuneType.LO),
                "§9+300% Armadura Reforzada §8| §c+200 Vida Máxima §8| §bArmadura Gélida"
        ));
    }

    public ItemStack createRune(RuneType type) {
        ItemStack item = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§lRuna " + type.runeName + " §7(Tier " + type.tier + ")");
            meta.setLore(List.of(
                    "§7Antigua runa mágica imbuida con poder arcano.",
                    "§eIncrústala en un objeto con ranuras rúnicas",
                    "§7para forjar poderosas palabras rúnicas."
            ));
            meta.getPersistentDataContainer().set(RUNE_ID_KEY, PersistentDataType.STRING, type.name());
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isRune(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(RUNE_ID_KEY, PersistentDataType.STRING);
    }

    public RuneType getRuneType(ItemStack item) {
        if (!isRune(item)) return null;
        String name = item.getItemMeta().getPersistentDataContainer().get(RUNE_ID_KEY, PersistentDataType.STRING);
        try {
            return RuneType.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean socketRune(Player player, ItemStack targetItem, ItemStack runeItem) {
        RuneType rune = getRuneType(runeItem);
        if (rune == null) return false;

        ItemMeta meta = targetItem.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String currentRunes = pdc.getOrDefault(RUNE_SLOTS_KEY, PersistentDataType.STRING, "");

        List<String> runeList = new ArrayList<>();
        if (!currentRunes.isEmpty()) {
            runeList.addAll(Arrays.asList(currentRunes.split(",")));
        }

        if (runeList.size() >= 6) {
            player.sendMessage(ChatColor.RED + "✖ Este objeto ya no tiene espacio para más runas.");
            return false;
        }

        runeList.add(rune.name());
        String newRuneStr = String.join(",", runeList);
        pdc.set(RUNE_SLOTS_KEY, PersistentDataType.STRING, newRuneStr);

        // Check for Runeword completion
        RunewordRecipe matched = checkRunewordMatch(runeList);
        if (matched != null) {
            pdc.set(ACTIVE_RUNEWORD_KEY, PersistentDataType.STRING, matched.id);
            meta.setDisplayName(matched.displayName + " §8- " + meta.getDisplayName());

            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add("§6✦ Palabras Rúnicas Despertadas:");
            lore.add("  " + matched.effectDescription);
            meta.setLore(lore);

            player.sendMessage(ChatColor.GOLD + "⚡ ¡PALABRA RÚNICA DESPERTADA! Has completado: " + matched.displayName);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        } else {
            player.sendMessage(ChatColor.GREEN + "✔ Runa " + rune.runeName + " incrustada con éxito.");
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.2f);
        }

        targetItem.setItemMeta(meta);
        runeItem.setAmount(runeItem.getAmount() - 1);
        return true;
    }

    private RunewordRecipe checkRunewordMatch(List<String> socketedRunes) {
        for (RunewordRecipe recipe : runewords.values()) {
            if (recipe.recipe.size() == socketedRunes.size()) {
                boolean match = true;
                for (int i = 0; i < recipe.recipe.size(); i++) {
                    if (!recipe.recipe.get(i).name().equals(socketedRunes.get(i))) {
                        match = false;
                        break;
                    }
                }
                if (match) return recipe;
            }
        }
        return null;
    }

    public Collection<RunewordRecipe> getAllRunewords() {
        return runewords.values();
    }
}
