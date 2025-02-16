package com.oheers.fish.database.model.fish;


import java.time.LocalDateTime;
import java.util.UUID;

public class FishStats {
    private final String fishName;
    private final String fishRarity;

    private final LocalDateTime firstCatchTime;
    private final float shortestLength;
    private final UUID shortestFisher;
    private final float longestLength;
    private final UUID longestFisher;
    private final int quantity;

    public FishStats(String fishName, String fishRarity, LocalDateTime firstCatchTime, float shortestLength, UUID shortestFisher, float longestLength, UUID longestFisher, int quantity) {
        this.fishName = fishName;
        this.fishRarity = fishRarity;
        this.firstCatchTime = firstCatchTime;
        this.shortestLength = shortestLength;
        this.shortestFisher = shortestFisher;
        this.longestLength = longestLength;
        this.longestFisher = longestFisher;
        this.quantity = quantity;
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

    public UUID getShortestFisher() {
        return shortestFisher;
    }

    public float getLongestLength() {
        return longestLength;
    }

    public UUID getLongestFisher() {
        return longestFisher;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "FishStats{" +
                "fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                ", firstCatchTime=" + firstCatchTime +
                ", shortestLength=" + shortestLength +
                ", shortestFisher=" + shortestFisher +
                ", longestLength=" + longestLength +
                ", longestFisher=" + longestFisher +
                ", quantity=" + quantity +
                '}';
    }
}
