package com.oheers.fish.database;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.database.model.FishReportOld;
import com.oheers.fish.database.model.user.UserFishStats;
import com.oheers.fish.database.model.user.UserReport;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DataManager {

    private static DataManager instance;

    private final LoadingCache<UUID, UserReport> userReportCache = Caffeine.newBuilder()
            .expireAfter(new Expiry<UUID, UserReport>() {
                @Override
                public long expireAfterCreate(@NotNull UUID uuid, @NotNull UserReport userReport, long currentTime) {
                    return getCacheDuration(uuid);
                }

                @Override
                public long expireAfterUpdate(@NotNull UUID uuid, @NotNull UserReport userReport, long currentTime, @NonNegative long currentDuration) {
                    return getCacheDuration(uuid);
                }

                @Override
                public long expireAfterRead(@NotNull UUID uuid, @NotNull UserReport userReport, long currentTime, @NonNegative long currentDuration) {
                    return getCacheDuration(uuid);
                }
            })
            .build(uuid -> EvenMoreFish.getInstance().getDatabase().readUserReport(uuid));
    private LoadingCache<UUID, UserFishStats> userFishStatsCache;
    private LoadingCache<UUID, List<FishReportOld>> fishReportCache;

    private void setup() {
        fishReportCache = Caffeine.newBuilder()
                .expireAfter(new Expiry<UUID, List<FishReportOld>>() {
                    @Override
                    public long expireAfterCreate(@NotNull UUID uuid, @NotNull List<FishReportOld> fishReports, long currentTime) {
                        return getCacheDuration(uuid);
                    }

                    @Override
                    public long expireAfterUpdate(@NotNull UUID uuid, @NotNull List<FishReportOld> fishReports, long currentTime, @NonNegative long currentDuration) {
                        return getCacheDuration(uuid);
                    }

                    @Override
                    public long expireAfterRead(@NotNull UUID uuid, @NotNull List<FishReportOld> fishReports, long currentTime, @NonNegative long currentDuration) {
                        return getCacheDuration(uuid);
                    }
                })
                .build(uuid -> EvenMoreFish.getInstance().getDatabase().readFishReports(uuid));
    }

    private long getCacheDuration(UUID uuid) {
        return Bukkit.getPlayer(uuid) != null ? Long.MAX_VALUE : 0;
    }

    public void uncacheUser(UUID uuid) {
        userReportCache.invalidate(uuid);
        fishReportCache.invalidate(uuid);
    }

    public void uncacheAll() {
        userReportCache.invalidateAll();
        fishReportCache.invalidateAll();
    }

    public boolean containsUser(UUID uuid) {
        return fishReportCache.get(uuid) != null;
    }

    public void putFishReportsCache(@NotNull final UUID uuid, @NotNull final List<FishReportOld> reports) {
        fishReportCache.put(uuid, reports);
    }

    public void putUserReportCache(@NotNull final UUID uuid, @NotNull final UserReport report) {
        userReportCache.put(uuid, report);
    }

    public UserReport getUserReportIfExists(UUID uuid) {
        return userReportCache.get(uuid);
    }

    public List<FishReportOld> getFishReportsIfExists(UUID uuid) {
        return fishReportCache.get(uuid);
    }

    public Collection<UserReport> getAllUserReports() {
        return userReportCache.asMap().values();
    }

    public ConcurrentMap<UUID, List<FishReportOld>> getAllFishReports() {
        return fishReportCache.asMap();
    }

    public static DataManager getInstance() {
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new DataManager();
            instance.setup();
        }
    }

    public void saveFishReports() {
        ConcurrentMap<UUID, List<FishReportOld>> allReports = getAllFishReports();
        EvenMoreFish.getInstance().getLogger().info("Saving " + allReports.size() + " fish reports.");
        for (Map.Entry<UUID, List<FishReportOld>> entry : allReports.entrySet()) {
            EvenMoreFish.getInstance().getDatabase().writeFishReports(entry.getKey(), entry.getValue());

            if (!EvenMoreFish.getInstance().getDatabase().hasUser(entry.getKey())) {
                EvenMoreFish.getInstance().getDatabase().createEmptyUserReport(entry.getKey());
            }
        }
    }

    public void saveUserReports() {
        EvenMoreFish.getInstance().getLogger().info("Saving " + getAllUserReports().size() + " user reports.");
        for (UserReport report : getAllUserReports()) {
            EvenMoreFish.getInstance().getDatabase().writeUserReport(report.getUUID(), report);
        }
    }

    public void handleFishCatch(@NotNull final UUID uuid, @NotNull final Fish fish) {
        List<FishReportOld> cachedReports = getCachedReportsOrReports(uuid, fish);
        putFishReportsCache(uuid, cachedReports);

        UserReport report = getUserReportIfExists(uuid);
        if (report == null) {
            return;
        }

        String fishID = fish.getRarity().getId() + ":" + fish.getName();

        report.setRecentFish(fishID);
        report.incrementFishCaught(1);
        report.incrementTotalLength(fish.getLength());
        if (report.getFirstFish().equals("None")) {
            report.setFirstFish(fishID);
        }
        if (fish.getLength() > report.getLargestLength()) {
            report.setLargestFish(fishID);
            report.setLargestLength(fish.getLength());
        }

        putUserReportCache(uuid, report);
    }

    private List<FishReportOld> getCachedReportsOrReports(final UUID uuid, final Fish fish) {
        List<FishReportOld> cachedReports = getFishReportsIfExists(uuid);
        if (cachedReports == null) {
            return List.of(
                    new FishReportOld(
                            fish.getRarity().getId(),
                            fish.getName(),
                            fish.getLength(),
                            1,
                            -1
                    )
            );
        }

        for (FishReportOld report : cachedReports) {
            if (report.getRarity().equals(fish.getRarity().getId()) && report.getName().equals(fish.getName())) {
                report.addFish(fish);
                return cachedReports;
            }
        }

        cachedReports.add(
                new FishReportOld(
                        fish.getRarity().getId(),
                        fish.getName(),
                        fish.getLength(),
                        1,
                        -1
                )
        );
        return cachedReports;
    }
}