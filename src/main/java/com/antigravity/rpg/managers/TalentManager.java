package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.RPGClass;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class TalentManager implements Manager {

    private RPGCore core;
    private PlayerManager playerManager;

    public enum Specialization {
        // Warrior
        BERSERKER("Berserker", "&c+25% Daño y Robo de Vida"),
        PALADIN("Paladín", "&e+40% Defensa y Escudos Sagrados"),
        JUGGERNAUT("Juggernaut", "&6+50% Vida Máxima"),

        // Mage
        ELEMENTALIST("Elementalista", "&b+30% Poder de Hechizos"),
        NECROMANCER("Nigromante", "&5+20% Drenaje de Maná y Maldiciones"),
        CHRONOMANCER("Cronomante", "&a+25% Reducción de Enfriamiento"),

        // Archer
        SHARPSHOOTER("Tirador de Élite", "&a+25% Probabilidad y Daño Crítico"),
        BEASTMASTER("Maestro de Bestias", "&e+20% Velocidad de Movimiento"),
        TRAPPER("Trampero", "&7+35% Flechas Explosivas"),

        // Assassin
        SHADOWBLADE("Hoja Sombría", "&8+40% Daño por la Espalda"),
        POISONER("Envenenador", "&2+25% Daño Venenoso Periódico"),
        NINJA("Ninja", "&f+30% Rango de Desplazamiento (Dash)");

        public final String displayName;
        public final String bonusDescription;

        Specialization(String displayName, String bonusDescription) {
            this.displayName = displayName;
            this.bonusDescription = bonusDescription;
        }
    }

    private final Map<UUID, Specialization> playerSpecializations = new HashMap<>();

    @Override
    public void onEnable(RPGCore core) {
        this.core = core;
    }

    public void setPlayerManager(PlayerManager pm) {
        this.playerManager = pm;
    }

    @Override
    public void onDisable() {
        playerSpecializations.clear();
    }

    public Specialization getSpecialization(UUID uuid) {
        return playerSpecializations.get(uuid);
    }

    public void setSpecialization(Player player, Specialization spec) {
        playerSpecializations.put(player.getUniqueId(), spec);
        player.sendMessage(ChatColor.GOLD + "✦ ¡Has elegido la especialización de " +
                ChatColor.YELLOW + ChatColor.BOLD + spec.displayName + ChatColor.GOLD + "!");
        player.sendMessage(ChatColor.GRAY + "Bono activo: " + ChatColor.translateAlternateColorCodes('&', spec.bonusDescription));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
    }

    public List<Specialization> getAvailableSpecs(RPGClass rpgClass) {
        return switch (rpgClass) {
            case WARRIOR -> List.of(Specialization.BERSERKER, Specialization.PALADIN, Specialization.JUGGERNAUT);
            case MAGE -> List.of(Specialization.ELEMENTALIST, Specialization.NECROMANCER, Specialization.CHRONOMANCER);
            case ARCHER -> List.of(Specialization.SHARPSHOOTER, Specialization.BEASTMASTER, Specialization.TRAPPER);
            case ASSASSIN -> List.of(Specialization.SHADOWBLADE, Specialization.POISONER, Specialization.NINJA);
            default -> Collections.emptyList();
        };
    }
}
