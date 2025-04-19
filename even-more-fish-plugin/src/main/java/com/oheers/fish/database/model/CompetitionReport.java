package com.oheers.fish.database.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompetitionReport {

    private final String competitionConfigId;
    private final String winnerFish;
    private final UUID winnerUuid;
    private final List<UUID> contestants = new ArrayList<>();

    private final float winnerScore;

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;


    public CompetitionReport(String competitionConfigID, String winnerFish, String winnerUUIDString, float winnerScore, String contestants, LocalDateTime startTime, LocalDateTime endTime) {
        this.competitionConfigId = competitionConfigID;
        this.winnerFish = winnerFish;
        this.winnerUuid = UUID.fromString(winnerUUIDString);
        this.winnerScore = winnerScore;
        for (String contestant : contestants.split(",")) {
            this.contestants.add(UUID.fromString(contestant));
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCompetitionConfigId() {
        return competitionConfigId;
    }

    public String getWinnerFish() {
        return winnerFish;
    }

    public UUID getWinnerUuid() {
        return winnerUuid;
    }

    public List<UUID> getContestants() {
        return contestants;
    }

    public float getWinnerScore() {
        return winnerScore;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
