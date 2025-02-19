package com.oheers.fish.database.model.fish;


import javax.annotation.Nullable;
import java.time.LocalDateTime;

public class FishLog {
    private final int userId;
    private final String fishName;
    private final String rarity;
    private final LocalDateTime catchTime;
    private final float length;

    private final boolean inCompetition;
    private final @Nullable String competitionId;


    public FishLog(int userId, String fishName, String rarity, LocalDateTime catchTime, float length, boolean inCompetition, @Nullable String competitionId) {
        this.userId = userId;
        this.fishName = fishName;
        this.rarity = rarity;
        this.catchTime = catchTime;
        this.length = length;
        this.inCompetition = inCompetition;
        this.competitionId = competitionId;
    }

    @Override
    public String toString() {
        return "FishLog{" +
                "userId=" + userId +
                ", fishName='" + fishName + '\'' +
                ", rarity='" + rarity + '\'' +
                ", catchTime=" + catchTime +
                ", length=" + length +
                ", inCompetition=" + inCompetition +
                ", competitionId='" + competitionId + '\'' +
                '}';
    }
}
