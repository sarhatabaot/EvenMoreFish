package com.oheers.fish.database.model.fish;


import java.time.LocalDateTime;

public class FishLog {
    private final int userId;
    private final String fishName;
    private final String rarity;
    private final LocalDateTime catchTime;
    private final float length;

    public FishLog(int userId, String fishName, String rarity, LocalDateTime catchTime, float length) {
        this.userId = userId;
        this.fishName = fishName;
        this.rarity = rarity;
        this.catchTime = catchTime;
        this.length = length;
    }

    @Override
    public String toString() {
        return "FishLog{" +
                "userId=" + userId +
                ", fishName='" + fishName + '\'' +
                ", rarity='" + rarity + '\'' +
                ", catchTime=" + catchTime +
                ", length=" + length +
                '}';
    }
}
