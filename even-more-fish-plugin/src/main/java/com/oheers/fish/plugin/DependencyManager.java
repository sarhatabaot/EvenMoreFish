package com.oheers.fish.plugin;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.economy.GriefPreventionEconomyType;
import com.oheers.fish.economy.PlayerPointsEconomyType;
import com.oheers.fish.economy.VaultEconomyType;
import com.oheers.fish.events.AuraSkillsFishingEvent;
import com.oheers.fish.events.AureliumSkillsFishingEvent;
import com.oheers.fish.events.McMMOTreasureEvent;
import com.oheers.fish.placeholders.PlaceholderReceiver;
import com.oheers.fish.utils.HeadDBIntegration;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class DependencyManager {
    private final EvenMoreFish plugin;
    private Permission permission;
    private HeadDatabaseAPI hdbapi;

    // Dependency flags
    private boolean usingVault;
    private boolean usingPAPI;
    private boolean usingMcMMO;
    private boolean usingHeadsDB;
    private boolean usingPlayerPoints;
    private boolean usingGriefPrevention;
    private boolean usingAureliumSkills;
    private boolean usingAuraSkills;

    public DependencyManager(EvenMoreFish plugin) {
        this.plugin = plugin;
    }

    public void checkDependencies() {
        PluginManager pm = Bukkit.getPluginManager();

        this.usingVault = pm.isPluginEnabled("Vault");
        this.usingGriefPrevention = pm.isPluginEnabled("GriefPrevention");
        this.usingPlayerPoints = pm.isPluginEnabled("PlayerPoints");
        this.usingMcMMO = pm.isPluginEnabled("mcMMO");
        this.usingHeadsDB = pm.isPluginEnabled("HeadDatabase");
        this.usingPAPI = pm.isPluginEnabled("PlaceholderAPI");
        this.usingAureliumSkills = pm.isPluginEnabled("AureliumSkills");
        this.usingAuraSkills = pm.isPluginEnabled("AuraSkills");

        if (usingVault) {
            setupVaultPermissions();
        }

        loadEconomy();
        checkPapi();
    }

    public void checkOptionalDependencies() {
        PluginManager pm = plugin.getServer().getPluginManager();
        if (usingMcMMO && MainConfig.getInstance().disableMcMMOTreasure()) {
            pm.registerEvents(McMMOTreasureEvent.getInstance(), plugin);
        }


        if (usingHeadsDB) {
            pm.registerEvents(new HeadDBIntegration(), plugin);
        }

        if (usingAureliumSkills && MainConfig.getInstance().disableAureliumSkills()) {
            pm.registerEvents(new AureliumSkillsFishingEvent(), plugin);
        }

        if (usingAuraSkills && MainConfig.getInstance().disableAureliumSkills()) {
            pm.registerEvents(new AuraSkillsFishingEvent(), plugin);
        }

    }

    private void setupVaultPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp =
                    plugin.getServer().getServicesManager().getRegistration(Permission.class);
            this.permission = rsp == null ? null : rsp.getProvider();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to setup Vault permissions", e);
            this.permission = null;
        }
    }

    public boolean isUsingVault() {
        return usingVault;
    }

    public boolean isUsingPAPI() {
        return usingPAPI;
    }

    public boolean isUsingMcMMO() {
        return usingMcMMO;
    }

    public boolean isUsingHeadsDB() {
        return usingHeadsDB;
    }

    public boolean isUsingPlayerPoints() {
        return usingPlayerPoints;
    }

    public boolean isUsingGriefPrevention() {
        return usingGriefPrevention;
    }

    public Permission getPermission() {
        return permission;
    }

    public HeadDatabaseAPI getHdbapi() {
        return hdbapi;
    }

    public boolean isUsingAureliumSkills() {
        return usingAureliumSkills;
    }

    public boolean isUsingAuraSkills() {
        return usingAuraSkills;
    }

    public boolean isEconomyAvailable() {
        return usingVault || usingPlayerPoints || usingGriefPrevention;
    }

    public boolean isHeadsDBLoaded() {
        return usingHeadsDB && hdbapi != null;
    }

    public boolean isVaultPermissionsAvailable() {
        return usingVault && permission != null;
    }

    private void loadEconomy() {
        if (isUsingVault()) {
            new VaultEconomyType().register();
        }

        if (isUsingPlayerPoints()) {
            new PlayerPointsEconomyType().register();
        }

        if (isUsingGriefPrevention()) {
            new GriefPreventionEconomyType().register();
        }
    }

    private void checkPapi() {
        if (isUsingPAPI()) {
            usingPAPI = true;
            new PlaceholderReceiver(plugin).register();
        }
    }

    public void setHdbapi(HeadDatabaseAPI hdbapi) {
        this.hdbapi = hdbapi;
    }
}
