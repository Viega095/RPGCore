package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetCompanionManager {

    private final RPGCore core;
    private final Map<UUID, Wolf> activePets = new HashMap<>();

    public PetCompanionManager(RPGCore core) {
        this.core = core;
    }

    public boolean summonPet(Player player, String petType) {
        dismissPet(player);

        Location loc = player.getLocation();
        Wolf pet = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
        pet.setOwner(player);
        pet.setTamed(true);
        pet.setAdult();
        pet.setCustomName(ChatColor.GOLD + "✦ Compañero de " + player.getName() + " ✦");
        pet.setCustomNameVisible(true);

        activePets.put(player.getUniqueId(), pet);

        player.sendMessage(ChatColor.GOLD + "🐾 ¡Has invocado a tu compañero de combate!");
        player.playSound(loc, Sound.ENTITY_WOLF_GROWL, 1f, 1.2f);
        loc.getWorld().spawnParticle(Particle.HEART, loc.add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
        return true;
    }

    public void dismissPet(Player player) {
        Wolf pet = activePets.remove(player.getUniqueId());
        if (pet != null && pet.isValid()) {
            pet.remove();
            player.sendMessage(ChatColor.GRAY + "Tu compañero ha regresado al reino espiritual.");
        }
    }
}
