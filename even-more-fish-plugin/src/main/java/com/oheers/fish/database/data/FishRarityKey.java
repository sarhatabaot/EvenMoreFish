package com.oheers.fish.database.data;

import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

//For use with caching only
public class FishRarityKey {
    private final String fishName;
    private final String fishRarity;

    public FishRarityKey(final String fishName, final String fishRarity) {
        this.fishName = fishName;
        this.fishRarity = fishRarity;
    }


    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull FishRarityKey of(final String fishName, final String fishRarity) {
        return new FishRarityKey(fishName, fishRarity);
    }

    public static @NotNull FishRarityKey of(final @NotNull Fish fish) {
        return new FishRarityKey(fish.getName(), fish.getRarity().getId());
    }


    @Override
    public String toString() {
        return fishName + "." + fishRarity;
    }

    public String toStringDefault() {
        return "FishRarityKey{" +
                "fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                '}';
    }
}
