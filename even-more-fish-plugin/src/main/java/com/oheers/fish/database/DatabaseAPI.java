package com.oheers.fish.database;

import com.oheers.fish.competition.Competition;
import com.oheers.fish.database.model.FishReportOld;
import com.oheers.fish.database.model.fish.FishLog;
import com.oheers.fish.database.model.fish.FishStats;
import com.oheers.fish.database.model.user.UserFishStats;
import com.oheers.fish.database.model.user.UserReport;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for database operations related to the fishing system.
 */
public interface DatabaseAPI {

    // User-related methods
    // Has User Report
    boolean hasUser(@NotNull UUID uuid);

    boolean hasUserLog(@NotNull UUID uuid);

    void createEmptyUserReport(@NotNull UUID uuid);

    int getUserId(@NotNull UUID uuid);

    // User report methods
    void writeUserReport(@NotNull UUID uuid, @NotNull UserReport report);

    UserReport readUserReport(@NotNull UUID uuid);

    // Fish-related methods
    boolean hasFishData(@NotNull Fish fish);

    void createFishData(@NotNull Fish fish, @NotNull UUID uuid);

    void incrementFish(@NotNull Fish fish);

    @Deprecated
    String getDiscoverer(@NotNull Fish fish);

    @Deprecated
    LocalDateTime getFirstCatchDateForPlayer(@NotNull Fish fish, @NotNull HumanEntity player);

    @Deprecated
    LocalDateTime getFirstCatchDate(@NotNull Fish fish);

    @Deprecated
    float getLargestFishSizeForPlayer(@NotNull Fish fish, @NotNull HumanEntity player);

    @Deprecated
    float getLargestFishSize(@NotNull Fish fish);

    @Deprecated
    int getAmountFishCaughtForPlayer(@NotNull Fish fish, @NotNull HumanEntity player);

    @Deprecated
    int getAmountFishCaught(@NotNull Fish fish);

    @Deprecated
    void updateLargestFish(@NotNull Fish fish, @NotNull UUID uuid);

    // Fish report methods
    @Deprecated
    List<FishReportOld> getFishReportsForPlayer(@NotNull UUID uuid);

    @Deprecated
    List<FishReportOld> getReportsForFish(@NotNull UUID uuid, @NotNull Fish fish);

    void addUserFish(@NotNull FishReportOld report, int userId);

    void updateUserFish(@NotNull FishReportOld report, int userId);

    void writeFishReports(@NotNull UUID uuid, @NotNull List<FishReportOld> reports);

    boolean userHasFish(@NotNull Fish fish, @NotNull HumanEntity user);

    boolean userHasFish(@NotNull String rarity, @NotNull String fish, int id);

    // Competition-related methods
    void createCompetitionReport(@NotNull Competition competition);

    // Transaction-related methods
    void createSale(
            @NotNull String transactionId,
            @NotNull String fishName,
            @NotNull String fishRarity,
            int fishAmount,
            double fishLength,
            double priceSold
    );

    void createTransaction(
            @NotNull String transactionId,
            int userId,
            @NotNull Timestamp timestamp
    );

    UserFishStats getUserFishStats(final int userId, final String fishName, final String fishRarity);
    void createUserFishStats(final UserFishStats userFishStats);

    FishLog getFishLog(final int userId, final String fishName, final String fishRarity, final LocalDateTime time);
    //todo add getFishLog with time range
    Set<FishLog> getFishLogEntries(final int userId, final String fishName, final String fishRarity);
    void setFishLogEntry(final FishLog fishLogEntry);

    FishStats getFishStats(final String fishName, final String fishRarity);
    void setFishStats(final FishStats fishStats);

}

