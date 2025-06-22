package com.oheers.fish.requirements;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.utils.FishUtils;
import com.oheers.fish.api.requirement.RequirementContext;
import com.oheers.fish.api.requirement.RequirementType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BiomeRequirementType extends RequirementType {

    @Override
    public boolean checkRequirement(@NotNull RequirementContext context, @NotNull List<String> values) {
        World world = context.getWorld();
        Location location = context.getLocation();
        String configLocation = context.getConfigPath();
        if (configLocation == null) {
            configLocation = "N/A";
        }
        if (world == null) {
            EvenMoreFish.getInstance().getLogger().severe("Could not get world for " + configLocation + ", returning false by " +
                    "default. The player may not have been given a fish if you see this message multiple times.");
            return false;
        }
        if (location == null) {
            EvenMoreFish.getInstance().getLogger().severe("Could not get location for " + configLocation + ", returning false by " +
                    "default. The player may not have been given a fish if you see this message multiple times.");
            return false;
        }
        Biome hookBiome = location.getBlock().getBiome();
        for (String value : values) {
            Biome biome = FishUtils.getBiome(value);
            if (biome == null) {
                continue;
            }
            if (hookBiome.equals(biome)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "BIOME";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oheers";
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return EvenMoreFish.getInstance();
    }

}
