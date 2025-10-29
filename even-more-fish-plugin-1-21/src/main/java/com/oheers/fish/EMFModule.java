package com.oheers.fish;



import com.mojang.brigadier.tree.LiteralCommandNode;
import com.oheers.fish.commands.MainCommandBrigadier;
import com.oheers.fish.config.MainConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;


@SuppressWarnings("UnstableApiUsage")
public class EMFModule extends EvenMoreFish{
    @Override
    public void loadCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            // Register the main command
            LiteralCommandNode<CommandSourceStack> main = MainCommandBrigadier.create();
            event.registrar().register(main, null, MainConfig.getInstance().getMainCommandAliases());

            // Register the main command's alternative name
            LiteralCommandNode<CommandSourceStack> altName = Commands.literal(MainConfig.getInstance().getMainCommandName())
                .redirect(main)
                .build();
            event.registrar().register(altName);

            // Register the admin shortcut command
            String adminShortcut = MainConfig.getInstance().getAdminShortcutCommandName();
            if (adminShortcut == null) {
                return;
            }
            LiteralCommandNode<CommandSourceStack> admin = MainCommandBrigadier.create();
            Commands.literal(adminShortcut).redirect(admin);
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
