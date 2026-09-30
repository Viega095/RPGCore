package com.antigravity.rpg;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import com.antigravity.rpg.managers.*;

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
        skillManager.registerSkill(new com.antigravity.rpg.skills.OmnivampirismSkill());
        skillManager.registerSkill(new com.antigravity.rpg.skills.FireballSkill());
        skillManager.registerSkill(new com.antigravity.rpg.skills.DashSkill());
        skillManager.registerSkill(new com.antigravity.rpg.skills.HealSkill());

        // Register Listeners
        com.antigravity.rpg.gui.BlacksmithGUI blacksmithGUI = new com.antigravity.rpg.gui.BlacksmithGUI(this, blacksmithManager, itemManager);
        getServer().getPluginManager().registerEvents(blacksmithGUI, this);

        com.antigravity.rpg.gui.TalentTreeGUI talentTreeGUI = new com.antigravity.rpg.gui.TalentTreeGUI(this, talentManager, playerManager);
        getServer().getPluginManager().registerEvents(talentTreeGUI, this);

        this.gemSocketingGUI = new com.antigravity.rpg.gui.GemSocketingGUI(this, gemManager, itemManager);
        getServer().getPluginManager().registerEvents(gemSocketingGUI, this);

        getServer().getPluginManager()
                .registerEvents(new com.antigravity.rpg.listeners.PlayerListener(this, playerManager), this);
        com.antigravity.rpg.listeners.ItemListener itemListener = new com.antigravity.rpg.listeners.ItemListener(this,
                itemManager, playerManager);
        itemListener.setSkillManager(skillManager);
        getServer().getPluginManager().registerEvents(itemListener, this);
        getServer().getPluginManager().registerEvents(new com.antigravity.rpg.listeners.MobListener(this, mobManager,
                itemManager, levelManager, questManager), this);

        // Register Commands
        getCommand("class").setExecutor(new com.antigravity.rpg.commands.ClassCommand(this, classManager));
        getCommand("rpg").setExecutor(
                new com.antigravity.rpg.commands.AdminCommand(this, levelManager, itemManager, mobManager));
        getCommand("gembag").setExecutor(new com.antigravity.rpg.commands.GemBagCommand(this, gemManager));
        getCommand("forge").setExecutor(new com.antigravity.rpg.commands.ForgeCommand(this, blacksmithGUI));
        getCommand("talents").setExecutor(new com.antigravity.rpg.commands.TalentsCommand(talentTreeGUI));
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

        this.dungeonEngine = new com.antigravity.rpg.dungeons.DungeonEngine(this, playerManager, mobManager, itemManager);
        this.runewordEngine = new com.antigravity.rpg.equipment.RunewordEngine(this);
        this.reforgeManager = new com.antigravity.rpg.equipment.ReforgeManager(this);
        this.reforgeManager.setItemManager(itemManager);

        getCommand("dungeon").setExecutor(new com.antigravity.rpg.commands.DungeonCommand(this, dungeonEngine));
        getCommand("runewords").setExecutor(new com.antigravity.rpg.commands.RunewordCommand(this, runewordEngine));
        getCommand("reforge").setExecutor(new com.antigravity.rpg.commands.ReforgeCommand(this, reforgeManager));

        this.keystoneDungeonManager = new com.antigravity.rpg.dungeons.KeystoneDungeonManager(this);
        this.partySynergyEngine = new com.antigravity.rpg.party.PartySynergyEngine(this);
        this.raidBossEngine = new com.antigravity.rpg.bosses.RaidBossEngine(this);
        this.setBonusEngine = new com.antigravity.rpg.equipment.SetBonusEngine(this);
        this.enchantingAltarManager = new com.antigravity.rpg.equipment.EnchantingAltarManager(this);
        this.petCompanionManager = new com.antigravity.rpg.managers.PetCompanionManager(this);
        this.rogueBountyManager = new com.antigravity.rpg.managers.RogueBountyManager(this);
        this.artifactRelicEngine = new com.antigravity.rpg.equipment.ArtifactRelicEngine(this);
        this.customSkillComboEngine = new com.antigravity.rpg.skills.CustomSkillComboEngine(this);
        this.endlessTowerAbyss = new com.antigravity.rpg.dungeons.EndlessTowerAbyss(this);
        this.talentTreeGUI = talentTreeGUI;

        getServer().getPluginManager().registerEvents(this.artifactRelicEngine, this);
        getServer().getPluginManager().registerEvents(this.customSkillComboEngine, this);
        getServer().getPluginManager().registerEvents(this.endlessTowerAbyss, this);

        getCommand("keystone").setExecutor(new com.antigravity.rpg.commands.KeystoneCommand(this, keystoneDungeonManager));
        getCommand("raid").setExecutor(new com.antigravity.rpg.commands.RaidCommand(this, raidBossEngine));
        getCommand("altar").setExecutor(new com.antigravity.rpg.commands.AltarCommand(this, enchantingAltarManager));
        getCommand("pet").setExecutor(new com.antigravity.rpg.commands.PetCommand(this, petCompanionManager));
        getCommand("rpgcontract").setExecutor(new com.antigravity.rpg.commands.BountyContractCommand(this, rogueBountyManager));

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
        com.antigravity.rpg.commands.RPGTabCompleter rpgTab = new com.antigravity.rpg.commands.RPGTabCompleter(this);
        getCommand("rpg").setTabCompleter(rpgTab);
        getCommand("class").setTabCompleter(rpgTab);
        getCommand("dungeon").setTabCompleter(rpgTab);
        getCommand("pet").setTabCompleter(rpgTab);
        getCommand("keystone").setTabCompleter(rpgTab);
        getCommand("runewords").setTabCompleter(rpgTab);
        getCommand("rpgcontract").setTabCompleter(rpgTab);
        if (getCommand("abyss") != null) getCommand("abyss").setTabCompleter(rpgTab);
        if (getCommand("artifacts") != null) getCommand("artifacts").setTabCompleter(rpgTab);

        logger.info("RPGCore enabled successfully!");
    }

    private com.antigravity.rpg.gui.GemSocketingGUI gemSocketingGUI;

    private com.antigravity.rpg.gui.TalentTreeGUI talentTreeGUI;
    private com.antigravity.rpg.dungeons.DungeonEngine dungeonEngine;
    private com.antigravity.rpg.equipment.RunewordEngine runewordEngine;
    private com.antigravity.rpg.equipment.ReforgeManager reforgeManager;
    private com.antigravity.rpg.dungeons.KeystoneDungeonManager keystoneDungeonManager;
    private com.antigravity.rpg.party.PartySynergyEngine partySynergyEngine;
    private com.antigravity.rpg.bosses.RaidBossEngine raidBossEngine;
    private com.antigravity.rpg.equipment.SetBonusEngine setBonusEngine;
    private com.antigravity.rpg.equipment.EnchantingAltarManager enchantingAltarManager;
    private com.antigravity.rpg.managers.PetCompanionManager petCompanionManager;
    private com.antigravity.rpg.managers.RogueBountyManager rogueBountyManager;
    private com.antigravity.rpg.equipment.ArtifactRelicEngine artifactRelicEngine;
    private com.antigravity.rpg.skills.CustomSkillComboEngine customSkillComboEngine;
    private com.antigravity.rpg.dungeons.EndlessTowerAbyss endlessTowerAbyss;

    @Override
    public void onDisable() {
        logger.info("RPGCore disabling...");

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

    public com.antigravity.rpg.gui.TalentTreeGUI getTalentTreeGUI() {
        return talentTreeGUI;
    }

    public com.antigravity.rpg.dungeons.DungeonEngine getDungeonEngine() {
        return dungeonEngine;
    }

    public com.antigravity.rpg.equipment.RunewordEngine getRunewordEngine() {
        return runewordEngine;
    }

    public com.antigravity.rpg.equipment.ReforgeManager getReforgeManager() {
        return reforgeManager;
    }

    public com.antigravity.rpg.dungeons.KeystoneDungeonManager getKeystoneDungeonManager() {
        return keystoneDungeonManager;
    }

    public com.antigravity.rpg.party.PartySynergyEngine getPartySynergyEngine() {
        return partySynergyEngine;
    }

    public com.antigravity.rpg.bosses.RaidBossEngine getRaidBossEngine() {
        return raidBossEngine;
    }

    public com.antigravity.rpg.equipment.SetBonusEngine getSetBonusEngine() {
        return setBonusEngine;
    }

    public com.antigravity.rpg.equipment.EnchantingAltarManager getEnchantingAltarManager() {
        return enchantingAltarManager;
    }

    public com.antigravity.rpg.managers.PetCompanionManager getPetCompanionManager() {
        return petCompanionManager;
    }

    public com.antigravity.rpg.managers.RogueBountyManager getRogueBountyManager() {
        return rogueBountyManager;
    }

    public com.antigravity.rpg.gui.GemSocketingGUI getGemSocketingGUI() {
        return gemSocketingGUI;
    }

    public com.antigravity.rpg.managers.EliteMobAffixEngine getEliteMobAffixEngine() {
        return managerHandler.get(com.antigravity.rpg.managers.EliteMobAffixEngine.class);
    }

    public com.antigravity.rpg.managers.AttributePointManager getAttributePointManager() {
        return managerHandler.get(com.antigravity.rpg.managers.AttributePointManager.class);
    }

    public com.antigravity.rpg.equipment.ArtifactRelicEngine getArtifactRelicEngine() {
        return artifactRelicEngine;
    }

    public com.antigravity.rpg.skills.CustomSkillComboEngine getCustomSkillComboEngine() {
        return customSkillComboEngine;
    }

    public com.antigravity.rpg.dungeons.EndlessTowerAbyss getEndlessTowerAbyss() {
        return endlessTowerAbyss;
    }
}
