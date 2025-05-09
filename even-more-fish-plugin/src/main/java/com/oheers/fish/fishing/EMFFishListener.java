package com.oheers.fish.fishing;


import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.EMFFishEvent;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.database.data.FishRarityKey;
import com.oheers.fish.database.data.UserFishRarityKey;
import com.oheers.fish.database.data.manager.DataManager;
import com.oheers.fish.database.model.fish.FishLog;
import com.oheers.fish.database.model.fish.FishStats;
import com.oheers.fish.database.model.user.UserFishStats;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class EMFFishListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEMFFishCatch(EMFFishEvent event) {
        if (!MainConfig.getInstance().isDatabaseOnline()) {
            return;
        }

        final int userId = EvenMoreFish.getInstance().getUserManager().getUserId(event.getPlayer().getUniqueId()); //could be thrown in the event, with a null option

        final Fish fish = event.getFish();

        handleFishLog(userId, fish, event.getCatchTime());
        handleUserFishStats(userId, fish);
        handleFishStats(fish);
    }

    private void handleFishStats(final @NotNull Fish fish) {
        final DataManager<FishStats> fishStatsDataManager = EvenMoreFish.getInstance().getFishStatsDataManager();
        final FishRarityKey fishRarityKey = FishRarityKey.of(fish);
        final FishStats stats = fishStatsDataManager.getOrCreate(
                fishRarityKey.toString(),
                key -> EvenMoreFish.getInstance().getDatabase().getFishStats(fish.getName(),fish.getRarity().getId()),
                () -> new FishStats(fish.getName(),fish.getRarity().getId(),LocalDateTime.now(),fish.getFisherman(),fish.getLength(),fish.getFisherman(),fish.getLength(),fish.getFisherman(),1)
        );

        if (stats.getLongestLength() < fish.getLength()) {
            stats.setLongestLength(fish.getLength());
            stats.setLongestFisher(fish.getFisherman());
        }

        if (stats.getShortestLength() > fish.getLength()) {
            stats.setShortestLength(fish.getLength());
            stats.setShortestFisher(fish.getFisherman());
        }

        stats.incrementQuantity();
        fishStatsDataManager.update(fishRarityKey.toString(), stats);
    }

    private void handleFishLog(final int userId, final Fish fish, final LocalDateTime catchTime) {
        final DataManager<Collection<FishLog>> fishLogDataManager = EvenMoreFish.getInstance().getFishLogDataManager();
        final String competitionId = Competition.getCurrentlyActive() != null ? Competition.getCurrentlyActive().getCompetitionName() : null;
        final FishLog log = new FishLog(userId, fish, catchTime, competitionId);
        final String key = UserFishRarityKey.of(userId,fish).toString();
        fishLogDataManager.update(key, Collections.singletonList(log)); //todo, what if there are multiple logs? we can fix this later, just don't forget
    }

    private void handleUserFishStats(final int userId, final @NotNull Fish fish) {
        final DataManager<UserFishStats> userFishStatsDataManager = EvenMoreFish.getInstance().getUserFishStatsDataManager();
        final UserFishStats stats = userFishStatsDataManager.getOrCreate(
                String.valueOf(userId),
                id -> EvenMoreFish.getInstance().getDatabase().getUserFishStats(userId, fish.getName(), fish.getRarity().getId()),
                () -> new UserFishStats(userId, fish, LocalDateTime.now())
        );

        if (stats.getLongestLength() < fish.getLength()) {
            stats.setLongestLength(fish.getLength());
        }

        if (stats.getShortestLength() > fish.getLength()) {
            stats.setShortestLength(fish.getLength());
        }

        stats.incrementQuantity();
        final String key = UserFishRarityKey.of(userId,fish).toString();
        userFishStatsDataManager.update(key, stats);
    }
}
