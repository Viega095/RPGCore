package com.viega.rpg.managers;

import com.viega.rpg.RPGCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClassAscensionManager implements Listener {

    public static class AscensionHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public enum AwakenedClass {
        BLOOD_BERSERKER("Guerrero", "§c✦ Berserker de Sangre ✦", Material.NETHERITE_AXE,
                Arrays.asList(
                        "§7Despierta la sed de sangre ancestral.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §c• Furia Sedienta: §f+50% Daño cuerpo a cuerpo cuando tu salud cae por debajo del 40%",
                        "  §c• Desgarro Carmesí: §fTus golpes aplican sangrado continuo que roba 15% de vida",
                        "  §c• Inquebrantable: §fInmunidad a la ralentización y empuje"
                )),
        VOID_ARCHMAGE("Mago", "§5✦ Archimago del Vacío ✦", Material.AMETHYST_CLUSTER,
                Arrays.asList(
                        "§7Domina las corrientes arcanas del vacío estelar.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §5• Singularidad Gravitatoria: §fLanzar hechizos genera vórtices que atraen enemigos",
                        "  §5• Ecos del Vacío: §f+40% Daño de Hechizos Mágicos en área",
                        "  §5• Escudo Astral: §fAbsorbe el 30% del daño recibido y lo convierte en maná"
                )),
        CELESTIAL_SNIPER("Arquero", "§b✦ Francotirador Celestial ✦", Material.BOW,
                Arrays.asList(
                        "§7Canaliza la luz estelar a través de tus proyectiles.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §b• Saeta de Nova: §f+75% Daño a larga distancia e impacto con rayo celestial",
                        "  §b• Perforación Estelar: §fTus flechas atraviesan defensas y armaduras enemigas",
                        "  §b• Ojo Cósmico: §fVisión verdadera y probabilidad de crítico aumentada en +25%"
                )),
        SHADOW_REAPER("Asesino", "§8✦ Sombra Asesina Fantasmal ✦", Material.NETHERITE_SWORD,
                Arrays.asList(
                        "§7Fúndete con las sombras de la muerte.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §8• Velo Espectral: §fGolpear por la espalda otorga Invisibilidad y Velocidad III por 3s",
                        "  §8• Hoja Fantasmal: §fIgnora el 50% de la reducción de daño del objetivo",
                        "  §8• Golpe Letal: §f+100% Daño de Golpe Crítico desde el sigilo"
                )),
        HOLY_PALADIN("Guerrero", "§e✦ Paladín de la Luz Sagrada ✦", Material.SHIELD,
                Arrays.asList(
                        "§7Canaliza la bendición del sol y la justicia celestial.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §e• Golpe Sacro: §f+40% Daño Sagrado con explosión de luz que ciega 2s",
                        "  §e• Baluarte Radiante: §fOtorga Resistencia II y Absorción tras cada impacto",
                        "  §e• Aura Redentora: §fInmunidad a veneno y fuego"
                )),
        CHRONO_ARCHMAGE("Mago", "§b✦ Crono-Mago Temporal ✦", Material.CLOCK,
                Arrays.asList(
                        "§7Manipula el fluir del tiempo y la gravedad.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §b• Distorsión Temporal: §fRalentiza enemigos a Lentitud IV por 3s",
                        "  §b• Salto Temporal: §fOtorga Velocidad III y regenera un 10% del maná",
                        "  §b• Paradoja Cuántica: §fTus ataques causan una réplica retardada de daño"
                )),
        DRAGON_KNIGHT("Guerrero", "§6✦ Caballero del Dragón ✦", Material.FIRE_CHARGE,
                Arrays.asList(
                        "§7Imbuido con el aliento y la sangre de dragones primigenios.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §6• Furia Dracónica: §f+45% Daño de fuego explosivo en área",
                        "  §6• Escamas de Dragón: §f+25% Resistencia a daño y protección ígnea",
                        "  §6• Aliento Devastador: §fIncendia objetivos y los hace vulnerables"
                )),
        SOUL_NECROMANCER("Mago", "§5✦ Nigromante de Almas ✦", Material.SOUL_LANTERN,
                Arrays.asList(
                        "§7Comanda las almas errantes del purgatorio.",
                        "",
                        "§6✦ Habilidades del Despertar:",
                        "  §5• Cosecha de Almas: §fRoba 20% de salud y maná en cada golpe mágico",
                        "  §5• Ráfaga Espectral: §fInvoca espíritus que atormentan y debilitan",
                        "  §5• Vínculo Funesto: §fDebilita el daño de los enemigos cercanos un 25%"
                ));

        public final String baseClass;
        public final String display;
        public final Material icon;
        public final List<String> perks;

        AwakenedClass(String baseClass, String display, Material icon, List<String> perks) {
            this.baseClass = baseClass;
            this.display = display;
            this.icon = icon;
            this.perks = perks;
        }

        public boolean matchesClass(String name) {
            if (name == null) return false;
            if (baseClass.equalsIgnoreCase(name)) return true;
            if (baseClass.equalsIgnoreCase("Guerrero") && name.equalsIgnoreCase("WARRIOR")) return true;
            if (baseClass.equalsIgnoreCase("Mago") && name.equalsIgnoreCase("MAGE")) return true;
            if (baseClass.equalsIgnoreCase("Arquero") && name.equalsIgnoreCase("ARCHER")) return true;
            if (baseClass.equalsIgnoreCase("Asesino") && name.equalsIgnoreCase("ASSASSIN")) return true;
            return false;
        }

        public static AwakenedClass fromBaseClass(String base) {
            if (base == null) return null;
            for (AwakenedClass ac : values()) {
                if (ac.baseClass.equalsIgnoreCase(base)) return ac;
            }
            return null;
        }
    }

    private final RPGCore plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, AwakenedClass> awakenedPlayers = new HashMap<>();

    public ClassAscensionManager(RPGCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "ascensions.yml");
        loadData();
    }

    public void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create ascensions.yml");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("ascended")) {
            for (String key : dataConfig.getConfigurationSection("ascended").getKeys(false)) {
                try {
                    UUID u = UUID.fromString(key);
                    String className = dataConfig.getString("ascended." + key);
                    awakenedPlayers.put(u, AwakenedClass.valueOf(className));
                } catch (Exception ignored) {}
            }
        }
    }

    public void saveData() {
        if (dataConfig == null) return;
        for (Map.Entry<UUID, AwakenedClass> e : awakenedPlayers.entrySet()) {
            dataConfig.set("ascended." + e.getKey().toString(), e.getValue().name());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save ascensions.yml");
        }
    }

    public boolean isAscended(UUID uuid) {
        return awakenedPlayers.containsKey(uuid);
    }

    public AwakenedClass getAwakenedClass(UUID uuid) {
        return awakenedPlayers.get(uuid);
    }

    public void openAscensionGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new AscensionHolder(), 45, "§8⚡ §6Altar del Despertar y Ascensión §8⚡");

        ItemStack border = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 45; i++) inv.setItem(i, border);

        UUID uuid = player.getUniqueId();
        boolean ascended = isAscended(uuid);
        AwakenedClass currentAwakened = getAwakenedClass(uuid);

        PlayerManager pm = plugin.getManagerHandler().get(PlayerManager.class);
        com.viega.rpg.models.PlayerData data = pm != null ? pm.getData(uuid) : null;

        String currentBaseClass = (data != null && data.getRpgClass() != null) ? data.getRpgClass().name() : "WARRIOR";
        int currentLevel = data != null ? data.getLevel() : 1;

        // Display Class Awakening options
        int[] slots = {10, 12, 14, 16, 20, 24};
        AwakenedClass[] all = AwakenedClass.values();
        for (int i = 0; i < all.length && i < slots.length; i++) {
            AwakenedClass ac = all[i];
            ItemStack item = new ItemStack(ac.icon);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ac.display);
                List<String> lore = new ArrayList<>(ac.perks);
                lore.add("");
                lore.add("§eClase Base Requerida: §f" + ac.baseClass);
                lore.add("§eNivel Requerido: §fNivel 50+ (Actual: " + currentLevel + ")");
                lore.add("");
                if (ascended && currentAwakened == ac) {
                    lore.add("§a✔ ¡DESPERTAR ACTIVO!");
                } else if (currentBaseClass != null && ac.matchesClass(currentBaseClass) && currentLevel >= 50) {
                    lore.add("§6▶ ¡Haz clic para realizar el Ritual del Despertar!");
                } else {
                    lore.add("§c✖ No cumples con los requisitos para esta ascensión.");
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slots[i], item);
        }

        // Center Slot 31: Info
        ItemStack info = new ItemStack(Material.NETHER_STAR);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName("§e✨ §lRITUAL DE ASCENSIÓN DE CLASE");
            iMeta.setLore(Arrays.asList(
                    "§7El Despertar de Clase libera el máximo potencial arcano",
                    "§7y cósmico de tu personaje, otorgando pasivas devastadoras.",
                    "",
                    "§7Tu Clase Base: §f" + (currentBaseClass != null ? currentBaseClass : "Ninguna"),
                    "§7Tu Nivel: §a" + currentLevel + " §7/ 50",
                    "§7Estado de Ascensión: " + (ascended ? "§a§lDESPERTADO (" + currentAwakened.display + "§a)" : "§cNo Ascendido")
            ));
            info.setItemMeta(iMeta);
        }
        inv.setItem(31, info);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.2f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof AscensionHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        AwakenedClass target = null;
        if (slot == 10) target = AwakenedClass.BLOOD_BERSERKER;
        else if (slot == 12) target = AwakenedClass.VOID_ARCHMAGE;
        else if (slot == 14) target = AwakenedClass.CELESTIAL_SNIPER;
        else if (slot == 16) target = AwakenedClass.SHADOW_REAPER;
        else if (slot == 20) target = AwakenedClass.HOLY_PALADIN;
        else if (slot == 24) target = AwakenedClass.CHRONO_ARCHMAGE;

        if (target != null) {
            performAscension(player, target);
        }
    }

    private void performAscension(Player player, AwakenedClass target) {
        PlayerManager pm = plugin.getManagerHandler().get(PlayerManager.class);
        com.viega.rpg.models.PlayerData data = pm != null ? pm.getData(player.getUniqueId()) : null;

        String currentBaseClass = (data != null && data.getRpgClass() != null) ? data.getRpgClass().name() : "";
        int currentLevel = data != null ? data.getLevel() : 1;

        if (currentLevel < 50) {
            player.sendMessage(ChatColor.RED + "✖ Necesitas ser al menos Nivel 50 para despertar tu clase (Nivel actual: " + currentLevel + ").");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        if (currentBaseClass == null || !target.matchesClass(currentBaseClass)) {
            player.sendMessage(ChatColor.RED + "✖ Tu clase actual (" + currentBaseClass + ") no coincide con la clase requerida (" + target.baseClass + ").");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        UUID uuid = player.getUniqueId();
        awakenedPlayers.put(uuid, target);
        saveData();

        player.closeInventory();
        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(Particle.TOTEM, loc.add(0, 1, 0), 80, 0.8, 1.2, 0.8, 0.2);
        loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 50, 0.5, 0.8, 0.5, 0.05);
        player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.4f);

        Bukkit.broadcastMessage("§6⚡ §e¡El jugador §b" + player.getName() + " §eha completado el Ritual de Ascensión y despertó como " + target.display + "§e!");
        player.sendMessage(ChatColor.GREEN + "✔ ¡Has despertado tu clase con éxito! Todas tus nuevas pasivas han sido activadas.");
    }

    public void forceAscension(Player player, AwakenedClass target) {
        UUID uuid = player.getUniqueId();
        awakenedPlayers.put(uuid, target);
        saveData();

        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(Particle.TOTEM, loc.add(0, 1, 0), 80, 0.8, 1.2, 0.8, 0.2);
        loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 50, 0.5, 0.8, 0.5, 0.05);
        player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.4f);

        player.sendMessage("§6⚡ ¡Has despertado tu clase instantáneamente como " + target.display + "§6!");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        Player player = null;
        boolean isArrow = false;
        if (event.getDamager() instanceof Player p) {
            player = p;
        } else if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player p) {
            player = p;
            isArrow = true;
        }

        if (player == null) return;
        AwakenedClass awakened = getAwakenedClass(player.getUniqueId());
        if (awakened == null) return;

        switch (awakened) {
            case BLOOD_BERSERKER:
                if (!isArrow) {
                    double hpPct = player.getHealth() / player.getMaxHealth();
                    if (hpPct < 0.40) {
                        event.setDamage(event.getDamage() * 1.50);
                        player.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3);
                    }
                    // Lifesteal 15%
                    double heal = Math.min(player.getMaxHealth() - player.getHealth(), event.getDamage() * 0.15);
                    if (heal > 0) player.setHealth(player.getHealth() + heal);
                    target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation().add(0, 1, 0), 4, 0.2, 0.2, 0.2, 0.1);
                }
                break;

            case VOID_ARCHMAGE:
                // AOE Singularity burst
                Location tLoc = target.getLocation();
                tLoc.getWorld().spawnParticle(Particle.PORTAL, tLoc.add(0, 0.8, 0), 30, 0.5, 0.5, 0.5, 0.2);
                for (org.bukkit.entity.Entity e : tLoc.getWorld().getNearbyEntities(tLoc, 4.0, 4.0, 4.0)) {
                    if (e instanceof LivingEntity nearby && nearby != player && nearby != target) {
                        nearby.damage(event.getDamage() * 0.40, player);
                        nearby.setVelocity(tLoc.toVector().subtract(nearby.getLocation().toVector()).normalize().multiply(0.2));
                    }
                }
                break;

            case CELESTIAL_SNIPER:
                if (isArrow) {
                    double distance = player.getLocation().distance(target.getLocation());
                    if (distance > 12.0) {
                        event.setDamage(event.getDamage() * 1.75);
                        target.getWorld().strikeLightningEffect(target.getLocation());
                    }
                }
                break;

            case SHADOW_REAPER:
                if (!isArrow) {
                    // Check backstab angle
                    double dot = player.getLocation().getDirection().dot(target.getLocation().getDirection());
                    if (dot > 0.5) {
                        event.setDamage(event.getDamage() * 1.60);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2, false, false));
                        player.sendMessage(ChatColor.DARK_GRAY + "✦ [Velo Espectral] ¡Golpe por la espalda asestado! Te vuelves invisible temporalmente.");
                    }
                }
                break;

            case HOLY_PALADIN:
                if (!isArrow) {
                    event.setDamage(event.getDamage() * 1.40);
                    target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation().add(0, 1.2, 0), 20, 0.4, 0.4, 0.4, 0.1);
                    target.getWorld().spawnParticle(Particle.TOTEM, target.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 0, false, false));
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 1.8f);
                }
                break;

            case CHRONO_ARCHMAGE:
                Location cLoc = target.getLocation();
                cLoc.getWorld().spawnParticle(Particle.PORTAL, cLoc.add(0, 1, 0), 40, 0.6, 0.6, 0.6, 0.3);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 3, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 2, false, false));
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 0.8f, 1.6f);
                break;

            case DRAGON_KNIGHT:
                if (!isArrow) {
                    event.setDamage(event.getDamage() * 1.45);
                    target.setFireTicks(100);
                    target.getWorld().spawnParticle(Particle.FLAME, target.getLocation().add(0, 1, 0), 25, 0.4, 0.4, 0.4, 0.08);
                    target.getWorld().spawnParticle(Particle.DRAGON_BREATH, target.getLocation().add(0, 0.8, 0), 15, 0.3, 0.3, 0.3, 0.05);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.6f, 1.4f);
                }
                break;

            case SOUL_NECROMANCER:
                double soulHeal = Math.min(player.getMaxHealth() - player.getHealth(), event.getDamage() * 0.20);
                if (soulHeal > 0) player.setHealth(player.getHealth() + soulHeal);
                target.getWorld().spawnParticle(Particle.SOUL, target.getLocation().add(0, 1, 0), 20, 0.4, 0.5, 0.4, 0.05);
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1, false, false));
                player.playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 0.8f, 1.2f);
                break;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof AscensionHolder) {
            event.setCancelled(true);
        }
    }
}
