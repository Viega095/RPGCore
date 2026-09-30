package com.antigravity.rpg.equipment;

import com.antigravity.rpg.RPGCore;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArtifactRelicEngine implements Listener {

    public enum RelicType {
        PHOENIX_HEART("§c❤ Corazón del Fénix Inmortal", Material.TOTEM_OF_UNDYING,
                Arrays.asList("§7Evita la muerte fatal restaurando el 50% de tu vida,",
                        "§7liberando una onda de fuego expansiva.",
                        "§8[Enfriamiento: 5 minutos]")),
        MJOLNIR_SHARD("§b⚡ Fragmento de Mjölnir Ancestral", Material.PRISMARINE_SHARD,
                Arrays.asList("§725% de probabilidad al golpear de desatar",
                        "§7rayos en cadena contra 3 enemigos cercanos.",
                        "§8[Efecto Pasivo Continuo]")),
        AEGIS_OF_IMMORTALITY("§6🛡 Égida de la Eternidad", Material.SHIELD,
                Arrays.asList("§7Genera un escudo místico que mitiga el 80% del daño",
                        "§7del siguiente impacto recibido cada 40s.",
                        "§8[Efecto Defensivo Automático]")),
        CHRONO_HOURGLASS("§d⏳ Reloj de Arena Cronos", Material.CLOCK,
                Arrays.asList("§7Aumenta la regeneración de maná un +50%",
                        "§7y reduce los tiempos de recarga de habilidades un 25%.",
                        "§8[Sincronización Temporal]"));

        private final String displayName;
        private final Material icon;
        private final List<String> lore;

        RelicType(String displayName, Material icon, List<String> lore) {
            this.displayName = displayName;
            this.icon = icon;
            this.lore = lore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getIcon() {
            return icon;
        }

        public List<String> getLore() {
            return lore;
        }
    }

    private final RPGCore core;
    private final Map<UUID, RelicType> equippedRelics = new ConcurrentHashMap<>();
    private final Map<UUID, Long> phoenixCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, Long> aegisCooldown = new ConcurrentHashMap<>();

    public static class RelicHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public ArtifactRelicEngine(RPGCore core) {
        this.core = core;
    }

    public RelicType getEquippedRelic(UUID uuid) {
        return equippedRelics.get(uuid);
    }

    public void setEquippedRelic(UUID uuid, RelicType relic) {
        equippedRelics.put(uuid, relic);
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new RelicHolder(), 27, "§8🔱 Reliquias y Artefactos Míticos");

        ItemStack border = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, border);

        RelicType equipped = getEquippedRelic(player.getUniqueId());
        RelicType[] all = RelicType.values();
        int[] slots = {10, 12, 14, 16};

        for (int i = 0; i < all.length && i < slots.length; i++) {
            RelicType relic = all[i];
            ItemStack item = new ItemStack(relic.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e§l" + relic.getDisplayName());
                List<String> lore = new ArrayList<>(relic.getLore());
                lore.add("");
                if (equipped == relic) {
                    lore.add("§a✔ [RELIQUIA EQUIPADA ACTUALMENTE]");
                } else {
                    lore.add("§e▶ Haz clic para equipar esta reliquia");
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slots[i], item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof RelicHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        RelicType selected = null;
        if (slot == 10) selected = RelicType.PHOENIX_HEART;
        else if (slot == 12) selected = RelicType.MJOLNIR_SHARD;
        else if (slot == 14) selected = RelicType.AEGIS_OF_IMMORTALITY;
        else if (slot == 16) selected = RelicType.CHRONO_HOURGLASS;

        if (selected != null) {
            setEquippedRelic(player.getUniqueId(), selected);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "✨ ¡Reliquia legendaria equipada: " + selected.getDisplayName() + "!");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.5f);
            openGUI(player);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RelicHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLethalDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            RelicType relic = getEquippedRelic(player.getUniqueId());
            if (relic == RelicType.PHOENIX_HEART) {
                if (player.getHealth() - event.getFinalDamage() <= 0) {
                    long now = System.currentTimeMillis();
                    long lastUsed = phoenixCooldown.getOrDefault(player.getUniqueId(), 0L);
                    if (now - lastUsed > 300000) { // 5 minutes cooldown
                        event.setCancelled(true);
                        phoenixCooldown.put(player.getUniqueId(), now);

                        player.setHealth(player.getMaxHealth() * 0.5);
                        Location loc = player.getLocation();
                        loc.getWorld().spawnParticle(Particle.TOTEM, loc.add(0, 1, 0), 60, 1, 1, 1, 0.3);
                        loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 1.5, 1.5, 1.5, 0.1);
                        loc.getWorld().playSound(loc, Sound.ITEM_TOTEM_USE, 1f, 1f);

                        // Flame shockwave to nearby monsters
                        for (org.bukkit.entity.Entity e : player.getNearbyEntities(6, 6, 6)) {
                            if (e instanceof LivingEntity living && !(e instanceof Player)) {
                                living.damage(15.0, player);
                                living.setFireTicks(100);
                            }
                        }

                        player.sendMessage(ChatColor.GOLD + "🔥 ¡El Corazón del Fénix ha impedido tu muerte fatal y ha restaurado tu vitalidad!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity target) {
            RelicType relic = getEquippedRelic(player.getUniqueId());
            if (relic == RelicType.MJOLNIR_SHARD && Math.random() < 0.25) {
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.damage(8.0, player);

                int chained = 0;
                for (org.bukkit.entity.Entity nearby : target.getNearbyEntities(5, 5, 5)) {
                    if (nearby instanceof LivingEntity living && !nearby.equals(player) && !nearby.equals(target)) {
                        living.damage(6.0, player);
                        living.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, living.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
                        if (++chained >= 3) break;
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.6f, 1.4f);
            }
        }
    }
}
