package com.oheers.fish.competition;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.database.DataManager;
import com.oheers.fish.database.model.FishReport;
import com.oheers.fish.database.model.UserReport;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinChecker implements Listener {
    private final Database database;

    public JoinChecker(Database database) {
        this.database = database;
    }

    // Gives the player the active fishing bar if there's a fishing event cracking off
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (database.getUserId(event.getPlayer().getUniqueId()) == 0) {
            database.updateOrCreateUserReport(new EmptyUserReport(event.getPlayer().getUniqueId()));
        }

        Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp != null) {
            activeComp.getStatusBar().addPlayer(event.getPlayer());
            if (activeComp.getStartMessage() != null) {
                EMFMessage competitionJoin = ConfigMessage.COMPETITION_JOIN.getMessage();
                competitionJoin.setCompetitionType(activeComp.getCompetitionType().getTypeVariable().getMessage());
                EvenMoreFish.getScheduler().runTaskLater(() -> competitionJoin.send(event.getPlayer()), 20L * 3);
            }
        }
    }

    // Removes the player from the bar list if they leave the server
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp != null) {
            activeComp.getStatusBar().removePlayer(event.getPlayer());
        }

        if (!MainConfig.getInstance().isDatabaseOnline()) {
            return;
        }

        EvenMoreFish.getInstance().getUserReportDataManager().flush();
        EvenMoreFish.getInstance().getUserFishStatsDataManager().flush();
        
    }
}
