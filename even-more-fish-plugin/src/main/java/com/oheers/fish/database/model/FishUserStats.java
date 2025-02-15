package com.oheers.fish.database.model;


import java.time.LocalDateTime;
import java.util.UUID;

public class FishUserStats {
    private final UUID user;
    private final String fishName;
    private final String fishRarity;

    private final LocalDateTime firstCatchTime;
    private final float shortestLength;
    private final float longestLength;

    private final int quantity;

    public FishUserStats(UUID user, String fishName, String fishRarity, LocalDateTime firstCatchTime, float shortestLength, float longestLength, int quantity) {
        this.user = user;
        this.fishName = fishName;
        this.fishRarity = fishRarity;
        this.firstCatchTime = firstCatchTime;
        this.shortestLength = shortestLength;
        this.longestLength = longestLength;
        this.quantity = quantity;
    }

    public UUID getUser() {
        return user;
    }

    public String getFishName() {
        return fishName;
    }

    public String getFishRarity() {
        return fishRarity;
    }

    public LocalDateTime getFirstCatchTime() {
        return firstCatchTime;
    }

    public float getShortestLength() {
        return shortestLength;
    }

    public float getLongestLength() {
        return longestLength;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "FishUserStats{" +
                "user=" + user +
                ", fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                ", firstCatchTime=" + firstCatchTime +
                ", shortestLength=" + shortestLength +
                ", longestLength=" + longestLength +
                ", quantity=" + quantity +
                '}';
    }
}
