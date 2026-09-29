package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RogueBountyManager {

    public static class BountyContract {
        public final String targetName;
        public final double rewardGold;
        public final long expirationTime;

        public BountyContract(String targetName, double rewardGold, long expirationTime) {
            this.targetName = targetName;
            this.rewardGold = rewardGold;
            this.expirationTime = expirationTime;
        }
    }

    private final RPGCore core;
    private final Map<UUID, BountyContract> playerContracts = new HashMap<>();

    public RogueBountyManager(RPGCore core) {
        this.core = core;
    }

    public void assignDailyContract(Player player) {
        long expire = System.currentTimeMillis() + (3600 * 1000L * 2); // 2 hours
        BountyContract contract = new BountyContract("Campeón Corrupto del Vacío", 35000.0, expire);
        playerContracts.put(player.getUniqueId(), contract);

        player.sendMessage(ChatColor.DARK_RED + "=== 📜 " + ChatColor.RED + "CONTRATO DE CAZARRECOMPENSAS" + ChatColor.DARK_RED + " ===");
        player.sendMessage(ChatColor.YELLOW + "Objetivo Asignado: " + ChatColor.WHITE + contract.targetName);
        player.sendMessage(ChatColor.YELLOW + "Recompensa: " + ChatColor.GOLD + "35,000 Monedas de Oro");
        player.sendMessage(ChatColor.GRAY + "Tienes 2 horas para cazar a tu objetivo en el mundo.");
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
    }

    public void claimContract(Player player) {
        BountyContract contract = playerContracts.remove(player.getUniqueId());
        if (contract == null) {
            player.sendMessage(ChatColor.YELLOW + "No tienes ningún contrato de cazarrecompensas activo.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "🏆 ¡Contrato completado! Has recibido " + contract.rewardGold + " de oro.");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }
}
