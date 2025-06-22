package com.oheers.fish.utils;

import com.oheers.fish.competition.Competition;
import com.oheers.fish.competition.configs.CompetitionFile;
import com.oheers.fish.messages.abstracted.EMFMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public final class CompetitionUtils {

    private CompetitionUtils() {
        throw new UnsupportedOperationException();
    }

    public static void broadcastFishMessage(EMFMessage message, Player referencePlayer, boolean actionBar) {
        if (message.isEmpty()) {
            return;
        }

        Competition activeComp = Competition.getCurrentlyActive();
        List<? extends Player> validPlayers = getValidPlayers(referencePlayer, activeComp);

        if (actionBar) {
            validPlayers.forEach(message::sendActionBar);
        } else {
            validPlayers.forEach(message::send);
        }
    }

    private static @NotNull List<? extends Player> getValidPlayers(@NotNull Player referencePlayer, @Nullable Competition activeComp) {
        if (activeComp == null) {
            return Bukkit.getOnlinePlayers().stream().toList();
        }

        CompetitionFile activeCompetitionFile = activeComp.getCompetitionFile();
        Stream<? extends Player> validPlayers = Bukkit.getOnlinePlayers().stream();

        if (activeCompetitionFile.shouldBroadcastOnlyRods() || activeCompetitionFile.getBroadcastRange() > -1) {
            validPlayers = validPlayers.filter(player ->
                    (!activeCompetitionFile.shouldBroadcastOnlyRods() || PlayerUtils.isHoldingMaterial(player, Material.FISHING_ROD))
                            && (activeCompetitionFile.getBroadcastRange() <= -1 || isWithinRange(referencePlayer, player, activeCompetitionFile.getBroadcastRange()))
            );
        }

        return validPlayers.toList();
    }

    private static boolean isWithinRange(Player player1, Player player2, int rangeSquared) {
        return player1.getWorld() == player2.getWorld()
                && player1.getLocation().distanceSquared(player2.getLocation()) <= rangeSquared;
    }
}
