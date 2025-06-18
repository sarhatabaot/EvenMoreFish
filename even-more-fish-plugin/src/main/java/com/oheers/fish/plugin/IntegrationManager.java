package com.oheers.fish.plugin;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.addons.DefaultAddons;
import com.oheers.fish.addons.InternalAddonLoader;
import com.oheers.fish.api.addons.AddonManager;
import com.oheers.fish.config.MainConfig;

import java.io.File;
import java.util.logging.Level;

public class IntegrationManager {
    private final EvenMoreFish plugin;
    private AddonManager addonManager;

    public IntegrationManager(EvenMoreFish plugin) {
        this.plugin = plugin;
    }

    public void loadAddons() {
        saveDefaultAddons();

        this.addonManager = new AddonManager();

        // Load external addons
        this.addonManager.load();

        // Load internal addons
        new InternalAddonLoader().load();
    }

    private void saveDefaultAddons() {
        if (!MainConfig.getInstance().useAdditionalAddons()) {
            return;
        }

        for (DefaultAddons defaultAddon : DefaultAddons.values()) {
            String fileName = defaultAddon.getFullFileName();
            File addonFile = new File(plugin.getDataFolder(), "addons/" + fileName);
            File jarFile = new File(plugin.getDataFolder(), "addons/" + fileName.replace(".addon", ".jar"));

            try {
                plugin.saveResource("addons/" + fileName, true);
                if (addonFile.renameTo(jarFile)) {
                    plugin.debug(Level.INFO, "Converted addon file: " + fileName);
                }
            } catch (IllegalArgumentException e) {
                plugin.debug(Level.WARNING, "Default addon not found: " + fileName);
            }
        }
    }

    public AddonManager getAddonManager() {
        return addonManager;
    }
}
