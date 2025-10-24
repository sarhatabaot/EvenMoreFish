package com.oheers.fish.commands;

import com.oheers.fish.api.economy.Economy;
import com.oheers.fish.messages.ConfigMessage;
import org.bukkit.command.CommandSender;

public class CommandUtils {
    private CommandUtils() throws IllegalAccessException{
        throw new IllegalAccessException("Cannot create instance of static class.");
    }

    public static boolean isEconomyDisabled(CommandSender sender) {
        if (!Economy.getInstance().isEnabled()) {
            ConfigMessage.ECONOMY_DISABLED.getMessage().send(sender);
            return true;
        }
        return false;
    }

    public static boolean isEconomyEnabled(CommandSender sender) {
        return !isEconomyDisabled(sender);
    }
}
