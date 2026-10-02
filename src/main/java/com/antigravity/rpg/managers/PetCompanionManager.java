package com.antigravity.rpg.managers;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PetCompanionManager implements Listener {

    public enum PetType {
        WOLF("Lobo Guardián Feroz", Material.BONE, "§7Ataques infligen sangrado y desgarro continuo"),
        GOLEM("Gólem de Granito Protector", Material.IRON_BLOCK, "§7Otorga barrera de absorción si tu vida baja del 35%"),
        HAWK("Halcón Sombrío Explorador", Material.FEATHER, "§7Rastrea puntos débiles (+20% Golpe Crítico)"),
        PHOENIX("Fénix Ígneo de las Cenizas", Material.BLAZE_POWDER, "§7Aliento de llamas y curación periódica de 5 HP"),
        TIGER("Tigre Abisal Acechante", Material.LEATHER, "§7Aumenta tu velocidad y daño en un +15%");

        public final String displayName;
        public final Material icon;
        public final String abilityDescription;

        PetType(String displayName, Material icon, String abilityDescription) {
            this.displayName = displayName;
            this.icon = icon;
            this.abilityDescription = abilityDescription;
        }
    }

    public static class PetProfile {
        public final UUID owner;
        public PetType type = PetType.WOLF;
        public int level = 1;
        public int currentExp = 0;
        public int satiety = 80; // 0 to 100%

        public PetProfile(UUID owner) {
            this.owner = owner;
        }

        public int getRequiredExp() {
            return level * 200;
        }

        public int getDamage() {
            return 5 + (level * 2);
        }

        public int getDefense() {
            return 3 + (level * 2);
        }

        public boolean isFed() {
            return satiety > 30;
        }

        public void addExp(int amount) {
            currentExp += amount;
            while (currentExp >= getRequiredExp() && level < 50) {
                currentExp -= getRequiredExp();
                level++;
                Player p = Bukkit.getPlayer(owner);
                if (p != null && p.isOnline()) {
                    p.sendMessage(ChatColor.GOLD + "🌟 ¡Tu compañero espiritual ha subido al §eNivel " + level + "§6!");
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.5f);
                }
            }
        }

        public void feed(int satietyAmount, int expAmount) {
            satiety = Math.min(100, satiety + satietyAmount);
            addExp(expAmount);
        }
    }

    private final RPGCore core;
    private final Map<UUID, PetProfile> profiles = new ConcurrentHashMap<>();
    private final Map<UUID, LivingEntity> activePets = new ConcurrentHashMap<>();
    private com.antigravity.rpg.gui.PetManagementGUI petGUI;

    public PetCompanionManager(RPGCore core) {
        this.core = core;
    }

    public void setPetGUI(com.antigravity.rpg.gui.PetManagementGUI petGUI) {
        this.petGUI = petGUI;
    }

    public PetProfile getProfile(UUID uuid) {
        return profiles.computeIfAbsent(uuid, PetProfile::new);
    }

    public boolean summonPet(Player player, String petTypeName) {
        dismissPet(player);

        PetProfile profile = getProfile(player.getUniqueId());
        try {
            profile.type = PetType.valueOf(petTypeName.toUpperCase());
        } catch (Exception ignored) {}

        Location loc = player.getLocation();
        Wolf pet = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
        pet.setOwner(player);
        pet.setTamed(true);
        pet.setAdult();
        pet.setCustomName("§6✦ " + profile.type.displayName + " §7(Nv." + profile.level + ") §6✦");
        pet.setCustomNameVisible(true);

        double hp = 40.0 + (profile.level * 10.0);
        if (pet.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            pet.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
            pet.setHealth(hp);
        }

        activePets.put(player.getUniqueId(), pet);

        player.sendMessage(ChatColor.GOLD + "🐾 ¡Has invocado a tu " + profile.type.displayName + "!");
        player.playSound(loc, Sound.ENTITY_WOLF_GROWL, 1f, 1.2f);
        loc.getWorld().spawnParticle(Particle.HEART, loc.add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
        return true;
    }

    public void dismissPet(Player player) {
        LivingEntity pet = activePets.remove(player.getUniqueId());
        if (pet != null && pet.isValid()) {
            pet.remove();
            player.sendMessage(ChatColor.GRAY + "Tu compañero ha regresado al reino espiritual.");
        }
    }

    public boolean isPet(Entity entity) {
        if (entity == null) return false;
        return activePets.containsValue(entity);
    }

    public LivingEntity getActivePet(UUID playerUuid) {
        return activePets.get(playerUuid);
    }

    @EventHandler
    public void onPetInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof LivingEntity entity) {
            LivingEntity myPet = activePets.get(player.getUniqueId());
            if (myPet != null && myPet.equals(entity) && player.isSneaking()) {
                event.setCancelled(true);
                if (petGUI != null) {
                    petGUI.open(player);
                }
            }
        }
    }

    @EventHandler
    public void onCombatAssist(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            PetProfile profile = getProfile(player.getUniqueId());
            if (profile.isFed()) {
                // Fed buff +25% player damage
                event.setDamage(event.getDamage() * 1.25);
            }

            // Companion gives exp on kill/damage
            if (Math.random() < 0.20) {
                profile.addExp(5);
            }

            // Golem low health barrier
            if (profile.type == PetType.GOLEM && player.getHealth() < (player.getMaxHealth() * 0.35)) {
                if (!player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
                    player.sendMessage(ChatColor.GOLD + "🛡 ¡Tu Gólem Protector ha desplegado un Escudo de Granito!");
                    player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
                }
            }
        }
    }
}
