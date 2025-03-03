package com.oheers.fish.database.model.fish;


import java.time.LocalDateTime;
import java.util.UUID;

public class FishStats {
    private final String fishName;
    private final String fishRarity;

    private final LocalDateTime firstCatchTime;
    private final UUID discoverer;
    private final float shortestLength;
    private final UUID shortestFisher;
    private final float longestLength;
    private final UUID longestFisher;
    private final int quantity;

    public FishStats(String fishName, String fishRarity, LocalDateTime firstCatchTime, UUID discoverer, float shortestLength, UUID shortestFisher, float longestLength, UUID longestFisher, int quantity) {
        this.fishName = fishName;
        this.fishRarity = fishRarity;
        this.firstCatchTime = firstCatchTime;
        this.discoverer = discoverer;
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

    public UUID getDiscoverer() {
        return discoverer;
    }

    @Override
    public String toString() {
        return "FishStats{" +
                "fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                ", firstCatchTime=" + firstCatchTime +
                ", discoverer=" + discoverer +
                ", shortestLength=" + shortestLength +
                ", shortestFisher=" + shortestFisher +
                ", longestLength=" + longestLength +
                ", longestFisher=" + longestFisher +
                ", quantity=" + quantity +
                '}';
    }
}
