package com.oheers.fish.commands;

import com.oheers.fish.api.economy.Economy;
import com.oheers.fish.database.DatabaseUtil;
import com.oheers.fish.messages.ConfigMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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

    public static boolean isLogDbError(final CommandSender sender) {
        if (!DatabaseUtil.isDatabaseOnline()) {
            sender.sendMessage("Database is offline.");
            return true;
        }
        return false;
    }

    public static String getPlayersVariable(List<Player> players) {
        if (players.size() == Bukkit.getOnlinePlayers().size()) {
            return "All Players";
        } else {
            return String.join(", ", players.stream().map(Player::getName).toList());
        }
    }

}
