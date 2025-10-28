package com.oheers.fish;



import com.oheers.fish.commands.MainCommandBrigadier;
import com.oheers.fish.config.MainConfig;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;


public class EMFModule extends EvenMoreFish{
    @Override
    public void loadCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {

            // Register the command with your own provided description and aliases:
            var command = MainCommandBrigadier.create();
            event.registrar().register(command, null, MainConfig.getInstance().getMainCommandAliases());
            Commands.literal(MainConfig.getInstance().getMainCommandName()).redirect(command);
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
