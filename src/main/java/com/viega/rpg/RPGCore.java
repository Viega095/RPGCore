package com.viega.rpg;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import com.viega.rpg.managers.*;

public class RPGCore extends JavaPlugin {

    private static RPGCore instance;
    private static Logger logger;
    private ManagerHandler managerHandler;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        logger.info("=============================");
        logger.info("   RPGCore v" + getDescription().getVersion());
        logger.info("   Enabling modules...");
        logger.info("=============================");

        // Initialize ManagerHandler
        managerHandler = new ManagerHandler(this);

        // Register Managers
        managerHandler.register(new ConfigManager());
        managerHandler.register(new PlayerManager());
        managerHandler.register(new StatManager());
        managerHandler.register(new LevelManager());
        managerHandler.register(new ClassManager());
        managerHandler.register(new SkillManager());
        managerHandler.register(new ItemManager());
        managerHandler.register(new MobManager());
        managerHandler.register(new QuestManager());
        managerHandler.register(new HUDManager());
        managerHandler.register(new GemManager());
        managerHandler.register(new BlacksmithManager());
        managerHandler.register(new TalentManager());
        managerHandler.register(new EliteMobAffixEngine());
        managerHandler.register(new AttributePointManager());

        // Enable Managers
        managerHandler.enable();

        // Resolve Dependencies
        PlayerManager playerManager = managerHandler.get(PlayerManager.class);
        StatManager statManager = managerHandler.get(StatManager.class);
        LevelManager levelManager = managerHandler.get(LevelManager.class);
        ClassManager classManager = managerHandler.get(ClassManager.class);
        SkillManager skillManager = managerHandler.get(SkillManager.class);
        ItemManager itemManager = managerHandler.get(ItemManager.class);
        MobManager mobManager = managerHandler.get(MobManager.class);
        QuestManager questManager = managerHandler.get(QuestManager.class);
        HUDManager hudManager = managerHandler.get(HUDManager.class);
        GemManager gemManager = managerHandler.get(GemManager.class);
        BlacksmithManager blacksmithManager = managerHandler.get(BlacksmithManager.class);
        TalentManager talentManager = managerHandler.get(TalentManager.class);
        EliteMobAffixEngine eliteMobAffixEngine = managerHandler.get(EliteMobAffixEngine.class);
        AttributePointManager attributePointManager = managerHandler.get(AttributePointManager.class);

        statManager.setPlayerManager(playerManager);
        levelManager.setPlayerManager(playerManager);
        classManager.setManagers(playerManager, statManager);
        skillManager.setPlayerManager(playerManager);
        hudManager.setPlayerManager(playerManager);
        questManager.setManagers(playerManager, levelManager, itemManager);
        gemManager.setManagers(playerManager, itemManager);
        blacksmithManager.setItemManager(itemManager);
        talentManager.setPlayerManager(playerManager);

        // Register Extra Skills
        skillManager.registerSkill(new com.viega.rpg.skills.OmnivampirismSkill());
        skillManager.registerSkill(new com.viega.rpg.skills.FireballSkill());
        skillManager.registerSkill(new com.viega.rpg.skills.DashSkill());
        skillManager.registerSkill(new com.viega.rpg.skills.HealSkill());

        // Register Listeners
        com.viega.rpg.gui.BlacksmithGUI blacksmithGUI = new com.viega.rpg.gui.BlacksmithGUI(this, blacksmithManager, itemManager);
        getServer().getPluginManager().registerEvents(blacksmithGUI, this);

        com.viega.rpg.gui.TalentTreeGUI talentTreeGUI = new com.viega.rpg.gui.TalentTreeGUI(this, talentManager, playerManager);
        getServer().getPluginManager().registerEvents(talentTreeGUI, this);

        this.gemSocketingGUI = new com.viega.rpg.gui.GemSocketingGUI(this, gemManager, itemManager);
        getServer().getPluginManager().registerEvents(gemSocketingGUI, this);

        getServer().getPluginManager()
                .registerEvents(new com.viega.rpg.listeners.PlayerListener(this, playerManager), this);
        com.viega.rpg.listeners.ItemListener itemListener = new com.viega.rpg.listeners.ItemListener(this,
                itemManager, playerManager);
        itemListener.setSkillManager(skillManager);
        getServer().getPluginManager().registerEvents(itemListener, this);
        getServer().getPluginManager().registerEvents(new com.viega.rpg.listeners.MobListener(this, mobManager,
                itemManager, levelManager, questManager), this);

        // Register Commands
        getCommand("class").setExecutor(new com.viega.rpg.commands.ClassCommand(this, classManager));
        getCommand("rpg").setExecutor(
                new com.viega.rpg.commands.AdminCommand(this, levelManager, itemManager, mobManager));
        getCommand("gembag").setExecutor(new com.viega.rpg.commands.GemBagCommand(this, gemManager));
        getCommand("forge").setExecutor(new com.viega.rpg.commands.ForgeCommand(this, blacksmithGUI));
        getCommand("talents").setExecutor(new com.viega.rpg.commands.TalentsCommand(talentTreeGUI));
        getCommand("gemsocket").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player p) {
                gemSocketingGUI.open(p);
            }
            return true;
        });
        getCommand("stats").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player p) {
                attributePointManager.openStatsGUI(p);
            }
            return true;
        });

        this.dungeonEngine = new com.viega.rpg.dungeons.DungeonEngine(this, playerManager, mobManager, itemManager);
        this.runewordEngine = new com.viega.rpg.equipment.RunewordEngine(this);
        this.reforgeManager = new com.viega.rpg.equipment.ReforgeManager(this);
        this.reforgeManager.setItemManager(itemManager);

        getCommand("dungeon").setExecutor(new com.viega.rpg.commands.DungeonCommand(this, dungeonEngine));
        getCommand("runewords").setExecutor(new com.viega.rpg.commands.RunewordCommand(this, runewordEngine));
        getCommand("reforge").setExecutor(new com.viega.rpg.commands.ReforgeCommand(this, reforgeManager));

        this.keystoneDungeonManager = new com.viega.rpg.dungeons.KeystoneDungeonManager(this);
        this.partySynergyEngine = new com.viega.rpg.party.PartySynergyEngine(this);
        this.raidBossEngine = new com.viega.rpg.bosses.RaidBossEngine(this);
        this.setBonusEngine = new com.viega.rpg.equipment.SetBonusEngine(this);
        this.enchantingAltarManager = new com.viega.rpg.equipment.EnchantingAltarManager(this);
        this.petCompanionManager = new com.viega.rpg.managers.PetCompanionManager(this);
        this.petManagementGUI = new com.viega.rpg.gui.PetManagementGUI(this, this.petCompanionManager);
        this.petCompanionManager.setPetGUI(this.petManagementGUI);
        this.classCraftingStation = new com.viega.rpg.gui.ClassCraftingStation(this);
        this.mysticCrowQuestGiver = new com.viega.rpg.managers.MysticCrowQuestGiver(this);
        this.updateManager = new com.viega.rpg.updater.RPGUpdateManager(this);
        this.updateManager.startAsyncCheck();

        this.classAscensionManager = new com.viega.rpg.managers.ClassAscensionManager(this);
        this.rogueBountyManager = new com.viega.rpg.managers.RogueBountyManager(this);
        this.artifactRelicEngine = new com.viega.rpg.equipment.ArtifactRelicEngine(this);
        this.customSkillComboEngine = new com.viega.rpg.skills.CustomSkillComboEngine(this);
        this.endlessTowerAbyss = new com.viega.rpg.dungeons.EndlessTowerAbyss(this);
        this.mythicWorldRaidEngine = new com.viega.rpg.bosses.MythicWorldRaidEngine(this);
        this.guildStrongholdSiegeManager = new com.viega.rpg.guilds.GuildStrongholdSiegeManager(this);
        this.dynamicQuestBoard = new com.viega.rpg.quests.DynamicQuestBoard(this);
        this.bloodMoonIncursionEngine = new com.viega.rpg.bosses.BloodMoonIncursionEngine(this);
        this.celestialCitadelDungeon = new com.viega.rpg.dungeons.CelestialCitadelDungeon(this);
        this.astralConstellationEngine = new com.viega.rpg.progression.AstralConstellationEngine(this);
        this.talentTreeGUI = talentTreeGUI;

        this.rpgMultiblockManager = new com.viega.rpg.multiblock.RpgMultiblockManager(this);
        this.rpgMultiblockManager.init();
        this.rpgMultiblockListener = new com.viega.rpg.multiblock.RpgMultiblockListener(this, this.rpgMultiblockManager);
        getServer().getPluginManager().registerEvents(this.rpgMultiblockListener, this);

        this.guiSecurityListener = new com.viega.rpg.listeners.GuiSecurityListener(this);
        getServer().getPluginManager().registerEvents(this.guiSecurityListener, this);

        getServer().getPluginManager().registerEvents(this.petCompanionManager, this);
        getServer().getPluginManager().registerEvents(this.petManagementGUI, this);
        getServer().getPluginManager().registerEvents(this.classCraftingStation, this);
        getServer().getPluginManager().registerEvents(this.mysticCrowQuestGiver, this);
        getServer().getPluginManager().registerEvents(this.classAscensionManager, this);
        getServer().getPluginManager().registerEvents(this.updateManager, this);
        getServer().getPluginManager().registerEvents(this.artifactRelicEngine, this);
        getServer().getPluginManager().registerEvents(this.customSkillComboEngine, this);
        getServer().getPluginManager().registerEvents(this.endlessTowerAbyss, this);
        getServer().getPluginManager().registerEvents(this.astralConstellationEngine, this);
        getServer().getPluginManager().registerEvents(this.mythicWorldRaidEngine, this);
        getServer().getPluginManager().registerEvents(this.guildStrongholdSiegeManager, this);
        getServer().getPluginManager().registerEvents(this.dynamicQuestBoard, this);
        getServer().getPluginManager().registerEvents(this.bloodMoonIncursionEngine, this);
        getServer().getPluginManager().registerEvents(this.celestialCitadelDungeon, this);

        getCommand("keystone").setExecutor(new com.viega.rpg.commands.KeystoneCommand(this, keystoneDungeonManager));
        getCommand("raid").setExecutor(new com.viega.rpg.commands.RaidCommand(this, raidBossEngine));
        getCommand("altar").setExecutor(new com.viega.rpg.commands.AltarCommand(this, enchantingAltarManager));
        getCommand("pet").setExecutor(new com.viega.rpg.commands.PetCommand(this, petCompanionManager));
        getCommand("rpgcontract").setExecutor(new com.viega.rpg.commands.BountyContractCommand(this, rogueBountyManager));

        if (getCommand("ascend") != null) {
            getCommand("ascend").setExecutor((sender, cmd, label, args) -> {
                if (sender instanceof org.bukkit.entity.Player p) {
                    classAscensionManager.openAscensionGUI(p);
                }
                return true;
            });
        }

        if (getCommand("craft") != null) {
            getCommand("craft").setExecutor((sender, cmd, label, args) -> {
                if (sender instanceof org.bukkit.entity.Player p) {
                    classCraftingStation.open(p);
                }
                return true;
            });
        }
        if (getCommand("crow") != null) {
            getCommand("crow").setExecutor((sender, cmd, label, args) -> {
                if (sender instanceof org.bukkit.entity.Player p) {
                    mysticCrowQuestGiver.spawnCrowNearPlayer(p);
                }
                return true;
            });
        }
        if (getCommand("artifacts") != null) {
            getCommand("artifacts").setExecutor((sender, cmd, label, args) -> {
                if (sender instanceof org.bukkit.entity.Player p) {
                    artifactRelicEngine.openGUI(p);
                }
                return true;
            });
        }
        if (getCommand("abyss") != null) {
            getCommand("abyss").setExecutor((sender, cmd, label, args) -> {
                if (sender instanceof org.bukkit.entity.Player p) {
                    if (args.length > 0 && args[0].equalsIgnoreCase("leave")) {
                        endlessTowerAbyss.leaveAbyss(p);
                    } else {
                        endlessTowerAbyss.startAbyss(p);
                    }
                }
                return true;
            });
        }

        // Set TabCompleters
        com.viega.rpg.commands.RPGTabCompleter rpgTab = new com.viega.rpg.commands.RPGTabCompleter(this);
        getCommand("rpg").setTabCompleter(rpgTab);
        getCommand("class").setTabCompleter(rpgTab);
        getCommand("dungeon").setTabCompleter(rpgTab);
        getCommand("pet").setTabCompleter(rpgTab);
        getCommand("keystone").setTabCompleter(rpgTab);
        getCommand("runewords").setTabCompleter(rpgTab);
        getCommand("rpgcontract").setTabCompleter(rpgTab);
        if (getCommand("abyss") != null) getCommand("abyss").setTabCompleter(rpgTab);
        if (getCommand("artifacts") != null) getCommand("artifacts").setTabCompleter(rpgTab);
        if (getCommand("craft") != null) getCommand("craft").setTabCompleter(rpgTab);
        if (getCommand("crow") != null) getCommand("crow").setTabCompleter(rpgTab);

        this.rpgTestLabGUI = new com.viega.rpg.gui.RPGTestLabGUI(this);
        getServer().getPluginManager().registerEvents(this.rpgTestLabGUI, this);

        this.rpgContentGuideGUI = new com.viega.rpg.gui.RPGContentGuideGUI(this);
        getServer().getPluginManager().registerEvents(this.rpgContentGuideGUI, this);

        this.globalEventsGUI = new com.viega.rpg.gui.GlobalEventsGUI(this);
        getServer().getPluginManager().registerEvents(this.globalEventsGUI, this);

        this.suiteMasterAdminGUI = new com.viega.rpg.gui.SuiteMasterAdminGUI(this);
        getServer().getPluginManager().registerEvents(this.suiteMasterAdminGUI, this);

        if (getCommand("eventos") != null) {
            getCommand("eventos").setExecutor(new com.viega.rpg.commands.EventsCommand(this));
        }
        if (getCommand("suite") != null) {
            getCommand("suite").setExecutor(new com.viega.rpg.commands.SuiteCommand(this));
        }

        this.globalEventScheduler = new com.viega.rpg.events.GlobalEventScheduler(this);
        this.globalEventScheduler.start();

        logger.info("RPGCore enabled successfully!");
    }

    private com.viega.rpg.gui.GlobalEventsGUI globalEventsGUI;
    private com.viega.rpg.gui.SuiteMasterAdminGUI suiteMasterAdminGUI;
    private com.viega.rpg.events.GlobalEventScheduler globalEventScheduler;

    private com.viega.rpg.gui.RPGTestLabGUI rpgTestLabGUI;
    private com.viega.rpg.gui.RPGContentGuideGUI rpgContentGuideGUI;
    private com.viega.rpg.gui.GemSocketingGUI gemSocketingGUI;

    private com.viega.rpg.gui.TalentTreeGUI talentTreeGUI;
    private com.viega.rpg.dungeons.DungeonEngine dungeonEngine;
    private com.viega.rpg.equipment.RunewordEngine runewordEngine;
    private com.viega.rpg.equipment.ReforgeManager reforgeManager;
    private com.viega.rpg.dungeons.KeystoneDungeonManager keystoneDungeonManager;
    private com.viega.rpg.party.PartySynergyEngine partySynergyEngine;
    private com.viega.rpg.bosses.RaidBossEngine raidBossEngine;
    private com.viega.rpg.equipment.SetBonusEngine setBonusEngine;
    private com.viega.rpg.equipment.EnchantingAltarManager enchantingAltarManager;
    private com.viega.rpg.managers.PetCompanionManager petCompanionManager;
    private com.viega.rpg.gui.PetManagementGUI petManagementGUI;
    private com.viega.rpg.gui.ClassCraftingStation classCraftingStation;
    private com.viega.rpg.managers.MysticCrowQuestGiver mysticCrowQuestGiver;
    private com.viega.rpg.updater.RPGUpdateManager updateManager;
    private com.viega.rpg.managers.ClassAscensionManager classAscensionManager;
    private com.viega.rpg.managers.RogueBountyManager rogueBountyManager;
    private com.viega.rpg.equipment.ArtifactRelicEngine artifactRelicEngine;
    private com.viega.rpg.skills.CustomSkillComboEngine customSkillComboEngine;
    private com.viega.rpg.dungeons.EndlessTowerAbyss endlessTowerAbyss;
    private com.viega.rpg.bosses.BloodMoonIncursionEngine bloodMoonIncursionEngine;
    private com.viega.rpg.dungeons.CelestialCitadelDungeon celestialCitadelDungeon;

    @Override
    public void onDisable() {
        logger.info("RPGCore disabling...");

        if (globalEventScheduler != null) {
            globalEventScheduler.stop();
        }

        if (rpgMultiblockManager != null) {
            rpgMultiblockManager.shutdown();
        }

        if (managerHandler != null) {
            managerHandler.disable();
        }

        logger.info("RPGCore disabled.");
    }

    public static RPGCore getInstance() {
        return instance;
    }

    public ManagerHandler getManagerHandler() {
        return managerHandler;
    }

    public ItemManager getItemManager() {
        return managerHandler.get(ItemManager.class);
    }

    public MobManager getMobManager() {
        return managerHandler.get(MobManager.class);
    }

    public ClassManager getClassManager() {
        return managerHandler.get(ClassManager.class);
    }

    public com.viega.rpg.gui.TalentTreeGUI getTalentTreeGUI() {
        return talentTreeGUI;
    }

    public com.viega.rpg.dungeons.DungeonEngine getDungeonEngine() {
        return dungeonEngine;
    }

    public com.viega.rpg.equipment.RunewordEngine getRunewordEngine() {
        return runewordEngine;
    }

    public com.viega.rpg.equipment.ReforgeManager getReforgeManager() {
        return reforgeManager;
    }

    public com.viega.rpg.dungeons.KeystoneDungeonManager getKeystoneDungeonManager() {
        return keystoneDungeonManager;
    }

    public com.viega.rpg.party.PartySynergyEngine getPartySynergyEngine() {
        return partySynergyEngine;
    }

    public com.viega.rpg.bosses.RaidBossEngine getRaidBossEngine() {
        return raidBossEngine;
    }

    public com.viega.rpg.equipment.SetBonusEngine getSetBonusEngine() {
        return setBonusEngine;
    }

    public com.viega.rpg.equipment.EnchantingAltarManager getEnchantingAltarManager() {
        return enchantingAltarManager;
    }

    public com.viega.rpg.managers.PetCompanionManager getPetCompanionManager() {
        return petCompanionManager;
    }

    public com.viega.rpg.managers.RogueBountyManager getRogueBountyManager() {
        return rogueBountyManager;
    }

    public com.viega.rpg.gui.GemSocketingGUI getGemSocketingGUI() {
        return gemSocketingGUI;
    }

    public com.viega.rpg.managers.EliteMobAffixEngine getEliteMobAffixEngine() {
        return managerHandler.get(com.viega.rpg.managers.EliteMobAffixEngine.class);
    }

    public com.viega.rpg.managers.AttributePointManager getAttributePointManager() {
        return managerHandler.get(com.viega.rpg.managers.AttributePointManager.class);
    }

    public com.viega.rpg.equipment.ArtifactRelicEngine getArtifactRelicEngine() {
        return artifactRelicEngine;
    }

    public com.viega.rpg.skills.CustomSkillComboEngine getCustomSkillComboEngine() {
        return customSkillComboEngine;
    }

    public com.viega.rpg.gui.ClassCraftingStation getClassCraftingStation() {
        return classCraftingStation;
    }

    public com.viega.rpg.gui.PetManagementGUI getPetManagementGUI() {
        return petManagementGUI;
    }

    public com.viega.rpg.managers.MysticCrowQuestGiver getMysticCrowQuestGiver() {
        return mysticCrowQuestGiver;
    }

    public com.viega.rpg.updater.RPGUpdateManager getUpdateManager() {
        return updateManager;
    }

    public com.viega.rpg.dungeons.EndlessTowerAbyss getEndlessTowerAbyss() {
        return endlessTowerAbyss;
    }

    public com.viega.rpg.managers.ClassAscensionManager getClassAscensionManager() {
        return classAscensionManager;
    }

    private com.viega.rpg.progression.AstralConstellationEngine astralConstellationEngine;
    private com.viega.rpg.bosses.MythicWorldRaidEngine mythicWorldRaidEngine;
    private com.viega.rpg.guilds.GuildStrongholdSiegeManager guildStrongholdSiegeManager;
    private com.viega.rpg.quests.DynamicQuestBoard dynamicQuestBoard;

    public com.viega.rpg.progression.AstralConstellationEngine getAstralConstellationEngine() {
        return astralConstellationEngine;
    }

    public com.viega.rpg.bosses.MythicWorldRaidEngine getMythicWorldRaidEngine() {
        return mythicWorldRaidEngine;
    }

    public com.viega.rpg.guilds.GuildStrongholdSiegeManager getGuildStrongholdSiegeManager() {
        return guildStrongholdSiegeManager;
    }

    public com.viega.rpg.quests.DynamicQuestBoard getDynamicQuestBoard() {
        return dynamicQuestBoard;
    }

    public com.viega.rpg.gui.RPGTestLabGUI getRPGTestLabGUI() {
        return rpgTestLabGUI;
    }

    public com.viega.rpg.gui.RPGContentGuideGUI getRPGContentGuideGUI() {
        return rpgContentGuideGUI;
    }

    public com.viega.rpg.bosses.BloodMoonIncursionEngine getBloodMoonIncursionEngine() {
        return bloodMoonIncursionEngine;
    }

    public com.viega.rpg.dungeons.CelestialCitadelDungeon getCelestialCitadelDungeon() {
        return celestialCitadelDungeon;
    }

    private com.viega.rpg.multiblock.RpgMultiblockManager rpgMultiblockManager;
    private com.viega.rpg.multiblock.RpgMultiblockListener rpgMultiblockListener;
    private com.viega.rpg.listeners.GuiSecurityListener guiSecurityListener;

    public com.viega.rpg.multiblock.RpgMultiblockManager getRpgMultiblockManager() {
        return rpgMultiblockManager;
    }

    public com.viega.rpg.gui.GlobalEventsGUI getGlobalEventsGUI() {
        return globalEventsGUI;
    }

    public com.viega.rpg.gui.SuiteMasterAdminGUI getSuiteMasterAdminGUI() {
        return suiteMasterAdminGUI;
    }
}
