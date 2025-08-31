package com.oheers.fish.events;

import com.oheers.fish.plugin.DependencyManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public class EconomyServiceRegisterEvent implements Listener {
    private final DependencyManager dependencyManager;

    public EconomyServiceRegisterEvent(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    @EventHandler
    public void onEconomyServiceRegister(@NotNull ServiceRegisterEvent event) {
        if (!dependencyManager.isUsingVault()) {
            return;
        }

        if (event.getProvider().getService() != Economy.class) {
            return;
        }

        dependencyManager.loadEconomy();
    }
}
