package com.oheers.fish.baits.model;

import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;

import java.util.List;

public record BaitData(
        String id,
        List<Rarity> rarities,
        List<Fish> fish,
        boolean disabled,
        boolean infinite,
        int maxApplications,
        int dropQuantity,
        double applicationWeight,
        double catchWeight,
        boolean canBeCaught,
        boolean disableUseAlert
) {}
