package com.oheers.fish.plugin;


import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.SkullSaver;
import com.oheers.fish.baits.BaitListener;
import com.oheers.fish.competition.JoinChecker;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.events.FishEatEvent;
import com.oheers.fish.events.FishInteractEvent;
import com.oheers.fish.fishing.EMFFishListener;
import com.oheers.fish.fishing.FishingProcessor;
import com.oheers.fish.fishing.HuntingProcessor;
import com.oheers.fish.update.UpdateNotify;
import com.oheers.fish.utils.protection.ItemProtectionListener;
import org.bukkit.plugin.PluginManager;

public class EventManager {
    private final EvenMoreFish plugin;
    private final PluginManager pm;

    private boolean checkingEatEvent;
    private boolean checkingIntEvent;

    public EventManager(EvenMoreFish plugin) {
        this.plugin = plugin;
        this.pm = plugin.getServer().getPluginManager();
    }

    public void registerCoreListeners() {
        // Database-related listeners
        if (MainConfig.getInstance().isDatabaseOnline()) {
            pm.registerEvents(new JoinChecker(), plugin);
            pm.registerEvents(plugin.getPluginDataManager().getUserManager(), plugin);
            pm.registerEvents(new EMFFishListener(), plugin);
        }

        // Always-registered listeners
        pm.registerEvents(new FishingProcessor(), plugin);
        pm.registerEvents(new HuntingProcessor(), plugin);
        pm.registerEvents(new SkullSaver(), plugin);
        pm.registerEvents(new UpdateNotify(), plugin);
        pm.registerEvents(new BaitListener(), plugin);
        pm.registerEvents(new ItemProtectionListener(), plugin);
    }

    public void registerOptionalListeners() {
        if (checkingEatEvent) {
            pm.registerEvents(FishEatEvent.getInstance(), plugin);
        }
        if (checkingIntEvent) {
            pm.registerEvents(FishInteractEvent.getInstance(), plugin);
        }
        plugin.getDependencyManager().checkOptionalDependencies();
    }

    public void setCheckingEatEvent(boolean checkingEatEvent) {
        this.checkingEatEvent = checkingEatEvent;
    }

    public void setCheckingIntEvent(boolean checkingIntEvent) {
        this.checkingIntEvent = checkingIntEvent;
    }
}