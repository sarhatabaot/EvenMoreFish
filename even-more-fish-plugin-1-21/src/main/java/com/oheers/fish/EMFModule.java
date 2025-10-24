package com.oheers.fish;


import com.oheers.fish.commands.MainCommand;
import com.oheers.fish.commands.MainCommandBrigadier;
import com.oheers.fish.config.MainConfig;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.util.List;

public class EMFModule extends EvenMoreFish{
    @Override
    public void loadCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            //event.registrar().register(MainCommandBrigadier.create(), );

            // Register the command with your own provided description and aliases:
            event.registrar().register(MainCommandBrigadier.create(), null, MainConfig.getInstance().getMainCommandAliases());
        }));
    }

    @Override
    public void enableCommands() {
        //nothing
    }

    @Override
    public void registerCommands() {
        //nothing, we register in onLoad()
    }

    @Override
    public void disableCommands() {
        //nothing
    }
}
