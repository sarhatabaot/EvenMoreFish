package com.oheers.fish.database.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompetitionReport {

    private final String competitionconfigid;
    private final String winnerFish;
    private final UUID winneruuid;
    private final List<UUID> contestants = new ArrayList<>();

    private final float winnerScore;

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;


    public CompetitionReport(String competitionConfigID, String winnerFish, String winnerUUIDString, float winnerScore, String contestants, LocalDateTime startTime, LocalDateTime endTime) {
        this.competitionconfigid = competitionConfigID;
        this.winnerFish = winnerFish;
        this.winneruuid = UUID.fromString(winnerUUIDString);
        this.winnerScore = winnerScore;
        for (String contestant : contestants.split(",")) {
            this.contestants.add(UUID.fromString(contestant));
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCompetitionconfigid() {
        return competitionconfigid;
    }

    public String getWinnerFish() {
        return winnerFish;
    }

    public UUID getWinneruuid() {
        return winneruuid;
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
