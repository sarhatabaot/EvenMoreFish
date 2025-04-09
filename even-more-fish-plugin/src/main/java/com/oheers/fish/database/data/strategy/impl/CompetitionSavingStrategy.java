package com.oheers.fish.database.data.strategy.impl;


import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.database.data.strategy.HybridSavingStrategy;

import java.util.concurrent.TimeUnit;

public class CompetitionSavingStrategy extends HybridSavingStrategy<Competition> {
    public CompetitionSavingStrategy(long interval) {
        super(
                competition -> EvenMoreFish.getInstance().getDatabase().updateCompetition(competition),
                competitions -> EvenMoreFish.getInstance().getDatabase().batchUpdateCompetition(competitions),
                interval,
                TimeUnit.MINUTES
        );
    }
}
