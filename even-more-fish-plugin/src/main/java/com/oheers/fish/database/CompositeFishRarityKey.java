package com.oheers.fish.database;

import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CompositeFishRarityKey{
    private final String fishName;
    private final String fishRarity;

    public CompositeFishRarityKey(final String fishName,final String fishRarity) {
        this.fishName = fishName;
        this.fishRarity = fishRarity;
    }


    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull CompositeFishRarityKey of(final String fishName, final String fishRarity) {
        return new CompositeFishRarityKey(fishName, fishRarity);
    }

    @Contract("_, _ -> new")
    public static @NotNull CompositeFishRarityKey of(final @NotNull Fish fish, final @NotNull Rarity rarity) {
        return new CompositeFishRarityKey(fish.getName(), rarity.getId());
    }


    @Override
    public String toString() {
        return fishName + "." + fishRarity;
    }

    public String toStringDefault() {
        return "CompositeFishRarityKey{" +
                "fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                '}';
    }
}
