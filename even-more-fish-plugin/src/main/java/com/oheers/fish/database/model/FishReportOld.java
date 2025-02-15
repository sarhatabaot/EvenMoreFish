package com.oheers.fish.database.model;

import com.oheers.fish.fishing.items.Fish;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * This is a per-player fish report.
 * Should probably also include the referenced player id, to properly reflect the database
 */
public class FishReportOld {
    private final int userId;
    private final String name;
    private final String rarity;
    private int timesCaught;
    private float size;
    private final LocalDateTime catchTime;


    public FishReportOld(String rarity, String name, float size, int timesCaught, long timeEpoch) {
        this.rarity = rarity;
        this.name = name;
        this.timesCaught = timesCaught;
        this.size = size;
        this.catchTime = getLocalDateTimeFromEpoch(timeEpoch);
    }

    public FishReportOld(String rarity, String name, float size, int timesCaught, LocalDateTime time) {
        this.rarity = rarity;
        this.name = name;
        this.timesCaught = timesCaught;
        this.size = size;
        this.catchTime = time;
    }



    private LocalDateTime getLocalDateTimeFromEpoch(long timeEpoch) {
        if (timeEpoch == -1)
            return LocalDateTime.now();
        return Instant.ofEpochSecond(timeEpoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public int getTimesCaught() {
        return timesCaught;
    }

    public void setTimesCaught(int timesCaught) {
        this.timesCaught = timesCaught;
    }

    public String getRarity() {
        return rarity;
    }

    public String getName() {
        return name;
    }

    public float getLargestLength() {
        return size;
    }

    public void setLargestLength(float largestLength) {
        this.size = largestLength;
    }

    public long getTimeEpoch() {
        return catchTime.atZone(ZoneId.systemDefault()) // Use system's default time zone
                .toEpochSecond();
    }

    public LocalDateTime getCatchTime() {
        return catchTime;
    }

    public void addFish(final @NotNull Fish fish) {
        if (fish.getLength() > this.size) {
            this.size = fish.getLength();
        }
        timesCaught++;
    }

    @Override
    public String toString() {
        return "FishReport{" +
                "name='" + name + '\'' +
                ", rarity='" + rarity + '\'' +
                ", numCaught=" + timesCaught +
                ", size=" + size +
                ", localDateTime=" + catchTime +
                '}';
    }

}
